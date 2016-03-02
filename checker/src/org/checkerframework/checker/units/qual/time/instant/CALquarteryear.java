package org.checkerframework.checker.units.qual.time.instant;

import java.lang.annotation.*;

import org.checkerframework.checker.units.qual.time.duration.quarteryear;
import org.checkerframework.checker.units.qual.time.duration.year;
import org.checkerframework.framework.qual.*;

/**
 * A Calendar Quarter of a Year. The variables with this unit has its values
 * bounded between 1 to 4 by the Java 8 Time API.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeInstant.class)
@DurationUnit(unit = quarteryear.class)
public @interface CALquarteryear {}
