package org.checkerframework.checker.units.qual.time.duration;

import org.checkerframework.checker.units.qual.time.UnknownTime;
import org.checkerframework.checker.units.qual.time.point.TimePoint;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Units of time duration. A time duration represents the duration of a single
 * event or the interval between two events, or more formally a length in a time
 * scale between two time points.
 *
 * Subtypes of this type represent units of specific time durations, such as
 * hours, mins, seconds, etc.
 *
 * Also see {@link TimePoint}
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@SubtypeOf(UnknownTime.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface TimeDuration {}
