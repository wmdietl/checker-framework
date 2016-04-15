import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

import java.util.List;
import java.util.LinkedList;

class UnitsGenericClassesAndMethods {

    // =======================
    // Generic Classes
    // =======================

    void referencesTest() {
        // local references are by default @UnknownUnits
        Number defaultRef = new Integer(5);
        Number defaultRef1 = new @UnknownUnits Integer(5);
        Number defaultRef2 = new @m Integer(5);

        @Scalar Number scalarRef = new Integer(5);
        //:: error: (assignment.type.incompatible)
        @Scalar Number scalarRef1 = new @UnknownUnits Integer(5);
        //:: error: (assignment.type.incompatible)
        @Scalar Number scalarRef2 = new @m Integer(5);
    }

    void listTest() {
        // Java Collections classes have been annotated with an upperbound of
        // @UnknownUnits, thus it can be instantiated with any unit
        List<Number> scalarList = new LinkedList<Number>();
        scalarList.add(new Integer(5));
        //:: error: (argument.type.incompatible)
        scalarList.add(new @m Integer(5));
        //:: error: (argument.type.incompatible)
        scalarList.add(new @UnknownUnits Integer(5));

        List<@UnknownUnits Number> unknownList = new LinkedList<Number>();
        unknownList.add(new Integer(5));
        unknownList.add(new @m Integer(5));

        List<@Length Number> lengthList = new LinkedList<@Length Number>();
        lengthList.add(new @m Integer(5));
        //:: error: (argument.type.incompatible)
        lengthList.add(new Integer(5));
        //:: error: (argument.type.incompatible)
        lengthList.add(new @s Integer(5));
    }

    // The default implicit upperbound is @Scalar, thus this class can only
    // be instantiated with Scalar type arguments
    class MyScalarList<T> {
        public MyScalarList() {
        }

        void add(T value) {
        }
    }

    void myScalarListTest() {
        MyScalarList<Number> list = new MyScalarList<Number>();
        MyScalarList<@Scalar Number> list2 = new MyScalarList<@Scalar Number>();
        //:: error: (type.argument.type.incompatible)
        MyScalarList<@UnknownUnits Number> list3 = new MyScalarList<@UnknownUnits Number>();
        //:: error: (type.argument.type.incompatible)
        MyScalarList<@m Number> list4 = new MyScalarList<@m Number>();

        list.add(new Integer(5));
        list.add(new @Scalar Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @UnknownUnits Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @m Integer(5));

        list2.add(new Integer(5));
        list2.add(new @Scalar Integer(5));
        //:: error: (argument.type.incompatible)
        list2.add(new @UnknownUnits Integer(5));
        //:: error: (argument.type.incompatible)
        list2.add(new @m Integer(5));
    }

    // The default explicit upperbound is also @Scalar
    class MyScalarList2<T extends Object> {
        public MyScalarList2() {
        }

        void add(T value) {
        }
    }

    void myScalarList2Test() {
        MyScalarList2<Number> list = new MyScalarList2<Number>();
        MyScalarList2<@Scalar Number> list2 = new MyScalarList2<@Scalar Number>();
        //:: error: (type.argument.type.incompatible)
        MyScalarList2<@UnknownUnits Number> list3 = new MyScalarList2<@UnknownUnits Number>();
        //:: error: (type.argument.type.incompatible)
        MyScalarList2<@m Number> list4 = new MyScalarList2<@m Number>();

        list.add(new Integer(5));
        list.add(new @Scalar Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @UnknownUnits Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @m Integer(5));

        list2.add(new Integer(5));
        list2.add(new @Scalar Integer(5));
        //:: error: (argument.type.incompatible)
        list2.add(new @UnknownUnits Integer(5));
        //:: error: (argument.type.incompatible)
        list2.add(new @m Integer(5));
    }

    // Custom generic classes must be explicitly annotated with an upperbound
    // unit in order to make it useable with other units in the units checker
    class MyList<T extends @UnknownUnits Object> {
        public MyList() {
        }

        void add(T value) {
        }
    }

    void myListTest() {
        MyList<@Length Number> list = new MyList<@Length Number>();
        list.add(new @m Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @s Integer(5));
        //:: error: (argument.type.incompatible)
        list.add(new @UnknownUnits Integer(5));
    }

