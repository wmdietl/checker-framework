package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.UnitsBottom;
import org.checkerframework.checker.units.qual.UnitsMultiple;
import org.checkerframework.checker.units.qual.UnknownUnits;
import org.checkerframework.checker.units.qual.km2;
import org.checkerframework.checker.units.qual.km3;
import org.checkerframework.checker.units.qual.m;
import org.checkerframework.checker.units.qual.m2;
import org.checkerframework.checker.units.qual.m3;
import org.checkerframework.checker.units.qual.mm2;
import org.checkerframework.checker.units.qual.mm3;
import org.checkerframework.checker.units.qual.time.duration.TimeDuration;
import org.checkerframework.checker.units.qual.time.instant.DurationUnit;
import org.checkerframework.checker.units.qual.time.instant.TimeInstant;
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
import org.checkerframework.javacutil.Pair;
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
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ParenthesizedTree;
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

    protected final AnnotationMirror scalar = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, Scalar.class);
    protected final AnnotationMirror TOP = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, UnknownUnits.class);
    protected final AnnotationMirror BOTTOM = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, UnitsBottom.class);

    private final AnnotationMirror m = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m.class);
    private final AnnotationMirror mm = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.milli);
    private final AnnotationMirror km = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.kilo);

    // used in square root unit resolution
    private final AnnotationMirror m2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m2.class);
    private final AnnotationMirror mm2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, mm2.class);
    private final AnnotationMirror km2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, km2.class);

    // used in cube root unit resolution
    private final AnnotationMirror m3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m3.class);
    private final AnnotationMirror km3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, km3.class);
    private final AnnotationMirror mm3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, mm3.class);

    // used in time units resolution
    private final AnnotationMirror timeDuration = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, TimeDuration.class);
    private final AnnotationMirror timeInstant = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, TimeInstant.class);

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

        if (!isAliasedAnnotation(mirror)) {
            // if it is not an aliased annotation, add to external quals map if
            // it isn't already in map
            String unitClassName = annoClass.getCanonicalName();
            if (!externalQualsMap.containsKey(unitClassName)) {
                externalQualsMap.put(unitClassName, annoClass);
            }
        } else {
            // if it is an aliased annotation
            Pair<Class<? extends Annotation>, Prefix> baseUnit = getBaseUnitClassAndPrefix(mirror);
            Class<? extends Annotation> baseUnitClass = baseUnit.first;

            // ensure it has a base unit
            if (baseUnitClass != null) {
                // if the base unit isn't already added, add that first
                String baseUnitClassName = baseUnitClass.getCanonicalName();
                if (!externalQualsMap.containsKey(baseUnitClassName)) {
                    loadExternalUnit(baseUnitClassName);
                }

                // Build and add the alias's standard annotation to the alias
                // map
                buildBaseUnitAnnotationForAlias(mirror);

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
        return getUnitsMultipleMetaAnnotation(anno) != null;
    }

    private /*@Nullable*/ AnnotationMirror getUnitsMultipleMetaAnnotation(AnnotationMirror anno) {
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (isUnitsMultiple(metaAnno)) {
                return metaAnno;
            }
        }
        return null;
    }

    private boolean isUnitsMultiple(AnnotationMirror metaAnno) {
        return AnnotationUtils.areSameByClass(metaAnno, UnitsMultiple.class);
    }

    /**
     * If given an alias annotation, this method will return a pair consisting
     * of the alias's base unit annotation class and the alias's prefix. If
     * given any other annotation, this method will return null.
     *
     * @param anno a units annotation
     * @return the pair, or null
     */
    private /*@Nullable*/ Pair<Class<? extends Annotation>, Prefix> getBaseUnitClassAndPrefix(AnnotationMirror anno) {
        // see if the annotation is an alias
        if (isAliasedAnnotation(anno)) {
            AnnotationMirror unitsMultipleAnno = getUnitsMultipleMetaAnnotation(anno);

            // retrieve the Class of the base unit annotation
            Class<? extends Annotation> baseUnitAnnoClass = AnnotationUtils.getElementValueClass(unitsMultipleAnno, "quantity", true).asSubclass(Annotation.class);

            // TODO: does every alias have to have a Prefix?
            // retrieve the Prefix of the alias unit
            Prefix prefix = AnnotationUtils.getElementValueEnum(unitsMultipleAnno, "prefix", Prefix.class, true);

            // return the Class and the Prefix as a pair
            return Pair.<Class<? extends Annotation>, Prefix> of(baseUnitAnnoClass, prefix);
        } else {
            return null;
        }
    }

    /**
     * Given an alias annotation, this method builds a base unit annotation with
     * the alias's prefix, adds the alias to the aliasMap, and then returns the
     * base annotation.
     *
     * Given any other annotation, this method returns null.
     *
     * e.g. given @kg this will build @g with prefix {@link Prefix#kilo}
     *
     * @param anno an alias annotation
     * @return the base unit annotation with the alias's prefix, null otherwise
     */
    private /*@Nullable*/ AnnotationMirror buildBaseUnitAnnotationForAlias(AnnotationMirror anno) {
        // Get the name of the aliased annotation
        String aname = anno.getAnnotationType().toString();

        // Obtain the base unit class and alias prefix
        Pair<Class<? extends Annotation>, Prefix> baseUnit = getBaseUnitClassAndPrefix(anno);

        AnnotationMirror result = null;

        if (baseUnit != null) {
            Class<? extends Annotation> baseUnitClass = baseUnit.first;
            Prefix prefix = baseUnit.second;

            // Try to build a base unit annotation with the prefix applied
            result = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, baseUnitClass, prefix);

            // see if we are able to build the base unit annotation with the
            // alias's prefix
            if (result != null) {
                // aliases shouldn't have Prefix.one, but if it does then clean
                // it
                // up here
                if (UnitsRelationsTools.getPrefix(result) == Prefix.one) {
                    result = removePrefix(result);
                }
                // add this to the alias map
                aliasMap.put(aname, result);
            }
        }

        return result;
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

        AnnotationMirror result = buildBaseUnitAnnotationForAlias(anno);

        if (result != null) {
            // Assert that this annotation is a prefix multiple of a
            // Unit that's in the supported type qualifiers list
            assert isSupportedQualifier(result);

            return result;
        } else {
            return super.aliasedAnnotation(anno);
        }
    }

    protected Map<String, UnitsRelations> getUnitsRelationsMap() {
        if (unitsRel == null) {
            unitsRel = new HashMap<String, UnitsRelations>();
            // Always add the default units relations, for the standard units.
            unitsRel.put(UnitsRelationsDefault.class.getCanonicalName(),
                    new UnitsRelationsDefault().init(processingEnv));
        }
        return unitsRel;
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

                if (!getUnitsRelationsMap().containsKey(classname)) {
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

    private boolean annotatedTypeIsSubtype(/*@Nullable*/ final AnnotatedTypeMirror t, /*@Nullable*/ final AnnotationMirror superType) {
        if (t == null || superType == null) {
            return false;
        }

        Set<AnnotationMirror> annos = t.getEffectiveAnnotations();

        // if one of the annotations of the ATM t is a subtype of superType then
        // return true
        for (AnnotationMirror anno : annos) {
            if (this.getQualifierHierarchy().isSubtype(anno, superType)) {
                return true;
            }
        }

        return false;
    }

    // Helper methods for processing time units
    private boolean isTimeDuration(/*@Nullable*/ final AnnotatedTypeMirror t) {
        return annotatedTypeIsSubtype(t, timeDuration);
    }

    private boolean isTimeInstant(/*@Nullable*/ final AnnotatedTypeMirror t) {
        return annotatedTypeIsSubtype(t, timeInstant);
    }


    private /*@Nullable*/ AnnotationMirror getTimeDurationUnit(/*@Nullable*/ final AnnotatedTypeMirror t) {
        if (t == null || !isTimeInstant(t)) {
            return null;
        }

        // get the time unit annotation
        AnnotationMirror timeUnit = UnitsRelationsTools.getUnit(t);
        // get the durationUnit meta annotation
        AnnotationMirror durationUnit = getDurationUnitMetaAnnotation(timeUnit);

        if (durationUnit != null) {
            // retrieve the Class of the duration unit
            Class<? extends Annotation> durationUnitAnnoClass = AnnotationUtils.getElementValueClass(durationUnit, "unit", true).asSubclass(Annotation.class);

            return UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, durationUnitAnnoClass);
        }

        return null;
    }

    private /*@Nullable*/ AnnotationMirror getDurationUnitMetaAnnotation(AnnotationMirror anno) {
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (isDurationUnitAnno(metaAnno)) {
                return metaAnno;
            }
        }
        return null;
    }

    private boolean isDurationUnitAnno(AnnotationMirror metaAnno) {
        return AnnotationUtils.areSameByClass(metaAnno, DurationUnit.class);
    }

    private AnnotationMirror removePrefix(AnnotationMirror anno) {
        return UnitsRelationsTools.removePrefix(elements, anno);
    }

    // =========================================================
    // Tree Annotators
    // =========================================================

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
        // cache expressions evaluated in evalMathExpression
        private final Map<String, Pair<Double, Boolean>> mathExpressions;

        private final TypeMirror stringType;
        private final TypeMirror mathType;
        private final TypeMirror strictMathType;

        UnitsTreeAnnotator(UnitsAnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);

            mathExpressions = new HashMap<String, Pair<Double, Boolean>>();

            stringType = getElementUtils().getTypeElement(java.lang.String.class.getCanonicalName()).asType();
            mathType = getElementUtils().getTypeElement(java.lang.Math.class.getCanonicalName()).asType();
            strictMathType = getElementUtils().getTypeElement(java.lang.StrictMath.class.getCanonicalName()).asType();
        }

        private boolean isSameUnderlyingType(TypeMirror lht, TypeMirror rht) {
            // use typeUtils.isSameType instead of TypeMirror.equals as this
            // will check only the underlying type and ignores declarations on
            // the type mirror
            return checker.getTypeUtils().isSameType(lht, rht);
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

            // java.lang.StrictMath implements the same methods as
            // java.lang.Math except for incrementExact, decrementExact, and
            // negateExact
            // The common methods have the same signature and same expected
            // units
            boolean isMathType = isSameUnderlyingType(underlyingType, mathType) || isSameUnderlyingType(underlyingType, strictMathType);
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

            if (!isMathType) {
                // if it isn't a math library method call, return super's result
                return super.visitMethodInvocation(node, type);
            }

            // process each math library method call
            if (isAddExact) {
                // addExact is always called with 2 arguments
                return processMathLibraryArithmeticOperation(node, Tree.Kind.PLUS, type, methodArguments);

            } else if (isSubtractExact) {
                // subtractExact is always called with 2 arguments
                return processMathLibraryArithmeticOperation(node, Tree.Kind.MINUS, type, methodArguments);

            } else if (isMultiplyExact) {
                // multiplyExact is always called with 2 arguments
                return processMathLibraryArithmeticOperation(node, Tree.Kind.MULTIPLY, type, methodArguments);

            } else if (isFloorDiv) {
                // floorDiv is always called with 2 arguments
                return processMathLibraryArithmeticOperation(node, Tree.Kind.DIVIDE, type, methodArguments);

            } else if (isIEEEremainder) {
                // IEEEremainder is always called with 2 arguments
                return processMathLibraryArithmeticOperation(node, Tree.Kind.REMAINDER, type, methodArguments);

            } else if (isAtan2) {
                // atan2 is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
                // atan2 must be called with the same units for both of its
                // arguments
                if (!UnitsRelationsTools.hasSameUnits(lht, rht)) {
                    checker.report(Result.failure("two.parameter.method.arguments.unit.mismatch", lht.toString(), rht.toString()), node);
                }

            } else if (isSqrt) {
                // sqrt is always called with 1 argument
                AnnotatedTypeMirror arg = getAnnotatedType(methodArguments.get(0));
                return processSquareRoot(arg, type);

            } else if (isCbrt) {
                // cbrt is always called with 1 argument
                AnnotatedTypeMirror arg = getAnnotatedType(methodArguments.get(0));
                return processCubeRoot(arg, type);

            } else if (isPow) {
                // pow is always called with 2 arguments
                AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
                ExpressionTree rh = methodArguments.get(1);

                double exp = 0.0;
                if (rh instanceof BinaryTree || rh instanceof ParenthesizedTree || rh instanceof LiteralTree) {
                    // if the second argument is a mathematical expression
                    // consisting of only parentheses and literals (eg (1.0 + 3) / 8)
                    // then evaluate it to a single value.
                    // if the second argument is a numerical literal, then
                    // extract the value.
                    Pair<Double, Boolean> evalValue = evalMathExpression(rh);

                    // the Boolean returned is false if the expression contains
                    // any operator or argument for which it cannot evaluate

                    // if successfully, set exponent to the evaluated value,
                    // otherwise set to 0.0
                    exp = evalValue.second ? evalValue.first : 0.0;
                } else {
                    // if the second argument is not a mathematical expression
                    // nor a numerical literal, then do nothing
                    return null;
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
                        // if the base unit is something other than m, mm, or km, then return unknown
                        type.replaceAnnotation(TOP);
                    }
                } else if (exp == 3.0) {
                    // detect the unit of the first parameter
                    if (UnitsRelationsTools.hasSpecificUnit(lht, m)) {
                        type.replaceAnnotation(m3);
                    } else if (UnitsRelationsTools.hasSpecificUnit(lht, mm)) {
                        type.replaceAnnotation(mm3);
                    } else if (UnitsRelationsTools.hasSpecificUnit(lht, km)) {
                        type.replaceAnnotation(km3);
                    } else {
                        // if the base unit is something other than m, mm, or km, then return unknown
                        type.replaceAnnotation(TOP);
                    }
                } else if (exp == 0.5) {
                    // taking the power of 0.5 is the same as taking the square
                    // root
                    return processSquareRoot(lht, type);
                } else if (exp == (1.0d / 3)) {
                    // taking the power of 1/3 is the same as taking the cube
                    // root
                    return processCubeRoot(lht, type);
                } else {
                    // return UnknownUnits
                    type.replaceAnnotation(TOP);
                }

            } else {
                // for any other math library method call, return super's result
                return super.visitMethodInvocation(node, type);
            }

            return null;
        }

        /**
         * Attempts to evaluate a mathematical expression (passed in as root)
         * into a double value. The method returns the double value and true as
         * a pair if the evaluation was successful, otherwise it returns 0.0 and
         * false as a pair.
         *
         * Expressions with any non number literals will not be successfully
         * evaluated.
         *
         * @param root the root node in the expression tree of a mathematical
         *            expression
         * @return a pair of a Double and a Boolean
         */
        private final Pair<Double, Boolean> evalMathExpression(ExpressionTree root) {
            String mathExpression = root.toString();
            // check against the cache to see if this expression has been
            // evaluated before
            if (mathExpressions.containsKey(mathExpression)) {
                // if so return prior results
                return mathExpressions.get(mathExpression);
            } else {
                // otherwise evaluate the expression, cache the results, and
                // return the results
                Pair<Double, Boolean> evaluation = evalMathExpression(root, true);
                mathExpressions.put(mathExpression, evaluation);
                return evaluation;
            }
        }

        // evaluate the expression and sub-expressions via recursion
        private final Pair<Double, Boolean> evalMathExpression(ExpressionTree node, Boolean success) {
            // if a previous step resulted in failure then terminate the
            // recursion chain by returning 0 as the double value
            if (success == false) {
                return Pair.of(0.0, false);
            }

            if (node instanceof LiteralTree) {
                // if the expression is a numerical literal, then extract the
                // value
                double literalVal = 0.0;
                Object val = ((LiteralTree) node).getValue();
                // convert the literal value from object type into a Double and
                // then return it
                if (node.getKind() == Tree.Kind.DOUBLE_LITERAL) {
                    literalVal = (double) val;
                } else if (node.getKind() == Tree.Kind.FLOAT_LITERAL) {
                    literalVal = (float) val;
                } else if (node.getKind() == Tree.Kind.LONG_LITERAL) {
                    literalVal = (long) val;
                } else if (node.getKind() == Tree.Kind.INT_LITERAL) {
                    literalVal = (int) val;
                }
                return Pair.of(literalVal, true);

            } else if (node instanceof ParenthesizedTree) {
                // if there's a parenthesis, evaluate and return the value of
                // the parenthesized expression
                ExpressionTree innerExpression = ((ParenthesizedTree) node).getExpression();
                return evalMathExpression(innerExpression, success);

            } else if (node instanceof BinaryTree) {
                // if it is a binary math operation, evaluate each operand and
                // then return the result
                BinaryTree expression = (BinaryTree) node;
                Pair<Double, Boolean> left = evalMathExpression(expression.getLeftOperand(), success);
                Pair<Double, Boolean> right = evalMathExpression(expression.getRightOperand(), left.second);

                // if either of the evaluations resulted in failure then
                // terminate the recursion chain
                if (left.second == false || right.second == false) {
                    return Pair.of(0.0, false);
                }

                // TODO: bitshift support?
                switch (node.getKind()) {
                case PLUS:
                    return Pair.of(left.first + right.first, true);
                case MINUS:
                    return Pair.of(left.first - right.first, true);
                case MULTIPLY:
                    return Pair.of(left.first * right.first, true);
                case DIVIDE:
                    return Pair.of(left.first / right.first, true);
                case REMAINDER:
                    return Pair.of(left.first % right.first, true);
                default:
                    break;
                }
            }

            // otherwise return 0 and false
            return Pair.of(0.0, false);
        }

        /**
         * Helper method which extracts the two arguments of a Math library
         * method call, then calls processMathOperation to check and assign the
         * resulting type
         *
         * @param kind kind of operation: PLUS, MINUS, MULTIPLY, DIVIDE,
         *            REMAINDER
         * @param type the resulting type
         * @param methodArguments the arguments to the Math library method call
         * @return the resulting type will be assigned according to math
         *         operation rules
         */
        private Void processMathLibraryArithmeticOperation(ExpressionTree node, Tree.Kind kind, AnnotatedTypeMirror type, List<? extends ExpressionTree> methodArguments) {
            AnnotatedTypeMirror lht = getAnnotatedType(methodArguments.get(0));
            AnnotatedTypeMirror rht = getAnnotatedType(methodArguments.get(1));
            return processMathOperation(node, kind, type, lht, rht);
        }

        /**
         * If arg is one of the supported area units, then it will return the
         * corresponding length unit
         *
         * @param arg a unit which may or may not be one of the supported area
         *            units
         * @param type the result unit
         * @return type will be replaced with the corresponding length unit if
         *         arg is a supported area unit
         */
        private Void processSquareRoot(AnnotatedTypeMirror arg, AnnotatedTypeMirror type) {
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
        }

        /**
         * If arg is one of the supported volume units, then it will return the
         * corresponding length unit
         *
         * @param arg a unit which may or may not be one of the supported volume
         *            units
         * @param type the result unit
         * @return type will be replaced with the corresponding length unit if
         *         arg is a supported volume unit
         */
        private Void processCubeRoot(AnnotatedTypeMirror arg, AnnotatedTypeMirror type) {
            if (UnitsRelationsTools.hasSpecificUnit(arg, m3)) {
                // if cbrt was called with a meter-cubed unit, return
                // meter
                type.replaceAnnotation(m);
            } else if (UnitsRelationsTools.hasSpecificUnit(arg, mm3)) {
                // if cbrt was called with a millimeter-cubed unit, return
                // millimeter
                type.replaceAnnotation(mm);
            } else if (UnitsRelationsTools.hasSpecificUnit(arg, km3)) {
                // if cbrt was called with a kilometer-cubed unit, return
                // kilometer
                type.replaceAnnotation(km);
            }
            return null;
        }

        /**
         * If the time instant is based on the same unit as the duration, then
         * it will return the time instant unit
         *
         * @param type the result unit
         * @param instant the time instant unit
         * @param duration the time duration unit
         * @return type will be replaced with the time instant unit, or
         *         {@link TimeInstant} if the above criterion is not satisfied.
         */
        private Void processInstantAndDurationMathOperation(AnnotatedTypeMirror type, AnnotatedTypeMirror instant, AnnotatedTypeMirror duration) {
            if (UnitsRelationsTools.hasSameUnits(getTimeDurationUnit(instant), UnitsRelationsTools.getUnit(duration))) {
                // If the instant is based upon the same unit as the duration,
                // then Time instant + or - time duration => time instant
                type.replaceAnnotations(instant.getAnnotations());
            } else {
                // if the instant isn't based upon the same unit however, then
                // return @TimeInstant
                type.replaceAnnotation(timeInstant);
            }
            return null;
        }

        private Void processMathOperation(ExpressionTree node, Tree.Kind kind, AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
            // Remove Prefix.one
            if (UnitsRelationsTools.getPrefix(lht) == Prefix.one) {
                lht = UnitsRelationsTools.removePrefix(elements, lht);
            }
            if (UnitsRelationsTools.getPrefix(rht) == Prefix.one) {
                rht = UnitsRelationsTools.removePrefix(elements, rht);
            }

            // First use units relations to resolve the operation
            AnnotationMirror bestResult = null;
            for (UnitsRelations ur : getUnitsRelationsMap().values()) {
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
                case PLUS:
                    // plus is treated the same as minus
                case MINUS:
                    // Process time units first
                    if (isTimeInstant(lht) && isTimeInstant(rht)) {
                        // Time instant + time instant ==> error
                        // One cannot add the 4th month of a year to the 7th month of some other year
                        checker.report(Result.failure("time.instant.addition.disallowed", lht.toString(), rht.toString()), node);
                    } else if(isTimeInstant(lht) && isTimeDuration(rht)) {
                        // instant +/- duration => instant
                        processInstantAndDurationMathOperation(resultType, lht, rht);
                    } else if(isTimeDuration(lht) && isTimeInstant(rht)) {
                        // duration +/- instant => instant
                        processInstantAndDurationMathOperation(resultType, rht, lht);
                    } else if (UnitsRelationsTools.hasSameUnits(lht, rht)) {
                        // If both operands for sum or difference have the same
                        // units, we return the unit
                        // this includes duration +/- duration => duration
                        resultType.replaceAnnotations(lht.getAnnotations());
                    } else {
                        // otherwise we return in the LUB of the left and right
                        // types
                        // this is more flexible: e.g. km + meter ==> length
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
                        // return unknown
                        // Future TODO: track partial units (unit ^ 2), so that
                        // equations like unit * unit / unit == unit work
                        // also unit * unknown = unknown
                        resultType.replaceAnnotation(TOP);
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
                        // scalar divided by any unit returns unknown
                        // Future TODO: track partial units (scalar / unit), so
                        // that equations like scalar / unit * unit == scalar
                        // work
                        // also scalar / unknown = unknown
                        resultType.replaceAnnotation(TOP);
                    } else {
                        // else it is a division of two units that have no
                        // defined relations from a relations class
                        // return unknown
                        // also unit / unknown = unknown, and unknown / unit =
                        // unknown
                        resultType.replaceAnnotation(TOP);
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

            // skip checking addition on Strings
            if (kind == Tree.Kind.PLUS) {
                // replace with the types of the string
                if (isSameUnderlyingType(lht.getUnderlyingType(), stringType)) {
                    type.replaceAnnotations(lht.getAnnotations());
                    return null;
                } else if (isSameUnderlyingType(rht.getUnderlyingType(), stringType)) {
                    type.replaceAnnotations(rht.getAnnotations());
                    return null;
                } else if (isSameUnderlyingType(lht.getUnderlyingType(), stringType) && isSameUnderlyingType(rht.getUnderlyingType(), stringType)){
                    // strings are always scalar by union
                    // TODO check variable declaration??
                    type.replaceAnnotation(scalar);
                    return null;
                }
                // if neither operands are strings, continue the checks
            }

            // see if it is a comparison operation
            switch (kind) {
            case EQUAL_TO:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
                // if it is a comparison operation, check to see if it has the
                // same units for both operands
                if (UnitsRelationsTools.hasSameUnits(lht, rht)) {
                    // if both operands of a comparison have the same unit, then
                    // the result boolean value has a unit of Scalar
                    type.replaceAnnotation(scalar);
                } else if (UnitsRelationsTools.hasSpecificUnit(lht, scalar) || UnitsRelationsTools.hasSpecificUnit(rht, scalar)) {
                    // also allow comparison of any unit to a Scalar
                    type.replaceAnnotation(scalar);
                } else if (UnitsRelationsTools.hasSpecificUnit(lht, BOTTOM) || UnitsRelationsTools.hasSpecificUnit(rht, BOTTOM)) {
                    // if either of the operand is the Bottom type (commonly for
                    // null reference comparisons) then return Scalar as well
                    type.replaceAnnotation(scalar);
                } else {
                    // if the operands have different units, then alert error
                    checker.report(Result.failure("operands.unit.mismatch", lht.toString(), rht.toString()), node);
                }
                break;
            default:
                // if it isn't a comparison operation, then check and process
                // arithmetic operations
                processMathOperation(node, kind, type, lht, rht);
            }

            return null;
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree node, AnnotatedTypeMirror type) {
            ExpressionTree var = node.getVariable();
            AnnotatedTypeMirror varType = atypeFactory.getAnnotatedType(var);

            // by default the type of the compound assign expression is the same as the variable being modified
            type.replaceAnnotations(varType.getAnnotations());

            // checks are done in UnitsVisitor, as only non-primitive types are checked in ATF
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

    // =========================================================
    // Qualifier Hierarchy
    // =========================================================

    /**
     * Set the Bottom qualifier as the bottom of the hierarchy.
     */
    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new UnitsQualifierHierarchy(factory, BOTTOM);
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
}
