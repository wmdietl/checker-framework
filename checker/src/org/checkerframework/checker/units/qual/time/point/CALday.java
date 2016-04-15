package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.day;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar day.
 *
 * This unit is used to denote a time point in days, such as the 15th day of
 * some month.
 *
 * The variables with this unit has its values bounded between 1 to 7 depending
 * on the week, or 1 to 31 depending on the month or 1 to 366 depending on the
 * year, by the Java 8 Time API.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = day.class)
public @interface CALday {}
