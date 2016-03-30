import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

import java.lang.StrictMath;

public class Java7StrictMath {
    // Constants
    @Scalar double e = StrictMath.E;
    @UnknownUnits double eU = StrictMath.E;
    //:: error: (assignment.type.incompatible)
    @m double eM = StrictMath.E;
    @m double eM2 = StrictMath.E * UnitsTools.m;

    @Scalar double pi = StrictMath.PI;
    @UnknownUnits double piU = StrictMath.PI;
    //:: error: (assignment.type.incompatible)
    @m double piM = StrictMath.PI;
    @m double piM2 = StrictMath.PI * UnitsTools.m;

    void absoluteValueTest(){
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;
        @C int cI = 20 * UnitsTools.C;
        @K long kL = 30 * UnitsTools.K;

        mD = StrictMath.abs(mD);
        gF = StrictMath.abs(gF);
        cI = StrictMath.abs(cI);
        kL = StrictMath.abs(kL);

        //:: error: (assignment.type.incompatible)
        gF = StrictMath.abs((float) mD);
        //:: error: (assignment.type.incompatible)
        mD = StrictMath.abs(gF);
        //:: error: (assignment.type.incompatible)
        kL = StrictMath.abs(cI);
        //:: error: (assignment.type.incompatible)
        cI = StrictMath.abs((int) kL);
    }

