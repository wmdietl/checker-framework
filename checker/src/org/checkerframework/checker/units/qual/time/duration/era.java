package org.checkerframework.checker.units.qual.time.duration;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Era (1 million Gregorian years). In Java 8, it is impossible to add an era
 * to a date or a date-time, as it is not defined in the ISO calendar system.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeDuration.class)
@TimeMultiple(timeUnit = s.class, multiplier = 31556952L * 1000000000L)
public @interface era {}
