package org.checkerframework.checker.units.qual;

import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sun.source.tree.Tree;

/**
 * A Scalar is defined in Physics as a quantity that is independent of specific
 * classes of coordinate systems in other words, a quantity that has absolutely
 * no units
 *
 * @Scalar is the type which represents a physical Scalar quantity It is the
 *         default annotation for primitive number literals when using the Units
 *         Checker
 *
 * @checker_framework.manual #units-checker Units Checker
 */
@InvisibleQualifier
@SubtypeOf(UnknownUnits.class)
// default type for primitive number literals only
@ImplicitFor(trees = {
        Tree.Kind.NEW_ARRAY,
        Tree.Kind.DOUBLE_LITERAL, Tree.Kind.FLOAT_LITERAL,
        Tree.Kind.INT_LITERAL, Tree.Kind.LONG_LITERAL
})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface Scalar {}