    void trigonometryTest() {
        @radians double angle = StrictMath.PI * 20.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        // receiving type is @UnknownUnits
        double sin = StrictMath.sin(angle);
        double cos = StrictMath.cos(angle);
        double tan = StrictMath.tan(angle);

        // receiving type is @Scalar
        @Scalar double sin2 = StrictMath.sin(angle);
        @Scalar double cos2 = StrictMath.cos(angle);
        @Scalar double tan2 = StrictMath.tan(angle);

        //:: error: (assignment.type.incompatible)
        @m double s = StrictMath.sin(angle);
        //:: error: (assignment.type.incompatible)
        @g double c = StrictMath.cos(angle);
        //:: error: (assignment.type.incompatible)
        @C double t = StrictMath.tan(angle);

        //:: error: (argument.type.incompatible)
        @Scalar double sin3 = StrictMath.sin(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double cos3 = StrictMath.cos(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double tan3 = StrictMath.tan(angDeg);

        @m double sides = 15.0d * UnitsTools.m;
        // hypotneuse's unit matches the unit of the sides
        @m double hypot = StrictMath.hypot(sides, sides);
        //:: error: (assignment.type.incompatible)
        @km double hypotBad = StrictMath.hypot(sides, sides);

        @m double hypot2 = StrictMath.hypot(30.0d * UnitsTools.m, 20.0d * UnitsTools.m);
        @km double hypot3 = StrictMath.hypot(30.0d * UnitsTools.km, 20.0d * UnitsTools.km);
        @Length double hypot4 = StrictMath.hypot(30.0d * UnitsTools.m, 20.0d * UnitsTools.km);
        @Length double hypot5 = StrictMath.hypot(30.0d * UnitsTools.mm, 20.0d * UnitsTools.km);
        @Angle double hypot6 = StrictMath.hypot(30.0d * UnitsTools.deg, 20.0d * UnitsTools.rad);
    }

    void inverseTrigTest() {
        @Scalar double constant = StrictMath.PI;
        @m double meter = 20.0d * UnitsTools.m;

        @radians double asin = StrictMath.asin(constant);
        @radians double acos = StrictMath.acos(constant);
        @radians double atan = StrictMath.atan(constant);

        //:: error: (assignment.type.incompatible)
        @degrees double asind = StrictMath.asin(constant);
        //:: error: (assignment.type.incompatible)
        @degrees double acosd = StrictMath.acos(constant);
        //:: error: (assignment.type.incompatible)
        @degrees double atand = StrictMath.atan(constant);

        //:: error: (argument.type.incompatible)
        @radians double asinm = StrictMath.asin(meter);
        //:: error: (argument.type.incompatible)
        @radians double acosm = StrictMath.acos(meter);
        //:: error: (argument.type.incompatible)
        @radians double atanm = StrictMath.atan(meter);

        @radians double atan2 = StrictMath.atan2(constant, constant);
        @radians double atan21 = StrictMath.atan2(meter, meter);

        //:: error: (assignment.type.incompatible)
        @degrees double atan2d = StrictMath.atan2(constant, constant);

        // x and y have to have the same units
        //:: error: (two.parameter.method.arguments.unit.mismatch)
        @radians double atan22 = StrictMath.atan2(meter, constant);
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
        @Scalar double sas = StrictMath.sin(StrictMath.asin(s));
        //:: error: (assignment.type.incompatible)
        @m double sasBad = StrictMath.sin(StrictMath.asin(s));

        // asin(sin(y)) == y
        @radians double ass = StrictMath.asin(StrictMath.sin(r));
        //:: error: (assignment.type.incompatible)
        @m double assBad = StrictMath.asin(StrictMath.sin(r));

        // cos(acos(x)) == x
        @Scalar double cac = StrictMath.cos(StrictMath.acos(s));
        //:: error: (assignment.type.incompatible)
        @m double cacBad = StrictMath.cos(StrictMath.acos(s));

        // acos(cos(y)) == y
        @radians double acc = StrictMath.acos(StrictMath.cos(r));
        //:: error: (assignment.type.incompatible)
        @m double accBad = StrictMath.acos(StrictMath.cos(r));

        // tan(atan(x)) == x
        @Scalar double tat = StrictMath.tan(StrictMath.atan(s));
        //:: error: (assignment.type.incompatible)
        @m double tatBad = StrictMath.tan(StrictMath.atan(s));

        // atan(tan(y)) == y
        @radians double att = StrictMath.atan(StrictMath.tan(r));
        //:: error: (assignment.type.incompatible)
        @m double attBad = StrictMath.atan(StrictMath.tan(r));

        // atan2(y, x) == atan(y / x) except the sign of both arguments are used
        // to determine the quadrant of the result
        // let z = y / x
        // z == tan(atan(z)) == tan(atan2(y, x))
        // result of z = s / s is a scalar
        @Scalar double tat2 = StrictMath.tan(StrictMath.atan2(s, s));
        //:: error: (assignment.type.incompatible)
        @m double tat2Bad = StrictMath.tan(StrictMath.atan2(s, s));
    }

    void hyperbolicTrigTest() {
        @radians double angle = StrictMath.PI * 20.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        // receiving type is @UnknownUnits
        double sinh = StrictMath.sinh(angle);
        double cosh = StrictMath.cosh(angle);
        double tanh = StrictMath.tanh(angle);

        @Scalar double sinh2 = StrictMath.sinh(angle);
        @Scalar double cosh2 = StrictMath.cosh(angle);
        @Scalar double tanh2 = StrictMath.tanh(angle);

        //:: error: (assignment.type.incompatible)
        @m double s = StrictMath.sinh(angle);
        //:: error: (assignment.type.incompatible)
        @g double c = StrictMath.cosh(angle);
        //:: error: (assignment.type.incompatible)
        @C double t = StrictMath.tanh(angle);

        //:: error: (argument.type.incompatible)
        @Scalar double sin3 = StrictMath.sinh(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double cos3 = StrictMath.cosh(angDeg);
        //:: error: (argument.type.incompatible)
        @Scalar double tan3 = StrictMath.tanh(angDeg);
    }

    void angleConversionTest() {
        @radians double angRad = 30.0d * UnitsTools.rad;
        @degrees double angDeg = 20.0d * UnitsTools.deg;

        angDeg = StrictMath.toDegrees(angRad);
        angRad = StrictMath.toRadians(angDeg);

        //:: error: (assignment.type.incompatible)
        angRad = StrictMath.toDegrees(angRad);
        //:: error: (assignment.type.incompatible)
        angDeg = StrictMath.toRadians(angDeg);
        //:: error: (argument.type.incompatible)
        angDeg = StrictMath.toDegrees(angDeg);
        //:: error: (argument.type.incompatible)
        angRad = StrictMath.toRadians(angRad);

        //:: error: (assignment.type.incompatible)
        @Scalar double scalar = StrictMath.toRadians(angDeg);
        //:: error: (assignment.type.incompatible)
        @Scalar double scalar2 = StrictMath.toDegrees(angRad);
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
        @g double rem = StrictMath.IEEEremainder(g, m);
        @m double rem2 = StrictMath.IEEEremainder(m, g);
        //:: error: (assignment.type.incompatible)
        @km double rem3 = StrictMath.IEEEremainder(m, g);
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
        @m double len1 = StrictMath.sqrt(m2);
        @mm double len2 = StrictMath.sqrt(mm2);
        @km double len3 = StrictMath.sqrt(km2);

        //:: error: (assignment.type.incompatible)
        @km double len4 = StrictMath.sqrt(m2);
        //:: error: (assignment.type.incompatible)
        @m double len5 = StrictMath.sqrt(mm2);
        //:: error: (assignment.type.incompatible)
        @mm double len6 = StrictMath.sqrt(km2);

        // for every other unit, sqrt returns a scalar
        @Scalar double len7 = StrictMath.sqrt(grams);
        //:: error: (assignment.type.incompatible)
        @km double len8 = StrictMath.sqrt(grams);

        // cbrt (cubic root)
        // Future TODO: should return the cubic root of the unit
        @Scalar double cbroot1 = StrictMath.cbrt(grams);
        //:: error: (assignment.type.incompatible)
        @km double cbroot2 = StrictMath.cbrt(grams);
    }

    void ceilFloorTest() {
        @m double m = 30 * UnitsTools.m;
        @s double s = 20 * UnitsTools.s;

        // ceil
        m = StrictMath.ceil(m);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.ceil(m);

        // floor
        m = StrictMath.floor(m);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.floor(m);
    }

    void signTest() {
        @m double mag = 30 * UnitsTools.m;
        @g double sign = 20 * UnitsTools.g;

        @m float magF = 30 * UnitsTools.m;
        @g float signF = 30 * UnitsTools.g;

        // copySign
        // TODO: force the sign to be a Scalar?
        mag = StrictMath.copySign(mag, sign);
        //:: error: (assignment.type.incompatible)
        sign = StrictMath.copySign(mag, sign);

        magF = StrictMath.copySign(magF, signF);
        //:: error: (assignment.type.incompatible)
        signF = StrictMath.copySign(magF, signF);

        // signum
        @Scalar double signum = StrictMath.signum(mag);
        //:: error: (assignment.type.incompatible)
        @m double signum2 = StrictMath.signum(mag);

        @Scalar float signumF = StrictMath.signum(magF);
        //:: error: (assignment.type.incompatible)
        @m float signumF2 = StrictMath.signum(magF);
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
        @m2 double complexMath = StrictMath.pow(m, 1.0 + (5 * (2l - 1)) % (10.0f / 2 - 1));
        // if the expression has any variables, then we cannot evaluate it
        //:: error: (assignment.type.incompatible)
        @m2 double complexMathbad1 = StrictMath.pow(m, 1.0 + (5 * (g - 1)) % (10.0f / 2 - 1));
        //:: error: (assignment.type.incompatible)
        @m3 double complexMathbad2 = StrictMath.pow(m, 1.0 + (5 * (2l - 1)) % (10.0f / 2 - 1));

        // raised to the power of 1 returns the unit itself
        @m double mb = StrictMath.pow(m, 1);
        @mm double mmb = StrictMath.pow(mm, 1l);
        @km double kmb = StrictMath.pow(km, 1.0);
        @g double gb = StrictMath.pow(g, 1.0f);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m, 1);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm, 1l);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km, 1.0);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(g, 1.0f);

        // raised to the power of 2 for m, mm, and km returns m2, mm2, and km2,
        // otherwise UnknownUnits
        @m2 double m2 = StrictMath.pow(m, 2);
        @mm2 double mm2 = StrictMath.pow(mm, 2l);
        @km2 double km2 = StrictMath.pow(km, 2.0);
        double gRaised2 = StrictMath.pow(g, 2.0f);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m, 2);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm, 2l);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km, 2.0);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(g, 2.0f);

        // raised to the power of 3 for m, mm, and km returns m3, mm3, and km3,
        // otherwise UnknownUnits
        @m3 double m3 = StrictMath.pow(m, 3);
        @mm3 double mm3 = StrictMath.pow(mm, 3l);
        @km3 double km3 = StrictMath.pow(km, 3.0);
        double gRaised3 = StrictMath.pow(g, 3.0f);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m, 3);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm, 3l);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km, 3.0);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(g, 3.0f);

        // raised to the power of 0.5 for m2, mm2, and km2 returns m, mm, and
        // km, otherwise UnknownUnits
        m = StrictMath.pow(m2, 0.5);
        mm = StrictMath.pow(mm2, 0.5f);
        km = StrictMath.pow(km2, 1.0 / 2);
        double gRaised05 = StrictMath.pow(g, 0.5);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m2, 0.5);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm2, 0.5f);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km2, 1.0 / 2);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(g, 0.5);

        // raised to the power of 1.0/3 for m3, mm3, and km3 returns m, mm, and
        // km, otherwise UnknownUnits
        m = StrictMath.pow(m3, 1.0 / 3);
        mm = StrictMath.pow(mm3, 1.0f / 3);
        km = StrictMath.pow(km3, 1.0 / 3);
        double gRaised03 = StrictMath.pow(g, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m3, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm3, 1.0f / 3);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km3, 1.0 / 3);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(g, 1.0 / 3);

        // raised to any other power for m, mm, and km returns UnknownUnits
        double u = StrictMath.pow(m, 4);
        u = StrictMath.pow(mm, 4l);
        u = StrictMath.pow(km, 4.0);
        u = StrictMath.pow(m, 4.0f);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m, 4);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(mm, 4l);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(km, 4.0);
        //:: error: (assignment.type.incompatible)
        s = StrictMath.pow(m, 4.0f);

        // passing in a variable as the second argument will default to
        // UnknownUnits as we do not analyze the value of the variable
        double m4 = StrictMath.pow(m, g);
        //:: error: (assignment.type.incompatible)
        @m double m4bad = StrictMath.pow(m, g);
    }

    void exponentsAndLogsTest() {
        @m double m = 30 * UnitsTools.m;
        @s float f = 60 * UnitsTools.s;

        // exp
        @Scalar double e5 = StrictMath.exp(5);
        //:: error: (assignment.type.incompatible)
        @m double e3 = StrictMath.exp(3);

        // expm1
        @Scalar double e5m1 = StrictMath.expm1(5);
        //:: error: (assignment.type.incompatible)
        @m double e3m1 = StrictMath.expm1(3);

        // getExponent
        @Scalar int expoD = StrictMath.getExponent(m);
        @Scalar int expoF = StrictMath.getExponent(f);
        //:: error: (assignment.type.incompatible)
        @m int expoDbad = StrictMath.getExponent(m);
        //:: error: (assignment.type.incompatible)
        @m int expoFbad = StrictMath.getExponent(f);

        // log
        @Scalar double log = StrictMath.log(m);
        //:: error: (assignment.type.incompatible)
        @m double logBad = StrictMath.log(m);

        // log10
        @Scalar double log10 = StrictMath.log10(m);
        //:: error: (assignment.type.incompatible)
        @m double log10Bad = StrictMath.log10(m);

        // log1p
        @Scalar double log1p = StrictMath.log1p(m);
        //:: error: (assignment.type.incompatible)
        @m double log1pBad = StrictMath.log1p(m);

        // log-exp identity
        // identity: exp(log(a)) == a
        // Future TODO: support retainment of units in these identifies
        // Future TODO: this should be a good case if the unit of meters is
        // retained and transfered
        //:: error: (assignment.type.incompatible)
        @m double expLog = StrictMath.exp(StrictMath.log(m));
        // Future TODO: this should be an error case
        @Scalar double expLog2 = StrictMath.exp(StrictMath.log(m));

        // identity: log(exp(x)) == x
        // Future TODO: support retainment of units in these identifies
        // Future TODO: this should be a good case if the unit of meters is
        // retained and transfered
        //:: error: (assignment.type.incompatible)
        @m double logExp = StrictMath.log(StrictMath.exp(m));
        // Future TODO: this should be an error case
        @Scalar double logExp2 = StrictMath.log(StrictMath.exp(m));
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
        mD = StrictMath.max(mD, mD);
        gF = StrictMath.max(gF, gF);
        cI = StrictMath.max(cI, cI);
        kL = StrictMath.max(kL, kL);
        //:: error: (assignment.type.incompatible)
        mD = StrictMath.max(mD, gD);
        //:: error: (assignment.type.incompatible)
        gF = StrictMath.max(gF, mF);
        //:: error: (assignment.type.incompatible)
        cI = StrictMath.max(cI, kI);
        //:: error: (assignment.type.incompatible)
        kL = StrictMath.max(kL, cL);

        @Length double len = StrictMath.max(mD, mmD);
        @Mass float mas = StrictMath.max(gF, kgF);
        @Temperature int tem = StrictMath.max(cI, kI);
        @Temperature long tem2 = StrictMath.max(kL, cL);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenBad = StrictMath.max(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masBad = StrictMath.max(gF, kgF);
        //:: error: (assignment.type.incompatible)
        @Scalar int temBad = StrictMath.max(cI, kI);
        //:: error: (assignment.type.incompatible)
        @Scalar long tem2Bad = StrictMath.max(kL, cL);

        // min
        mD = StrictMath.min(mD, mD);
        gF = StrictMath.min(gF, gF);
        cI = StrictMath.min(cI, cI);
        kL = StrictMath.min(kL, kL);
        //:: error: (assignment.type.incompatible)
        mD = StrictMath.min(mD, gD);
        //:: error: (assignment.type.incompatible)
        gF = StrictMath.min(gF, mF);
        //:: error: (assignment.type.incompatible)
        cI = StrictMath.min(cI, kI);
        //:: error: (assignment.type.incompatible)
        kL = StrictMath.min(kL, cL);

        @Length double lenMin = StrictMath.min(mD, mmD);
        @Mass float masMin = StrictMath.min(gF, kgF);
        @Temperature int temMin = StrictMath.min(cI, kI);
        @Temperature long tem2Min = StrictMath.min(kL, cL);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenMinBad = StrictMath.min(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masMinBad = StrictMath.min(gF, kgF);
        //:: error: (assignment.type.incompatible)
        @Scalar int temMinBad = StrictMath.min(cI, kI);
        //:: error: (assignment.type.incompatible)
        @Scalar long tem2MinBad = StrictMath.min(kL, cL);
    }

    void nextMethodsTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        @mm double mmD = 30.0d * UnitsTools.mm;
        @kg float kgF = 20.0f * UnitsTools.kg;

        // nextAfter
        mD = StrictMath.nextAfter(mD, mD);
        gF = StrictMath.nextAfter(gF, gF);
        @Length double len = StrictMath.nextAfter(mD, mmD);
        @Mass float mas = StrictMath.nextAfter(gF, kgF);
        //:: error: (assignment.type.incompatible)
        mmD = StrictMath.nextAfter(mD, mD);
        //:: error: (assignment.type.incompatible)
        kgF = StrictMath.nextAfter(gF, gF);
        //:: error: (assignment.type.incompatible)
        @Scalar double lenBad = StrictMath.nextAfter(mD, mmD);
        //:: error: (assignment.type.incompatible)
        @Scalar float masBad = StrictMath.nextAfter(gF, kgF);

        // nextUp
        mD = StrictMath.nextUp(mD);
        gF = StrictMath.nextUp(gF);
        //:: error: (assignment.type.incompatible)
        mmD = StrictMath.nextUp(mD);
        //:: error: (assignment.type.incompatible)
        kgF = StrictMath.nextUp(gF);
    }

    void randomTest() {
        double x = StrictMath.random();
        @Scalar double y = StrictMath.random();
        //:: error: (assignment.type.incompatible)
        @m double z = StrictMath.random();
    }

    void roundingTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        // rint
        mD = StrictMath.rint(mD);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = StrictMath.rint(mD);

        // round
        @m long mL = StrictMath.round(mD);
        @g int gI = StrictMath.round(gF);
        //:: error: (assignment.type.incompatible)
        @mm long mmL = StrictMath.round(mD);
        //:: error: (assignment.type.incompatible)
        @km long kgI = StrictMath.round(gF);
    }

    void scalbTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        mD = StrictMath.scalb(mD, 5);
        gF = StrictMath.scalb(gF, 2);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = StrictMath.scalb(mD, 9);
        //:: error: (assignment.type.incompatible)
        @kg float kgF = StrictMath.scalb(gF, 1);
    }

    void ulpTest() {
        @m double mD = 20.0d * UnitsTools.m;
        @g float gF = 30.0f * UnitsTools.g;

        mD = StrictMath.ulp(mD);
        gF = StrictMath.ulp(gF);
        //:: error: (assignment.type.incompatible)
        @mm double mmD = StrictMath.ulp(mD);
        //:: error: (assignment.type.incompatible)
        @kg float kgF = StrictMath.ulp(gF);
    }
}
