// Test case for Issue #3377:
// https://github.com/typetools/checker-framework/issues/3377

class Issue3377 {
    class Box<S> {}

    interface Unboxer {
        <T> T unbox(Box<T> p);
    }

    class Crash {
        Box<String> crash(Unboxer ub) {
            if (ub.hashCode() > 10) {
                return ub.unbox(new Box<>() {});
            } else {
                return ub.unbox(new Box<Box<String>>() {});
            }
        }
    }
}
