import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

public class BoxedNumbers {
    void ByteTest() {
        @Scalar byte scalarByte = (byte) 100;
        @m byte meterByte = (@m byte) (100 * UnitsTools.m);
        @s byte secondByte = (@s byte) (100 * UnitsTools.s);

        @Scalar Byte scalarByteBox = new Byte(scalarByte);
        @s Byte secondByteBox = new @s Byte((byte) 30);
        // currently unsupported, even though the constructor is declared @PolyUnit Byte(@PolyUnit byte)
        //:: error: (assignment.type.incompatible)
        @m Byte meterByteBox = new Byte(meterByte);
        @m Byte meterByteBox2 = new @m Byte((byte) 50);
        @m Byte meterByteBox3 = meterByte;  // auto boxing
        // valueOf construction
        @m Byte meterByteBox4 = Byte.valueOf(meterByte);
        //:: error: (assignment.type.incompatible)
        @m Byte meterByteBox4Bad = Byte.valueOf(scalarByte);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Byte meterByteBox5 = new @m Byte(secondByte);

        meterByte = meterByteBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterByte = scalarByteBox;

        @m byte mByte = meterByteBox.byteValue();
        @m short mShort = meterByteBox.shortValue();
        @m int mInteger = meterByteBox.intValue();
        @m long mLong = meterByteBox.longValue();
        @m float mFloat = meterByteBox.floatValue();
        @m double mDouble = meterByteBox.doubleValue();

        String x = meterByteBox.toString();
        x = Byte.toString(meterByte);
        @Scalar int hash = meterByteBox.hashCode();
        hash = Byte.hashCode(meterByte);

        meterByteBox.equals(meterByteBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterByteBox.equals(5);
        meterByteBox.equals(scalarByteBox);
        meterByteBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterByteBox.equals(secondByteBox);

        @Scalar int r = meterByteBox.compareTo(meterByteBox2);
        //:: error: (operands.unit.mismatch)
        r = meterByteBox.compareTo(secondByteBox);

        r = Byte.compare(meterByte, meterByte);
        //:: error: (operands.unit.mismatch)
        r = Byte.compare(meterByte, secondByte);

        @m int meterInt = Byte.toUnsignedInt(meterByte);
        @m long meterLong = Byte.toUnsignedLong(meterByte);
    }

    void ShortTest() {
        @Scalar short scalarShort = 100;
        @m short meterShort = (@m short) (100 * UnitsTools.m);
        @s short secondShort = (@s short) (100 * UnitsTools.s);

        @Scalar Short scalarShortBox = new Short(scalarShort);
        @s Short secondShortBox = new @s Short((short) 30);
        // currently unsupported, even though the constructor is declared @PolyUnit Short(@PolyUnit short)
        //:: error: (assignment.type.incompatible)
        @m Short meterShortBox = new Short(meterShort);
        @m Short meterShortBox2 = new @m Short((short) 50);
        @m Short meterShortBox3 = meterShort;  // auto boxing
        // valueOf construction
        @m Short meterShortBox4 = Short.valueOf(meterShort);
        //:: error: (assignment.type.incompatible)
        @m Short meterShortBox4Bad = Short.valueOf(scalarShort);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Short meterShortBox5 = new @m Short(secondShort);

        meterShort = meterShortBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterShort = scalarShortBox;

        @m byte mByte = meterShortBox.byteValue();
        @m short mShort = meterShortBox.shortValue();
        @m int mInteger = meterShortBox.intValue();
        @m long mLong = meterShortBox.longValue();
        @m float mFloat = meterShortBox.floatValue();
        @m double mDouble = meterShortBox.doubleValue();

        String x = meterShortBox.toString();
        x = Short.toString(meterShort);
        @Scalar int hash = meterShortBox.hashCode();
        hash = Short.hashCode(meterShort);

        meterShortBox.equals(meterShortBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterShortBox.equals(5);
        meterShortBox.equals(scalarShortBox);
        meterShortBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterShortBox.equals(secondShortBox);

        @Scalar int r = meterShortBox.compareTo(meterShortBox2);
        //:: error: (operands.unit.mismatch)
        r = meterShortBox.compareTo(secondShortBox);

        r = Short.compare(meterShort, meterShort);
        //:: error: (operands.unit.mismatch)
        r = Short.compare(meterShort, secondShort);

        @m int meterInt = Short.toUnsignedInt(meterShort);
        @m long meterLong = Short.toUnsignedLong(meterShort);
        meterShort = Short.reverseBytes(meterShort);
    }

    void IntegerTest() {
        @Scalar int scalarInteger = 100;
        @m int meterInteger = 100 * UnitsTools.m;
        @s int secondInteger = 100 * UnitsTools.s;

        @Scalar Integer scalarIntegerBox = new Integer(scalarInteger);
        @s Integer secondIntegerBox = new @s Integer(30);
        // currently unsupported, even though the constructor is declared @PolyUnit Integer(@PolyUnit int)
        //:: error: (assignment.type.incompatible)
        @m Integer meterIntegerBox = new Integer(meterInteger);
        @m Integer meterIntegerBox2 = new @m Integer(50);
        @m Integer meterIntegerBox3 = meterInteger;  // auto boxing
        // valueOf construction
        @m Integer meterIntegerBox4 = Integer.valueOf(meterInteger);
        //:: error: (assignment.type.incompatible)
        @m Integer meterIntegerBox4Bad = Integer.valueOf(scalarInteger);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Integer meterIntegerBox5 = new @m Integer(secondInteger);

        meterInteger = meterIntegerBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterInteger = scalarIntegerBox;

        @m byte mByte = meterIntegerBox.byteValue();
        @m short mShort = meterIntegerBox.shortValue();
        @m int mInteger = meterIntegerBox.intValue();
        @m long mLong = meterIntegerBox.longValue();
        @m float mFloat = meterIntegerBox.floatValue();
        @m double mDouble = meterIntegerBox.doubleValue();

        String x = meterIntegerBox.toString();
        x = Integer.toString(meterInteger);
        x = Integer.toString(meterInteger, 16);
        x = Integer.toUnsignedString(meterInteger);
        x = Integer.toUnsignedString(meterInteger, 20);
        x = Integer.toHexString(meterInteger);
        x = Integer.toOctalString(meterInteger);
        x = Integer.toBinaryString(meterInteger);

        @Scalar int hash = meterIntegerBox.hashCode();
        hash = Integer.hashCode(meterInteger);

        meterIntegerBox = Integer.getInteger("test", meterInteger);
        meterIntegerBox = Integer.getInteger("test", meterIntegerBox);

        meterIntegerBox.equals(meterIntegerBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterIntegerBox.equals(5);
        meterIntegerBox.equals(scalarIntegerBox);
        meterIntegerBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterIntegerBox.equals(secondIntegerBox);

        @Scalar int r = meterIntegerBox.compareTo(meterIntegerBox2);
        //:: error: (operands.unit.mismatch)
        r = meterIntegerBox.compareTo(secondIntegerBox);

        r = Integer.compare(meterInteger, meterInteger);
        //:: error: (operands.unit.mismatch)
        r = Integer.compare(meterInteger, secondInteger);

        r = Integer.compareUnsigned(meterInteger, meterInteger);
        //:: error: (operands.unit.mismatch)
        r = Integer.compareUnsigned(meterInteger, secondInteger);

        @m long meterLong = Integer.toUnsignedLong(meterInteger);

        @mPERs int mps = Integer.divideUnsigned(meterInteger, secondInteger);
        meterInteger = Integer.remainderUnsigned(meterInteger, secondInteger);

        meterInteger = Integer.highestOneBit(meterInteger);
        meterInteger = Integer.lowestOneBit(meterInteger);

        @Scalar int y = Integer.numberOfLeadingZeros(meterInteger);
        y = Integer.numberOfTrailingZeros(meterInteger);
        y = Integer.bitCount(meterInteger);

        meterInteger = Integer.rotateLeft(meterInteger, 5);
        meterInteger = Integer.rotateRight(meterInteger, 5);
        meterInteger = Integer.reverse(meterInteger);
        y = Integer.signum(meterInteger);
        meterInteger = Integer.reverseBytes(meterInteger);

        meterInteger = Integer.sum(meterInteger, meterInteger);
        meterInteger = Integer.max(meterInteger, meterInteger);
        meterInteger = Integer.min(meterInteger, meterInteger);
    }

    void LongTest() {
        @Scalar long scalarLong = 100l;
        @m long meterLong = 100l * UnitsTools.m;
        @s long secondLong = 100l * UnitsTools.s;

        @Scalar Long scalarLongBox = new Long(scalarLong);
        @s Long secondLongBox = new @s Long(30l);
        // currently unsupported, even though the constructor is declared @PolyUnit Long(@PolyUnit long)
        //:: error: (assignment.type.incompatible)
        @m Long meterLongBox = new Long(meterLong);
        @m Long meterLongBox2 = new @m Long(50l);
        @m Long meterLongBox3 = meterLong;  // auto boxing
        // valueOf construction
        @m Long meterLongBox4 = Long.valueOf(meterLong);
        //:: error: (assignment.type.incompatible)
        @m Long meterLongBox4Bad = Long.valueOf(scalarLong);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Long meterLongBox5 = new @m Long(secondLong);

        meterLong = meterLongBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterLong = scalarLongBox;

        @m byte mByte = meterLongBox.byteValue();
        @m short mShort = meterLongBox.shortValue();
        @m int mInteger = meterLongBox.intValue();
        @m long mLong = meterLongBox.longValue();
        @m float mFloat = meterLongBox.floatValue();
        @m double mDouble = meterLongBox.doubleValue();

        String x = meterLongBox.toString();
        x = Long.toString(meterLong);
        x = Long.toString(meterLong, 16);
        x = Long.toUnsignedString(meterLong);
        x = Long.toUnsignedString(meterLong, 20);
        x = Long.toHexString(meterLong);
        x = Long.toOctalString(meterLong);
        x = Long.toBinaryString(meterLong);

        @Scalar long hash = meterLongBox.hashCode();
        hash = Long.hashCode(meterLong);

        meterLongBox = Long.getLong("test", meterLong);
        meterLongBox = Long.getLong("test", meterLongBox);

        meterLongBox.equals(meterLongBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterLongBox.equals(5);
        meterLongBox.equals(scalarLongBox);
        meterLongBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterLongBox.equals(secondLongBox);

        @Scalar long r = meterLongBox.compareTo(meterLongBox2);
        //:: error: (operands.unit.mismatch)
        r = meterLongBox.compareTo(secondLongBox);

        r = Long.compare(meterLong, meterLong);
        //:: error: (operands.unit.mismatch)
        r = Long.compare(meterLong, secondLong);

        r = Long.compareUnsigned(meterLong, meterLong);
        //:: error: (operands.unit.mismatch)
        r = Long.compareUnsigned(meterLong, secondLong);

        @mPERs long mps = Long.divideUnsigned(meterLong, secondLong);
        meterLong = Long.remainderUnsigned(meterLong, secondLong);

        meterLong = Long.highestOneBit(meterLong);
        meterLong = Long.lowestOneBit(meterLong);

        @Scalar long y = Long.numberOfLeadingZeros(meterLong);
        y = Long.numberOfTrailingZeros(meterLong);
        y = Long.bitCount(meterLong);

        meterLong = Long.rotateLeft(meterLong, 5);
        meterLong = Long.rotateRight(meterLong, 5);
        meterLong = Long.reverse(meterLong);
        y = Long.signum(meterLong);
        meterLong = Long.reverseBytes(meterLong);

        meterLong = Long.sum(meterLong, meterLong);
        meterLong = Long.max(meterLong, meterLong);
        meterLong = Long.min(meterLong, meterLong);
    }

    void FloatTest() {
        @Scalar float scalarFloat = 100.0f;
        @m float meterFloat = 100f * UnitsTools.m;
        @s float secondFloat = 100f * UnitsTools.s;
        @m double meterDouble = 100d * UnitsTools.m;
        @s double secondDouble = 100d * UnitsTools.s;

        @Scalar Float scalarFloatBox = new Float(scalarFloat);
        @s Float secondFloatBox = new @s Float(30.0f);
        // currently unsupported, even though the constructor is declared @PolyUnit Float(@PolyUnit float)
        //:: error: (assignment.type.incompatible)
        @m Float meterFloatBox = new Float(meterFloat);
        @m Float meterFloatBox2 = new @m Float(50.0f);
        @m Float meterFloatBox3 = meterFloat;  // auto boxing
        // valueOf construction
        @m Float meterFloatBox4 = Float.valueOf(meterFloat);
        //:: error: (assignment.type.incompatible)
        @m Float meterFloatBox4Bad = Float.valueOf(scalarFloat);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Float meterFloatBox5 = new @m Float(secondFloat);
        @m Float meterFloatBoxFromDouble = new @m Float(meterDouble);
        //:: error: (constructor.invocation.invalid)
        @m Float meterFloatBoxFromDoubleBad = new @m Float(secondDouble);

        meterFloat = meterFloatBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterFloat = scalarFloatBox;

        @m byte mByte = meterFloatBox.byteValue();
        @m short mShort = meterFloatBox.shortValue();
        @m int mInteger = meterFloatBox.intValue();
        @m long mLong = meterFloatBox.longValue();
        @m float mFloat = meterFloatBox.floatValue();
        @m double mDouble = meterFloatBox.doubleValue();

        String x = meterFloatBox.toString();
        x = Float.toString(meterFloat);
        x = Float.toHexString(meterFloat);

        @Scalar float hash = meterFloatBox.hashCode();
        hash = Float.hashCode(meterFloat);

        boolean b = meterFloatBox.isNaN();
        b = Float.isNaN(meterFloat);
        b = meterFloatBox.isInfinite();
        b = Float.isInfinite(meterFloat);
        b = Float.isFinite(meterFloat);

        meterFloatBox.equals(meterFloatBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterFloatBox.equals(5);
        meterFloatBox.equals(scalarFloatBox);
        meterFloatBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterFloatBox.equals(secondFloatBox);

        @Scalar float r = meterFloatBox.compareTo(meterFloatBox2);
        //:: error: (operands.unit.mismatch)
        r = meterFloatBox.compareTo(secondFloatBox);

        r = Float.compare(meterFloat, meterFloat);
        //:: error: (operands.unit.mismatch)
        r = Float.compare(meterFloat, secondFloat);

        @m int meterInt = Float.floatToIntBits(meterFloat);
        meterInt = Float.floatToRawIntBits(meterFloat);
        meterFloat = Float.intBitsToFloat(meterInt);

        meterFloat = Float.sum(meterFloat, meterFloat);
        meterFloat = Float.max(meterFloat, meterFloat);
        meterFloat = Float.min(meterFloat, meterFloat);
    }

    void DoubleTest() {
        @Scalar double scalarDouble = 100.0d;
        @m double meterDouble = 100d * UnitsTools.m;
        @s double secondDouble = 100d * UnitsTools.s;

        @Scalar Double scalarDoubleBox = new Double(scalarDouble);
        @s Double secondDoubleBox = new @s Double(30.0d);
        // currently unsupported, even though the constructor is declared @PolyUnit Double(@PolyUnit double)
        //:: error: (assignment.type.incompatible)
        @m Double meterDoubleBox = new Double(meterDouble);
        @m Double meterDoubleBox2 = new @m Double(50.0d);
        @m Double meterDoubleBox3 = meterDouble;  // auto boxing
        // valueOf construction
        @m Double meterDoubleBox4 = Double.valueOf(meterDouble);
        //:: error: (assignment.type.incompatible)
        @m Double meterDoubleBox4Bad = Double.valueOf(scalarDouble);
        // cannot force units conversion through constructor
        //:: error: (constructor.invocation.invalid)
        @m Double meterDoubleBox5 = new @m Double(secondDouble);

        meterDouble = meterDoubleBox;  // auto unboxing
        //:: error: (assignment.type.incompatible)
        meterDouble = scalarDoubleBox;

        @m byte mByte = meterDoubleBox.byteValue();
        @m short mShort = meterDoubleBox.shortValue();
        @m int mInteger = meterDoubleBox.intValue();
        @m long mLong = meterDoubleBox.longValue();
        @m float mFloat = meterDoubleBox.floatValue();
        @m double mDouble = meterDoubleBox.doubleValue();

        String x = meterDoubleBox.toString();
        x = Double.toString(meterDouble);
        x = Double.toHexString(meterDouble);

        @Scalar double hash = meterDoubleBox.hashCode();
        hash = Double.hashCode(meterDouble);

        boolean b = meterDoubleBox.isNaN();
        b = Double.isNaN(meterDouble);
        b = meterDoubleBox.isInfinite();
        b = Double.isInfinite(meterDouble);
        b = Double.isFinite(meterDouble);

        meterDoubleBox.equals(meterDoubleBox2);
        // comparison with scalar literals and objects, and null literals are allowed
        meterDoubleBox.equals(5);
        meterDoubleBox.equals(scalarDoubleBox);
        meterDoubleBox.equals(null);
        // otherwise comparison must be between the same unit
        //:: error: (operands.unit.mismatch)
        meterDoubleBox.equals(secondDoubleBox);

        @Scalar double r = meterDoubleBox.compareTo(meterDoubleBox2);
        //:: error: (operands.unit.mismatch)
        r = meterDoubleBox.compareTo(secondDoubleBox);

        r = Double.compare(meterDouble, meterDouble);
        //:: error: (operands.unit.mismatch)
        r = Double.compare(meterDouble, secondDouble);

        @m long meterLong = Double.doubleToLongBits(meterDouble);
        meterLong = Double.doubleToRawLongBits(meterDouble);
        meterDouble = Double.longBitsToDouble(meterLong);

        meterDouble = Double.sum(meterDouble, meterDouble);
        meterDouble = Double.max(meterDouble, meterDouble);
        meterDouble = Double.min(meterDouble, meterDouble);
    }
}