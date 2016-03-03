import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.instant.*;
import org.checkerframework.checker.units.UnitsTools;

public class ParamReturn {
    @m int meter = 20 * UnitsTools.m;
    @s int second = 30 * UnitsTools.s;

    void voidReturn() {
        return;
    }

    Object nullObjReturn() {
        return null;
    }

    Object objReturn() {
        return new Object();
    }

    int intScalarReturn() {
        return 5;
    }

    int intObjReturn() {
        return new Integer(5);
    }

    int[] intArrReturn() {
        return new int[5];
    }

    int paramNoRefineReturn(int x) {
        return x;
    }

    int paramRefinedReturn(int x) {
        // expected to fail, can't pass a meter out unless return type is annotated
        //:: error: (return.type.incompatible)
        return x * meter;
    }

    @m int paramRefinedReturnMeter(int x) {
        // expected to pass
        return x * meter;
    }

    @m int paramRefinedReturnMeterBad(int x) {
        // expected to fail as we are returning a second where a meter is expected
        //:: error: (return.type.incompatible)
        return x * second;
    }

    @Scalar int paramIsScalar(int x) {
        return x + x;
    }

    void methodAcceptingScalarObjects(Integer x) {
        return;
    }

    void methodAcceptingMeterObjects(@m Integer x) {
        return;
    }

    void methodCalls() {
        // assigning meter into unknown, x becomes meter // pass
        int x = paramRefinedReturnMeter(5) + meter;
        // assigning unknown into scalar should fail
        //:: error: (assignment.type.incompatible)
        @Scalar int xBad = paramRefinedReturnMeter(5) + second;
        // assigning unknown into scalar should fail
        //:: error: (assignment.type.incompatible)
        @Scalar int y = paramRefinedReturnMeter(5) + meter;

        // pass
        @Scalar int a = paramNoRefineReturn(5);
        // pass
        @Scalar int b = paramRefinedReturn(5);


        int unknownPrimitiveInt = (@UnknownUnits int) 5;
        //:: error: (argument.type.incompatible)
        b = paramRefinedReturn(unknownPrimitiveInt);

        // Classes, and thus local objects by default are Scalar
        Integer unknownInteger = new @UnknownUnits Integer(5);
        // default receiver is Scalar, but we allow it to be invoked on UnknownUnits receivers in ATF
        unknownInteger.toString();

        methodAcceptingScalarObjects(unknownInteger);
        methodAcceptingMeterObjects(new @m Integer(30));
        //:: error: (argument.type.incompatible)
        methodAcceptingMeterObjects(unknownInteger);

        Integer scalarInteger = new Integer(30);
        scalarInteger.toString();
        //:: error: (argument.type.incompatible)
        methodAcceptingMeterObjects(scalarInteger);


    }

    @Scalar Object methodReturningUnknownObject() {
        Object x = new @UnknownUnits Object();
        //:: error: (return.type.incompatible)
        return x;
    }

    void foreachLoopIndexComparison() {
        int [] x = new int[5];
        for(int i : x);
    }
}