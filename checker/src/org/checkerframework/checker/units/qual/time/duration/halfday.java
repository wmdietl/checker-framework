package org.checkerframework.checker.units.qual.time.duration;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

/**
 * Half-Day (12 hours).
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeDuration.class)
@TimeMultiple(timeUnit = s.class, multiplier = 43200L)
public @interface halfday {}
