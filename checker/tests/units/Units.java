import org.checkerframework.checker.units.*;
import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import static org.checkerframework.checker.units.UnitsTools.s;

public class Units {
    @m(Prefix.one) int m1 = 5 * UnitsTools.m;

    // The advantage of using the multiplication with a unit is that
    // also double, float, etc. are easily handled and we don't need
    // to end a huge number of methods to UnitsTools.
    @m double dm = 9.34d * UnitsTools.m;

    // With a static import:
    @s float time = 5.32f * s;

    //    @SuppressWarnings("units")
    //    @Unit float scalar = 50f;
    //
    @SuppressWarnings("units")
    @Unit(numeratorUnits = {m.class}, numeratorPrefixValues = {1}) float meter = 50f;
    //
    //    @SuppressWarnings("units")
    //    @Unit(denominatorUnits = {s.class}, denominatorPrefixValues = {1}) float perSecond = 50f;
    //
    //    @SuppressWarnings("units")
    //    @Unit(numeratorUnits = {m.class}, numeratorPrefixValues = {1}, denominatorUnits = {s.class}, denominatorPrefixValues = {1}) float mPERs = 50f;

}
