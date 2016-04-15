package org.checkerframework.checker.units;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.DefaultAnnotatedTypeFormatter;

import javax.lang.model.util.Elements;

public class UnitsAnnotatedTypeFormatter extends DefaultAnnotatedTypeFormatter {
    protected final BaseTypeChecker checker;
    protected final Elements elements;

    public UnitsAnnotatedTypeFormatter(BaseTypeChecker checker) {
        // Utilize the Default Type Formatter, but force it to print out
        // Invisible Qualifiers
        // keep super call in sync with implementation in
        // DefaultAnnotatedTypeFormatter
        // keep checker options in sync with implementation in
        // AnnotatedTypeFactory
        super(new UnitsAnnotationFormatter(checker), checker.hasOption("printVerboseGenerics"), true);

        this.checker = checker;
        this.elements = checker.getElementUtils();
    }
}
