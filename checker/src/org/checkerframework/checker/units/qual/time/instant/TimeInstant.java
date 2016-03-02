package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.checker.units.qual.UnknownUnits;
import org.checkerframework.checker.units.qual.time.duration.TimeDuration;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Units of time instants. A time instant is the precise time at which an event
 * occurs, where as a time duration {@link TimeDuration} represents the duration
 * of a single event or the interval between two events. Subtypes of this type
 * represent units of specific time instants, such as calendar years, calendar
 * day, calendar hour, etc.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
// TODO: intention: subtraction of two time instants yields a time difference,
// which is in a unit of time (seconds, year, etc)
@SubtypeOf(UnknownUnits.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface TimeInstant {}
