package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.*;

import java.lang.annotation.*;

/**
 * UnknownUnits is the top type of the type hierarchy.
 *
 * UnknownUnits is the default type for any un-annotated local variables,
 * resource variables, exceptions and exception parameters, and for the {@link java.lang.Throwable} class.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@InvisibleQualifier
@SubtypeOf({})
@DefaultFor({
    // Allows flow based type refinement in the body of methods
    TypeUseLocation.LOCAL_VARIABLE,
    TypeUseLocation.EXCEPTION_PARAMETER,
    TypeUseLocation.RESOURCE_VARIABLE
})
@DefaultInUncheckedCodeFor({ TypeUseLocation.UPPER_BOUND })
// Exceptions are always TOP type, so Throwable must be as well
@ImplicitFor(typeNames = { java.lang.Throwable.class })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface UnknownUnits {}
