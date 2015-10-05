package org.checkerframework.framework.qual;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.qualframework.base.Checker;

/**
 * This meta-annotation is deprecated.
 * <p>
 *
 * Each type-checker should either place all qualifiers within a <tt>qual</tt>
 * subfolder, with the subfolder located directly in the same folder as the
 * {@code Checker}, or override
 * {@link AnnotatedTypeFactory#createSupportedTypeQualifiers()}.
 * <p>
 *
 * Qualifiers placed within the <tt>qual</tt> directory will be automatically
 * loaded by the checker framework using reflective lookup of qualifier names.
 * By default @PolyAll is not included, but can be added by overriding
 * {@link AnnotatedTypeFactory#createSupportedTypeQualifiers()
 * createSupportedTypeQualifiers}.
 * <p>
 *
 * There's four recommended ways to write an override implementation of
 * {@link AnnotatedTypeFactory#createSupportedTypeQualifiers()
 * createSupportedTypeQualifiers}:
 * <p>
 *
 * A) using reflective lookup to add all qualifiers, and add @PolyAll. Example:
 *
 * <pre>
 * &#64;Override
 * protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
 *     return loadTypeQualifiersFromQualDir(true, null);
 * }
 * </pre>
 *
 * B) using reflective lookup to add qualifiers, add @PolyAll, and add a manual
 * list of qualifiers. Example:
 *
 * <pre>
 * &#64;Override
 * protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
 *     return loadTypeQualifiersFromQualDir(true,
 *             new HashSet<Class<? extends Annotation>>(
 *                     Arrays.asList(H1Top.class, H1S1.class, H1S2.class,
 *                             H1Bot.class, H2Top.class, H2S1.class, H2S2.class,
 *                             H2Bot.class, H1Poly.class, H2Poly.class)));
 * }
 * </pre>
 *
 * The use of a manual list is necessary for any qualifiers that are not located in
 * the <tt>qual</tt> folder, or for qualifiers which do not or cannot have the
 * <tt>@Target({ElementType.TYPE_USE})</tt> meta-annotation (eg some Bottoms).
 *
 * See {@code PolyAllAnnotatedTypeFactory} for details.
 * <p>
 *
 * C) using reflective lookup to add qualifiers and add a manual list of
 * qualifiers, but without @PolyAll. Example:
 *
 * <pre>
 * &#64;Override
 * protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
 *     return loadTypeQualifiersFromQualDir(false,
 *             new HashSet<Class<? extends Annotation>>(
 *                     Arrays.asList(IGJBottom.class)));
 * }
 * </pre>
 *
 * See {@code IGJAnnotatedTypeFactory} for details.
 * <p>
 *
 * D) only using a manual list of qualifiers. Example:
 *
 * <pre>
 * &#64;Override
 * protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
 *     return Collections.unmodifiableSet(
 *             new HashSet<Class<? extends Annotation>>(Arrays.asList(A.class,
 *                     B.class, C.class, D.class, E.class, F.class)));
 * }
 * </pre>
 *
 * See {@code LubGlbAnnotatedTypeFactory}
 *
 * @see AnnotatedTypeFactory#createSupportedTypeQualifiers()
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE } )
public @interface TypeQualifiers {
    /** The type qualifier annotations supported by the annotated {@link Checker}.
     * The checker may also support other, non-type-qualifier, annotations. */
    Class<? extends Annotation>[] value();
}
