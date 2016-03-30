import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

import java.lang.Math;

public class Java7Math {
    // Constants
    @Scalar double e = Math.E;
    @UnknownUnits double eU = Math.E;
    //:: error: (assignment.type.incompatible)
    @m double eM = Math.E;
    @m double eM2 = Math.E * UnitsTools.m;

    @Scalar double pi = Math.PI;
    @UnknownUnits double piU = Math.PI;
    //:: error: (assignment.type.incompatible)
    @m double piM = Math.PI;
    @m double piM2 = Math.PI * UnitsTools.m;

    void absoluteValueTest(){
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;
        @C int cI = 20 * UnitsTools.C;
        @K long kL = 30 * UnitsTools.K;

        mD = Math.abs(mD);
        gF = Math.abs(gF);
        cI = Math.abs(cI);
        kL = Math.abs(kL);

        //:: error: (assignment.type.incompatible)
        gF = Math.abs((float) mD);
        //:: error: (assignment.type.incompatible)
        mD = Math.abs(gF);
        //:: error: (assignment.type.incompatible)
        kL = Math.abs(cI);
        //:: error: (assignment.type.incompatible)
        cI = Math.abs((int) kL);
    }

    void trigonometryTest() {
        @radians double angle = Math.PI * 20.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        // receiving type is @UnknownUnits
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double tan = Math.tan(angle);

        // receiving type is @Scalar
        @Scalar double sin2 = Math.sin(angle);
        @Scalar double cos2 = Math.cos(angle);
        @Scalar double tan2 = Math.tan(angle);

        //:: error: (assignment.type.incompatible)
        @m double s = Math.sin(angle);
        //:: error: (assignment.type.incompatible)
        @g double c = Math.cos(angle);
        //:: error: (assignment.type.incompatible)
        @C double t = Math.tan(angle);

        //:: error: (argument.type.incompatible)
        @Scalar double sin3 = Math.sin(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double cos3 = Math.cos(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double tan3 = Math.tan(angDeg);

        @m double sides = 15.0d * UnitsTools.m;
        // hypotneuse's unit matches the unit of the sides
        @m double hypot = Math.hypot(sides, sides);
        //:: error: (assignment.type.incompatible)
        @km double hypotBad = Math.hypot(sides, sides);

        @m double hypot2 = Math.hypot(30.0d * UnitsTools.m, 20.0d * UnitsTools.m);
        @km double hypot3 = Math.hypot(30.0d * UnitsTools.km, 20.0d * UnitsTools.km);
        @Length double hypot4 = Math.hypot(30.0d * UnitsTools.m, 20.0d * UnitsTools.km);
        @Length double hypot5 = Math.hypot(30.0d * UnitsTools.mm, 20.0d * UnitsTools.km);
        @Angle double hypot6 = Math.hypot(30.0d * UnitsTools.deg, 20.0d * UnitsTools.rad);
    }

    void inverseTrigTest() {
        @Scalar double constant = Math.PI;
        @m double meter = 20.0d * UnitsTools.m;

        @radians double asin = Math.asin(constant);
        @radians double acos = Math.acos(constant);
        @radians double atan = Math.atan(constant);

        //:: error: (assignment.type.incompatible)
        @degrees double asind = Math.asin(constant);
        //:: error: (assignment.type.incompatible)
        @degrees double acosd = Math.acos(constant);
        //:: error: (assignment.type.incompatible)
        @degrees double atand = Math.atan(constant);

        //:: error: (argument.type.incompatible)
        @radians double asinm = Math.asin(meter);
        //:: error: (argument.type.incompatible)
        @radians double acosm = Math.acos(meter);
        //:: error: (argument.type.incompatible)
        @radians double atanm = Math.atan(meter);

        @radians double atan2 = Math.atan2(constant, constant);
        @radians double atan21 = Math.atan2(meter, meter);

        //:: error: (assignment.type.incompatible)
        @degrees double atan2d = Math.atan2(constant, constant);

        // x and y have to have the same units
        //:: error: (two.parameter.method.arguments.unit.mismatch)
        @radians double atan22 = Math.atan2(meter, constant);
    }

    // Future TODO: keep and pass the unit through the trigonometric identities
    void inverseTrigIdentitiesTest() {
        // full list of identities
        // http://www.sosmath.com/trig/Trig5/trig5/trig5.html
        @radians double r = 30 * UnitsTools.rad;
        @Scalar double s = 30;

        // Inverse Trigonometry identities in terms of units (value is subject
        // to domain)
        // sin(asin(x)) == x
        @Scalar double sas = Math.sin(Math.asin(s));
        //:: error: (assignment.type.incompatible)
        @m double sasBad = Math.sin(Math.asin(s));

        // asin(sin(y)) == y
        @radians double ass = Math.asin(Math.sin(r));
        //:: error: (assignment.type.incompatible)
        @m double assBad = Math.asin(Math.sin(r));

        // cos(acos(x)) == x
        @Scalar double cac = Math.cos(Math.acos(s));
        //:: error: (assignment.type.incompatible)
        @m double cacBad = Math.cos(Math.acos(s));

        // acos(cos(y)) == y
        @radians double acc = Math.acos(Math.cos(r));
        //:: error: (assignment.type.incompatible)
        @m double accBad = Math.acos(Math.cos(r));

        // tan(atan(x)) == x
        @Scalar double tat = Math.tan(Math.atan(s));
        //:: error: (assignment.type.incompatible)
        @m double tatBad = Math.tan(Math.atan(s));

        // atan(tan(y)) == y
        @radians double att = Math.atan(Math.tan(r));
        //:: error: (assignment.type.incompatible)
        @m double attBad = Math.atan(Math.tan(r));

        // atan2(y, x) == atan(y / x) except the sign of both arguments are used
        // to determine the quadrant of the result
        // let z = y / x
        // z == tan(atan(z)) == tan(atan2(y, x))
        // result of z = s / s is a scalar
        @Scalar double tat2 = Math.tan(Math.atan2(s, s));
        //:: error: (assignment.type.incompatible)
        @m double tat2Bad = Math.tan(Math.atan2(s, s));
    }

    void hyperbolicTrigTest() {
        @radians double angle = Math.PI * 20.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        // receiving type is @UnknownUnits
        double sinh = Math.sinh(angle);
        double cosh = Math.cosh(angle);
        double tanh = Math.tanh(angle);

        @Scalar double sinh2 = Math.sinh(angle);
        @Scalar double cosh2 = Math.cosh(angle);
        @Scalar double tanh2 = Math.tanh(angle);

        //:: error: (assignment.type.incompatible)
        @m double s = Math.sinh(angle);
        //:: error: (assignment.type.incompatible)
        @g double c = Math.cosh(angle);
        //:: error: (assignment.type.incompatible)
        @C double t = Math.tanh(angle);

        //:: error: (argument.type.incompatible)
        @Scalar double sin3 = Math.sinh(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double cos3 = Math.cosh(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double tan3 = Math.tanh(angDeg);
    }

    void angleConversionTest() {
        @radians double angRad = 30.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        angDeg = Math.toDegrees(angRad);
        angRad = Math.toRadians(angDeg);

        //:: error: (assignment.type.incompatible)
        angRad = Math.toDegrees(angRad);
        //:: error: (assignment.type.incompatible)
        angDeg = Math.toRadians(angDeg);
        //:: error: (argument.type.incompatible)
        angDeg = Math.toDegrees(angDeg);
        //:: error: (argument.type.incompatible)
        angRad = Math.toRadians(angRad);

        //:: error: (assignment.type.incompatible)
        @Scalar double scalar = Math.toRadians(angDeg);
        //:: error: (assignment.type.incompatible)
        @Scalar double scalar2 = Math.toDegrees(angRad);
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

        // IEEEremainder
        @g double rem = Math.IEEEremainder(g, m);
        @m double rem2 = Math.IEEEremainder(m, g);
        //:: error: (assignment.type.incompatible)
        @km double rem3 = Math.IEEEremainder(m, g);
    }

    void rootTest() {
        @m2 double m2 = 30 * UnitsTools.m2;
        @mm2 double mm2 = 30 * UnitsTools.mm2;
        @km2 double km2 = 30 * UnitsTools.km2;
        @g double grams = 40 * UnitsTools.g;

        // sqrt (square root)
        // for m2, mm2, and km2, it will return m, mm, and km respectively
        // Future TODO: should return the square root of any unit (tracked as a
        // partial unit)
        @m double len1 = Math.sqrt(m2);
        @mm double len2 = Math.sqrt(mm2);
        @km double len3 = Math.sqrt(km2);

        //:: error: (assignment.type.incompatible)
        @km double len4 = Math.sqrt(m2);
        //:: error: (assignment.type.incompatible)
        @m double len5 = Math.sqrt(mm2);
        //:: error: (assignment.type.incompatible)
        @mm double len6 = Math.sqrt(km2);

        // for every other unit, sqrt returns a scalar
        @Scalar double len7 = Math.sqrt(grams);
        //:: error: (assignment.type.incompatible)
        @km double len8 = Math.sqrt(grams);

        // cbrt (cubic root)
        // Future TODO: should return the cubic root of the unit
        @Scalar double cbroot1 = Math.cbrt(grams);
        //:: error: (assignment.type.incompatible)
        @km double cbroot2 = Math.cbrt(grams);
    }

    void ceilFloorTest() {
        @m double m = 30 * UnitsTools.m;
        @s double s = 20 * UnitsTools.s;

        // ceil
        m = Math.ceil(m);
        //:: error: (assignment.type.incompatible)
        s = Math.ceil(m);

        // floor
        m = Math.floor(m);
        //:: error: (assignment.type.incompatible)
        s = Math.floor(m);
    }

    void signTest() {
        @m double mag = 30 * UnitsTools.m;
        @g double sign = 20 * UnitsTools.g;

        @m float magF = 30 * UnitsTools.m;
        @g float signF = 30 * UnitsTools.g;

        // copySign
        // TODO: force the sign to be a Scalar?
        mag = Math.copySign(mag, sign);
        //:: error: (assignment.type.incompatible)
        sign = Math.copySign(mag, sign);

        magF = Math.copySign(magF, signF);
        //:: error: (assignment.type.incompatible)
        signF = Math.copySign(magF, signF);

        // signum
        @Scalar double signum = Math.signum(mag);
        //:: error: (assignment.type.incompatible)
        @m double signum2 = Math.signum(mag);

        @Scalar float signumF = Math.signum(magF);
        //:: error: (assignment.type.incompatible)
        @m float signumF2 = Math.signum(magF);
    }

    void powTest() {
        @m double m = 30 * UnitsTools.m;
        @mm double mm = 30 * UnitsTools.mm;
        @km double km = 40 * UnitsTools.km;
        @g double g = 40 * UnitsTools.g;
        @s double s = 40 * UnitsTools.s;
        @s float f = 60 * UnitsTools.s;

        // pow
        // a complex math expression as the exponent is allowed as long as it can be evaluated down to a single double
        // 1.0 + (5 * (2l - 1)) % (10.0f / 2 - 1) == 2.0
        @m2 double complexMath = Math.pow(m, 1.0 + (5 * (2l - 1)) % (10.0f / 2 - 1));
        // if the expression has any variables, then we cannot evaluate it
        //:: error: (assignment.type.incompatible)
        @m2 double complexMathbad1 = Math.pow(m, 1.0 + (5 * (g - 1)) % (10.0f / 2 - 1));
        //:: error: (assignment.type.incompatible)
        @m3 double complexMathbad2 = Math.pow(m, 1.0 + (5 * (2l - 1)) % (10.0f / 2 - 1));

        // raised to the power of 1 returns the unit itself
        @m double mb = Math.pow(m, 1);
        @mm double mmb = Math.pow(mm, 1l);
        @km double kmb = Math.pow(km, 1.0);
        @g double gb = Math.pow(g, 1.0f);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m, 1);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm, 1l);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km, 1.0);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(g, 1.0f);

        // raised to the power of 2 for m, mm, and km returns m2, mm2, and km2,
        // otherwise UnknownUnits
        @m2 double m2 = Math.pow(m, 2);
        @mm2 double mm2 = Math.pow(mm, 2l);
        @km2 double km2 = Math.pow(km, 2.0);
        double gRaised2 = Math.pow(g, 2.0f);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m, 2);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm, 2l);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km, 2.0);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(g, 2.0f);

        // raised to the power of 3 for m, mm, and km returns m3, mm3, and km3,
        // otherwise UnknownUnits
        @m3 double m3 = Math.pow(m, 3);
        @mm3 double mm3 = Math.pow(mm, 3l);
        @km3 double km3 = Math.pow(km, 3.0);
        double gRaised3 = Math.pow(g, 3.0f);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m, 3);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm, 3l);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km, 3.0);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(g, 3.0f);

        // raised to the power of 0.5 for m2, mm2, and km2 returns m, mm, and
        // km, otherwise UnknownUnits
        m = Math.pow(m2, 0.5);
        mm = Math.pow(mm2, 0.5f);
        km = Math.pow(km2, 1.0 / 2);
        double gRaised05 = Math.pow(g, 0.5);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m2, 0.5);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm2, 0.5f);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km2, 1.0 / 2);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(g, 0.5);

        // raised to the power of 1.0/3 for m3, mm3, and km3 returns m, mm, and
        // km, otherwise UnknownUnits
        m = Math.pow(m3, 1.0 / 3);
        mm = Math.pow(mm3, 1.0f / 3);
        km = Math.pow(km3, 1.0 / 3);
        double gRaised03 = Math.pow(g, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m3, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm3, 1.0f / 3);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km3, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(g, 1.0 / 3);

        // raised to any other power for m, mm, and km returns UnknownUnits
        double u = Math.pow(m, 4);
        u = Math.pow(mm, 4l);
        u = Math.pow(km, 4.0);
        u = Math.pow(m, 4.0f);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m, 4);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(mm, 4l);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(km, 4.0);
        //:: error: (assignment.type.incompatible)
        s = Math.pow(m, 4.0f);

        // passing in a variable as the second argument will default to
        // UnknownUnits as we do not analyze the value of the variable
        double m4 = Math.pow(m, g);
        //:: error: (assignment.type.incompatible)
        @m double m4bad = Math.pow(m, g);
    }

    void exponentsAndLogsTest() {
        @m double m = 30 * UnitsTools.m;
        @s float f = 60 * UnitsTools.s;

        // exp
        @Scalar double e5 = Math.exp(5);
        //:: error: (assignment.type.incompatible)
        @m double e3 = Math.exp(3);

        // expm1
        @Scalar double e5m1 = Math.expm1(5);
        //:: error: (assignment.type.incompatible)
        @m double e3m1 = Math.expm1(3);

        // getExponent
        @Scalar int expoD = Math.getExponent(m);
        @Scalar int expoF = Math.getExponent(f);
        //:: error: (assignment.type.incompatible)
        @m int expoDbad = Math.getExponent(m);
        //:: error: (assignment.type.incompatible)
        @m int expoFbad = Math.getExponent(f);

        // log
        @Scalar double log = Math.log(m);
        //:: error: (assignment.type.incompatible)
        @m double logBad = Math.log(m);

        // log10
        @Scalar double log10 = Math.log10(m);
        //:: error: (assignment.type.incompatible)
        @m double log10Bad = Math.log10(m);

        // log1p
        @Scalar double log1p = Math.log1p(m);
        //:: error: (assignment.type.incompatible)
        @m double log1pBad = Math.log1p(m);

        // log-exp identity
        // identity: exp(log(a)) == a
        // Future TODO: support retainment of units in these identifies
        // Future TODO: this should be a good case if the unit of meters is
        // retained and transfered
        //:: error: (assignment.type.incompatible)
        @m double expLog = Math.exp(Math.log(m));
        // Future TODO: this should be an error case
        @Scalar double expLog2 = Math.exp(Math.log(m));

        // identity: log(exp(x)) == x
        // Future TODO: support retainment of units in these identifies
        // Future TODO: this should be a good case if the unit of meters is
        // retained and transfered
        //:: error: (assignment.type.incompatible)
        @m double logExp = Math.log(Math.exp(m));
        // Future TODO: this should be an error case
        @Scalar double logExp2 = Math.log(Math.exp(m));
    }

    void minMaxTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;
        @C int cI = 20 * UnitsTools.C;
        @K long kL = 30 * UnitsTools.K;

        @mm double mmD = 30.0d * UnitsTools.mm;
        @kg float kgF = 20.0f * UnitsTools.kg;

        @g double gD = 20.0d * UnitsTools.g;
        @m float mF = 30.0f * UnitsTools.m;
        @K int kI = 20 * UnitsTools.K;
        @C long cL = 30 * UnitsTools.C;

        // max
        mD = Math.max(mD, mD);
        gF = Math.max(gF, gF);
        cI = Math.max(cI, cI);
        kL = Math.max(kL, kL);
        //:: error: (assignment.type.incompatible)
        mD = Math.max(mD, gD);
        //:: error: (assignment.type.incompatible)
        gF = Math.max(gF, mF);
        //:: error: (assignment.type.incompatible)
        cI = Math.max(cI, kI);
        //:: error: (assignment.type.incompatible)
        kL = Math.max(kL, cL);

        @Length double len = Math.max(mD, mmD);
        @Mass float mas = Math.max(gF, kgF);
        @Temperature int tem = Math.max(cI, kI);
        @Temperature long tem2 = Math.max(kL, cL);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenBad = Math.max(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masBad = Math.max(gF, kgF);
        //:: error: (assignment.type.incompatible)
        @Scalar int temBad = Math.max(cI, kI);
        //:: error: (assignment.type.incompatible)
        @Scalar long tem2Bad = Math.max(kL, cL);

        // min
        mD = Math.min(mD, mD);
        gF = Math.min(gF, gF);
        cI = Math.min(cI, cI);
        kL = Math.min(kL, kL);
        //:: error: (assignment.type.incompatible)
        mD = Math.min(mD, gD);
        //:: error: (assignment.type.incompatible)
        gF = Math.min(gF, mF);
        //:: error: (assignment.type.incompatible)
        cI = Math.min(cI, kI);
        //:: error: (assignment.type.incompatible)
        kL = Math.min(kL, cL);

        @Length double lenMin = Math.min(mD, mmD);
        @Mass float masMin = Math.min(gF, kgF);
        @Temperature int temMin = Math.min(cI, kI);
        @Temperature long tem2Min = Math.min(kL, cL);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenMinBad = Math.min(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masMinBad = Math.min(gF, kgF);
        //:: error: (assignment.type.incompatible)
        @Scalar int temMinBad = Math.min(cI, kI);
        //:: error: (assignment.type.incompatible)
        @Scalar long tem2MinBad = Math.min(kL, cL);
    }

    void nextMethodsTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        @mm double mmD = 30.0d * UnitsTools.mm;
        @kg float kgF = 20.0f * UnitsTools.kg;

        // nextAfter
        mD = Math.nextAfter(mD, mD);
        gF = Math.nextAfter(gF, gF);
        @Length double len = Math.nextAfter(mD, mmD);
        @Mass float mas = Math.nextAfter(gF, kgF);
        //:: error: (assignment.type.incompatible)
        mmD = Math.nextAfter(mD, mD);
        //:: error: (assignment.type.incompatible)
        kgF = Math.nextAfter(gF, gF);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenBad = Math.nextAfter(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masBad = Math.nextAfter(gF, kgF);

        // nextUp
        mD = Math.nextUp(mD);
        gF = Math.nextUp(gF);
        //:: error: (assignment.type.incompatible)
        mmD = Math.nextUp(mD);
        //:: error: (assignment.type.incompatible)
        kgF = Math.nextUp(gF);
    }

    void randomTest() {
        double x = Math.random();
        @Scalar double y = Math.random();
        //:: error: (assignment.type.incompatible)
        @m double z = Math.random();
    }

    void roundingTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        // rint
        mD = Math.rint(mD);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = Math.rint(mD);

        // round
        @m long mL = Math.round(mD);
        @g int gI = Math.round(gF);
        //:: error: (assignment.type.incompatible)
        @mm long mmL = Math.round(mD);
        //:: error: (assignment.type.incompatible)
        @km long kgI = Math.round(gF);
    }

    void scalbTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        mD = Math.scalb(mD, 5);
        gF = Math.scalb(gF, 2);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = Math.scalb(mD, 9);
        //:: error: (assignment.type.incompatible)
        @kg float kgF = Math.scalb(gF, 1);
    }

    void ulpTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        mD = Math.ulp(mD);
        gF = Math.ulp(gF);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = Math.ulp(mD);
        //:: error: (assignment.type.incompatible)
        @kg float kgF = Math.ulp(gF);
    }
}
