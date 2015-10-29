package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import java.util.AbstractSet;
import java.util.HashSet;

/**
 * A LabelledDiagram where
 *
 * A = \emptyset
 * lambda_s has range V_I
 * lambda_c has range V_C
 */
public class DatatypeDiagram extends LabelledDiagram {

    public DatatypeDiagram() {
        super();
    }

    public DatatypeDiagram(LabelledMultiDiagram parent) {
        super(parent);
    }

    // TODO : datatype diagram constraints to add
    // - no arrows


    @Override
    public AbstractSet<Zone> getZonesToTest(AbstractSet<Zone> Zdash, FastCurveSet curveMask, FastCurveSet IN, FastCurveSet OUT, FastCurveSet removedCurves) {
        AbstractSet<Zone> zonesToTest = new HashSet<Zone>();
        for (Zone z : Z()) {
            if (!Zdash.contains(z)) {
                // fine but do we need to test it given what we have removed from OUT, so maybe remove
                if(removedCurves.intersectionEmpty(z.inAsFastCurveSet(), curveMask)) {
                    zonesToTest.add(z);
                }
            }
        }
        return zonesToTest;
    }

}
