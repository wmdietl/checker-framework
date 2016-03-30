package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.halfday;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar half-day of either AM or PM, the integer which stores this unit only
 * has two values (0 for AM and 1 for PM).
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = halfday.class)
public @interface CALhalfday {}
