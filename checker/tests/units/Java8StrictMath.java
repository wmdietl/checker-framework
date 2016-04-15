import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

import java.lang.StrictMath;

// this test contains only the new methods introduced in java 8 that are not in java 7
// @below-java8-jdk-skip-test

public class Java8StrictMath {

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

        // The following tests must use units relations to resolve the unit
        // addExact
        m = StrictMath.addExact(m, m);
        len = StrictMath.addExact(m, m);
        len = StrictMath.addExact(m, km);
        //:: error: (assignment.type.incompatible)
        m = StrictMath.addExact(m, km);
        //:: error: (assignment.type.incompatible)
        len = StrictMath.addExact(m, g);

        mL = StrictMath.addExact(mL, mL);
        lenL = StrictMath.addExact(mL, mL);
        lenL = StrictMath.addExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        mL = StrictMath.addExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        lenL = StrictMath.addExact(mL, gL);

        // subtractExact
        m = StrictMath.subtractExact(m, m);
        len = StrictMath.subtractExact(m, m);
        len = StrictMath.subtractExact(m, km);
        //:: error: (assignment.type.incompatible)
        m = StrictMath.subtractExact(m, km);
        //:: error: (assignment.type.incompatible)
        len = StrictMath.subtractExact(m, g);

        mL = StrictMath.subtractExact(mL, mL);
        lenL = StrictMath.subtractExact(mL, mL);
        lenL = StrictMath.subtractExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        mL = StrictMath.subtractExact(mL, kmL);
        //:: error: (assignment.type.incompatible)
        lenL = StrictMath.subtractExact(mL, gL);

        // multiplyExact
        @m2 int area = StrictMath.multiplyExact(m, m);
        //:: error: (assignment.type.incompatible)
        @km2 int area2 = StrictMath.multiplyExact(m, m);

        @m2 long areaL = StrictMath.multiplyExact(mL, mL);
        //:: error: (assignment.type.incompatible)
        @km2 long areaL2 = StrictMath.multiplyExact(mL, mL);
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
        @mPERs int mps = StrictMath.floorDiv(m, s);
        @kmPERh long kmph = StrictMath.floorDiv(kmL, hL);
        //:: error: (assignment.type.incompatible)
        @km int mps2 = StrictMath.floorDiv(m, s);
        //:: error: (assignment.type.incompatible)
        @m long kmph2 = StrictMath.floorDiv(kmL, hL);
    }

    void floorModTest() {
        @m int m = 20 * UnitsTools.m;
        @s int s = 30 * UnitsTools.s;

        // floorMod returns the same unit as it's first argument
        @m int m2 = StrictMath.floorMod(m, s);
        @s int s2 = StrictMath.floorMod(s, m);
        //:: error: (assignment.type.incompatible)
        @s int m3 = StrictMath.floorMod(m, s);
        //:: error: (assignment.type.incompatible)
        @m int s3 = StrictMath.floorMod(s, m);
    }

    void nextMethodsTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        @mm double mmD = 30.0d * UnitsTools.mm;
        @kg float kgF = 20.0f * UnitsTools.kg;

        // nextDown
        mD = StrictMath.nextDown(mD);
        gF = StrictMath.nextDown(gF);
        //:: error: (assignment.type.incompatible)
        mmD = StrictMath.nextDown(mD);
        //:: error: (assignment.type.incompatible)
        kgF = StrictMath.nextDown(gF);
    }

    void roundingTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;
        @m long mL = StrictMath.round(mD);

        // toIntExact
        @m int mI = StrictMath.toIntExact(mL);
        //:: error: (assignment.type.incompatible)
        @mm int mmI = StrictMath.toIntExact(mL);
    }
}