    // Custom generic classes can be explicitly annotated for a category of
    // units, making it useable with only that category of units, and forbidding
    // its use with other incompatible units
    class A<T extends @Length Object> {
        public A() {
        }

        T method(T input) {
            return input;
        }
    }

    void classATest() {
        A<@m(Prefix.nano) Number> a = new A<@m(Prefix.nano) Number>();
        a.method(new @m(Prefix.nano) Integer(5));
        //:: error: (argument.type.incompatible)
        a.method(new @UnknownUnits Integer(5));

        //:: error: (type.argument.type.incompatible)
        A<@s Number> b = new A<@s Number>();
    }

    // =======================
    // Generic Methods
    // =======================

    // Fields are by default Scalar
    Object x = null;
    Number y = null;
    @UnknownUnits Object uu = new @UnknownUnits Object();
    @m Integer m = new @m Integer(5);
    @s Integer s = new @s Integer(5);

    // the default implicit upperbound of method type arguments is @Scalar
    // lowerbound is defaulted to UnitsBottom, all units are accepted unless
    // the type instantiated for T from this.<T> clashes with the input's type
    <T> T defaultImplicitExtendsBound(T input) {
        return input;
    }

    void defaultImplicitExtendsBoundTest() {
        // the only arugments that can be accepted are the ones which have
        // @Scalar or its subtype @UnitsBottom as its unit
        x = this.defaultImplicitExtendsBound(x);
        y = this.defaultImplicitExtendsBound(y);
        //:: error: (type.argument.type.incompatible)
        this.defaultImplicitExtendsBound(uu);
        //:: error: (type.argument.type.incompatible)
        this.defaultImplicitExtendsBound(m);

        // if the method was invoked with an explicit type argument, the type
        // argument must be a subtype of Scalar as well
        this.<Object> defaultImplicitExtendsBound(x);
        //:: error: (type.argument.type.incompatible)
        this.<@UnknownUnits Object> defaultImplicitExtendsBound(x);
        // because uu is UnknownUnits, declaring T explicitly as Object causes
        // argument type incompatible error since w is an UnknownUnits Object,
        // and Scalar is the default type of Classes including Object
        //:: error: (argument.type.incompatible)
        this.<Object> defaultImplicitExtendsBound(uu);
        // explicitly declaring T as Scalar Integer causes argument type
        // incompatible error as m is a Meter Integer
        //:: error: (argument.type.incompatible)
        this.<Integer> defaultImplicitExtendsBound(m);

        // @UnitsBottom (for null) is a subtype of @m, but @m is not a subtype
        // of @Scalar
        //:: error: (type.argument.type.incompatible)
        this.<@m Object> defaultImplicitExtendsBound(null);
    }

    // the default explicit upperbound of method type arguments is @Scalar as
    // well lowerbound is defaulted to UnitsBottom, all units are accepted
    // unless the type instantiated for T from this.<T> clashes with the input's
    // type
    <T extends Object> T defaultExplicitExtendsBound(T input) {
        return input;
    }

    void defaultExplicitExtendsBoundTest() {
        this.defaultExplicitExtendsBound(x);
        this.defaultExplicitExtendsBound(y);
        //:: error: (type.argument.type.incompatible)
        this.defaultExplicitExtendsBound(uu);
        //:: error: (type.argument.type.incompatible)
        this.defaultExplicitExtendsBound(m);

        this.<Object> defaultImplicitExtendsBound(x);
        //:: error: (type.argument.type.incompatible)
        this.<@UnknownUnits Object> defaultImplicitExtendsBound(x);
        //:: error: (argument.type.incompatible)
        this.<Object> defaultImplicitExtendsBound(uu);
        //:: error: (argument.type.incompatible)
        this.<Integer> defaultImplicitExtendsBound(m);
        //:: error: (type.argument.type.incompatible)
        this.<@m Object> defaultImplicitExtendsBound(null);
    }

