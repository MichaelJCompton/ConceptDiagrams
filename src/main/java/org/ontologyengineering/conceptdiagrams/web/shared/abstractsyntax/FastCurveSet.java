package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A minimal implementation of a bitset.  Seems that java.util.BitSet isn't supported in GWT, so this will be a
 * minimal version good enough for the simple operations required for Zone calculations
 *
 * the bitsets are implemented as a long array and all the opperations are just java's bitwise and shift operators.
 * No resizing or anything, just use as is and expand all operations to the max of the two
 */
public class FastCurveSet implements Iterable<Integer>, Iterator<Integer> {

    private long[] bitset;

    private int iteratorCursor;

    // let's assume an initial max of two longs as enough and expand if required
    // that would be 128 curves in a single diagram, so it's enough to not be exceeded often
    public FastCurveSet() {
        bitset = new long[2];
    }

    public FastCurveSet(FastCurveSet toCopy) {
        bitset = new long[toCopy.numWords()];

        for(int i = 0; i < toCopy.numWords(); i++) {
            bitset[i] = toCopy.bitset[i];
        }
    }

    // find the point in bitset, given a bit address
    // longs are 64 bit twos compliment, don't think we need to care about the twos compliment, just treat it as
    // 64 bits to play with.  So bits 0..63 are in index 0, 63..127 are in index 2, etc.
    // we aren't likely to need more than an index of 2 for diagrams.
    private int index(int bit) {
        return bit / 64;
    }


    public int numBits() {
        return (bitset.length + 1) * 64;
    }

    public int numWords() {
        return bitset.length + 1;
    }

    // might need to improve this with some caching ... but how would it work for AND etc?
    // maybe just invalidate it on those?
    public int numBitsSet() {
        int result = 0;
        for(int i = 0; i < numBits(); i++) {
            if(isSet(i)) {
                result++;
            }
        }
        return result;
    }

    protected void expand(int numBits) {
        int newSize = Math.max(numWords(), numBits/64);
        if(newSize > numWords()) {
            bitset = Arrays.copyOf(bitset, newSize); // I think this sets the new to 0x00000000
        }
    }

    public void set(int bit) {
        int index = bit / 64;
        if(index > bitset.length) {
            expand(bit);
        }

        // 1L is 00000..0001
        bitset[index] |= (1L << (bit - (index * 64)));
    }

    public void set(Curve c) {
        set(c.getCurveID());
    }

    public void clear(int bit) {
        int index = bit / 64;
        if(index > bitset.length) {
            expand(bit);
        }

        bitset[index] &= ~(1L << (bit - (index * 64)));
    }

    public void clear(Curve c) {
        clear(c.getCurveID());
    }

    public boolean isSet(int bit) {
        int index = bit / 64;
        if(index > bitset.length) {
            return false;
        }
        return (bitset[index] & (1L << (bit - (index * 64)))) == 0L;
    }

    public boolean isSet(Curve c) {
        return isSet(c.getCurveID());
    }

    public boolean isZero() {
        boolean isZero = true;
        for(int i = 0; i < numWords(); i++) {
            isZero = (bitset[i] == 0L) && isZero;
        }
        return isZero;
    }

    public void logicalXOR(FastCurveSet other, FastCurveSet result) {
        int maxLen = Math.max(numWords(), other.numWords());

        if(maxLen > numWords()) {
            expand(other.numBits());
        } else if (maxLen > other.numWords()) {
            other.expand(numBits());
        }

        result.expand(maxLen);

        for(int i = 0; i < numWords(); i++) {
            result.bitset[i] = bitset[i] ^ other.bitset[i];
        }
    }

    public FastCurveSet logicalXOR(FastCurveSet other) {
        FastCurveSet result = new FastCurveSet();
        logicalXOR(other, result);
        return result;
    }

    // FIXME might make something like this the equals for the class
    public boolean logicalEQ(FastCurveSet other) {
        FastCurveSet test = logicalXOR(other);
        return test.isZero();
    }

    public void logicalAND(FastCurveSet other, FastCurveSet result) {
        int maxLen = Math.max(numWords(), other.numWords());

        if(maxLen > numWords()) {
            expand(other.numBits());
        } else if (maxLen > other.numWords()) {
            other.expand(numBits());
        }

        result.expand(maxLen);

        for(int i = 0; i < numWords(); i++) {
            result.bitset[i] = bitset[i] & other.bitset[i];
        }
    }

    public void logicalAND(FastCurveSet other) {
        logicalAND(other, this);
    }

    public void logicalOR(FastCurveSet other, FastCurveSet result) {
        int maxLen = Math.max(numWords(), other.numWords());

        if(maxLen > numWords()) {
            expand(other.numBits());
        } else if (maxLen > other.numWords()) {
            other.expand(numBits());
        }

        result.expand(maxLen);

        for(int i = 0; i < numWords(); i++) {
            result.bitset[i] = bitset[i] | other.bitset[i];
        }
    }

    public void logicalOR(FastCurveSet other) {
        logicalOR(other, this);
    }

    public void logicalNOT(FastCurveSet result) {
        result.expand(numBits());

        for(int i = 0; i < numWords(); i++) {
            result.bitset[i] = ~bitset[i];
        }
    }

    public void logicalNOT() {
        logicalNOT(this);
    }

    // is this set a subset of other
    public boolean subseteqOF(FastCurveSet other) {
        FastCurveSet mask = new FastCurveSet();

        logicalAND(other, mask);
        return logicalEQ(mask);
    }


    // is this set a subset of other if we ignore the curves in mask
    public boolean subseteqOF(FastCurveSet other, FastCurveSet mask) {
        FastCurveSet thisMasked = new FastCurveSet(this);
        thisMasked.logicalXOR(mask);

        FastCurveSet otherMasked = new FastCurveSet(other);
        otherMasked.logicalXOR(mask);

        otherMasked.logicalAND(thisMasked);
        return otherMasked.logicalEQ(thisMasked);
    }

    public void intersection(FastCurveSet other, FastCurveSet result, FastCurveSet mask) {
        FastCurveSet thisMasked = new FastCurveSet(this);
        thisMasked.logicalXOR(mask);

        FastCurveSet otherMasked = new FastCurveSet(other);
        otherMasked.logicalXOR(mask);

        otherMasked.logicalAND(thisMasked, result);
    }

    public boolean intersectionEmpty(FastCurveSet other, FastCurveSet mask) {
        FastCurveSet test = new FastCurveSet();
        intersection(other, test, mask);
        return test.isZero();
    }

    public boolean intersectionNOTempty(FastCurveSet other, FastCurveSet mask) {
        return !intersectionEmpty(other, mask);
    }




    // ---------------------------------------------------------------------------------------
    //                          Iterator impl
    // ---------------------------------------------------------------------------------------


    // how else to iterate through all the set bits??

    public Iterator<Integer> iterator() {
        iteratorCursor = 0;
        return this;
    }

    public boolean hasNext() {
        boolean foundNext = false;
        for(int i = iteratorCursor + 1; i < numBits(); i++) {
            if(isSet(i)) {
                foundNext = true;
            }
        }
        return foundNext;
    }

    public Integer next() {

        for(int i = iteratorCursor + 1; i < numBits(); i++) {
            if(isSet(i)) {
                iteratorCursor = i;
                return i;
            }
        }
        // didn't find a next
        throw new NoSuchElementException();

    }

    public void remove() {
        throw new UnsupportedOperationException();
    }


}
