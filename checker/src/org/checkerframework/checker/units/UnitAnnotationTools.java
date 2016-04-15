package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Unit;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.Pair;

import java.util.*;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;

import com.sun.tools.javac.code.Type.ClassType;

public class UnitAnnotationTools {

    private static UnitsAnnotationMirrors mirrors;

    private static Map<String, AnnotationMirror> standardUnitAnnoMap = new HashMap<String, AnnotationMirror>();

    protected static void init(UnitsAnnotationMirrors mirrors) {
        UnitAnnotationTools.mirrors = mirrors;

        // TODO: init the standardUnitAnnoMap here
        for (String scientificSymbols : mirrors.mirrorsMap.keySet()) {
            standardUnitAnnoMap.put("@Unit(" + scientificSymbols + ")", mirrors.mirrorsMap.get(scientificSymbols));
        }


        standardUnitAnnoMap.put("@Unit(UnknownUnits)", mirrors.TOP);
        standardUnitAnnoMap.put("@Unit(UnitsBottom)", mirrors.BOTTOM);
        standardUnitAnnoMap.put("@Unit(Scalar)", mirrors.SCALAR);

        standardUnitAnnoMap.put("@Unit(m)", mirrors.m);
        standardUnitAnnoMap.put("@Unit(m^2)", mirrors.m2);

        standardUnitAnnoMap.put("@Unit(mm^2)", mirrors.mm2);

        standardUnitAnnoMap.put("@Unit(s)", mirrors.s);
    }

//    private static AnnotationMirror getStandardAnno(AnnotationMirror unitAnno) {
//        return convertToStandardAnno(unitAnno).first;
//    }

    protected static Pair<AnnotationMirror, Boolean> convertToStandardAnno(AnnotationMirror unitAnno) {
        if (isUnitAnno(unitAnno)) {
            String normalizedUnitName = getNormalizedUnitName(unitAnno);
            if (standardUnitAnnoMap.containsKey(normalizedUnitName)) {
                return Pair.of(standardUnitAnnoMap.get(normalizedUnitName), true);
            }
        }
        return Pair.of(unitAnno, false);
    }

    protected static boolean isUnitAnno(AnnotationMirror anno) {
        return AnnotationUtils.areSameByClass(anno, Unit.class);
    }

    /**
     * The unit parameters are normalized according to the following rules:
     * 1) negative powers are converted into their reciprocals, only positive powers are allowed
     * 2) units are grouped into a single numerator and a single denominator
     * 3) units are alphabetically sorted in the numerator and denominator
     *
     * @param anno
     * @return
     */
    protected static String getNormalizedUnitName(AnnotationMirror anno) {
        StringBuilder normalizedName = new StringBuilder();

        // parse in order
        // key of unit,prefix  ==> value of exponent   (eg kg^2)

        List<ClassType> numeratorUnits = AnnotationUtils.getElementValueArray(anno, "numeratorUnits", ClassType.class, true);
        List<Double> numeratorPrefixValues = AnnotationUtils.getElementValueArray(anno, "numeratorPrefixValues", Double.class, true);

        List<ClassType> denominatorUnits = AnnotationUtils.getElementValueArray(anno, "denominatorUnits", ClassType.class, true);
        List<Double> denominatorPrefixValues = AnnotationUtils.getElementValueArray(anno, "denominatorPrefixValues", Double.class, true);

        String numerator = getNormalizedUnitPartName(numeratorUnits, numeratorPrefixValues).intern();
        String denominator = getNormalizedUnitPartName(denominatorUnits, denominatorPrefixValues).intern();

        // TODO: return the alias name if possible from Map
        // otherwise return @Unit( ____ )

        normalizedName.append("@Unit(");

        if(numerator == "1" && denominator == "1") {
            // if it is 1/1, return scalar
            normalizedName.append(getSimpleName(mirrors.SCALAR.toString()));
        } else if (denominator == "1") {
            // if it is unit/1 return unit
            normalizedName.append(numerator);
        } else {
            // else return unit / unit
            normalizedName.append(numerator);
            normalizedName.append('/');
            normalizedName.append(denominator);
        }

        normalizedName.append(")");
        return normalizedName.toString();
    }

    private static String getNormalizedUnitPartName(List<ClassType> units, List<Double> prefixes) {
        StringBuilder normalizedName = new StringBuilder();

        // map of unit class name, to prefix value, to whole integer exponent
        // TODO: are fractional exponents on a unit possible?
        Map<String, Map<Double, Long>> numerators = new TreeMap<String, Map<Double, Long>>();

        Iterator<Double> numeratorPrefix = prefixes.iterator();

        for(ClassType unit : units) {
            // building it could check for safety, but a lot of overhead for each call
            //                AnnotationMirror numeratorAnno = AnnotationUtils.fromName(elements, c.toString());
            //                normalizedName.append(numeratorAnno.toString());

            String unitName = unit.toString().intern();

            // create a submap if the unit isn't in the numerators map
            if(!numerators.containsKey(unitName)) {
                numerators.put(unitName, new HashMap<Double, Long>());
            }

            // retrieve the submap
            Map<Double, Long> subMap = numerators.get(unitName);

            // obtain the prefix related to this map
            Double prefix = numeratorPrefix.next();

            if(subMap.containsKey(prefix)) {
                Long exponent = subMap.get(prefix);
                exponent++;
                subMap.put(prefix, exponent);
            } else {
                subMap.put(prefix, Long.valueOf(1));
            }
        }

        // printing
        for( Entry<String, Map<Double, Long>> unit : numerators.entrySet()) {

            for( Entry<Double, Long> subMapEntry : unit.getValue().entrySet() ) {
                // if prefix is not 1.0 append prefix
                Double prefix = subMapEntry.getKey();

                if(prefix != 1.0) {
                    normalizedName.append(prefix);
                    normalizedName.append(" ");
                }

                String unitName = getSimpleName(unit.getKey().toString()).intern();
                if(unitName == "Scalar") {
                    unitName = "1";
                }

                normalizedName.append(unitName);    // simple class names

                Long exponent = subMapEntry.getValue();
                if(exponent != 1) {

                    normalizedName.append("^");
                    normalizedName.append(exponent);
                }
            }
        }

        return normalizedName.toString();
    }

    private static String getSimpleName(String className) {
        // TODO add caching
        return className.substring(className.lastIndexOf('.') + 1);
    }
}
