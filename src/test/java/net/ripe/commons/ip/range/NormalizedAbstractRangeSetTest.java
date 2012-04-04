package net.ripe.commons.ip.range;

import static junit.framework.Assert.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ripe.commons.ip.resource.Asn;
import org.junit.Before;
import org.junit.Test;

public class NormalizedAbstractRangeSetTest {

    private static NormalizedAbstractRangeSet<Asn, AsnRange> subject;

    @Before
    public void before() {
        subject = new NormalizedAbstractRangeSet<Asn, AsnRange>();
    }

    private static void initSubject() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        subject.add(new AsnRange(Asn.of(20l), Asn.of(25l)));
    }

    //---------------------------------------------------------------
    // void add(R rangeToAdd)
    //---------------------------------------------------------------

    @Test
    public void testAddOverlappingRange() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add           |----|          [4,11]
        // result      |--------|  |--|  [0,15] [20,25]
        subject.add(new AsnRange(Asn.of(4l), Asn.of(11l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(15l)));
        result.add(new AsnRange(Asn.of(20l), Asn.of(25l)));

        Set<AsnRange> actual = subject.unmodifiableSet();
        assertEquals(result, actual);
    }

    @Test
    public void testAddOverlappingRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add                 |----|    [13,22]
        // add           |----|          [4,11]
        // result      |--------------|  [0,25]
        subject.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        subject.add(new AsnRange(Asn.of(4l), Asn.of(11l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAdjacentRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add            |--|  |--|     [5,10] [15,20]
        // result      |--------------|  [0,25]
        subject.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        subject.add(new AsnRange(Asn.of(15l), Asn.of(20l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |------------------|  [0,25]
        subject.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        subject.add(new AsnRange(Asn.of(16l), Asn.of(19l)));

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    //---------------------------------------------------------------
    // void addAll(NormalizedAbstractRangeSet<C, R> rangesToAdd)
    //---------------------------------------------------------------

    @Test
    public void testAddAllOverlappingRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add all       |----||----|    [13,22] [4,11]
        // result      |--------------|  [0,25]
        NormalizedAbstractRangeSet<Asn, AsnRange> setToAdd = new NormalizedAbstractRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(13l), Asn.of(22l)));
        setToAdd.add(new AsnRange(Asn.of(4l), Asn.of(11l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllAdjacentRanges() {
        initSubject();
        // subject     |--|  |--|  |--|  [0,5] [10,15] [20,25]
        // add            |--|  |--|     [5,10] [15,20]
        // result      |--------------|  [0,25]
        NormalizedAbstractRangeSet<Asn, AsnRange> setToAdd = new NormalizedAbstractRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(5l), Asn.of(10l)));
        setToAdd.add(new AsnRange(Asn.of(15l), Asn.of(20l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testAddAllConsecutiveRanges() {
        initSubject();
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // add             |--|    |--|      [6,9] [16,19]
        // result      |------------------|  [0,25]
        NormalizedAbstractRangeSet<Asn, AsnRange> setToAdd = new NormalizedAbstractRangeSet<Asn, AsnRange>();
        setToAdd.add(new AsnRange(Asn.of(6l), Asn.of(9l)));
        setToAdd.add(new AsnRange(Asn.of(16l), Asn.of(19l)));
        subject.addAll(setToAdd);

        Set<AsnRange> result = new HashSet<AsnRange>();
        result.add(new AsnRange(Asn.of(0l), Asn.of(25l)));

        assertEquals(result, subject.unmodifiableSet());
    }

    @Test
    public void testContainsRange() {
        // subject    |-|     |--------|      [0,2] [10,20]
        // contains   |-|     |--------|      [0,2] [10,20]
        // contains   |-| |--|.        .      [0,2] [6,9]
        // contains   |-|  |--|        .      [0,2] [7,10]
        // contains   |-|   |--|       .      [0,2] [8,11]
        // contains   |-|     |--|     .      [0,2] [10,13]
        // contains   |-|     .  |--|  .      [0,2] [13,16]
        // contains   |-|     .     |--|      [0,2] [17,20]
        // contains   |-|     .       |--|    [0,2] [18,21]
        // contains   |-|     .        |--|   [0,2] [20,23]
        // contains   |-|     .        .|--|  [0,2] [21,24]
        // contains   |-|   |------------|    [0,2] [8,22]
        subject.add(new AsnRange(Asn.of(0l), Asn.of(2l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(20l)));

        assertTrue(subject.contains(new AsnRange(Asn.of(10l), Asn.of(20l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(6l), Asn.of(9l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(7l), Asn.of(10l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(8l), Asn.of(11l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(10l), Asn.of(13l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(13l), Asn.of(16l))));
        assertTrue(subject.contains(new AsnRange(Asn.of(17l), Asn.of(20l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(18l), Asn.of(21l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(20l), Asn.of(23l))));
        assertFalse(subject.contains(new AsnRange(Asn.of(8l), Asn.of(22l))));
    }

    @Test
    public void testClear() {
        // subject     |--|    |--|    |--|  [0,5] [10,15] [20,25]
        // clear
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(15l)));
        subject.add(new AsnRange(Asn.of(20l), Asn.of(25l)));
        subject.clear();
        assertTrue(subject.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(subject.isEmpty());
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        assertFalse(subject.isEmpty());
    }

    @Test
    public void testIterator() {
        AsnRange range1 = new AsnRange(Asn.of(0l), Asn.of(5l));
        AsnRange range2 = new AsnRange(Asn.of(10l), Asn.of(20l));
        subject.add(range1);
        subject.add(range2);
        Iterator<AsnRange> it = subject.iterator();
        assertTrue(it.hasNext());
        assertEquals(range1, it.next());
        it.remove();
        assertTrue(it.hasNext());
        assertEquals(range2, it.next());
        it.remove();
        assertTrue(subject.isEmpty());
    }

    @Test
    public void testToString() {
        subject.add(new AsnRange(Asn.of(0l), Asn.of(5l)));
        subject.add(new AsnRange(Asn.of(10l), Asn.of(20l)));
        assertEquals("[[0..5], [10..20]]", subject.toString());
    }

}
