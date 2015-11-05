package org.checkerframework.checker.fenum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultLocation;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * The top of the fake enumeration type hierarchy.
 * <p>
 *
 * This annotation may not be written in source code; it is an
 * implementation detail of the checker.
 *
 * @checker_framework.manual #fenum-checker Fake Enum Checker
 */
@SubtypeOf( { } )
@DefaultFor({ DefaultLocation.LOCAL_VARIABLE, DefaultLocation.RESOURCE_VARIABLE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // empty target prevents programmers from writing this in a program
public @interface FenumTop {}
