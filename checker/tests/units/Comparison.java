import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.*;

public class Comparison {
    @m int meter = 20 * UnitsTools.m;
    @s int second = 30 * UnitsTools.s;

    void basicComparison() {
        if(meter == meter);
        if(meter != meter);
        if(meter > meter);
        if(meter >= meter);
        if(meter < meter);
        if(meter <= meter);

        // comparisons can only be performed on operands that have matching units
        //:: error: (operands.unit.mismatch)
        if(meter == second);
        //:: error: (operands.unit.mismatch)
        if(meter != second);
        //:: error: (operands.unit.mismatch)
        if(meter > second);
        //:: error: (operands.unit.mismatch)
        if(meter >= second);
        //:: error: (operands.unit.mismatch)
        if(meter < second);
        //:: error: (operands.unit.mismatch)
        if(meter <= second);
    }

    void undeclaredComparison(int x, int y) {
        // comparison of two Scalar variables
        if(x == y);
        if(x != y);
        if(x > y);
        if(x >= y);
        if(x < y);
        if(x <= y);

        // comparison of Scalar variable to Scalar constant
        if(x == 30);
        if(x != 30);
        if(x > 30);
        if(x >= 30);
        if(x < 30);
        if(x <= 30);
    }

    void ternaryComparison() {
        @m double x;

        x = meter == meter ? meter : meter;
        x = meter != meter ? meter : meter;
        x = meter > meter ? meter : meter;
        x = meter >= meter ? meter : meter;
        x = meter < meter ? meter : meter;
        x = meter <= meter ? meter : meter;

        //:: error: (operands.unit.mismatch)
        x = meter == second ? meter : meter;
        //:: error: (operands.unit.mismatch)
        x = meter != second ? meter : meter;
        //:: error: (operands.unit.mismatch)
        x = meter > second ? meter : meter;
        //:: error: (operands.unit.mismatch)
        x = meter >= second ? meter : meter;
        //:: error: (operands.unit.mismatch)
        x = meter < second ? meter : meter;
        //:: error: (operands.unit.mismatch)
        x = meter <= second ? meter : meter;
    }

    void foreachLoopIndexComparison() {
        int [] x = new int[5];
        for(int i : x);
    }
}