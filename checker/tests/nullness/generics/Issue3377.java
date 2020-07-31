// Test case for Issue #3377:
// https://github.com/typetools/checker-framework/issues/3377

import org.checkerframework.checker.nullness.qual.Nullable;

class Issue3377 {
    class Box<S> {}

    interface Unboxer {
        <T> T unbox(Box<T> p);
    }

    class Crash {
        Box<@Nullable String> crash(Unboxer ub) {
            return ub.unbox(new Box<>() {});
        }
    }
}
