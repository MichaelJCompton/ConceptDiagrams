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

    private AbstractSet<Zone> IN;
    private AbstractSet<Zone> OUT;
    private LabelledDiagram v;

    private AbstractSet<Zone> result;

    public ZonalRegion(AbstractSet<Zone> IN, AbstractSet<Zone> OUT, LabelledDiagram v) {
        this.IN = IN;
        this.OUT = OUT;
        this.v = v;
        // should be that all the zones are in v
    }

    public AbstractSet<Zone> IN() {
        return IN;
    }

    public AbstractSet<Zone> OUT() {
        return OUT;
    }

    // FIXME : implement if needed
    public AbstractSet<Zone> computeZonalRegion() {
        result = null;
        return null;
    }

}
