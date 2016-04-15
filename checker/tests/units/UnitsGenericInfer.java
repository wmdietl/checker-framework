import org.checkerframework.checker.units.qual.*;

import java.util.*;

// Test 1:
class ClassA{}
class ClassB< TT, HH extends TT >{}
class MM{
    public void mm( ClassB< ClassA, ? > input) {}
}

// Test 2:
class ClassC<V extends ClassC<V>> {}
class ClassD extends ClassC<ClassD> {
    public ClassD(ClassE<ClassD, ?> input) {}
}
class ClassE<V extends ClassC<V>, S extends ClassE<V, S>> {}

// Test 3:
interface A<T> {
    public abstract int transformSuper(List<? super T> function);
    public abstract int transformExtend(List<? extends T> function);
}

// Test 4:
class MyClass<T extends String>{}
class XX{
    // extends bound higher than class declaration allowed in java
    MyClass<? extends Object> x = new MyClass<String>();
}

// Test 5:
class TypeVarsArrays<T> {
    private T[] array;

    public void trigger(int index, T val) {
        array[index] = val;
        array[index] = null;
    }
}

// Test 6:
class Comp<T extends @UnknownUnits Object> {
    Comp<T> method() {
        return new Comp<T>();
    }

    static <T> Comp<T> method0() {
        return new Comp<T>();
    }

    <T> Comp<T> method1() {
        return new Comp<T>();
    }

    <T extends @Length Object> Comp<T> method2() {
        return new Comp<T>();
    }

    <U> Comp<U> method3() {
        return new Comp<U>();
    }

    <U extends @Length Object> Comp<U> method4() {
        return new Comp<U>();
    }

    <U> void addToBindingList(Map<U, List<String>> map, U key, String value) {}
}

// Test 7:
abstract class Ordering<T> implements Comparator<T> {}

