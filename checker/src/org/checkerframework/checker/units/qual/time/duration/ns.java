package org.checkerframework.checker.units.qual.time.duration;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.UnitsMultiple;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Nanosecond.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeDuration.class)
@UnitsMultiple(quantity = s.class, prefix = Prefix.nano)
@TimeMultiple(timeUnit = ns.class, multiplier = 1L)
public @interface ns {}
