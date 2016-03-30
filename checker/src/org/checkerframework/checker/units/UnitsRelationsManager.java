package org.checkerframework.checker.units;

import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.ErrorReporter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
 */

/**
 * Handles the loading of external and internally defined UnitsRelations classes
 * and providing the classes to the units ATF
 */
public class UnitsRelationsManager {
    // Map from canonical class name to the corresponding UnitsRelations
    // instance. The string is used to prevent instantiating the UnitsRelations
    // multiple times.
    private Map<String, UnitsRelations> unitsRelations;

    private static final Class<org.checkerframework.checker.units.qual.UnitsRelations> unitsRelationsAnnoClass = org.checkerframework.checker.units.qual.UnitsRelations.class;

    private final UnitsAnnotatedTypeFactory factory;
    private final UnitsChecker checker;
    private final ProcessingEnvironment processingEnv;
    private final Elements elements;

    protected UnitsRelationsManager(UnitsChecker uChecker, UnitsAnnotatedTypeFactory uFactory) {
        this.checker = uChecker;
        this.factory = uFactory;
        this.processingEnv = checker.getProcessingEnvironment();
        this.elements = factory.getElementUtils();
    }

    /**
     * Look for an @UnitsRelations annotation on the qualifier and add it to the
     * list of UnitsRelations.
     *
     * @param qual The qualifier to investigate.
     */
    protected void addUnitsRelations(Class<? extends Annotation> qual) {
        AnnotationMirror am = AnnotationUtils.fromClass(elements, qual);

        for (AnnotationMirror ama : am.getAnnotationType().asElement().getAnnotationMirrors()) {
            if (AnnotationUtils.areSameByClass(ama, unitsRelationsAnnoClass)) {
                Class<? extends UnitsRelations> theclass = AnnotationUtils.getElementValueClass(ama, "value", true).asSubclass(UnitsRelations.class);
                String classname = theclass.getCanonicalName().intern();

                if (!getUnitsRelationsMap().containsKey(classname)) {
                    try {
                        unitsRelations.put(classname, ((UnitsRelations) theclass.newInstance()).init(processingEnv));
                    } catch (InstantiationException e) {
                        ErrorReporter.errorAbort("Could not instantiate "
                                + classname + " is it on the classpath?");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        ErrorReporter.errorAbort("Could not reflectively instantiate "
                                + classname + " is it on the classpath?");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected Map<String, UnitsRelations> getUnitsRelationsMap() {
        if (unitsRelations == null) {
            unitsRelations = new HashMap<String, UnitsRelations>();
            // Always add the default units relations for the standard units.
            unitsRelations.put(UnitsRelationsDefault.class.getCanonicalName().intern(), new UnitsRelationsDefault().init(processingEnv));
        }
        return unitsRelations;
    }
}
