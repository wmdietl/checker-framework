import org.checkerframework.checker.units.qual.*;

// Tests for unqualified assignments
public class UnqualTest {
    // Fields are by default @UnknownUnits, inference should transfer units for UnknownUnits, but not Scalars

    //:: error: (assignment.type.incompatible)
    @kg int kg = 5;
    // inferred to be kg
    int inferredKg = kg;
    // accepted due to inference of kg unit
    @kg int alsokg = inferredKg;

    //:: error: (assignment.type.incompatible)
    @kg int kg2 = 5;
    //:: error: (assignment.type.incompatible)
    @Scalar int notKg = kg2;
    //:: error: (assignment.type.incompatible)
    @kg int alsokg2 = notKg;

    // Local Variables are by default @UnknownUnits, inference should transfer units for UnknownUnits
    void m(){
        // primitive number literals are by default @Scalar
        int scalar = 5;

        //:: error: (assignment.type.incompatible)
        @kg int kgLV = scalar;

        int nonkgLV = kgLV;

        @kg int alsokgLV = nonkgLV;

        int alsoScalar = scalar;
    }
}
