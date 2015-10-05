package org.checkerframework.framework.type;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.PolyAll;
import org.checkerframework.framework.util.AnnotationBuilder;

public class AnnotatedTypeLoader {
    // For loading from a source package directory
    private String packageName;
    private static final String QUAL_PACKAGE_SUFFIX = ".qual";

    // For loading from a Jar file
    private static final String CLASS_SUFFIX = ".class";

    // Constants
    private static final char DOT = '.';
    private static final char SLASH = '/';

    // Processing Env used to create an Annotation Builder, which is in turn
    // used to build the annotation mirror from the loaded Class object
    protected ProcessingEnvironment processingEnv;

    // Stores the Resource URL of the qual directory of a Checker class, or AnnotatedTypeFactory class of a particular checker
    URL resourceURL;

    // Stores a mapping of the Annotation object and the converted AnnotationMirror
    private Map<Class<? extends Annotation>, AnnotationMirror> loadedAnnotations;

    // constructor for loading type qualifiers defined for a checker
    public AnnotatedTypeLoader(ProcessingEnvironment pe, BaseTypeChecker checker) {
        processingEnv = pe;
        packageName = checker.getClass().getPackage().getName().replace(SLASH, DOT) + QUAL_PACKAGE_SUFFIX;
        ProcessPackageNameAndLoadAnnos();
    }

    // constructor for loading type qualifiers defined for an annotated type factory
    public AnnotatedTypeLoader(ProcessingEnvironment pe, AnnotatedTypeFactory factory) {
        processingEnv = pe;
        packageName = factory.getClass().getPackage().getName().replace(SLASH, DOT) + QUAL_PACKAGE_SUFFIX;
        ProcessPackageNameAndLoadAnnos();
    }

    // sees if the package name of the qual directory is a valid resource and
    // retrieve its resource URL (either a jar or regular file folder), then
    // proceeds to reflectively load annotations if it is valid. Returns an
    // empty set if it isn't a valid URL
    private void ProcessPackageNameAndLoadAnnos() {
        //resourceURL = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(DOT, SLASH));
        resourceURL = this.getClass().getClassLoader().getResource(packageName.replace(DOT, SLASH));

        // in checkers, there will be a resource URL for the qual folder. But
        // when called in the framework (eg GeneralAnnotatedTypeFactory), there
        // won't be a resourceURL since there isn't a qual folder.
        if (resourceURL != null) {
            // if there's a URL, load from it
            loadedAnnotations = loadAnnotations();
        } else {
            // otherwise create an empty set
            loadedAnnotations = new HashMap<Class<? extends Annotation>, AnnotationMirror>();
        }
    }

