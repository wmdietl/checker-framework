import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

public class Scalars {
    @Scalar double s = 20.0d;
    @UnknownUnits double u = 25.0d;
    @m double m = 30.0d * UnitsTools.m;
    @g double g = UnitsTools.g;

    void assignment() {
        // scalar should not be assigned an unknown
        //:: error: (assignment.type.incompatible)
        s = u;

        // scalar should not be assigned a unit
        //:: error: (assignment.type.incompatible)
        s = m;

        // a unit should not be directly assigned a scalar
        //:: error: (assignment.type.incompatible)
        m = s;

        // through unit conversion or multiplication it can take on a unit
        m = s * UnitsTools.m;
        m = s * m;

        // a unit should not be assigned an unknown
        //:: error: (assignment.type.incompatible)
        m = u;

        // unknown can be assigned a unit, (now flow-refined as meter)
        u = m;

        // unknown can be assigned a scalar, (now flow-refined as scalar)
        u = s;

        // scalar / unit = mixed, scalar shold not receive mixed units
        //:: error: (assignment.type.incompatible)
        s = s / m;

        // unit should not receive mixed units
        //:: error: (assignment.type.incompatible)
        m = s / m;
    }

    void multiUnitLUBTest() {
        @m int m = 20 * UnitsTools.m;
        @km int km = 20 * UnitsTools.km;
        @mm int mm = 30 * UnitsTools.mm;
        @m(Prefix.one) int m1 = 20 * UnitsTools.m;
        @m(Prefix.kilo) int mk = 20 * UnitsTools.km;

        // No Prefix ======================
        // super type of m and m is m
        @m int m_m = (m == m? m : m);

        // Single Prefix ==================
        // super type of m & km is Length
        @Length int m_km = (m == m? m : km);
        // in reverse order
        @Length int km_m = (m == m? km : m);

        // Multiple Prefix ================
        // super type of mm and km is Length
        @Length int mm_km = (m == m? mm : km);
        // in reverse order
        @Length int km_mm = (m == m? km : mm);

        // Multiple Prefix with Single Prefix.one =============
        // testing @m(Prefix.one) which is the same as @m
        @Length int m1_mk = (m1 == m1? m1 : mk);
        // in reverse order
        @Length int mk_m1 = (m1 == m1? mk : m1);

        // Double Prefix.one ==============
        @m int m1_m1 = (m1 == m1? m1 : m1);
    }

    void primitiveNumberTypeRefinement() {
        @m double m = 30.0d * UnitsTools.m;

        // basic assignment
        @Scalar double s = 10.0d;

        // in lower case number types, any numerical literal is automatically a Scalar by default,
        // so this line makes variable u a scalar through flow-refinement
        @UnknownUnits double u = 20.0d;

        // scalar plus scalar = scalar, and flow-refine u to a scalar
        u = u + s;

        // now s can accept a scalar as its value
        s = u + s;

        // flow-refine u to a meter
        u = m;

        // now s cannot accept meter
        //:: error: (assignment.type.incompatible)
        s = u;

        // meter + scalar results in mixed
        //:: error: (assignment.type.incompatible)
        m = u + s;

        // flow-refine u to scalar again
        u = s;

        // now s can accept u
        s = u;
    }

    void localFlowRefinementOfFields() {
        // not okay
        // cannot assign an unknown unit into a scalar
        //:: error: (assignment.type.incompatible)
        s = u;

        // flow-refine unknown into scalar
        u = s + s;

        // now okay
        s = u;
    }

    void scalarUnknownAdd() {
        // addition ====================
        // scalar + scalar = scalar
        s = s + s;

        // unknown + unknown = unknown
        u = u + u;

        // unit + unit = unit
        m = m + m;

        // scalar + unit = unknown
        //:: error: (assignment.type.incompatible)
        s = s + m;
        //:: error: (assignment.type.incompatible)
        s = m + s;

        // unknown + unit = unknown
        //:: error: (assignment.type.incompatible)
        m = u + m;
        //:: error: (assignment.type.incompatible)
        m = m + u;

        // scalar + unknown = unknown
        //:: error: (assignment.type.incompatible)
        m = s + u;
        //:: error: (assignment.type.incompatible)
        m = u + s;
    }

