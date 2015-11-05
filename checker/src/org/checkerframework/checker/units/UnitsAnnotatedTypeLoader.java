package org.checkerframework.checker.units;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.units.qual.UnitsMultiple;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeLoader;
import org.checkerframework.framework.util.AnnotationBuilder;

public class UnitsAnnotatedTypeLoader extends AnnotatedTypeLoader {

    public UnitsAnnotatedTypeLoader(ProcessingEnvironment pe, AnnotatedTypeFactory factory) {
        super(pe, factory);
    }

    // custom filter for units qualifiers
    @Override
    protected AnnotationMirror createAnnotationMirrorFromClass(Class<? extends Annotation> annoClass) {
        // build the initial annotation mirror (missing prefix)
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, annoClass);
        AnnotationMirror initialResult = builder.build();

        // further refine to see if the annotation is an alias of some other SI Unit annotation
        for (AnnotationMirror metaAnno : initialResult.getAnnotationType().asElement().getAnnotationMirrors() ) {
            // TODO : special treatment of invisible qualifiers?

            // if the annotation is a SI prefix multiple of some base unit, then return null
            // classic Units checker does not need to load the annotations of SI prefix multiples of base units
            if ( metaAnno.getAnnotationType().toString().equals(UnitsMultiple.class.getCanonicalName())) {
                return null;
            }
        }

        // Not an alias unit
        return initialResult;
    }

}
