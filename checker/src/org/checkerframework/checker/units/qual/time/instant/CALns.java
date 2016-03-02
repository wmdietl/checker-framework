package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.checker.units.qual.time.duration.ns;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Calendar nanosecond.
 *
 * This unit is used to denote a time instant in nanoseconds.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
@SubtypeOf(TimeInstant.class)
@DurationUnit(unit = ns.class)
public @interface CALns {}
