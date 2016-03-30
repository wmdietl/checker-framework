import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.qual.time.duration.*;

import java.lang.Iterable;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
class CollectionsTests {

    // java.lang.Iterable tests
    class MyCollection<E extends @UnknownUnits Object> implements Iterable<E> {
        public Iterator<E> iterator() {
            return new MyIterator<E>();
        }
    }

    class MyIterator<T extends @UnknownUnits Object> implements Iterator<T> {
        // dummy return values for testing purposes
        public boolean hasNext() {
            return true;
        }

        public T next() {
            return null;
        }

        public void remove() {
        }
    }

    void customIterableTest() {
        MyCollection<@Length Integer> collection = new MyCollection<@Length Integer>();

        for (@Length Integer len : collection) {
            // do nothing
        }

        //:: error: (enhancedfor.type.incompatible)
        for (@m Integer meter : collection) {

        }
    }

    // ================================================
    // tests for java.util collections classes
    // ================================================

    void iteratorTest() {
        Vector<@Length Integer> v = new Vector<@Length Integer>();

        Iterator<@Length Integer> itrLength = v.iterator();
        Iterator<@UnknownUnits Integer> itrUU = v.iterator();
        //:: error: (assignment.type.incompatible)
        Iterator<@m Integer> itrMeter = v.iterator();
    }

    void hashSetTest(){
        @Length Integer x = new @Length Integer(5);
        HashSet<@Length Integer> set = new HashSet<@Length Integer>();

        set.add(x);
        set.add(new @m Integer(5));
        //:: error: (argument.type.incompatible)
        set.add(new @s Integer(5));

        set.clear();
        Object y = set.clone();
        set.contains(x);
        set.isEmpty();
        Iterator<@Length Integer> itr = set.iterator();
        set.remove(x);
        set.size();

        set.equals(set);
        set.hashCode();
        set.removeAll(set);

        set.addAll(set);
        set.containsAll(set);
        set.retainAll(set);
        set.toArray();
        set.toArray(new @Length Integer[6]);
        set.toString();
    }

    void linkedListTest(){
        @Length Integer x = new @Length Integer(5);
        LinkedList<@Length Integer> list = new LinkedList<@Length Integer>();

        list.add(x);
        list.add(1, x);
        list.addAll(list);
        list.addAll(2, list);
        list.addFirst(x);
        list.addLast(x);
        list.clear();
        list.clone();
        list.contains(x);
        Iterator<@Length Integer> itr = list.descendingIterator();
        x = list.element();
        x = list.get(1);
        x = list.getFirst();
        x = list.getLast();
        list.indexOf(x);
        list.lastIndexOf(x);
        ListIterator<@Length Integer> lstItr = list.listIterator();
        list.offer(x);
        list.offerFirst(x);
        list.offerLast(x);
        x = list.peek();
        x = list.peekFirst();
        x = list.peekLast();
        x = list.poll();
        x = list.pollFirst();
        x = list.pollLast();
        x = list.pop();
        list.push(x);
        x = list.remove();
        x = list.remove(5);
        list.remove(x);
        x = list.removeFirst();
        list.removeFirstOccurrence(x);
        x = list.removeLast();
        list.removeLastOccurrence(x);
        list.set(3, x);
        list.size();
        list.toArray();
        list.toArray(new @Length Integer[5]);

        itr = list.iterator();

        list.equals(list);
        list.hashCode();
        List<@Length Integer> l = list.subList(0, 5);

        list.containsAll(list);
        list.isEmpty();
        list.removeAll(list);
        list.retainAll(list);
        list.toString();
        list.sort(null);
    }

    void priorityQueueTest() {
        @Length Integer x = new @Length Integer(5);
        PriorityQueue<@Length Integer> pq = new PriorityQueue<@Length Integer>();

        pq.add(x);
        pq.clear();
        pq.comparator();
        pq.contains(x);
        Iterator<@Length Integer> itr = pq.iterator();
        pq.offer(x);
        x = pq.peek();
        x = pq.poll();
        pq.remove(x);
        pq.size();
        pq.toArray();
        pq.toArray(new @Length Integer[5]);

        pq.addAll(pq);
        x = pq.element();
        x = pq.remove();

        pq.containsAll(pq);
        pq.isEmpty();
        pq.removeAll(pq);
        pq.retainAll(pq);
        pq.toString();

        pq.equals(pq);
        pq.hashCode();
        pq.isEmpty();
    }