    /**
     * Every subclass of AnnotatedTypeLoader must implement how it will convert a class into an annotation mirror
     * @param annoClass an annotation's class
     * @return AnnotationMirror of the annotation, or null if this annotation isn't required
     */
    protected AnnotationMirror createAnnotationMirrorFromClass(Class<? extends Annotation> annoClass) {
        // build the annotation mirror
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, annoClass);
        AnnotationMirror annoMirroResult = builder.build();
        return annoMirroResult;
    }

    /**
     * gets the qualifier set from the loaded annotations, with option to include @PolyAll
     * @param includePolyAll
     * @return an unmodifiable set of the qualifiers
     */
    public final Set<Class<? extends Annotation>> getAnnotatedTypeQualifierSet(final boolean includePolyAll) {
        Set<Class<? extends Annotation>> annotatedTypeQualSet = new HashSet<Class<? extends Annotation>>();
        annotatedTypeQualSet.addAll(loadedAnnotations.keySet());

        if (includePolyAll) {
            // add PolyAll to the qualifier set if requested
            annotatedTypeQualSet.add(PolyAll.class);
        }

        // return the set of annotation classes
        return Collections.unmodifiableSet(annotatedTypeQualSet);
    }

    /**
     * gets the annotation mirror set from the loaded annotations
     * @return a hashset of the annotation mirrors
     */
    public final Set<AnnotationMirror> getAnnotationMirrorSet() {
        return new HashSet<AnnotationMirror>(loadedAnnotations.values());
    }

    // loads annotations via reflection
    private final Map<Class<? extends Annotation>, AnnotationMirror> loadAnnotations() {
        Map<Class<? extends Annotation>, AnnotationMirror> annos = new HashMap<Class<? extends Annotation>, AnnotationMirror>();

        Set<String> annoFiles = getAnnotationNames();

        for (String fileName : annoFiles) {
            try {
                String annoName = packageName + DOT + fileName;

                // Load in the class files
                Class<?> cls = Class.forName(annoName);

                // ensure that the freshly loaded class is an annotation, and has the @Target annotation
                if (cls.isAnnotation() && cls.getAnnotation(Target.class) != null) {
                    // scan through the @Target annotation for its values
                    for (ElementType element : cls.getAnnotation(Target.class).value()) {
                        // ensure that the @Target annotation has the value of ElementType.TYPE_USE
                        if (element.equals(ElementType.TYPE_USE)) {
                            // if so, process the annotation and get its equivalent AnnotationMirror
                            Class<? extends Annotation> annoClass = cls.asSubclass(Annotation.class);
                            // createAnnotationMirrorFromClass is optionally overridden by each individual checker
                            // returns an annotation mirror if the checker handles it, or 
                            // null if it either doesn't handle it or fails to produce an annotation mirror
                            AnnotationMirror convertedMirror = createAnnotationMirrorFromClass(annoClass);

                            if (convertedMirror != null) {
                                // convert the annotation class into an annotation mirror, add the class and the mirror into the hashmap
                                annos.put(annoClass, convertedMirror);
                            }

                            break;
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                //TODO: give better feedback
                e.printStackTrace();
            }
        }

        return annos;
    }

    // Retrieves the annotation class file names from the qual directory of a particular checker
    private final Set<String> getAnnotationNames() {
        Set<String> results = null;

        // if the Checker class file is contained within a jar, which means the whole checker is shipped or loaded as a jar file, then process
        // the package as a jar file and load the annotations contained within the jar
        if (resourceURL.getProtocol().equals("jar")) {
            try {
                JarURLConnection connection = (JarURLConnection) resourceURL.openConnection();
                JarFile jarFile = connection.getJarFile();

                // get class names inside the jar file within the particular package
                results = getAnnotationNamesFromJar(jarFile);
            } catch (IOException e) {
                // Error: cannot open connection to Jar file, or cannot retrieve the jar file from connection
                // TODO: give better feedback
                e.printStackTrace();
            }
        }
        // else if the Checker class file is found within the file system itself within some directory (usually development build directories), then
        // process the package as a file directory in the file system and load the annotations contained in the qual directory
        else if (resourceURL.getProtocol().equals("file")) {
            results = new HashSet<String>();
            // open up the directory
            File packageDir = new File(resourceURL.getFile());
            for (File file : packageDir.listFiles()) {
                String fileName = file.getName();
                // filter for just class files
                if (fileName.endsWith(CLASS_SUFFIX)) {
                    String annotationClassName = fileName.substring(0, fileName.lastIndexOf('.'));
                    results.add(annotationClassName);
                }
            }
        }

        return results;
    }

    // Retrieves the annotation class file names from the qual directory contained inside a jar
    private final Set<String> getAnnotationNamesFromJar(JarFile jar) {
        Set<String> annos = new HashSet<String>();

        // get an enumeration iterator for all the content entries in the jar file
        Enumeration<JarEntry> jarEntries = jar.entries();

        // enumerate through the entries
        while (jarEntries.hasMoreElements()) {
            JarEntry je = jarEntries.nextElement();
            // filter out directories and non-class files
            if (je.isDirectory() || !je.getName().endsWith(CLASS_SUFFIX)) {
                continue;
            }

            // get rid of the .class suffix
            String className = je.getName().substring(0, je.getName().lastIndexOf('.'));
            // convert path notation to class notation 
            className = className.replace(SLASH, DOT);

            // filter for qual package and only add class names that are relevant
            if (className.startsWith(packageName)) {
                // remove qual package prefix, keeping only the class name
                className = className.substring( (packageName + DOT).length() );
                // add to set
                annos.add(className);
            }
        }

        return annos;
    }
}

