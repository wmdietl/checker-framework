package org.checkerframework.framework.qual;

import java.lang.annotation.*;

/**
 * This meta-annotation is deprecated.
 * <p>
 *
 * An annotation will no longer use this meta-annotation. To indicate that an
 * annotation is a type qualifier, it should now have the meta-annotation
 * <tt>&#64;Target({ElementType.TYPE_USE})</tt>.
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TypeQualifier {

}
