package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.util.DefaultAnnotationFormatter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;

//format the error printout of any units qualifier that uses Prefix.one
public class UnitsAnnotationFormatter extends DefaultAnnotationFormatter {
    protected final BaseTypeChecker checker;
    protected final Elements elements;

    public UnitsAnnotationFormatter(BaseTypeChecker checker) {
        this.checker = checker;
        this.elements = checker.getElementUtils();
    }

    @Override
    protected void formatAnnotationMirror(AnnotationMirror anno, StringBuilder sb) {
        // remove Prefix.one
        if (UnitsRelationsTools.getPrefix(anno) == Prefix.one) {
            anno = UnitsRelationsTools.removePrefix(elements, anno);
        }

        if (UnitAnnotationTools.isUnitAnno(anno)) {
            // @Unit annotations need special formatting and processing
            sb.append(UnitAnnotationTools.getNormalizedUnitName(anno));
        } else {
            // other units will be formatted by super implementation
            super.formatAnnotationMirror(anno, sb);
        }
        return;
    }
}
