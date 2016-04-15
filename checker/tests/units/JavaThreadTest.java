import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.time.duration.*;
import static org.checkerframework.checker.units.UnitsTools.s;
import static org.checkerframework.checker.units.UnitsTools.ms;
import static org.checkerframework.checker.units.UnitsTools.ns;

import java.util.Date;

public class JavaThreadTest {
    void m() throws InterruptedException {
        long start = System.currentTimeMillis();

        System.out.println(new Date() + "\n");

        Thread.sleep(500 * ms);
        //:: error: (argument.type.incompatible)
        Thread.sleep(500 * s);

        Thread.sleep(500 * ms, 200 * ns);
        //:: error: (argument.type.incompatible)
        Thread.sleep(500 * ms, 300 * s);

        System.out.println(new Date() + "\n");

        long end = System.currentTimeMillis();

        @ms long diff = end - start;
        @s(Prefix.milli) long diff2 = end - start;

        //:: error: (assignment.type.incompatible)
        @s long diffBad = end - start;

        //:: error: (time.point.addition.disallowed)
        long addBad = end + start;

        System.out.println("Difference is : " + diff);
    }

    class ThreadDemo implements Runnable {
        public void run() {
            Thread t = Thread.currentThread();
            System.out.print(t.getName());
            //checks if this thread is alive
            System.out.println(", status = " + t.isAlive());
        }

        void n() throws InterruptedException {
            Thread t = new Thread(new ThreadDemo());
            // this will call run() function
            t.start();

            // waits for this thread to die
            t.join(500 * ms);
            //:: error: (argument.type.incompatible)
            t.join(500 * s);

            t.join(500 * ms, 200 * ns);
            //:: error: (argument.type.incompatible)
            t.join(500 * ms, 300 * s);

            System.out.print(t.getName());
            //checks if this thread is alive
            System.out.println(", status = " + t.isAlive());
        }
    }
}
