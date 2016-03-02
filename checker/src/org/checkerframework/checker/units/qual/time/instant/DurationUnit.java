package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.checker.units.qual.UnknownUnits;

import java.lang.annotation.Annotation;

/**
 * Defines the relation between a time instant unit and it's corresponding time duration unit.
 *
 * E.g. two calendar years (time instant) are separated by a year (time duration).
 */
public @interface DurationUnit {
    /**
     * @return The base time unit.
     */
    Class<? extends Annotation> unit() default UnknownUnits.class;
}
