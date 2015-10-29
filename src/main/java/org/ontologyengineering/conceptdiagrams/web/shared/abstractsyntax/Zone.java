package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 * A Zone is a pair (in, K - in) where in \subseteq K.
 *
 * Here with represent the zone with the in set, the set K - in is computed from the diagram LabelledDiagram.  This will
 * be ok if we don't need to compute it much, but it may need to be cached if it requires computing often.  However, as
 * diagram LabelledDiagram changes the K - in set will also be changed, so it would either need to be notified to each
 * child zone, or if it's only needed for particular operations it should be computed at the start of the operation and
 * kept as valid for only the operation.
 */
public class Zone extends DiagramElement<LabelledDiagram, ConcreteZone> {

    private Boolean isShaded;
    private AbstractSet<Curve> in;
    private FastCurveSet fastCurveSet;

    Zone() {
        isShaded = false;
        in = new HashSet<Curve>();
        fastCurveSet = new FastCurveSet();
    }

    Zone(Boolean shading) {
        isShaded = shading;
        in = new HashSet<Curve>();
        fastCurveSet = new FastCurveSet();
    }

    Zone(LabelledDiagram diagram, Boolean shading) {
        super(diagram);
        isShaded = shading;
        in = new HashSet<Curve>();
        fastCurveSet = new FastCurveSet();
    }

    public FastCurveSet inAsFastCurveSet() {
        return fastCurveSet;
    }

    public FastCurveSet outAsFastCurveSet() {
        FastCurveSet result = new FastCurveSet();

        inAsFastCurveSet().logicalNOT(result);
        result.logicalAND(diagram().getCurvesInUse());

        return result;
    }

    public Boolean isShaded() {
        return isShaded;
    }

    public void setInSet(AbstractCollection<Curve> inSet) {
        for(Curve c : inSet) {
            addToInSet(c);
        }
    }

    public void addToInSet(Curve c) {
        in.add(c);
        fastCurveSet.set(c.getCurveID());
    }

    public void removeFromInSet(Curve c) {
        in.remove(c);
        fastCurveSet.clear(c.getCurveID());
    }


    public FastCurveSet minusCurveSet(FastCurveSet takeAway) {
        FastCurveSet result = new FastCurveSet();

        fastCurveSet.logicalAND(takeAway, result);
        result.logicalNOT();
        fastCurveSet.logicalAND(result, result);

        return result;
    }

    // Don't think spiders are children of their zones.  They can live in many zones.
    // But we may need to collect the spiders that live in particular zones.  At the moment the diagrams (/spiders)
    // give this the other way around.
    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }


    // FIXME might need this if we copy zones
//    public Zone clone() {
//
//    }

}
