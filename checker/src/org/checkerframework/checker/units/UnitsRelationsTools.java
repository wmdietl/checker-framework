package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.time.point.DurationUnit;
import org.checkerframework.checker.units.qual.time.point.TimePoint;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.ErrorReporter;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;

import com.sun.source.tree.*;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.util.List;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
 */

/**
 * A helper class for UnitsRelations, providing numerous methods which help
 * process Annotations and Annotated Types representing various units
 */
public class UnitsRelationsTools {
    // Used to detect annotations that are subtypes of timeInstant
    private static String timeInstantClassName = TimePoint.class.getCanonicalName().intern();

    /**
     * Creates an AnnotationMirror representing a unit defined by annoClass,
     * with the default Prefix of Prefix.one
     *
     * @param env Checker Processing Environment, provided as a parameter in
     *            init() of a UnitsRelations implementation
     * @param annoClass The Class of an Annotation representing a Unit (eg
     *            m.class for meters)
     * @return An AnnotationMirror of the Unit with Prefix.one, or null if it
     *         cannot be constructed
     */
    public static /*@Nullable*/ AnnotationMirror buildAnnoMirrorWithDefaultPrefix(/*@Nullable*/ final ProcessingEnvironment env, /*@Nullable*/ final Class<? extends Annotation> annoClass) {
        if (env == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithDefaultPrefix(ProcessingEnvironment, Class<? extends Annotation>) must be" +
                    " called by passing in a reference to the Checker" +
                    " Processing Environment, provided as a parameter in" +
                    " init() of a UnitsRelations implementation");
        }
        if (annoClass == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithDefaultPrefix(ProcessingEnvironment, Class<? extends Annotation>) must be" +
                    " called with the class of an Annotation representing a Unit (eg m.class for meters)");
        }

        return buildAnnoMirrorWithSpecificPrefix(env, annoClass, Prefix.one);
    }

    /**
     * Creates an AnnotationMirror representing a unit defined by annoClass,
     * with the specific Prefix p
     *
     * @param env Checker Processing Environment, provided as a parameter in
     *            init() of a UnitsRelations implementation
     * @param annoClass The Class of an Annotation representing a Unit (eg
     *            m.class for meters)
     * @param p A Prefix value
     * @return An AnnotationMirror of the Unit with the Prefix p, or null if it
     *         cannot be constructed
     */
    public static /*@Nullable*/ AnnotationMirror buildAnnoMirrorWithSpecificPrefix(/*@Nullable*/ final ProcessingEnvironment env, /*@Nullable*/ final Class<? extends Annotation> annoClass, /*@Nullable*/ final Prefix p) {
        if (env == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(ProcessingEnvironment, Class<? extends Annotation>, Prefix) must be" +
                    " called by passing in a reference to the Checker" +
                    " Processing Environment, provided as a parameter in" +
                    " init() of a UnitsRelations implementation");
        }
        if (annoClass == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(ProcessingEnvironment, Class<? extends Annotation>, Prefix) must be" +
                    " called with the class of an Annotation representing a Unit (eg m.class for meters)");
        }
        if (p == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(ProcessingEnvironment, Class<? extends Annotation>, Prefix) must be" +
                    " called with a Prefix");
        }

        AnnotationBuilder builder = new AnnotationBuilder(env, annoClass);
        builder.setValue("value", p);
        return builder.build();
    }

    /**
     * Creates an AnnotationMirror representing a unit defined by annoClass,
     * with no prefix
     *
     * @param env Checker Processing Environment, provided as a parameter in
     *            init() of a UnitsRelations implementation
     * @param annoClass The Class of an Annotation representing a Unit (eg
     *            m.class for meters)
     * @return An AnnotationMirror of the Unit with no prefix, or null if it
     *         cannot be constructed
     */
    public static /*@Nullable*/ AnnotationMirror buildAnnoMirrorWithNoPrefix(/*@Nullable*/ final ProcessingEnvironment env, /*@Nullable*/ final Class<? extends Annotation> annoClass) {
        if (env == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(ProcessingEnvironment, Class<? extends Annotation>) must be" +
                    " called by passing in a reference to the Checker" +
                    " Processing Environment, provided as a parameter in" +
                    " init() of a UnitsRelations implementation");
        }
        if (annoClass == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(ProcessingEnvironment, Class<? extends Annotation>) must be" +
                    " called with the class of an Annotation representing a Unit (eg m.class for meters)");
        }

        return AnnotationUtils.fromClass(env.getElementUtils(), annoClass);
    }

    /**
     * Retrieves the SI Prefix of an Annotated Type
     *
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @return A Prefix value (including Prefix.one), or null if it has none
     */
    public static /*@Nullable*/ Prefix getPrefix(/*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getPrefix(AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }

        Prefix result = null;

        // go through each Annotation of an Annotated Type, find the prefix and
        // return it
        for (AnnotationMirror mirror : annoType.getEffectiveAnnotations()) {
            // try to get a prefix
            result = getPrefix(mirror);
            // if it is not null, then return the retrieved prefix immediately
            if (result != null) {
                return result;
            }
        }

        // if it can't find any prefix at all, then return null
        return result;
    }

    /**
     * Retrieves the SI Prefix of an Annotation
     *
     * @param unitsAnnotation An AnnotationMirror representing a Units
     *            Annotation
     * @return A Prefix value (including Prefix.one), or null if it has none
     */
    public static /*@Nullable*/ Prefix getPrefix(/*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (unitsAnnotation == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getPrefix(AnnotationMirror) must be" +
                    " called with an AnnotationMirror representing a Units Annotation");
        }

        AnnotationValue annotationValue = getAnnotationMirrorPrefix(unitsAnnotation);

        // if this Annotation has no prefix, return null
        if (hasNoPrefix(annotationValue)) {
            return null;
        }

        // if the Annotation has a value, then detect and match the string name
        // of the prefix, and return the matching Prefix
        String prefixString = annotationValue.getValue().toString().intern();
        for (Prefix prefix : Prefix.values()) {
            if (prefixString == prefix.toString().intern()) {
                return prefix;
            }
        }

        // if none of the strings match, then return null
        return null;
    }

    /**
     * Checks to see if an Annotated Type has no prefix
     *
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @return true if it has no prefix, false otherwise
     */
    public static boolean hasNoPrefix(/*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasNoPrefix(AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }

        for (AnnotationMirror mirror : annoType.getEffectiveAnnotations()) {
            // if any Annotation has a prefix, return false
            if (!hasNoPrefix(mirror)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks to see if an Annotation has no prefix
     *
     * @param unitsAnnotation An AnnotationMirror representing a Units
     *            Annotation
     * @return true if it has no prefix, false otherwise
     */
    public static boolean hasNoPrefix(/*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (unitsAnnotation == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasNoPrefix(AnnotationMirror) must be" +
                    " called with an AnnotationMirror representing a Units Annotation");
        }

        AnnotationValue annotationValue = getAnnotationMirrorPrefix(unitsAnnotation);
        return hasNoPrefix(annotationValue);
    }

    // given an Annotation, returns the prefix (eg kilo) as an AnnotationValue
    // if there is any, otherwise returns null
    private static /*@Nullable*/ AnnotationValue getAnnotationMirrorPrefix(final AnnotationMirror unitsAnnotation) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = unitsAnnotation.getElementValues();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            if (entry.getKey().getSimpleName().toString().intern() == "value".intern()) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static boolean hasNoPrefix(/*@Nullable*/ final AnnotationValue annotationValue) {
        // Annotation has no element value (ie no SI prefix)
        if (annotationValue == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Given an Annotated Type, this method returns the Annotation representing
     * the unit that this Annotated Type has.
     *
     * @param annoType an Annotated Type
     * @return an AnnotationMirror representing the unit that this Annotated
     *         Type has
     */
    public static /*@Nullable*/ AnnotationMirror getUnit(/*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getUnit(AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }

        // return the first unit (should always be the only unit) in the
        // annotated type
        Iterator<AnnotationMirror> it = annoType.getEffectiveAnnotations().iterator();
        if (it.hasNext()) {
            return annoType.getEffectiveAnnotations().iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Removes the Prefix value from an Annotation, by constructing and
     * returning a copy of its base SI unit's Annotation
     *
     * @param elements Element Utilities from a checker's processing
     *            environment, typically obtained by calling
     *            env.getElementUtils() in init() of a Units Relations
     *            implementation
     * @param unitsAnnotation An AnnotationMirror representing a Units
     *            Annotation
     * @return The base SI Unit's AnnotationMirror, or null if the base SI Unit
     *         cannot be constructed
     */
    public static /*@Nullable*/ AnnotationMirror removePrefix(/*@Nullable*/ final Elements elements, /*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (elements == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.removePrefix(Elements, AnnotationMirror) must be" +
                    " called by passing in a reference to the Element Utilities," +
                    " typically obtained by calling env.getElementUtils() in" +
                    " init() of a Units Relations implementation");
        }
        if (unitsAnnotation == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.removePrefix(Elements, AnnotationMirror) must be" +
                    " called with an AnnotationMirror representing a Units Annotation");
        }

        if (hasNoPrefix(unitsAnnotation)) {
            return unitsAnnotation;
        } else {
            // the only value is the prefix value in Units Checker
            // Future TODO: in case extension Annotations have more than just Prefix in
            // its values, remove only the Prefix. Currently this removes all values
            return AnnotationUtils.fromName(elements, unitsAnnotation.getAnnotationType().toString());
        }
    }

    /**
     * Removes the Prefix value from an Annotated Type, by constructing and
     * returning a copy of the Annotated Type without the prefix
     *
     * @param elements Element Utilities from a checker's processing
     *            environment, typically obtained by calling
     *            env.getElementUtils() in init() of a Units Relations
     *            implementation
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @return A copy of the Annotated Type without the prefix, or null if it
     *         cannot be copied
     */
    public static AnnotatedTypeMirror removePrefix(/*@Nullable*/ final Elements elements, /*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (elements == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.removePrefix(Elements, AnnotatedTypeMirror) must be" +
                    " called by passing in a reference to the Element Utilities," +
                    " typically obtained by calling env.getElementUtils() in" +
                    " init() of a Units Relations implementation");
        }
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.removePrefix(Elements, AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }

        // deep copy the Annotated Type Mirror without any of the Annotations
        AnnotatedTypeMirror result = annoType.deepCopy(false);

        // get all of the original Annotations in the Annotated Type
        Set<AnnotationMirror> annos = annoType.getEffectiveAnnotations();

        // loop through all the Annotations to see if they use Prefix.one,
        // remove Prefix.one if it does
        for (AnnotationMirror anno : annos) {
            // try to clean the Annotation Mirror of the Prefix
            AnnotationMirror cleanedMirror = removePrefix(elements, anno);
            // if successful, add the cleaned annotation to the deep copy
            if (cleanedMirror != null) {
                result.addAnnotation(cleanedMirror);
            }
            // if unsuccessful, add the original annotation
            else {
                result.addAnnotation(anno);
            }
        }

        return result;
    }

    /**
     * Checks to see if a particular Annotated Type has no units, such as scalar
     * constants in calculations.
     *
     * Any number that isn't assigned a unit will automatically get the
     * Annotation UnknownUnits. eg: int x = 5; //x has @UnknownUnits
     *
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @return true if the Type has no units, false otherwise
     */
    public static boolean hasNoUnits(/*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasNoUnits(AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }

        return (annoType.getAnnotation(Scalar.class) != null);
    }

    /**
     * Checks to see if a particular Annotated Type has a specific unit
     * (represented by its Annotation)
     *
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @param unitsAnnotation An AnnotationMirror representing a Units
     *            Annotation of a specific unit
     * @return true if the Type has the specific unit, false otherwise
     */
    public static boolean hasSpecificUnit(/*@Nullable*/ final AnnotatedTypeMirror annoType, /*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasSpecificUnit(AnnotatedTypeMirror, AnnotationMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }
        if (unitsAnnotation == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasSpecificUnit(AnnotatedTypeMirror, AnnotationMirror) must be" +
                    " called with an AnnotationMirror representing a Units Annotation");
        }

        return AnnotationUtils.containsSame(annoType.getEffectiveAnnotations(), unitsAnnotation);
    }

    /**
     * Checks to see if a particular Annotated Type has a particular base unit
     * (represented by its Annotation)
     *
     * @param annoType An AnnotatedTypeMirror representing a Units Annotated
     *            Type
     * @param unitsAnnotation An AnnotationMirror representing a Units
     *            Annotation of the base unit
     * @return true if the Type has the specific unit, false otherwise
     */
    public static boolean hasSpecificUnitIgnoringPrefix(/*@Nullable*/ final AnnotatedTypeMirror annoType, /*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasSpecificUnitIgnoringPrefix(AnnotatedTypeMirror, AnnotationMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type");
        }
        if (unitsAnnotation == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.hasSpecificUnitIgnoringPrefix(AnnotatedTypeMirror, AnnotationMirror) must be" +
                    " called with an AnnotationMirror representing a Units Annotation of the base unit");
        }

        return AnnotationUtils.containsSameIgnoringValues(annoType.getEffectiveAnnotations(), unitsAnnotation);
    }

    /**
     * Checks to see if the two Annotated Types passed in are the exact same
     * units
     *
     * @param t1 first annotated type
     * @param t2 second annotated type
     * @return true if they have the same unit, false otherwise
     */
    public static boolean areSameUnits(/*@Nullable*/ final AnnotatedTypeMirror t1, /*@Nullable*/ final AnnotatedTypeMirror t2) {
        if (t1 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnits(AnnotatedTypeMirror t1, AnnotatedTypeMirror t2) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type at t1");
        }
        if (t2 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnits(AnnotatedTypeMirror t1, AnnotatedTypeMirror t2) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type at t2");
        }

        return AnnotationUtils.areSame(t1.getEffectiveAnnotations(), t2.getEffectiveAnnotations());
    }

    /**
     * Checks to see if the two Annotated Types passed in are the exact same
     * base units
     *
     * @param t1 first annotated type
     * @param t2 second annotated type
     * @return true if they have the same unit, false otherwise
     */
    public static boolean areSameUnitsIgnoringPrefix(/*@Nullable*/ final AnnotatedTypeMirror t1, /*@Nullable*/ final AnnotatedTypeMirror t2) {
        if (t1 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnitsIgnoringPrefix(AnnotatedTypeMirror t1, AnnotatedTypeMirror t2) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type at t1");
        }
        if (t2 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnitsIgnoringPrefix(AnnotatedTypeMirror t1, AnnotatedTypeMirror t2) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type at t2");
        }

        Collection<? extends AnnotationMirror> c1 = t1.getEffectiveAnnotations();
        Collection<? extends AnnotationMirror> c2 = t2.getEffectiveAnnotations();

        if (c1.size() != c2.size())
            return false;
        if (c1.size() == 1)
            return areSameUnitsIgnoringPrefix(c1.iterator().next(), c2.iterator().next());

        Set<AnnotationMirror> s1 = AnnotationUtils.createAnnotationSet();
        Set<AnnotationMirror> s2 = AnnotationUtils.createAnnotationSet();
        s1.addAll(c1);
        s2.addAll(c2);

        // depend on the fact that Set is an ordered set.
        Iterator<AnnotationMirror> iter1 = s1.iterator();
        Iterator<AnnotationMirror> iter2 = s2.iterator();

        while (iter1.hasNext()) {
            AnnotationMirror anno1 = iter1.next();
            AnnotationMirror anno2 = iter2.next();
            if (!areSameUnitsIgnoringPrefix(anno1, anno2))
                return false;
        }
        return true;
    }

    /**
     * Checks to see if the two annotation mirrors passed in are the exact same
     * units
     *
     * @param m1 first annotation mirror
     * @param m2 second annotation mirror
     * @return true if they have the same unit, false otherwise
     */
    public static boolean areSameUnits(/*@Nullable*/ final AnnotationMirror m1, /*@Nullable*/ final AnnotationMirror m2) {
        if (m1 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnits(AnnotationMirror m1, AnnotationMirror m2) must be" +
                    " called with an AnnotationMirror representing a Units Annotation of the base unit at m1");
        }
        if (m2 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnits(AnnotationMirror m1, AnnotationMirror m2) must be" +
                    " called with an AnnotationMirror representing a Units Annotation of the base unit at m2");
        }

        return AnnotationUtils.areSame(m1, m2);
    }

    /**
     * Checks to see if the two annotation mirrors passed in are the exact same
     * base units
     *
     * @param m1 first annotation mirror
     * @param m2 second annotation mirror
     * @return true if they have the same unit, false otherwise
     */
    public static boolean areSameUnitsIgnoringPrefix(/*@Nullable*/ final AnnotationMirror m1, /*@Nullable*/ final AnnotationMirror m2) {
        if (m1 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnitsIgnoringPrefix(AnnotationMirror m1, AnnotationMirror m2) must be" +
                    " called with an AnnotationMirror representing a Units Annotation of the base unit at m1");
        }
        if (m2 == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.areSameUnitsIgnoringPrefix(AnnotationMirror m1, AnnotationMirror m2) must be" +
                    " called with an AnnotationMirror representing a Units Annotation of the base unit at m2");
        }

        return AnnotationUtils.areSameIgnoringValues(m1, m2);
    }

    /**
     * Given an Annotated Type with a time point unit, this method returns the
     * Annotation representing the time duration unit that this Annotated Type
     * is related to via {@link DurationUnit}. For any other Annotated Type,
     * this method returns null.
     *
     * @param env Checker Processing Environment, provided as a parameter in
     *            init() of a UnitsRelations implementation
     * @param annoType an Annotated Type with a time point unit
     * @return an AnnotationMirror representing the time duration unit that this
     *         Annotated Type is related to, or null
     */
    public static /*@Nullable*/ AnnotationMirror getTimeDurationUnit(/*@Nullable*/ final ProcessingEnvironment env, /*@Nullable*/ final AnnotatedTypeMirror annoType) {
        if (env == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getTimeDurationUnit(ProcessingEnvironment, AnnotatedTypeMirror) must be" +
                    " called by passing in a reference to the Checker" +
                    " Processing Environment, provided as a parameter in" +
                    " init() of a UnitsRelations implementation");
        }
        if (annoType == null) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getTimeDurationUnit(ProcessingEnvironment, AnnotatedTypeMirror) must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type with a time point unit");
        }
        if (!isTimePoint(annoType)) {
            ErrorReporter.errorAbort("UnitsRelationsTools.getTimeDurationUnit(ProcessingEnvironment, AnnotatedTypeMirror) the" +
                    " AnnotatedTypeMirror passed in is not a time point unit, the method must be" +
                    " called with an AnnotatedTypeMirror representing a Units Annotated Type with a time point unit");
        }

        // get the time unit annotation
        AnnotationMirror timeUnit = UnitsRelationsTools.getUnit(annoType);
        // get the durationUnit meta annotation
        AnnotationMirror durationUnitAnno = getDurationUnitMetaAnnotation(timeUnit);

        if (durationUnitAnno != null) {
            // retrieve the Class of the duration unit
            Class<? extends Annotation> durationUnitAnnoClass = AnnotationUtils.getElementValueClass(durationUnitAnno, "unit", true).asSubclass(Annotation.class);
            return UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(env, durationUnitAnnoClass);
        }

        return null;
    }

    // checks to see if an entire mathematical expression consists of only
    // number literals
    public static boolean isPrimitiveNumberLiteralExpression(ExpressionTree tree) {
        // TODO: bitshift support?
        switch (tree.getKind()) {
        case TYPE_CAST:
            // any type casting of the number literals are ignored
            return isPrimitiveNumberLiteralExpression(((TypeCastTree) tree).getExpression());
        case PARENTHESIZED:
            // descend into parentheses
            return isPrimitiveNumberLiteralExpression(((ParenthesizedTree) tree).getExpression());
        case PLUS:
        case MINUS:
        case MULTIPLY:
        case DIVIDE:
        case REMAINDER:
            // for the 5 mathematical operations, return true if both operands
            // are mathematical sub-expression that consists of only number
            // literals
            BinaryTree bTree = (BinaryTree) tree;
            return isPrimitiveNumberLiteralExpression(bTree.getLeftOperand())
                    && isPrimitiveNumberLiteralExpression(bTree.getRightOperand());
        case INT_LITERAL:
        case LONG_LITERAL:
        case FLOAT_LITERAL:
        case DOUBLE_LITERAL:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks to see if the units annotation in the annotated type mirror is a
     * subtype of {@link TimePoint}
     *
     * Specifically, it checks to see if the {@link DurationUnit}
     * meta-annotation is present, and that it is a direct subtype of
     * {@link TimePoint} through the {@link SubtypeOf} meta-annotation. Every
     * time point unit must be defined with both of these meta-annotations.
     *
     * @param atm annotated type mirror with a units annotation
     * @return true if it is a subtype of {@link TimePoint}, false otherwise
     */
    private static boolean isTimePoint(final AnnotatedTypeMirror annotatedType) {
        // return true if the annotated type is @TimePoint
        if (annotatedType.getAnnotation(TimePoint.class) != null) {
            return true;
        }

        boolean hasDurationUnitMetaAnno = false;
        boolean isDirectSubtypeOfTimePoint = false;

        // loop through all of the annotations on the type
        for (AnnotationMirror mirror : annotatedType.getEffectiveAnnotations()) {
            // see if DurationUnit meta annotation is present
            if (getDurationUnitMetaAnnotation(mirror) != null) {
                hasDurationUnitMetaAnno = true;
            }

            // see if SubtypeOf meta annotation is present
            AnnotationMirror subtypeOfMetaAnno = getSubtypeOfMetaAnnotation(mirror);
            if (subtypeOfMetaAnno != null) {
                // if Subtypeof meta annotation is present, check to see if it's
                // value is TimePoint
                // obtain the classes declared in the SubtypeOf annotation
                @SuppressWarnings("unchecked")
                List<Attribute.Class> supertypes = AnnotationUtils.getElementValue(subtypeOfMetaAnno, "value", List.class, true);

                // loop through those classes, check to see that it's name
                // matches the TimePoint class's canonical name
                for(Attribute.Class supertype : supertypes) {
                    String subtypeAnnoValue = supertype.getValue().asElement().getQualifiedName().toString().intern();
                    if (subtypeAnnoValue == timeInstantClassName) {
                        isDirectSubtypeOfTimePoint = true;
                    }
                }
            }
        }

        // return true if it has the DurationUnit meta annotation and is a
        // direct subtype of TimePoint
        return hasDurationUnitMetaAnno && isDirectSubtypeOfTimePoint;
    }

    private static /*@Nullable*/ AnnotationMirror getDurationUnitMetaAnnotation(AnnotationMirror anno) {
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (AnnotationUtils.areSameByClass(metaAnno, DurationUnit.class)) {
                return metaAnno;
            }
        }
        return null;
    }

    private static /*@Nullable*/ AnnotationMirror getSubtypeOfMetaAnnotation(AnnotationMirror anno) {
        for (AnnotationMirror metaAnno : anno.getAnnotationType().asElement().getAnnotationMirrors()) {
            // see if the meta annotation is UnitsMultiple
            if (AnnotationUtils.areSameByClass(metaAnno, SubtypeOf.class)) {
                return metaAnno;
            }
        }
        return null;
    }

}
