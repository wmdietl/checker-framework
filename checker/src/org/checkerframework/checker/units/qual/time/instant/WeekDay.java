package org.checkerframework.checker.units.qual.time.instant;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Weekday (monday to friday, and unknown).
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(TimeInstant.class)
// TODO: monday - sunday = 1 day (time)
public @interface WeekDay {
    WeekdaysEnum weekday() default WeekdaysEnum.unknown;
}
