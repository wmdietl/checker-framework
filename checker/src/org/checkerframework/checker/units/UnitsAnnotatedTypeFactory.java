package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.MixedUnits;
import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.UnitsBottom;
import org.checkerframework.checker.units.qual.UnitsMultiple;
import org.checkerframework.checker.units.qual.UnknownUnits;
import org.checkerframework.checker.units.qual.km2;
import org.checkerframework.checker.units.qual.m;
import org.checkerframework.checker.units.qual.m2;
import org.checkerframework.checker.units.qual.mm2;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeFormatter;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ImplicitsTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.PropagationTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.util.GraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
 */

/**
 * Annotated type factory for the Units Checker.
 *
 * Handles multiple names for the same unit, with different prefixes, e.g. @kg
 * is the same as @g(Prefix.kilo).
 *
 * Supports relations between units, e.g. if "m" is a variable of type "@m" and
 * "s" is a variable of type "@s", the division "m/s" is automatically annotated
 * as "mPERs", the correct unit for the result.
 */
public class UnitsAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    private static final Class<org.checkerframework.checker.units.qual.UnitsRelations> unitsRelationsAnnoClass = org.checkerframework.checker.units.qual.UnitsRelations.class;

    protected final AnnotationMirror mixedUnits = AnnotationUtils.fromClass(elements, MixedUnits.class);
    protected final AnnotationMirror scalar = AnnotationUtils.fromClass(elements, Scalar.class);
    protected final AnnotationMirror TOP = AnnotationUtils.fromClass(elements, UnknownUnits.class);
    protected final AnnotationMirror BOTTOM = AnnotationUtils.fromClass(elements, UnitsBottom.class);

    // used in square root unit resolution
    private final AnnotationMirror m2 = AnnotationUtils.fromClass(elements, m2.class);
    private final AnnotationMirror mm2 = AnnotationUtils.fromClass(elements, mm2.class);
    private final AnnotationMirror km2 = AnnotationUtils.fromClass(elements, km2.class);

    private final AnnotationMirror m = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m.class);
    private final AnnotationMirror mm = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.milli);
    private final AnnotationMirror km = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.kilo);

    // Map from canonical class name to the corresponding UnitsRelations
    // instance.
    // We use the string to prevent instantiating the UnitsRelations multiple
    // times.
    private Map<String, UnitsRelations> unitsRel;

    private static final Map<String, Class<? extends Annotation>> externalQualsMap = new HashMap<String, Class<? extends Annotation>>();

    private static final Map<String, AnnotationMirror> aliasMap = new HashMap<String, AnnotationMirror>();

    public UnitsAnnotatedTypeFactory(BaseTypeChecker checker) {
        // use true to enable flow inference, false to disable it
        super(checker, true);

        this.postInit();
    }

    // In Units Checker, we always want to print out the Invisible Qualifiers
    // (UnknownUnits), and to format the print out of qualifiers by removing
    // Prefix.one
    @Override
    protected AnnotatedTypeFormatter createAnnotatedTypeFormatter() {
        return new UnitsAnnotatedTypeFormatter(checker);
    }

    // Converts all metric-prefixed units' alias annotations (eg @kg) into base
    // unit annotations with prefix values (eg @g(Prefix.kilo))
    @Override
    public AnnotationMirror aliasedAnnotation(AnnotationMirror anno) {
        // Get the name of the aliased annotation
        String aname = anno.getAnnotationType().toString();

        // See if we already have a map from this aliased annotation to its
        // corresponding base unit annotation
        if (aliasMap.containsKey(aname)) {
            // if so return it
            return aliasMap.get(aname);
        }

        boolean built = false;
        AnnotationMirror result = null;
        // if not, look for the UnitsMultiple meta annotations of this aliased
        // annotation
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (isUnitsMultiple(metaAnno)) {
                // retrieve the Class of the base unit annotation
                Class<? extends Annotation> baseUnitAnnoClass = AnnotationUtils.getElementValueClass(metaAnno, "quantity", true).asSubclass(Annotation.class);

                // retrieve the SI Prefix of the aliased annotation
                Prefix prefix = AnnotationUtils.getElementValueEnum(metaAnno, "prefix", Prefix.class, true);

                // Build a base unit annotation with the prefix applied
                result = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, baseUnitAnnoClass, prefix);

                // TODO: assert that this annotation is a prefix multiple of a
                // Unit that's in the supported type qualifiers list
                // currently this breaks for externally loaded annotations if
                // the order was an alias before a base annotation
                // assert isSupportedQualifier(result);

                built = true;
                break;
            }
        }

        if (built) {
            // aliases shouldn't have Prefix.one, but if it does then clean it
            // up here
            if (UnitsRelationsTools.getPrefix(result) == Prefix.one) {
                result = removePrefix(result);
            }

            // add this to the alias map
            aliasMap.put(aname, result);
            return result;
        }

        return super.aliasedAnnotation(anno);
    }

    protected Map<String, UnitsRelations> getUnitsRel() {
        if (unitsRel == null) {
            unitsRel = new HashMap<String, UnitsRelations>();
            // Always add the default units relations, for the standard units.
            unitsRel.put(UnitsRelationsDefault.class.getCanonicalName(), new UnitsRelationsDefault().init(processingEnv));
        }
        return unitsRel;
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        // Use the Units Annotated Type Loader instead of the default one
        loader = new UnitsAnnotationClassLoader(checker);

        // get all the loaded annotations
        Set<Class<? extends Annotation>> qualSet = new HashSet<Class<? extends Annotation>>();
        qualSet.addAll(getBundledTypeQualifiersWithPolyAll());

        // load all the external units
        loadAllExternalUnits();

        // copy all loaded external Units to qual set
        qualSet.addAll(externalQualsMap.values());

        return Collections.unmodifiableSet(qualSet);
    }

    private void loadAllExternalUnits() {
        // load external individually named units
        String qualNames = checker.getOption("units");
        if (qualNames != null) {
            for (String qualName : qualNames.split(",")) {
                loadExternalUnit(qualName);
            }
        }

        // load external directories of units
        String qualDirectories = checker.getOption("unitsDirs");
        if (qualDirectories != null) {
            for (String directoryName : qualDirectories.split(":")) {
                loadExternalDirectory(directoryName);
            }
        }
    }

    // loads and processes a single external units qualifier
    private void loadExternalUnit(String annoName) {
        Class<? extends Annotation> annoClass = loader.loadExternalAnnotationClass(annoName);

        addUnitToExternalQualMap(annoClass);
    }

    // loads and processes the units qualifiers from a single external directory
    private void loadExternalDirectory(String directoryName) {
        Set<Class<? extends Annotation>> annoClassSet = loader.loadExternalAnnotationClassesFromDirectory(directoryName);

        for (Class<? extends Annotation> annoClass : annoClassSet) {
            addUnitToExternalQualMap(annoClass);
        }
    }

    // adds the annotation class to the external qualifier map if it is not an
    // alias annotation
    private void addUnitToExternalQualMap(final Class<? extends Annotation> annoClass) {
        AnnotationMirror mirror = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, annoClass);

        // if it is not an aliased annotation, add to external quals map if it
        // isn't already in map
        if (!isAliasedAnnotation(mirror)) {
            String unitClassName = annoClass.getCanonicalName();
            if (!externalQualsMap.containsKey(unitClassName)) {
                externalQualsMap.put(unitClassName, annoClass);
            }
        }
        // if it is an aliased annotation
        else {
            // ensure it has a base unit
            Class<? extends Annotation> baseUnitClass = getBaseUnitAnnoClass(mirror);
            if (baseUnitClass != null) {
                // if the base unit isn't already added, add that first
                String baseUnitClassName = baseUnitClass.getCanonicalName();
                if (!externalQualsMap.containsKey(baseUnitClassName)) {
                    loadExternalUnit(baseUnitClassName);
                }

                // then add the aliased annotation to the alias map
                // TODO: refactor so we can directly add to alias map, skipping
                // the assert check in aliasedAnnotation
                aliasedAnnotation(mirror);
            } else {
                // error: somehow the aliased annotation has @UnitsMultiple meta
                // annotation, but no base class defined in that meta annotation
                // TODO: error abort
            }
        }

        // process the units annotation and add its corresponding units
        // relations class
        addUnitsRelations(annoClass);
    }

    private boolean isAliasedAnnotation(AnnotationMirror anno) {
        // loop through the meta annotations of the annotation, look for
        // UnitsMultiple
        @SuppressWarnings("unchecked")
        List<? extends AnnotationMirror> metaAnnos = anno.getAnnotationType().asElement().getAnnotationMirrors();
        for (AnnotationMirror metaAnno : metaAnnos) {
            // see if the meta annotation is UnitsMultiple
            if (isUnitsMultiple(metaAnno)) {
                // TODO: does every alias have to have Prefix?
                return true;
            }
        }

        // if we are unable to find UnitsMultiple meta annotation, then this is
        // not an Aliased Annotation
        return false;
    }

    private /*@Nullable*/ Class<? extends Annotation> getBaseUnitAnnoClass(AnnotationMirror anno) {
        // loop through the meta annotations of the annotation, look for
        // UnitsMultiple
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (isUnitsMultiple(metaAnno)) {
                // TODO: does every alias have to have Prefix?
                // retrieve the Class of the base unit annotation
                Class<? extends Annotation> baseUnitAnnoClass = AnnotationUtils.getElementValueClass(metaAnno, "quantity", true).asSubclass(Annotation.class);

                return baseUnitAnnoClass;
            }
        }
        return null;
    }

    private boolean isUnitsMultiple(AnnotationMirror metaAnno) {
        return AnnotationUtils.areSameByClass(metaAnno, UnitsMultiple.class);
    }

    /**
     * Look for an @UnitsRelations annotation on the qualifier and add it to the
     * list of UnitsRelations.
     *
     * @param qual The qualifier to investigate.
     */
    private void addUnitsRelations(Class<? extends Annotation> qual) {
        AnnotationMirror am = AnnotationUtils.fromClass(elements, qual);

        for (AnnotationMirror ama : am.getAnnotationType().asElement().getAnnotationMirrors()) {
            if (AnnotationUtils.areSameByClass(ama, unitsRelationsAnnoClass)) {
                Class<? extends UnitsRelations> theclass = AnnotationUtils.getElementValueClass(ama, "value", true).asSubclass(UnitsRelations.class);
                String classname = theclass.getCanonicalName();

                if (!getUnitsRel().containsKey(classname)) {
                    try {
                        unitsRel.put(classname, ((UnitsRelations) theclass.newInstance()).init(processingEnv));
                    } catch (InstantiationException e) {
                        // TODO
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        ImplicitsTreeAnnotator implicitsTreeAnnotator = new ImplicitsTreeAnnotator(this);
        implicitsTreeAnnotator.addTreeKind(Tree.Kind.NULL_LITERAL, BOTTOM);
        return new ListTreeAnnotator(new UnitsPropagationTreeAnnotator(this), implicitsTreeAnnotator, new UnitsTreeAnnotator(this));
    }

    private static class UnitsPropagationTreeAnnotator
            extends PropagationTreeAnnotator {

        public UnitsPropagationTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        // Handled completely by UnitsTreeAnnotator
        @Override
        public Void visitBinary(BinaryTree node, AnnotatedTypeMirror type) {
            return null;
        }

        // Handled completely by UnitsTreeAnnotator
        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree node, AnnotatedTypeMirror type) {
            return null;
        }
    }

    /**
     * A class for adding annotations based on tree
     */
    private class UnitsTreeAnnotator extends TreeAnnotator {

        UnitsTreeAnnotator(UnitsAnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree node, AnnotatedTypeMirror type) {
            Name methodName = TreeUtils.methodName(node);
            AnnotatedTypeMirror receiverType = getReceiverType(node);

            // If there's no receivers, then return immediately
            if (receiverType == null) {
                return null;
            }

            // check to see if the class is java.lang.Math or
            // java.lang.StrictMath
            TypeMirror underlyingType = receiverType.getUnderlyingType();
            TypeMirror mathType = getElementUtils().getTypeElement(java.lang.Math.class.getCanonicalName()).asType();
            TypeMirror strictMathType = getElementUtils().getTypeElement(java.lang.StrictMath.class.getCanonicalName()).asType();

            // java.lang.StrictMath implements the same methods as
            // java.lang.Math except for incrementExact, decrementExact, and
            // negateExact
            // The common methods have the same signature and same expected
            // units
            boolean isMathType = underlyingType.equals(mathType) || underlyingType.equals(strictMathType);
            // see which method in the Math library was called
            boolean isAddExact = methodName.contentEquals("addExact");
            boolean isSubtractExact = methodName.contentEquals("subtractExact");
            boolean isMultiplyExact = methodName.contentEquals("multiplyExact");
            boolean isFloorDiv = methodName.contentEquals("floorDiv");
            boolean isIEEEremainder = methodName.contentEquals("IEEEremainder");
            boolean isAtan2 = methodName.contentEquals("atan2");
            boolean isSqrt = methodName.contentEquals("sqrt");
            boolean isCbrt = methodName.contentEquals("cbrt");
            boolean isPow = methodName.contentEquals("pow");

            // stores the list of method call arguments
            List<? extends ExpressionTree> methodArguments = node.getArguments();

            // process each math library method call
            if (isMathType && isAddExact) {
                // addExact is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                visitMathOperation(Tree.Kind.PLUS, type, lht, rht);
                return null;
            } else if (isMathType && isSubtractExact) {
                // subtractExact is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                visitMathOperation(Tree.Kind.MINUS, type, lht, rht);
                return null;

            } else if (isMathType && isMultiplyExact) {
                // multiplyExact is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                visitMathOperation(Tree.Kind.MULTIPLY, type, lht, rht);
                return null;

            } else if (isMathType && isFloorDiv) {
                // floorDiv is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                visitMathOperation(Tree.Kind.DIVIDE, type, lht, rht);
                return null;

            } else if (isMathType && isIEEEremainder) {
                // IEEEremainder is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                visitMathOperation(Tree.Kind.REMAINDER, type, lht, rht);
                return null;

            } else if (isMathType && isAtan2) {
                // atan2 is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                // atan2 must be called with the same units for both of its
                // arguments
                if (!UnitsRelationsTools.hasSameUnits(lht, rht)) {
                    checker.report(Result.failure("argument.units.mismatch", lht.toString(), rht.toString()), node);
                }
                return null;

            } else if (isMathType && isSqrt) {
                // sqrt is always called with 1 argument
                AnnotatedTypeMirror arg = getAnnotatedType(methodArguments.get(0));
                if (UnitsRelationsTools.hasSpecificUnit(arg, m2)) {
                    // if sqrt was called with a meter-squared unit, return
                    // meter
                    type.replaceAnnotation(m);
                } else if (UnitsRelationsTools.hasSpecificUnit(arg, mm2)) {
                    // if sqrt was called with a millimeter-squared unit, return
                    // millimeter
                    type.replaceAnnotation(mm);
                } else if (UnitsRelationsTools.hasSpecificUnit(arg, km2)) {
                    // if sqrt was called with a kilometer-squared unit, return
                    // kilometer
                    type.replaceAnnotation(km);
                }
                return null;

            } else if (isMathType && isCbrt) {
                // Future TODO: return a partial unit representing the cubic
                // root of a unit
                return null;

            } else if (isMathType && isPow) {
                // pow is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                String secondParameter = methodArguments.get(1).toString();

                // try to parse the second parameter into a double
                double exp = 0.0;
                try {
                    exp = Double.parseDouble(secondParameter);
                } catch (NumberFormatException | NullPointerException e) {
                    // if the second parameter is a variable, we do not process
                    // further and simply return scalar
                    // keeping exp as 0.0
                }

                // see what is the exponent
                if (exp == 1.0) {
                    // return the unit of the first parameter
                    type.replaceAnnotations(lht.getAnnotations());
                } else if (exp == 2.0) {
                    // detect the unit of the first parameter
                    if (UnitsRelationsTools.hasSpecificUnit(lht, m)) {
                        type.replaceAnnotation(m2);
                    } else if (UnitsRelationsTools.hasSpecificUnit(lht, mm)) {
                        type.replaceAnnotation(mm2);
                    } else if (UnitsRelationsTools.hasSpecificUnit(lht, km)) {
                        type.replaceAnnotation(km2);
                    } else {
                        type.replaceAnnotation(scalar);
                    }
                } else {
                    // return scalar
                    type.replaceAnnotation(scalar);
                }
                return null;

            } else {
                // if it isn't a math library method call, return super's result
                return super.visitMethodInvocation(node, type);
            }
        }

        private Void visitMathOperation(Tree.Kind kind, AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
            // Remove Prefix.one
            if (UnitsRelationsTools.getPrefix(lht) == Prefix.one) {
                lht = UnitsRelationsTools.removePrefix(elements, lht);
            }
            if (UnitsRelationsTools.getPrefix(rht) == Prefix.one) {
                rht = UnitsRelationsTools.removePrefix(elements, rht);
            }

            // First use units relations to resolve the operation
            AnnotationMirror bestResult = null;
            for (UnitsRelations ur : getUnitsRel().values()) {
                AnnotationMirror res = useUnitsRelation(kind, ur, lht, rht);

                if (bestResult != null && res != null && !bestResult.equals(res)) {
                    checker.message(Kind.WARNING, "UnitsRelation mismatch, taking neither! Previous: " + bestResult + " and current: " + res);
                    return null; // super.visitBinary(node, type);
                }

                if (res != null) {
                    bestResult = res;
                }
            }

            // If there's a result from units relations, then set the result
            // type to that and return
            if (bestResult != null) {
                resultType.replaceAnnotation(bestResult);
            } else {
                // If none of the units relations classes could resolve the
                // units, then apply default rules

                // if both are UnknownUnits, leave it unchanged
                if (UnitsRelationsTools.hasSpecificUnit(lht, TOP) && UnitsRelationsTools.hasSpecificUnit(rht, TOP)) {
                    resultType.replaceAnnotation(TOP);
                    return null;
                }

                switch (kind) {
                case MINUS:
                case PLUS:
                    if (UnitsRelationsTools.hasSameUnits(lht, rht)) {
                        // The sum or difference has the same units as both
                        // operands.
                        resultType.replaceAnnotations(lht.getAnnotations());
                    } else {
                        // otherwise it results in the LUB of the left and right
                        // types
                        resultType.replaceAnnotation(getLUB(lht, rht));
                    }
                    break;
                case MULTIPLY:
                    if (UnitsRelationsTools.hasNoUnits(lht)) {
                        // any unit multiplied by a scalar keeps the unit
                        // also unknown * scalar = unknown
                        resultType.replaceAnnotations(rht.getAnnotations());
                    } else if (UnitsRelationsTools.hasNoUnits(rht)) {
                        // any scalar multiplied by a unit becomes the unit
                        // also scalar * unknown = unknown
                        resultType.replaceAnnotations(lht.getAnnotations());
                    } else if (UnitsRelationsTools.hasSpecificUnit(lht, TOP) && UnitsRelationsTools.hasSpecificUnit(rht, TOP)) {
                        // unknown * unknown = unknown
                        resultType.replaceAnnotation(TOP);
                    } else {
                        // else it is a multiplication of two units that have no
                        // defined relations from a relations class, so we
                        // return mixed
                        // Future TODO: track partial units (unit ^ 2), so that
                        // equations like unit * unit / unit == unit work
                        // also unit * unknown = mixed
                        resultType.replaceAnnotation(mixedUnits);
                    }
                    break;
                case DIVIDE:
                    if (UnitsRelationsTools.hasSpecificUnit(lht, TOP) && UnitsRelationsTools.hasSpecificUnit(rht, TOP)) {
                        // unknown / unknown = unknown
                        resultType.replaceAnnotation(TOP);
                    } else if (UnitsRelationsTools.hasSameUnits(lht, rht)) {
                        // otherwise if the units of the division match, return
                        // scalar
                        resultType.replaceAnnotation(scalar);
                    } else if (UnitsRelationsTools.hasNoUnits(rht)) {
                        // any unit divided by a scalar keeps that unit
                        // also unknown / scalar = unknown
                        resultType.replaceAnnotations(lht.getAnnotations());
                    } else if (UnitsRelationsTools.hasNoUnits(lht)) {
                        // scalar divided by any unit returns mixed
                        // Future TODO: track partial units (scalar / unit), so
                        // that equations like scalar / unit * unit == scalar
                        // work
                        // also scalar / unknown = mixed
                        resultType.replaceAnnotation(mixedUnits);
                    } else {
                        // else it is a division of two units that have no
                        // defined relations from a relations class
                        // return mixed
                        // also unit / unknown = mixed, and unknown / unit =
                        // mixed
                        resultType.replaceAnnotation(mixedUnits);
                    }
                    break;
                case REMAINDER:
                    // in modulo operation, it always returns the left unit
                    // regardless of what it is (scalar, unknown, or some unit)
                    resultType.replaceAnnotations(lht.getAnnotations());
                    break;
                default:
                    // Placeholders for unhandled binary operations
                    // Do nothing
                }
            }

            return null;
        }

        @Override
        public Void visitBinary(BinaryTree node, AnnotatedTypeMirror type) {
            AnnotatedTypeMirror lht = getAnnotatedType(node.getLeftOperand());
            AnnotatedTypeMirror rht = getAnnotatedType(node.getRightOperand());
            Tree.Kind kind = node.getKind();

            visitMathOperation(kind, type, lht, rht);
            return null;
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree node, AnnotatedTypeMirror type) {
            ExpressionTree var = node.getVariable();
            AnnotatedTypeMirror varType = getAnnotatedType(var);

            // checker.message(Kind.NOTE, "Units ATF visit Compound, variable: "
            // + var.toString() + " replacement type: " + varType.toString());
            type.replaceAnnotations(varType.getAnnotations());
            return null;
        }

        /**
         * Uses a units relations class to resolve and return an annotation
         * mirror representing the type of the result of a calculation, or null
         * if this units relations class doesn't specify what type to return
         *
         * @param kind Tree.Kind representing a mathematical operation
         * @param ur units relations class
         * @param lht left type
         * @param rht right type
         * @return an AnnotationMirror representing the type of the result of a
         *         calculation, or null if this units relations class doesn't
         *         specify what type to return
         */
        private AnnotationMirror useUnitsRelation(Tree.Kind kind, UnitsRelations ur, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {

            AnnotationMirror res = null;
            if (ur != null) {
                switch (kind) {
                // TODO: addition, subtraction, remainder
                case DIVIDE:
                    res = ur.division(lht, rht);
                    break;
                case MULTIPLY:
                    res = ur.multiplication(lht, rht);
                    break;
                default:
                    // Do nothing
                }
            }
            return res;
        }

        /**
         * Returns the least upper bound of the two annotated types through the
         * type factory
         *
         * @param lht left type
         * @param rht right type
         * @return the least upper bound of the two annotated types
         */
        private AnnotationMirror getLUB(AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
            AnnotationMirror lhtMirror = lht.getAnnotations().iterator().next();
            AnnotationMirror rhtMirror = rht.getAnnotations().iterator().next();
            return atypeFactory.getQualifierHierarchy().leastUpperBound(lhtMirror, rhtMirror);
        }
    }

    /**
     * Set the Bottom qualifier as the bottom of the hierarchy.
     */
    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new UnitsQualifierHierarchy(factory, AnnotationUtils.fromClass(elements, UnitsBottom.class));
    }

    protected class UnitsQualifierHierarchy extends GraphQualifierHierarchy {

        public UnitsQualifierHierarchy(MultiGraphFactory mgf, AnnotationMirror bottom) {
            super(mgf, bottom);
        }

        @Override
        public boolean isSubtype(AnnotationMirror rhs, AnnotationMirror lhs) {
            if (AnnotationUtils.areSameIgnoringValues(lhs, rhs)) {
                return AnnotationUtils.areSame(lhs, rhs);
            }
            lhs = removePrefix(lhs);
            rhs = removePrefix(rhs);

            return super.isSubtype(rhs, lhs);
        }

        // Overriding leastUpperBound due to the fact that alias annotations are
        // not placed in the Supported Type Qualifiers set, instead, their base
        // SI units are in the set.
        // Whenever an alias annotation or prefix-multiple of a base SI unit is
        // used in ternary statements or through mismatched PolyUnit method
        // parameters, we handle the LUB resolution here so that these units can
        // correctly resolve to an LUB Unit.
        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            AnnotationMirror result;

            // if the prefix is Prefix.one, automatically strip it for LUB
            // checking
            if (UnitsRelationsTools.getPrefix(a1) == Prefix.one) {
                a1 = removePrefix(a1);
            }
            if (UnitsRelationsTools.getPrefix(a2) == Prefix.one) {
                a2 = removePrefix(a2);
            }

            // if the two units have the same base SI unit
            if (UnitsRelationsTools.hasSameUnitsIgnoringPrefix(a1, a2)) {
                if (UnitsRelationsTools.hasSameUnits(a1, a2)) {
                    // and if they have the same Prefix, it means it is the same
                    // unit, so we return the unit
                    result = a1;
                } else {
                    // if they don't have the same Prefix, find the LUB

                    // check if a1 is a prefixed multiple of a base unit
                    boolean a1Prefixed = !UnitsRelationsTools.hasNoPrefix(a1);
                    // check if a2 is a prefixed multiple of a base unit
                    boolean a2Prefixed = !UnitsRelationsTools.hasNoPrefix(a2);

                    // when calling findLub(), the left AnnoMirror has to be a
                    // type within the supertypes Map
                    // this means it has to be one of the base SI units, so
                    // always strip the left unit or ensure it has no prefix
                    if (a1Prefixed && a2Prefixed) {
                        // if both are prefixed, strip the left and find LUB
                        result = this.findLub(removePrefix(a1), a2);
                    } else if (a1Prefixed && !a2Prefixed) {
                        // if only the left is prefixed, swap order and find LUB
                        result = this.findLub(a2, a1);
                    } else {
                        // else (only right is prefixed), just find the LUB
                        result = this.findLub(a1, a2);
                    }
                }
            } else {
                // if they don't have the same base SI unit, let super find it
                result = super.leastUpperBound(a1, a2);
            }

            return result;
        }
    }

    private AnnotationMirror removePrefix(AnnotationMirror anno) {
        return UnitsRelationsTools.removePrefix(elements, anno);
    }
}