    // method type arguments must also be declared with an explicit extends
    // bound unit to make it usable with other units in the units checker
    // lowerbound is defaulted to UnitsBottom, upperbound is declared as
    // UnknownUnits. all units are accepted unless the type instantiated
    // for T from this.<T> clashes with the input's type
    <T extends @UnknownUnits Object> T declaredExplicitExtendsBound(T input) {
        return input;
    }

    void declaredExplicitExtendsBoundTest() {
        // implicit type invocation
        uu = declaredExplicitExtendsBound(uu);
        x = declaredExplicitExtendsBound(x);
        y = declaredExplicitExtendsBound(y);
        m = declaredExplicitExtendsBound(m);
        s = declaredExplicitExtendsBound(s);

        // explicit type invocation
        this.<@UnknownUnits Object> declaredExplicitExtendsBound(uu);

        // because uu is UnknownUnits, declaring T explicitly as Object causes
        // argument type incompatible error since uu is an UnknownUnits Object,
        // and Scalar is the default type of classes including Object
        //:: error: (argument.type.incompatible)
        this.<Object> declaredExplicitExtendsBound(uu);

        // x is Scalar Object, no problem here
        this.<Object> declaredExplicitExtendsBound(x);

        // y is Scalar Number, also no problem
        this.<Number> declaredExplicitExtendsBound(y);

        // explicitly declaring T as Scalar Integer causes argument type
        // incompatible error as m is a Meter Integer
        //:: error: (argument.type.incompatible)
        this.<Integer> declaredExplicitExtendsBound(m);

        // bottom is a subtype of m
        this.<@m Object> declaredExplicitExtendsBound(null);

        this.<@m Integer> declaredExplicitExtendsBound(m);
        // meter is a subtype of length
        this.<@Length Integer> declaredExplicitExtendsBound(m);

        // meter is not a subtype of scalar
        //:: error: (argument.type.incompatible)
        this.<Integer> declaredExplicitExtendsBound(m);
    }

    // lowerbound is defaulted to UnitsBottom, upperbound is declared as Length.
    // all subtypes of Length are accepted unless the type instantiated for T
    // from this.<T> clashes with the input's type
    <T extends @Length Object> T declaredExplicitLengthUpperBound(T input) {
        return input;
    }

    void declaredExplicitLengthUpperBoundTest() {
        // implicit type invocation
        // T = UnknownUnits Object from argument w, and it is not a subtype of
        // Length
        //:: error: (type.argument.type.incompatible)
        uu = declaredExplicitLengthUpperBound(uu);
        declaredExplicitLengthUpperBound(null);

        m = declaredExplicitLengthUpperBound(m);
        //:: error: (type.argument.type.incompatible)
        declaredExplicitLengthUpperBound(s);

        // explicit type invocation
        // the explicitly declared T has to be a subtype of Length
        //:: error: (type.argument.type.incompatible)
        this.<@UnknownUnits Object> declaredExplicitLengthUpperBound(uu);

        // w is UnknownUnits Object, which is not a subtype of Length
        //:: error: (argument.type.incompatible)
        this.<@Length Object> declaredExplicitLengthUpperBound(uu);

        // T = Scalar Object is not a subtype of Length Object
        //:: error: (type.argument.type.incompatible)
        this.<Object> declaredExplicitLengthUpperBound(x);

        // UnitsBottom is a subtype of Length
        this.<@Length Object> declaredExplicitLengthUpperBound(null);

        // bottom is a subtype of m
        this.<@m Object> declaredExplicitLengthUpperBound(null);

        this.<@m Integer> declaredExplicitLengthUpperBound(m);
        // meter is a subtype of length
        this.<@Length Integer> declaredExplicitLengthUpperBound(m);
    }

    // cannot declare the lower bound of a method type argument to any unit
    // unless it is a subtype of the upperbound, which is by default scalar
    //:: error: (bound.type.incompatible)
    <@m T> T meterLowerBoundBad(T input) {
        return input;
    }

    // to declare the lower bound, the upperbound must also be declared
    // lower bound is declared to be meters, accepted units of T are Unknown,
    // Length and Meter
    <@m T extends @UnknownUnits Object> T meterLowerBound(T input) {
        return input;
    }