    void arrayDequeTest() {
        @Length Integer x = new @Length Integer(5);
        ArrayDeque<@Length Integer> ad = new ArrayDeque<@Length Integer>();

        ad.add(x);
        ad.addFirst(x);
        ad.addLast(x);
        ad.clear();
        ad = ad.clone();
        ad.contains(x);
        Iterator<@Length Integer> itr = ad.descendingIterator();
        x = ad.element();
        x = ad.getFirst();
        x = ad.getLast();
        ad.isEmpty();
        itr = ad.iterator();
        ad.offer(x);
        ad.offerFirst(x);
        ad.offerLast(x);
        x = ad.peek();
        x = ad.peekFirst();
        x = ad.peekLast();
        x = ad.poll();
        x = ad.pollFirst();
        x = ad.pollLast();
        x = ad.pop();
        ad.push(x);;
        x = ad.remove();
        ad.remove(x);
        x = ad.removeFirst();
        ad.removeFirstOccurrence(x);
        x = ad.removeLast();
        ad.removeLastOccurrence(x);
        ad.size();
        ad.toArray();
        ad.toArray(new @Length Integer[6]);

        ad.addAll(ad);
        ad.containsAll(ad);
        ad.removeAll(ad);
        ad.retainAll(ad);
        ad.toString();

        ad.equals(ad);
        ad.hashCode();
    }

    void weakHashMapTest(){
        @m Integer key = new @m Integer(5);
        Integer key2 = new Integer(39);
        @m2 Double val = new @m2 Double(23.5);

        WeakHashMap<@Length Integer, @Area Double> whm = new WeakHashMap<@Length Integer, @Area Double>();
        whm = new WeakHashMap(whm);
        whm = new WeakHashMap<@Length Integer, @Area Double>(whm);
        WeakHashMap<@UnknownUnits Integer, @UnknownUnits Double> whm2 = new WeakHashMap(whm);

        whm.put(key, val);
        //:: error: (argument.type.incompatible)
        whm.put(key2, val);

        @Area Double value = whm.get(key);
        //:: error: (assignment.type.incompatible)
        @Temperature Double value2 = whm.get(key2);

        whm.containsKey(key);
        whm.containsKey(key2);

        whm.putAll(whm);
        // cannot put the collection of unknown Integers and Doubles into a
        // weak hash map of length Integers and area Doubles
        //:: error: (argument.type.incompatible)
        whm.putAll(whm2);

        whm.remove(key);

        whm.containsValue(val);

        Set<@Length Integer> keySet = whm.keySet();
        //:: error: (assignment.type.incompatible)
        Set<Integer> keySetBad = whm.keySet();

        //:: error: (assignment.type.incompatible)
        Collection<Double> valuesBad = whm.values();

        //:: error: (assignment.type.incompatible)
        Set<Entry<Integer, Double>> entrySetBad = whm.entrySet();
    }

    void vectorTest(){
        @Length Integer e1 = new @Length Integer(5);
        @m Integer e2 = new @m Integer(20);
        @s Integer e3 = new @s Integer(30);

        Vector<@Length Integer> v = new Vector<@Length Integer>();
        v.add(e1);
        v.add(e2);
        //:: error: (argument.type.incompatible)
        v.add(e3);

        v.contains(e1);
        v.contains(e2);
        v.contains(e3);

        v.remove(e1);
        v.remove(e2);
        v.remove(e3);

        //:: error: (assignment.type.incompatible)
        Enumeration<@m Integer> en = v.elements();
        // okay only because of the forced type on en
        @m Integer i = en.nextElement();

        //:: error: (assignment.type.incompatible)
        i = v.elements().nextElement();
        // test toArray as well

        // Cloning constructors
        Vector<@Length Integer> v2 = new Vector(v);
        Vector<@Length Integer> v2ok = new Vector<@Length Integer>(v);
        // By default, type arguments have the unit of @Scalar
        //:: error: (argument.type.incompatible)
        Vector<Integer> v2Scalar = new Vector(v);
        // The only reference that can accept the cloning constructor without
        // an explicity type argument is if it has @UnknownUnits as the unit of
        // each element in the collection
        Vector<@UnknownUnits Integer> v2Top = new Vector(v);
        // downcasting length to meter is also an error
        //:: error: (argument.type.incompatible)
        Vector<@m Integer> v3 = new Vector<@m Integer>(v);

        // array copy
        @Length Integer[] intLengthArr = new @Length Integer[10];
        @Area Integer[] intAreaArr = new @Area Integer[10];

        v.copyInto(intLengthArr);
        // see BaseTypeVisitor
        //:: error: (vector.copyinto.type.incompatible)
        v.copyInto(intAreaArr);

        intLengthArr = v.toArray(intLengthArr);
        // future TODO: should also be an error like above
        intAreaArr = v.toArray(intAreaArr);

        // future TODO: this should copy the unit within the type parameter of
        // Vector, and not be an error
        //:: error: (assignment.type.incompatible)
        @Length Object[] objArr = v.toArray();

        // clone retains the unit of the whole vector object
        //:: error: (assignment.type.incompatible)
        @Length Object vClone = v.clone();

        v.containsAll(v);
        v.addAll(v);
        v.retainAll(v);
        v.removeAll(v);

        List<@Length Integer> l = v.subList(0, v.size());
        // can't assign the list to a downcasted list reference
        //:: error: (assignment.type.incompatible)
        List<@m Integer> lBad = v.subList(0, v.size());

        Iterator<@Length Integer> itr = v.iterator();

        // can't assign the iterator to a downcasted iterator reference
        //:: error: (assignment.type.incompatible)
        Iterator<@m Integer> itrBad = v.iterator();
    }
}