    void scalarUnknownSub() {
        // subtraction ====================
        // scalar - scalar = scalar
        s = s - s;

        // unknown - unknown = unknown
        u = u - u;

        // unit - unit = unit
        m = m - m;

        // scalar - unit = unknown
        //:: error: (assignment.type.incompatible)
        s = s - m;

        // unit - scalar = unknown
        //:: error: (assignment.type.incompatible)
        s = m - s;

        // unknown - unit = unknown
        //:: error: (assignment.type.incompatible)
        m = u - m;

        // unit - unknown = unknown
        //:: error: (assignment.type.incompatible)
        m = m - u;

        // scalar - unknown = unknown
        //:: error: (assignment.type.incompatible)
        m = s - u;

        // unknown - scalar = unknown
        //:: error: (assignment.type.incompatible)
        m = u - s;
    }

    void scalarUnknownMul() {
        // multiplication =============
        // scalar * scalar = scalar
        s = s * s;

        // unknown * unknown = unknown
        u = u * u;

        // scalar * unknown = unknown
        u = s * u;
        u = u * s;

        // multiplication of two units that doesn't get handled by relations class produces mixed
        //:: error: (assignment.type.incompatible)
        m = m * m * m * m;

        // scalar * unit = unit
        m = s * m;
        m = m * s;

        // unknown * unit = mixed
        //:: error: (assignment.type.incompatible)
        m = u * m;
        //:: error: (assignment.type.incompatible)
        m = m * u;
    }

    void scalarUnknownDiv() {
        // division =================
        // scalar / scalar = scalar
        s = s / s;

        // unknown / unknown = unknown
        u = u / u;

        // unit / unit = scalar
        s = m / m;

        // division of 2 different units that are not handled by relations class produces mixed
        //:: error: (assignment.type.incompatible)
        s = g / m;

        // unit / scalar = unit
        m = m / s;

        // scalar / unit = (1 / unit) = mixed
        //:: error: (assignment.type.incompatible)
        m = s / m;

        // unit / unknown = mixed
        //:: error: (assignment.type.incompatible)
        m = m / u;

        // unknown / unit = mixed
        //:: error: (assignment.type.incompatible)
        m = u / m;

        // unknown / scalar = unknown
        u = u / s;

        // scalar / unknown = mixed
        //:: error: (assignment.type.incompatible)
        m = s / u;
    }

    void scalarUnknownMod() {
        // remainder ================
        // scalar % scalar = scalar
        s = s % s;

        // unknown % unknown = unknown
        u = u % u;

        // left unit % right unit = left unit
        g = g % m;
        //:: error: (assignment.type.incompatible)
        m = g % m;

        // unit % scalar = unit
        m = m % s;

        // scalar % unit = scalar
        s = s % m;

        // unit % unknown = unit
        m = m % u;

        // unknown % unit = unknown
        u = u % m;

        // scalar % unknown = scalar
        s = s % u;

        // unknown % scalar = unknown
        u = u % s;
    }

    void toScalarTest() {
        // Test wrapper classes
        @m Double x = new @m Double(20.0d);
        @Scalar Double y = UnitsTools.toScalar(x);
        //:: error: (assignment.type.incompatible)
        x = UnitsTools.toScalar(x);

        @g Integer a = new @g Integer(4);
        @Scalar Integer b = UnitsTools.toScalar(a);
        //:: error: (assignment.type.incompatible)
        a = UnitsTools.toScalar(a);

        // Test primitive types
        @kg double d = 20.5d * UnitsTools.kg;
        @Scalar double e = UnitsTools.toScalar(d);
        //:: error: (assignment.type.incompatible)
        d = UnitsTools.toScalar(d);

        @C int i = 50 * UnitsTools.C;
        @Scalar int j = UnitsTools.toScalar(i);
        //:: error: (assignment.type.incompatible)
        i = UnitsTools.toScalar(i);
    }
}