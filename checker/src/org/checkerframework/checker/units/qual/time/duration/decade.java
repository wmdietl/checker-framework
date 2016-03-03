package org.checkerframework.checker.units.qual.time.duration;

import java.lang.annotation.*;

import org.checkerframework.framework.qual.*;

/**
 * Decade (10 Gregorian Years).
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeDuration.class)
@TimeMultiple(timeUnit = s.class, multiplier = 315569520L)
public @interface decade {}
