package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.checker.units.qual.time.duration.day;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Calendar Day. The variables with this unit has its values bounded between 1
 * to 31 depending on the month or 1 to 366 depending on the year, by the Java 8
 * Time API.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeInstant.class)
@DurationUnit(unit = day.class)
public @interface CALday {}
