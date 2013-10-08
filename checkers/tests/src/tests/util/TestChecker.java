package tests.util;

import checkers.basetype.BaseTypeChecker;
import checkers.quals.Bottom;
import checkers.quals.TypeQualifiers;
import checkers.quals.Unqualified;
import checkers.types.BasicAnnotatedTypeFactory;
import checkers.types.QualifierHierarchy;
import checkers.util.GraphQualifierHierarchy;
import checkers.util.MultiGraphQualifierHierarchy.MultiGraphFactory;

import javacutils.AnnotationUtils;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;

/**
 * A simple checker used for testing the Checker Framework. It treats the
 * {@code @Odd} and {@code @Even} annotations as a subtype-style qualifiers with
 * no special semantics.
 *
 * <p>
 * This checker should only be used for testing the framework.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@TypeQualifiers({ Odd.class, MonotonicOdd.class, Even.class, Unqualified.class,
        Bottom.class })
public final class TestChecker extends BaseTypeChecker {

}

class TestAnnotatedTypeFactory extends BasicAnnotatedTypeFactory {
    protected AnnotationMirror BOTTOM;

    public TestAnnotatedTypeFactory(TestChecker checker) {
        super(checker, true);
        Elements elements = processingEnv.getElementUtils();
        BOTTOM = AnnotationUtils.fromClass(elements, Bottom.class);

        this.postInit();

        this.typeAnnotator.addTypeName(java.lang.Void.class, BOTTOM);
        this.treeAnnotator.addTreeKind(com.sun.source.tree.Tree.Kind.NULL_LITERAL, BOTTOM);
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new GraphQualifierHierarchy(factory, BOTTOM);
    }
}
