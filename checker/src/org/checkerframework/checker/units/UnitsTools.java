package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;

/**
 * Utility constants to assign units to types and methods to convert between them.
 *
 * All constants are declared as public static final with a value of 1.
 *
 * To assign a unit to a number, simply multiply with the corresponding unit.
 *
 * E.g. {@code @m int x = 5 * UnitsTools.m}
 */
// Developer notes: add fromTo methods for all useful unit combinations here.

// Forcefully suppress all warnings here, since this tools class provides a
// means to assign types to value literals and convert between types
@SuppressWarnings("units")
public class UnitsTools {
    // Acceleration
    public static final @mPERs2 int mPERs2 = 1;

    // Angle
    public static final @radians double rad = 1;
    public static final @degrees double deg = 1;

    public static @radians double toRadians(@degrees double angdeg) {
        return Math.toRadians(angdeg);
    }

    public static @degrees double toDegrees(@radians double angrad) {
        return Math.toDegrees(angrad);
    }

    // Area
    public static final @mm2 int mm2 = 1;
    public static final @m2 int m2 = 1;
    public static final @km2 int km2 = 1;

    // Current
    public static final @A int A = 1;

    // Luminance
    public static final @cd int cd = 1;

    // Lengths
    public static final @mm int mm = 1;
    public static final @m int m = 1;
    public static final @km int km = 1;

    public static @m int fromMilliMeterToMeter(@mm int mm) {
        return mm / 1000;
    }

    public static @mm int fromMeterToMilliMeter(@m int m) {
        return m * 1000;
    }

    public static @km int fromMeterToKiloMeter(@m int m) {
        return m / 1000;
    }

    public static @m int fromKiloMeterToMeter(@km int km) {
        return km * 1000;
    }

    // Mass
    public static final @g int g = 1;
    public static final @kg int kg = 1;

    public static @kg int fromGramToKiloGram(@g int g) {
        return g / 1000;
    }

    public static @g int fromKiloGramToGram(@kg int kg) {
        return kg * 1000;
    }

    // Speed
    public static final @mPERs int mPERs = 1;
    public static final @kmPERh int kmPERh = 1;

    public static @kmPERh double fromMeterPerSecondToKiloMeterPerHour(@mPERs double mps) {
        return mps * 3.6d;
    }

    public static @mPERs double fromKiloMeterPerHourToMeterPerSecond(@kmPERh double kmph) {
        return kmph / 3.6d;
    }

    // Substance
    public static final @mol int mol = 1;

    // Temperature
    public static final @K int K = 1;
    public static final @C int C = 1;

    public static @C int fromKelvinToCelsius(@K int k) {
        return k - (int) 273.15;
    }

    public static @K int fromCelsiusToKelvin(@C int c) {
        return c + (int) 273.15;
    }

    // Time Duration
    public static final @century int century = 1;
    public static final @day int day = 1;
    public static final @decade int decade = 1;
    public static final @era int era = 1;
    public static final @forever int forever = 1;
    public static final @h int h = 1;
    public static final @halfday int halfday = 1;
    public static final @millennia int millennia = 1;
    public static final @min int min = 1;
    public static final @month int month = 1;
    public static final @ms int ms = 1;
    public static final @ns int ns = 1;
    public static final @quarteryear int quarteryear = 1;
    public static final @s int s = 1;
    public static final @us int us = 1;
    public static final @week int week = 1;
    public static final @year int year = 1;

    // Time Instant
    public static final @CALcentury int CALcentury = 1;
    public static final @CALday int CALday = 1;
    public static final @CALdecade int CALdecade = 1;
    public static final @CALera int CALera = 1;
    public static final @CALforever int CALforever = 1;
    public static final @CALh int CALh = 1;
    public static final @CALhalfday int CALhalfday = 1;
    public static final @CALmillennia int CALmillennia = 1;
    public static final @CALmin int CALmin = 1;
    public static final @CALmonth int CALmonth = 1;
    public static final @CALms int CALms = 1;
    public static final @CALns int CALns = 1;
    public static final @CALquarteryear int CALquarteryear = 1;
    public static final @CALs int CALs = 1;
    public static final @CALus int CALus = 1;
    public static final @CALweek int CALweek = 1;
    public static final @CALyear int CALyear = 1;

    // Future TODO: would be nice to encode conversion values by looking up @TimeMultiple
    public static @min int fromSecondToMinute(@s int s) {
        return s / 60;
    }

    public static @s int fromMinuteToSecond(@min int min) {
        return min * 60;
    }

    public static @h int fromMinuteToHour(@min int min) {
        return min / 60;
    }

    public static @min int fromHourToMinute(@h int h) {
        return h * 60;
    }

    // Volume
    public static final @mm3 int mm3 = 1;
    public static final @m3 int m3 = 1;
    public static final @km3 int km3 = 1;

    // Scalar Conversion Method
    /**
     * This method takes in any variable or reference and returns the same
     * variable or reference with the Scalar unit instead of what it had.
     *
     * @param x a variable or reference with any unit
     * @return x with the Scalar unit replacing the unit it had
     */
    public static final @Scalar <T> T toScalar(@UnknownUnits T x) {
        // for version 8:
        // public static final <T> @Scalar T toScalar(@UnknownUnits T x) {
        return x;
    }
}
