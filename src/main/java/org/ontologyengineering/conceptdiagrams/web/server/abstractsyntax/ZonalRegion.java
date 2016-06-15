package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;

/**
 * <IN, OUT, v> = {(in,out) : EZ(v) U MZ(v) . IN \subseteq in and OUT \ subseteq out}
 *
 */
public class ZonalRegion {

    private FastCurveSet IN;
    private FastCurveSet OUT;
    private LabelledDiagram v;

    private HashSet<Zone> result;

    public ZonalRegion() {

    }

    public ZonalRegion(FastCurveSet IN, FastCurveSet OUT, LabelledDiagram v) {
        this.IN = IN;
        this.OUT = OUT;
        this.v = v;
        // should be that all the zones are in v
        result = null;
    }

    public FastCurveSet IN() {
        return IN;
    }

    public FastCurveSet OUT() {
        return OUT;
    }

    public boolean INcontainsCurve(Curve c) {
        return IN().isSet(c);
    }

    public LabelledDiagram diagram() {
        return v;
    }

    public boolean OUTcontainsCurve(Curve c) {
        return OUT().isSet(c);
    }

    // FIXME : implement if needed
    public AbstractSet<Zone> computeZonalRegion() {
        return result;
    }

    // is A <= B
    // When thinking about the actual IN OUT sets, not just object inclusion
    public static boolean zonalRegionSubSets(Collection<ZonalRegion> A, Collection<ZonalRegion> B) {

        for(ZonalRegion zr : A) {
            for(ZonalRegion zrB : B) {
                if(zr.IN().logicalEQ(zrB.IN()) && zr.OUT().logicalEQ(zrB.OUT())) {

                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean zonalRegionSetsEQ(Collection<ZonalRegion> A, Collection<ZonalRegion> B) {
        return zonalRegionSubSets(A, B) && zonalRegionSubSets(B, A);
    }

}
