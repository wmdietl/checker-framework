package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.quarteryear;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar quarter-year.
 *
 * This unit is used to denote a time point in quarter-year, such as the 4
 * different seasons of the year.
 *
 * The variables with this unit has its values bounded between 1 to 4 by the
 * Java 8 Time API.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = quarteryear.class)
public @interface CALquarteryear {}
