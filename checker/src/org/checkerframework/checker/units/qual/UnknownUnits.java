package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultInUncheckedCodeFor;
import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.lang.model.type.TypeKind;

/**
 * UnknownUnits is the top type of the type hierarchy.
 *
 * UnknownUnits is the default type for any un-annotated local variables,
 * resource variables, and exception parameters, as well as for Package
 * declarations and the Void class.
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@InvisibleQualifier
@SubtypeOf({})
@DefaultFor({ TypeUseLocation.LOCAL_VARIABLE,
    TypeUseLocation.RESOURCE_VARIABLE,
    TypeUseLocation.EXCEPTION_PARAMETER
})
@ImplicitFor(
    types = { TypeKind.VOID, TypeKind.PACKAGE, TypeKind.NONE },
    // necessary for passing exceptions into methods, since method parameters are Scalar by default
    typeNames = { java.lang.Throwable.class }
)
@DefaultInUncheckedCodeFor({
    TypeUseLocation.UPPER_BOUND
})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface UnknownUnits {}
