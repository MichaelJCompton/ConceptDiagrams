package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
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
    private HashSet<Curve> in;
    private FastCurveSet fastCurveSet;

    // Ideally this would be done in the concrete, but there isn't a nice way (other than reimplementing all the area
    // computations) to be able to do it in client side code (even the gwt version of awt.geom isn't serializable).  E.G.
    // as we go through the construction history, we do this bookkeeping in the concrete zone.
    //
    // So as we go through the command history (thus the transformations) we keep a record of the area of the
    // zone as computed from the concrete syntax.  Each time an intersecting curve is added, the area is taken away.
    // If the zone's area goes to 0,  then it is removed from the Curve and LabelledDiagram as the abstract zone it
    // represents is now covered and thus a missing zone.
    //
    // For example if we start with zones {({}, {A,B}), ({A}, {B}), ({A,B}, {}), ({B}, {A})}
    // and add a C, such that the new zones are
    // {({}, {A,B,C}), ({A}, {B,C}), ({A,B}, {C}), ({A,B,C}, {}), ({A,C}, {B}), ({B,C}, {A}), ({C}, {A,B})}
    // then the zone ({B}, {A}) which would have become ({B}, {A,C}) became a missing zone.  Now we needed the concerete
    // zone for that during the construction process (thus is was kept on the client side and this command history),
    // but now the ({B}, {A}) has been covered over - its remaining area is zero once the concrete intersection resulting
    // from B intersecting C has been subtracted from it - so we remove it from the lists of active zones.
    private Area remainingArea;

    public Zone() {
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

    public void shade() {
        isShaded = true;
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

    public boolean INcontainsCurve(Curve c) {
        return fastCurveSet.isSet(c);
    }

    public boolean OUTcontainsCurve(Curve c) {
        return c.diagram() == diagram() && !fastCurveSet.isSet(c);
    }


    public FastCurveSet minusCurveSet(FastCurveSet takeAway) {
        FastCurveSet result = new FastCurveSet();

        fastCurveSet.logicalAND(takeAway, result);
        result.logicalNOT();
        fastCurveSet.logicalAND(result, result);

        return result;
    }

    public AbstractSet<Curve> IN() {
        return in;
    }

    // Don't think spiders are children of their zones.  They can live in many zones.
    // But we may need to collect the spiders that live in particular zones.  At the moment the diagrams (/spiders)
    // give this the other way around.
    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }

    // really expecting just a main zone here as the starting point
    protected void setRemainingArea(ConcreteZone z) {
        remainingArea = new Area(new RoundRectangle2D.Double(z.getX(), z.getY(), z.getWidth(), z.getHeight(), z.getCornerRadius(), z.getCornerRadius()));
    }

    protected void setRemainingArea() {
        if(getConcreteRepresentation() != null) {
            setRemainingArea(getConcreteRepresentation());
        }
    }


    private Area remainingArea() {
        return remainingArea;
    }

    protected boolean covered() {
        return remainingArea().isEmpty();
    }

    protected void cover() {
        if(covered()) {
            for(Curve c : in) {
                c.checkAndCoverZone(this);
            }
        }
    }

    // subtract one from the other - remembering to use the orriginal zones
    protected static void xorZoneAreas(Zone z1, Zone z2) {
        Area z1_orig = new Area(z1.remainingArea());

        z1.subtractArea(z2.remainingArea());
        z2.subtractArea(z1_orig);
    }

    // subtracts other's area from this area
    protected void subtractArea(Area other) {
        remainingArea().subtract(other);
    }

    // subtracts other's area from this area
    protected void subtractZone(Zone other) {
        remainingArea().subtract(other.remainingArea());
    }


    protected void intersectMainZoneOriginal(Zone other) {
        remainingArea().intersect(new Area(new RoundRectangle2D.Double(other.getConcreteRepresentation().getX(),
                other.getConcreteRepresentation().getY(),
                other.getConcreteRepresentation().getWidth(),
                other.getConcreteRepresentation().getHeight(),
                other.getConcreteRepresentation().getCornerRadius(),
                other.getConcreteRepresentation().getCornerRadius())));
    }

    // FIXME might need this if we copy zones
//    public Zone clone() {
//
//    }

}
