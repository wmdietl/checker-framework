// Test case for Issue #3377:
// https://github.com/typetools/checker-framework/issues/3377

// @skip-test until #979 is fixed

class Issue3377 {
    class Box<S> {}

    interface Unboxer {
        <T> T unbox(Box<T> p);
    }

    class Crash {
        Box<String> crash(Unboxer ub) {
            return ub.unbox(new Box<>() {});
        }
    }
}
