package org.checkerframework.checker.units.qual.time.duration;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Century (100 Gregorian Years).
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeDuration.class)
@TimeMultiple(timeUnit = s.class, multiplier = 3155695200L)
public @interface century {}
