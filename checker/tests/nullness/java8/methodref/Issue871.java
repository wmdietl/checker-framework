// Test case for issue #871: https://github.com/typetools/checker-framework/issues/871
// @skip-test until it is fixed
// @below-java8-jdk-skip-test

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

interface Issue871 {
    default Iterable<Path> a() {
        return f(Files::isRegularFile);
    }

    Iterable<Path> f(Predicate<Path> condition);
}