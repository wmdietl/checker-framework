import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;
import org.checkerframework.checker.units.qual.time.instant.*;
import org.checkerframework.checker.units.UnitsTools;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;

@TimeInstant class A {
    @TimeInstant public A() {

    }
    void m() {
        // default receiver is @Scalar
    }
}

@TimeInstant class B {
    @TimeInstant public B() {

    }
    void m(@TimeInstant B this) {
        // receiver is a @TimeInstant
    }
}

class Other {
    void X() {
        A a = new @CALyear A();
        //:: error: (method.invocation.invalid)
        a.m();

        B b = new @CALyear B();
        b.m();
    }
}



