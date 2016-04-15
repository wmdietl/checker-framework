import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

public class Strings {
    void scalarString() {
        @Scalar String s = "word";
        @m int x = 10 * UnitsTools.m;
        @s long y = 20l * UnitsTools.s;
        @C float a = 30.0f * UnitsTools.C;
        @K double b = 40.0d * UnitsTools.K;

        s = s + s;
        s += s;

        s = s + x;
        s += x;

        s = s + y;
        s += y;

        s = s + a;
        s += a;

        s = s + b;
        s += b;
    }

    void unknownUnitsString() {
        String s = "word";
        @m int x = 10 * UnitsTools.m;
        @s long y = 20l * UnitsTools.s;
        @C float a = 30.0f * UnitsTools.C;
        @K double b = 40.0d * UnitsTools.K;

        s = s + s;
        s += s;

        s = s + x;
        s += x;

        s = s + y;
        s += y;

        s = s + a;
        s += a;

        s = s + b;
        s += b;
    }
}