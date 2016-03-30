import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

public class CompoundAssignment {
    @UnknownUnits int unknown = (@UnknownUnits int) 5;
    int scalar = 40;
    @m int meter = 20 * UnitsTools.m;
    @s int second = 30 * UnitsTools.s;

    @UnknownUnits Integer unknownBox = new @UnknownUnits Integer(5);
    Integer scalarBox = new Integer(5);
    @m Integer meterBox = new @m Integer(5);
    @s Integer secondBox = new @s Integer(5);

    void plusAssign() {
        // unknown
        unknown += unknown;
        unknown += scalar;
        unknown += meter;
        unknown += second;

        unknownBox += unknownBox;
        unknownBox += scalarBox;
        unknownBox += meterBox;
        unknownBox += secondBox;

        // scalar
        //:: error: (compound.assignment.type.incompatible)
        scalar += unknown;
        scalar += 5;
        scalar += scalar;
        //:: error: (compound.assignment.type.incompatible)
        scalar += meter;
        //:: error: (compound.assignment.type.incompatible)
        scalar += second;

        //:: error: (compound.assignment.type.incompatible)
        scalarBox += unknownBox;
        scalarBox += 5;
        scalarBox += scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox += meterBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox += secondBox;

        // unit
        //:: error: (compound.assignment.type.incompatible)
        meter += unknown;
        //:: error: (compound.assignment.type.incompatible)
        meter += 5;
        //:: error: (compound.assignment.type.incompatible)
        meter += scalar;
        meter += meter;
        //:: error: (compound.assignment.type.incompatible)
        meter += second;

        //:: error: (compound.assignment.type.incompatible)
        meterBox += unknownBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox += 5;
        //:: error: (compound.assignment.type.incompatible)
        meterBox += scalarBox;
        meterBox += meterBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox += secondBox;

        meterBox += (meterBox += meterBox);
        //:: error: (compound.assignment.type.incompatible)
        meterBox += (meterBox += secondBox);
    }

    void minusAssign() {
        // unknown
        unknown -= unknown;
        unknown -= scalar;
        unknown -= meter;
        unknown -= second;

        unknownBox -= unknownBox;
        unknownBox -= scalarBox;
        unknownBox -= meterBox;
        unknownBox -= secondBox;

        // scalar
        //:: error: (compound.assignment.type.incompatible)
        scalar -= unknown;
        scalar -= 5;
        scalar -= scalar;
        //:: error: (compound.assignment.type.incompatible)
        scalar -= meter;
        //:: error: (compound.assignment.type.incompatible)
        scalar -= second;

        //:: error: (compound.assignment.type.incompatible)
        scalarBox -= unknownBox;
        scalarBox -= 5;
        scalarBox -= scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox -= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox -= secondBox;

        // unit
        //:: error: (compound.assignment.type.incompatible)
        meter -= unknown;
        //:: error: (compound.assignment.type.incompatible)
        meter -= 5;
        //:: error: (compound.assignment.type.incompatible)
        meter -= scalar;
        meter -= meter;
        //:: error: (compound.assignment.type.incompatible)
        meter -= second;

        //:: error: (compound.assignment.type.incompatible)
        meterBox -= unknownBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox -= 5;
        //:: error: (compound.assignment.type.incompatible)
        meterBox -= scalarBox;
        meterBox -= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox -= secondBox;

        meterBox -= (meterBox -= meterBox);
        //:: error: (compound.assignment.type.incompatible)
        meterBox -= (meterBox -= secondBox);
    }

    void multiplyAssign() {
        // unknown
        unknown *= unknown;
        unknown *= scalar;
        unknown *= meter;
        unknown *= second;

        unknownBox *= unknownBox;
        unknownBox *= scalarBox;
        unknownBox *= meterBox;
        unknownBox *= secondBox;

        // scalar
        //:: error: (compound.assignment.type.incompatible)
        scalar *= unknown;
        scalar *= 5;
        scalar *= scalar;
        //:: error: (compound.assignment.type.incompatible)
        scalar *= meter;
        //:: error: (compound.assignment.type.incompatible)
        scalar *= second;

        //:: error: (compound.assignment.type.incompatible)
        scalarBox *= unknownBox;
        scalarBox *= 5;
        scalarBox *= scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox *= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox *= secondBox;

        // unit
        //:: error: (compound.assignment.type.incompatible)
        meter *= unknown;
        meter *= 5;
        meter *= scalar;
        //:: error: (compound.assignment.type.incompatible)
        meter *= meter;
        //:: error: (compound.assignment.type.incompatible)
        meter *= second;

        //:: error: (compound.assignment.type.incompatible)
        meterBox *= unknownBox;
        meterBox *= 5;
        meterBox *= scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox *= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox *= secondBox;

        //:: error: (compound.assignment.type.incompatible)
        meterBox *= (meterBox *= scalarBox);
        //:: error: (compound.assignment.type.incompatible)
        meterBox *= (meterBox *= secondBox);
    }

    void divideAssign() {
        // unknown
        unknown /= unknown;
        unknown /= scalar;
        unknown /= meter;
        unknown /= second;

        unknownBox /= unknownBox;
        unknownBox /= scalarBox;
        unknownBox /= meterBox;
        unknownBox /= secondBox;

        // scalar
        //:: error: (compound.assignment.type.incompatible)
        scalar /= unknown;
        scalar /= 5;
        scalar /= scalar;
        //:: error: (compound.assignment.type.incompatible)
        scalar /= meter;
        //:: error: (compound.assignment.type.incompatible)
        scalar /= second;

        //:: error: (compound.assignment.type.incompatible)
        scalarBox /= unknownBox;
        scalarBox /= 5;
        scalarBox /= scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox /= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        scalarBox /= secondBox;

        // unit
        //:: error: (compound.assignment.type.incompatible)
        meter /= unknown;
        meter /= 5;
        meter /= scalar;
        //:: error: (compound.assignment.type.incompatible)
        meter /= meter;
        //:: error: (compound.assignment.type.incompatible)
        meter /= second;

        //:: error: (compound.assignment.type.incompatible)
        meterBox /= unknownBox;
        meterBox /= 5;
        meterBox /= scalarBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox /= meterBox;
        //:: error: (compound.assignment.type.incompatible)
        meterBox /= secondBox;

        //:: error: (compound.assignment.type.incompatible)
        meterBox /= (meterBox /= scalarBox);
        //:: error: (compound.assignment.type.incompatible)
        meterBox /= (meterBox /= secondBox);
    }

    void remainderAssign() {
        // unknown
        unknown %= unknown;
        unknown %= scalar;
        unknown %= meter;
        unknown %= second;

        unknownBox %= unknownBox;
        unknownBox %= scalarBox;
        unknownBox %= meterBox;
        unknownBox %= secondBox;

        // scalar
        scalar %= unknown;
        scalar %= 5;
        scalar %= scalar;
        scalar %= meter;
        scalar %= second;

        scalarBox %= unknownBox;
        scalarBox %= 5;
        scalarBox %= scalarBox;
        scalarBox %= meterBox;
        scalarBox %= secondBox;

        // unit
        meter %= unknown;
        meter %= 5;
        meter %= scalar;
        meter %= meter;
        meter %= second;

        meterBox %= unknownBox;
        meterBox %= 5;
        meterBox %= scalarBox;
        meterBox %= meterBox;
        meterBox %= secondBox;

        meterBox %= (meterBox %= meterBox);
        meterBox %= (meterBox %= secondBox);
    }
}
