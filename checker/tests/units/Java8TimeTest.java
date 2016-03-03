import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.instant.*;
import org.checkerframework.checker.units.UnitsTools;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;

// this test is for the new java 8 time api
// @below-java8-jdk-skip-test

public class Java8TimeTest {

    void AddSubtractTest() {
        @SuppressWarnings("units") @CALs long si = 30;
        @SuppressWarnings("units") @CALyear long yi = 30;

        @SuppressWarnings("units") @s long sd = 30;
        @SuppressWarnings("units") @year long yd = 30;

        sd = sd + sd;
        si = si + sd;
        si = sd + si;
        //:: error: (time.instant.addition.disallowed)
        si = si + si;

        yd = yd - yd;
        yi = yi - yd;
        yi = yd - yi;
        //:: error: (time.instant.addition.disallowed)
        yi = yi - yi;
    }

    public static void main(String[] args) {
        Duration m = Duration.ofHours(2 * UnitsTools.h);

        @s long s = m.get(ChronoUnit.SECONDS);
        System.out.println(s);
        @ns long ns = m.get(ChronoUnit.NANOS);
        System.out.println(ns);

        //:: error: (assignment.type.incompatible)
        s = m.get(ChronoUnit.NANOS); // expected error
        //:: error: (assignment.type.incompatible)
        ns = m.get(ChronoUnit.SECONDS); // expected error

        Duration n = Duration.of(30 * UnitsTools.h, ChronoUnit.HOURS);
        // todo:
        Duration nbad = Duration.of(30 * UnitsTools.h, ChronoUnit.SECONDS);

        M();
    }

    static void M() {
        @SuppressWarnings("units") @CALs long s = 30;
        @SuppressWarnings("units") @CALns long ns = 50;

        ValueRange vr = ValueRange.of(s, s);

        ValueRange vr2 = ValueRange.of(ns, ns);

        //:: error: (operands.unit.mismatch)
        if (vr == vr2);

        ChronoField cf = ChronoField.DAY_OF_MONTH;

        ValueRange vr3 = cf.range(); // this should be @CALday
        // (ChronoField.DAY_OF_MONTH).range();

        //:: error: (operands.unit.mismatch)
        if (vr == vr3);

        Instant currentTime = Clock.systemDefaultZone().instant();
        @CALs long currentSecond = currentTime.getLong(ChronoField.INSTANT_SECONDS);
        @CALms long currentMillisec = currentTime.getLong(ChronoField.MILLI_OF_SECOND);
        @CALus long currentMicrosec = currentTime.getLong(ChronoField.MICRO_OF_SECOND);
        @CALns long currentNanosec = currentTime.getLong(ChronoField.NANO_OF_SECOND);

        System.out.println("current time is: " + currentSecond + " seconds and "
                + currentNanosec + " nanoseconds.");

        if (ChronoField.INSTANT_SECONDS.range().isValidValue(currentSecond)) {
            System.out.println("Current seconds fits in an Instant Second");
        }

        currentSecond = ChronoField.INSTANT_SECONDS.range().checkValidValue(currentSecond, ChronoField.SECOND_OF_DAY);

        // TODO: remove this error: range() and getMinimum() needs to transfer the unit of the INSTANT_SECONDS enum
        //:: error: (operands.unit.mismatch)
        if (ChronoField.INSTANT_SECONDS.range().getMinimum() < currentSecond) {
            System.out.println("Current seconds is larger than the minimum value of an Instant Second");
        }
    }

    static void N() {
        Instant currentTime = Clock.systemDefaultZone().instant();
        @CALs long currentSecond = currentTime.getLong(ChronoField.INSTANT_SECONDS);
        ChronoField.INSTANT_SECONDS.range().isValidValue(currentSecond);

    }
}
