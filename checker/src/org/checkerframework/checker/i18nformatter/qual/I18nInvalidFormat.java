package org.checkerframework.checker.i18nformatter.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.SubtypeOf;

/**
 * This annotation, attached to a {@link java.lang.String String} type,
 * indicates that if the String is passed to
 * {@link java.text.MessageFormat#format(String, Object...)},
 * an exception will result.
 * <p>
 *
 * This annotation may not be written in source code; it is an
 * implementation detail of the checker.
 *
 * @checker_framework.manual #i18n-formatter-checker Internationalization
 *                           Format String Checker
 * @author Siwakorn Srisakaokul
 */
@SubtypeOf(I18nUnknownFormat.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface I18nInvalidFormat {
    /**
     * Using a value of the annotated type as the first argument to
     * {@link java.text.MessageFormat#format(String, Object...)}
     * will lead to this exception message.
     */
    String value();
}
