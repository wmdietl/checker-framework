import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.point.*;
import org.checkerframework.checker.units.UnitsTools;

// Tests for unqualified assignments
public class UnqualTest {
    // Fields are by default @Scalar, inference should transfer units for UnknownUnits, but not Scalar
    int scalar = 5;

    //:: error: (assignment.type.incompatible)
    @kg int kg = scalar;

    // inferred to be kg
    @UnknownUnits int inferredKg = kg;
    // accepted due to inference of kg unit
    @kg int alsokg = inferredKg;

    int alsoScalar = scalar;

    //:: error: (assignment.type.incompatible)
    @kg int kg2 = scalar;
    //:: error: (assignment.type.incompatible)
    @Scalar int notKg = kg2;
    //:: error: (assignment.type.incompatible)
    @kg int alsokg2 = notKg;

    void m(){
        // Local Variables are by default @UnknownUnits, inference should transfer units for UnknownUnits

        // primitive number literals are by default @Scalar
        // this is inferred to be scalar
        int inferredScalar = 5;

        //:: error: (assignment.type.incompatible)
        @kg int kgLV = inferredScalar;

        // inferred to be kg
        int inferredKgLV = kgLV;
        // accepted due to inference of kg unit
        @kg int alsokgLV = inferredKgLV;

        @Scalar int scalar = inferredScalar;

        //:: error: (assignment.type.incompatible)
        @kg int kg2 = inferredScalar;
        //:: error: (assignment.type.incompatible)
        @Scalar int notKg = kg2;
        //:: error: (assignment.type.incompatible)
        @kg int alsokg2 = notKg;
    }
}