    // lower bound is declared to be length, accepted units of T are Unknown,
    // Length
    <@Length T extends @UnknownUnits Object> T lengthLowerBound(T input) {
        return input;
    }

    void lowerBoundsTest() {
        @m Integer meter = new @m Integer(5);
        @Length Integer length = new @Length Integer(5);
        @s Integer second = new @s Integer(5);

        // implicit type invocation
        meterLowerBound(meter);
        meterLowerBound(null);
        meterLowerBound(length);
        // type parameter unit second is not a supertype of meter
        //:: error: (type.argument.type.incompatible)
        meterLowerBound(second);

        meter = meterLowerBound(meter);
        meter = meterLowerBound(new @UnitsBottom Integer(5));
        meter = meterLowerBound(null);

        length = meterLowerBound(length);

        // meter sets the type of T, and seconds is not a subtype of meter
        //:: error: (assignment.type.incompatible)
        meter = meterLowerBound(second);
        // type parameter unit second is not a supertype of meter
        //:: error: (type.argument.type.incompatible)
        second = meterLowerBound(second);
        // scalar is also not a supertype of meter
        //:: error: (type.argument.type.incompatible)
        x = meterLowerBound(x);

        uu = meterLowerBound(uu);
        // TODO: should be error
        uu = meterLowerBound(null);

        // explicit type invocation
        // all types involved are precisely @m
        this.<@m Object> meterLowerBound(meter);
        // this passes because @Length Object is a supertype of @m T and bottom
        // is a subtype of @Length
        this.<@Length Object> meterLowerBound(null);
        this.<@Length Object> meterLowerBound(length);
        // same for UnknownUnits
        this.<@UnknownUnits Object> meterLowerBound(null);
        this.<@UnknownUnits Object> meterLowerBound(length);
        this.<@UnknownUnits Object> meterLowerBound(meter);
        // this passes because @Unknown Object overrides @m T, and @s is a
        // subtype of @Unknown
        this.<@UnknownUnits Object> meterLowerBound(second);
        // while bottom is a subtype of Speed, Speed is not a supertype of m
        //:: error: (type.argument.type.incompatible)
        this.<@Speed Object> meterLowerBound(null);

        this.<@Length Object> lengthLowerBound(meter);
        // second is not a subtype of length
        //:: error: (argument.type.incompatible)
        this.<@Length Object> lengthLowerBound(second);
        // meter is not a supertype of length
        //:: error: (type.argument.type.incompatible)
        this.<@m Object> lengthLowerBound(meter);
        // scalar is not a supertype of length
        //:: error: (type.argument.type.incompatible)
        this.<Object> meterLowerBound(null);
    }

    // both the lower bound and the upperbound can be set to specific units
    // lowerbound is declared to Meters, upperbound is declared as Length.
    // T must be either Meters or Length and the type instantiated for T
    // from this.<T> must not clashes with the input's type
    <@m T extends @Length Object> T declaredExplicitBounds(T input) {
        return input;
    }

    //:: error: (bound.type.incompatible)
    <@Length T extends @m Object> T declaredExplicitBoundsBAD(T input) {
        return input;
    }

    void declaredExplicitBoundsTest() {
        // implicit type invocation
        // T = UnknownUnits Object from argument w, and it is not a subtype of
        // Length
        //:: error: (type.argument.type.incompatible)
        uu = declaredExplicitBounds(uu);
        // Scalar is not a subtype of Length
        //:: error: (type.argument.type.incompatible)
        x = declaredExplicitBounds(x);

        m = declaredExplicitBounds(m);

        // explicit type invocation
        // the explicitly declared T has to be a subtype of Length
        //:: error: (type.argument.type.incompatible)
        this.<@UnknownUnits Object> declaredExplicitBounds(uu);

        // the explicitly declared T has to be a supertype of Meter
        //:: error: (type.argument.type.incompatible)
        this.<@UnitsBottom Object> declaredExplicitBounds(null);

        this.<@m Object> declaredExplicitBounds(null);
        // TODO: should be error
        this.declaredExplicitBounds(null);

        this.<@m Integer> declaredExplicitBounds(m);
        // meter is a subtype of length
        this.<@Length Integer> declaredExplicitBounds(m);
    }
}
