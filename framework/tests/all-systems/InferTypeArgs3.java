import java.util.Arrays;
import java.util.HashSet;

class InferTypeArgs3 {
    @SuppressWarnings({"deprecation", "cast.unsafe.constructor.invocation"})
    void test() {
        java.util.Arrays.asList(new Integer(1), "");
    }

    @SuppressWarnings("") // only check for crashes until #979 is fixed
    void foo() {
        new HashSet<>(Arrays.asList(new Object()));
        new HashSet<Object>(Arrays.asList(new Object()));
        new HashSet<>(Arrays.asList(new Object())) {};
        new HashSet<Object>(Arrays.asList(new Object())) {};
    }
}
