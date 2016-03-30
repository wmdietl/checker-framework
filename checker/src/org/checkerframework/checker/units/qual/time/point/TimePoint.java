package org.checkerframework.checker.units.qual.time.point;

import org.checkerframework.checker.units.qual.time.UnknownTime;
import org.checkerframework.checker.units.qual.time.duration.TimeDuration;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Units of time points. A time point is the precise time at which an event
 * occurs, or more formally a point in a time scale.
 *
 * Subtypes of this type represent units of specific time points, such as
 * calendar years, calendar day, calendar hour, etc.
 *
 * Subtraction of two time points yields a time duration, which is in a unit of
 * time (seconds, year, etc). Addition of two time points results in error.
 * Adding a time duration to a time point yields another time point.
 * Conceptually this is equivalent to "5 am + 5 hours = 10 am".
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@SubtypeOf(UnknownTime.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@DurationUnit(unit = TimeDuration.class)
public @interface TimePoint {}
