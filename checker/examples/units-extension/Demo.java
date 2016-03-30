import org.checkerframework.checker.units.UnitsTools;
import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.time.duration.s;

import qual.Hz;
import qual.kHz;
import qual.Frequency;

class UnitsExtensionDemo {
    @Hz int frq;

    void bad() {
        // Error! Scalar value assigned to a @Hz value.
        //:: error: (assignment.type.incompatible)
        frq = 5;

        // surpress all warnings issued by the units checker for the d1
        // assignment statement
        @SuppressWarnings("units")
        @Hz int d1 = 9;

        // specifically surpress warnings related to any frequency units for the
        // d2 assigment statement
        @SuppressWarnings("frequency")
        @Hz int d2 = 10;
    }

    // specifically surpresses warnings for the hz annotation for the toHz
    // method
    @SuppressWarnings("hz")
    static @Hz int toHz(int value) {
        return value;
    }

    void good() {
        frq = toHz(9);

        @s double time = 5 * UnitsTools.s;
        @Hz double freq2 = 20 / time;
    }

    void auto(@s int time) {
        // The @Hz annotation is automatically added to the result
        // of the division, as the rule is defined in FrequencyRelations class.
        frq = 99 / time;
    }

    public static void main(String[] args) {
        @Hz int hertz = toHz(20);
        @s int seconds = 5 * UnitsTools.s;
        @s int otherSeconds = 7 * UnitsTools.s;

        @SuppressWarnings("units")
        @s(Prefix.milli) int millisec = 10;

        @SuppressWarnings("hz")
        @kHz int kilohertz = 30;

        @Hz int resultHz = hertz + 20 / seconds;
        System.out.println(resultHz);

        @kHz int resultkHz = kilohertz + 50 / millisec;
        System.out.println(resultkHz);

        // the supertype of Hz and kHz is Frequency, so this statement will pass
        @Frequency int okTernaryAssign = seconds > otherSeconds ? hertz : kilohertz;

        // on the other hand, this statement expects the right hand side to be a
        // Hz, so it will fail
        //:: error: (assignment.type.incompatible)
        @Hz int badTernaryAssign = seconds > otherSeconds ? hertz : kilohertz;
    }
}