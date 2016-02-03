package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import java.util.AbstractSet;

/**
 * <IN, OUT, v> = {(in,out) : EZ(v) U MZ(v) . IN \subseteq in and OUT \ subseteq out}
 *
 */
public class ZonalRegion {

    private FastCurveSet IN;
    private FastCurveSet OUT;
    private LabelledDiagram v;

    private AbstractSet<Zone> result;

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

}
