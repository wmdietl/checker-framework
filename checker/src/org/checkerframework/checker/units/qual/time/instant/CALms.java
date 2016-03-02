package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.checker.units.qual.time.duration.ms;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Calendar millisecond.
 *
 * This unit is used to denote a time instant in milliseconds, such as the
 * amount of milliseconds since Java Epoch, or the amount of milliseconds of the
 * current second of some particular time.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeInstant.class)
@DurationUnit(unit = ms.class)
public @interface CALms {}
