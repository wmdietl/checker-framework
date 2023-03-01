import org.checkerframework.framework.testchecker.util.*;

import java.util.List;

public class WildcardSuper {
    void test(List<? super @Odd String> list) {
        // :: error: (assignment)
        @Odd Object odd = list.get(0);
    }
}
