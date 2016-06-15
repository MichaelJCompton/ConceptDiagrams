package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br> Date: September 2015<br> See license information in base directory.
 */


import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Set;

/**
 * a LabelledDiagram where
 *
 * lambda_s has range V_I
 * lambda_c has range V_C
 */
public class ClassAndObjectPropertyDiagram extends LabelledDiagram {

    public ClassAndObjectPropertyDiagram() {
        super();
    }

    public ClassAndObjectPropertyDiagram(LabelledMultiDiagram parent) {
        super(parent);
    }

    // TODO : add constraints


    @Override
    // Z(v) \ (Z' U Z*(v))    also \ the removed curves
    public AbstractSet<Zone> getZonesToTest(AbstractSet<Zone> Zdash, FastCurveSet curveMask, FastCurveSet IN, FastCurveSet OUT, FastCurveSet removedCurves) {
        AbstractSet<Zone> zonesToTest = new HashSet<Zone>();
        for (Zone z : Z()) {
            boolean keep = true;
            for(Zone zdash : Zdash) {
                if(z.inAsFastCurveSet().logicalEQ(zdash.inAsFastCurveSet(), curveMask)) {
                    keep = false;
                }
            }
            if(z.isShaded() && shadedAfterMinus(z, curveMask)) {
                keep = false;
            }

            if(keep && z.inAsFastCurveSet().intersectionEmpty(removedCurves, curveMask)) {
               zonesToTest.add(z);
            }
        }
        return zonesToTest;
    }

    // Definition 30 ... carried down from LabelledMultiDiagram
    public Set<Arrow> DE(Arrow arrow) {

        // FIXME --- Cache these and just build up???

        HashSet<Arrow> result = new HashSet<Arrow>();
        for (Arrow a : A()) {
            if (a.isInverse() == arrow.isInverse()) {  // because it's a ClassAndObjectPropertyDiagram, they are ObjectPropertyArrow(s)
                result.add(a);
            }
        }
        return result;
    }


}
