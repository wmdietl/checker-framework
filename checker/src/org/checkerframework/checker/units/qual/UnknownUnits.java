package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultInUncheckedCodeFor;
import org.checkerframework.framework.qual.DefaultLocation;
import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedNoType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@DefaultFor({ DefaultLocation.LOCAL_VARIABLE,
    DefaultLocation.RESOURCE_VARIABLE,
    DefaultLocation.EXCEPTION_PARAMETER
})
@ImplicitFor(
    typeClasses = { AnnotatedNoType.class },
    // necessary for passing exceptions into methods, since method parameters are Scalar by default
    typeNames = { java.lang.Throwable.class }
)
@DefaultInUncheckedCodeFor({
    DefaultLocation.UPPER_BOUNDS,
    DefaultLocation.EXCEPTION_PARAMETER
})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface UnknownUnits {}
