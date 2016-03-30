package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.min;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar minute.
 *
 * This unit is used to denote a time point in minutes, such as the minute
 * of the current hour.
 *
 * The variables with this unit has its values bound between 0-59 or
 * 1-60.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = min.class)
public @interface CALmin {}
