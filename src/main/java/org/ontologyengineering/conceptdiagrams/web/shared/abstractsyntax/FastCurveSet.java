package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import java.util.Arrays;

/**
 * A minimal implementation of a bitset.  Seems that java.util.BitSet isn't supported in GWT, so this will be a
 * minimal version good enough for the simple operations required for Zone calculations
 *
 * the bitsets are implemented as a long array and all the opperations are just java's bitwise and shift operators.
 * No resizing or anything, just use as is and expand all operations to the max of the two
 */
public class FastCurveSet {

    private long[] bitset;

    // let's assume an initial max of two longs as enough and expand if required
    // that would be 128 curves in a single diagram, so it's enough to not be exceeded often
    public FastCurveSet() {
        bitset = new long[2];
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

    public void clear(int bit) {
        int index = bit / 64;
        if(index > bitset.length) {
            expand(bit);
        }

        bitset[index] &= ~(1L << (bit - (index * 64)));
    }

    public boolean isSet(int bit) {
        int index = bit / 64;
        if(index > bitset.length) {
            return false;
        }
        return (bitset[index] & (1L << (bit - (index * 64)))) == 0;
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

}
