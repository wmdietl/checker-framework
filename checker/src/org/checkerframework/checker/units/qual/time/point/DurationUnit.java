package org.checkerframework.checker.units.qual.time.point;

import java.lang.annotation.*;

/**
 * Defines the relation between a time point unit and it's corresponding time
 * duration unit.
 *
 * This meta-annotation is mandatory on all time point units, and must map to
 * a corresponding time duration unit.
 *
 * E.g. two calendar years (time point) are separated by a number of years
 * (time duration).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationUnit {
    /**
     * @return The base time unit.
     */
    Class<? extends Annotation> unit();
}
