import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

import java.lang.Math;

// this test contains only the new methods introduced in java 8 that are not in java 7
// @below-java8-jdk-skip-test

public class Java8Math {

    void exactTest() {
        // setup
        @m int m = 20 * UnitsTools.m;
        @km int km = 30 * UnitsTools.km;
        @g int g = 30 * UnitsTools.g;
        @Length int len;

        @m long mL = 20 * UnitsTools.m;
        @km long kmL = 30 * UnitsTools.km;
        @g long gL = 30 * UnitsTools.g;
        @Length long lenL;

        // incrementExact
        m = Math.incrementExact(m);
        kmL = Math.incrementExact(kmL);
        //:: error: (assignment.type.incompatible)
        km = Math.incrementExact(m);
        //:: error: (assignment.type.incompatible)
        mL = Math.incrementExact(kmL);

        // decrementExact
        m = Math.decrementExact(m);
        kmL = Math.decrementExact(kmL);
        //:: error: (assignment.type.incompatible)
        km = Math.decrementExact(m);
        //:: error: (assignment.type.incompatible)
        mL = Math.decrementExact(kmL);

        // negateExact
        m = Math.negateExact(m);
        kmL = Math.negateExact(kmL);
        //:: error: (assignment.type.incompatible)
        km = Math.negateExact(m);
        //:: error: (assignment.type.incompatible)
        mL = Math.negateExact(kmL);

        // The following tests must use units relations to resolve the unit
        // addExact
        m = Math.addExact(m, m);
        len = Math.addExact(m, m);
        len = Math.addExact(m, km);
        //:: error: (assignment.type.incompatible)
        m = Math.addExact(m, km);
        //:: error: (assignment.type.incompatible)
        len = Math.addExact(m, g);

        mL = Math.addExact(mL, mL);
        lenL = Math.addExact(mL, mL);
        lenL = Math.addExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        mL = Math.addExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        lenL = Math.addExact(mL, gL);

        // subtractExact
        m = Math.subtractExact(m, m);
        len = Math.subtractExact(m, m);
        len = Math.subtractExact(m, km);
        //:: error: (assignment.type.incompatible)
        m = Math.subtractExact(m, km);
        //:: error: (assignment.type.incompatible)
        len = Math.subtractExact(m, g);

        mL = Math.subtractExact(mL, mL);
        lenL = Math.subtractExact(mL, mL);
        lenL = Math.subtractExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        mL = Math.subtractExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        lenL = Math.subtractExact(mL, gL);

        // multiplyExact
        @m2 int area = Math.multiplyExact(m, m);
        //:: error: (assignment.type.incompatible)
        @km2 int area2 = Math.multiplyExact(m, m);

        @m2 long areaL = Math.multiplyExact(mL, mL);
        //:: error: (assignment.type.incompatible)
        @km2 long areaL2 = Math.multiplyExact(mL, mL);
    }

    // contains all other test cases that must use units relations to resolve
    // for Math methods
    void useUnitsRelationsTest() {
        @m int m = 20 * UnitsTools.m;
        @s int s = 30 * UnitsTools.s;
        @km int km = 30 * UnitsTools.km;
        @h int h = 40 * UnitsTools.h;

        @m long mL = 20 * UnitsTools.m;
        @s long sL = 30 * UnitsTools.s;
        @km long kmL = 30 * UnitsTools.km;
        @h long hL = 40 * UnitsTools.h;

        @g double g = 30 * UnitsTools.g;

        // floorDiv
        @mPERs int mps = Math.floorDiv(m, s);
        @kmPERh long kmph = Math.floorDiv(kmL, hL);
        //:: error: (assignment.type.incompatible)
        @km int mps2 = Math.floorDiv(m, s);
        //:: error: (assignment.type.incompatible)
        @m long kmph2 = Math.floorDiv(kmL, hL);
    }

    void floorModTest() {
        @m int m = 20 * UnitsTools.m;
        @s int s = 30 * UnitsTools.s;

        // floorMod returns the same unit as it's first argument
        @m int m2 = Math.floorMod(m, s);
        @s int s2 = Math.floorMod(s, m);
        //:: error: (assignment.type.incompatible)
        @s int m3 = Math.floorMod(m, s);
        //:: error: (assignment.type.incompatible)
        @m int s3 = Math.floorMod(s, m);
    }

    void nextMethodsTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        @mm double mmD = 30.0d * UnitsTools.mm;
        @kg float kgF = 20.0f * UnitsTools.kg;

        // nextDown
        mD = Math.nextDown(mD);
        gF = Math.nextDown(gF);
        //:: error: (assignment.type.incompatible)
        mmD = Math.nextDown(mD);
        //:: error: (assignment.type.incompatible)
        kgF = Math.nextDown(gF);
    }

    void roundingTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;
        @m long mL = Math.round(mD);

        // toIntExact
        @m int mI = Math.toIntExact(mL);
        //:: error: (assignment.type.incompatible)
        @mm int mmI = Math.toIntExact(mL);
    }
}
