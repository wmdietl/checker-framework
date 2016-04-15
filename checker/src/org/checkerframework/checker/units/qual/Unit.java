package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * A Unit defined by the normalized unit parameters of this annotation.
 *
 * The unit parameters are normalized according to the following rules:
 * 1) negative powers are converted into their reciprocals, only positive powers are allowed
 * 2) units are grouped into a single numerator and a single denominator
 * 3) units are alphabetically sorted in the numerator and denominator
 *
 * E.g. Unit(kg m / s^2) is a unit of force, Unit(m / s) is a unit of speed
 *
 * It is possible to define units based upon each other with this annotation
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@SubtypeOf(UnknownUnits.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Unit {
    Class<? extends Annotation>[] numeratorUnits() default { Scalar.class };
    double[] numeratorPrefixValues() default { 1.0d };
    Class<? extends Annotation>[] denominatorUnits() default { Scalar.class };
    double[] denominatorPrefixValues() default { 1.0d };
}

/*
 * rules:
 *
 * plus or minus has to match the unit exactly
 *
 * first normalize a unit that is expected to be multiplied or divided into the current unit
 *      eg  * s^-1 ==> / s
 *      eg  / s^-2 ==> * s^2 => * s * s
 *      -> convert negative exponents w.r.t the operation
 *      -> expand powers
 *
 * multiply by a unit:
 *      if unit is in denominator and prefix matches, pop off denominator
 *      else add to numerator
 *
 *      eg:   (1 / kg) * g  == (g / kg), as we do not perform implicit unit conversions and thus affect the magnitude of the calculation
 *            (1 / kg) * kg == scalar
 *
 * divide by a unit:
 *      if unit is in numerator and prefix matches, pop off numerator
 *      else add to denominator
 *
 * add to numerator or denominator:
 *      alphabetically sort the units
 *      in-order insertion
 *
 */

