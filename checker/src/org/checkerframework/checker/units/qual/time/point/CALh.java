package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.h;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar hour.
 *
 * This unit is used to denote a time point in hours, such as 5am.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = h.class)
public @interface CALh {}
