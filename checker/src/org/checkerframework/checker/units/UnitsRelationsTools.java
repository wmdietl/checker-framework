package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
 */

/**
 * A helper class for UnitsRelations, providing numerous methods which help
 * process Annotations and Annotated Types representing various units
 */
public class UnitsRelationsTools {

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
        if (env == null || annoClass == null) {
            return null;
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
        if (env == null || annoClass == null || p == null) {
            return null;
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
        if (env == null || annoClass == null) {
            return null;
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
            return null;
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
        AnnotationValue annotationValue = getAnnotationMirrorPrefix(unitsAnnotation);

        // if this Annotation has no prefix, return null
        if (hasNoPrefix(annotationValue)) {
            return null;
        }

        // if the Annotation has a value, then detect and match the string name
        // of the prefix, and return the matching Prefix
        String prefixString = annotationValue.getValue().toString();
        for (Prefix prefix : Prefix.values()) {
            if (prefixString.equals(prefix.toString())) {
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
            return true;
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
        AnnotationValue annotationValue = getAnnotationMirrorPrefix(unitsAnnotation);
        return hasNoPrefix(annotationValue);
    }

    private static boolean hasNoPrefix(/*@Nullable*/ final AnnotationValue annotationValue) {
        // Annotation has no element value (ie no SI prefix)
        if (annotationValue == null) {
            return true;
        } else {
            return false;
        }
    }

    // given an Annotation, returns the prefix (eg kilo) as an AnnotationValue
    // if there is any, otherwise returns null
    private static /*@Nullable*/ AnnotationValue getAnnotationMirrorPrefix(/*@Nullable*/ final AnnotationMirror unitsAnnotation) {
        if (unitsAnnotation == null) {
            return null;
        }

        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = unitsAnnotation.getElementValues();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                return entry.getValue();
            }
        }

        return null;
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
        if (elements == null || unitsAnnotation == null) {
            return null;
        }

        if (hasNoPrefix(unitsAnnotation)) {
            return unitsAnnotation;
        } else {
            // the only value is the prefix value in Units Checker
            // TODO: refine sensitivity of removal for extension units, in case
            // extension Annotations have more than just Prefix in its values.
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
        if (elements == null || annoType == null) {
            return null;
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
            return false;
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
        if (annoType == null || unitsAnnotation == null) {
            return false;
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
        if (annoType == null || unitsAnnotation == null) {
            return false;
        }

        return AnnotationUtils.containsSameIgnoringValues(annoType.getEffectiveAnnotations(), unitsAnnotation);
    }

    /**
     * Checks to see if the two Annotated Types passed in have the exact same
     * units
     *
     * @param t1 first annotated type
     * @param t2 second annotated type
     * @return true if they have the same unit, false otherwise
     */
    public static boolean hasSameUnits(/*@Nullable*/ final AnnotatedTypeMirror t1, /*@Nullable*/ final AnnotatedTypeMirror t2) {
        if (t1 == null || t2 == null) {
            return false;
        }

        return AnnotationUtils.areSame(t1.getEffectiveAnnotations(), t2.getEffectiveAnnotations());
    }

    /**
     * Checks to see if the two Annotated Types passed in have the exact same
     * base units
     *
     * @param t1 first annotated type
     * @param t2 second annotated type
     * @return true if they have the same unit, false otherwise
     */
    public static boolean hasSameUnitsIgnoringPrefix(/*@Nullable*/ final AnnotatedTypeMirror t1, /*@Nullable*/ final AnnotatedTypeMirror t2) {
        if (t1 == null || t2 == null) {
            return false;
        }

        Collection<? extends AnnotationMirror> c1 = t1.getEffectiveAnnotations();
        Collection<? extends AnnotationMirror> c2 = t2.getEffectiveAnnotations();

        if (c1.size() != c2.size())
            return false;
        if (c1.size() == 1)
            return hasSameUnitsIgnoringPrefix(c1.iterator().next(), c2.iterator().next());

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
            if (!hasSameUnitsIgnoringPrefix(anno1, anno2))
                return false;
        }
        return true;
    }

    /**
     * Checks to see if the two annotation mirrors passed in have the exact same
     * units
     *
     * @param m1 first annotation mirror
     * @param m2 second annotation mirror
     * @return true if they have the same unit, false otherwise
     */
    public static boolean hasSameUnits(/*@Nullable*/ final AnnotationMirror m1, /*@Nullable*/ final AnnotationMirror m2) {
        if (m1 == null || m2 == null) {
            return false;
        }

        return AnnotationUtils.areSame(m1, m2);
    }

    /**
     * Checks to see if the two annotation mirrors passed in have the exact same
     * base units
     *
     * @param m1 first annotation mirror
     * @param m2 second annotation mirror
     * @return true if they have the same unit, false otherwise
     */
    public static boolean hasSameUnitsIgnoringPrefix(/*@Nullable*/ final AnnotationMirror m1, /*@Nullable*/ final AnnotationMirror m2) {
        if (m1 == null || m2 == null) {
            return false;
        }

        return AnnotationUtils.areSameIgnoringValues(m1, m2);
    }
}
