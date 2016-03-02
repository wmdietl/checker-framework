package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultInUncheckedCodeFor;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UnitsBottom is the bottom type of the type hierarchy.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@SubtypeOf({}) // programmatically assigned as the bottom qualifier of every units qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@DefaultFor(TypeUseLocation.LOWER_BOUND)
@DefaultInUncheckedCodeFor(TypeUseLocation.LOWER_BOUND)
@ImplicitFor(typeNames = Void.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface UnitsBottom {}
