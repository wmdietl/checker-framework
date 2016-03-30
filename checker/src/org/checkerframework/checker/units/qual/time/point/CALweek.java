package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.duration.week;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Calendar week.
 *
 * This unit is used to denote a time point in weeks, such as the week within
 * the current month or year.
 *
 * The variables with this unit has its values bounded between 1 to 5 depending
 * on the month or 1 to 52 depending on the year, by the Java 8 Time API.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimePoint.class)
@DurationUnit(unit = week.class)
public @interface CALweek {}
