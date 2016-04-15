package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.TimeDuration;
import org.checkerframework.checker.units.qual.time.duration.s;
import org.checkerframework.checker.units.qual.time.point.TimePoint;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;

public class UnitsAnnotationMirrors {
//    private final ProcessingEnvironment processingEnv;

    protected final AnnotationMirror SCALAR;
    protected final AnnotationMirror TOP;
    protected final AnnotationMirror BOTTOM;
    protected final AnnotationMirror UNIT;

    // TODO: add remaining ones

    protected final AnnotationMirror m;
    protected final AnnotationMirror mm;
    protected final AnnotationMirror km;

    protected final AnnotationMirror m2;
    protected final AnnotationMirror mm2;
    protected final AnnotationMirror km2;

    protected final AnnotationMirror m3;
    protected final AnnotationMirror km3;
    protected final AnnotationMirror mm3;

    protected final AnnotationMirror timeDuration;
    protected final AnnotationMirror timeInstant;

    // durations
    protected final AnnotationMirror s;


    // TODO: move alias map logic here
    // ALL mirrors are managed here
    // alias -> standardized unit mirrors

    // scientific symbol notation -> unit mirrors
    protected final Map<String, AnnotationMirror> mirrorsMap = new TreeMap<String, AnnotationMirror>();

    public UnitsAnnotationMirrors(ProcessingEnvironment processingEnv) {
//        this.processingEnv = processingEnv;

        SCALAR = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, Scalar.class);
        TOP = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, UnknownUnits.class);
        BOTTOM = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, UnitsBottom.class);
        UNIT = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, Unit.class);

        m = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m.class);
        mm = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.milli);
        km = UnitsRelationsTools.buildAnnoMirrorWithSpecificPrefix(processingEnv, m.class, Prefix.kilo);

        m2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m2.class);
        mm2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, mm2.class);
        km2 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, km2.class);

        m3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, m3.class);
        km3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, km3.class);
        mm3 = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, mm3.class);

        timeDuration = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, TimeDuration.class);
        timeInstant = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, TimePoint.class);

        s = UnitsRelationsTools.buildAnnoMirrorWithNoPrefix(processingEnv, s.class);
    }

    protected void addMirror(AnnotationMirror mirror) {
        String mirrorName = mirror.toString().intern();
        if(!mirrorsMap.containsKey(mirrorName)){
            mirrorsMap.put(mirrorName, mirror);
        }
    }
}
