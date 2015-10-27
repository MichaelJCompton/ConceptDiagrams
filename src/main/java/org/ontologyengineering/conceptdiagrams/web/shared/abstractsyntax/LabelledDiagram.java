package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 *
 * A LabelledDiagram is a tuple (rect, Sigma, K, Z, Z*, eta, tau, A, lambda_s, lambda_c, lambda_a, lambda_#)
 *
 * where
 *
 * rect     : the boundry rectangle of the diagram
 * Sigma    : finite set of Spider(s)
 * K        : finite set of Curve(s)
 * Z        : set of Zone(s) s.t. z \subseteq {(in, K-in) : in \subseteq K}
 * Z*       : set of shaded Zone(s) s.t. Z* \subseteq Z
 * eta      : function giving the set of Zone(s) a Spider resides in
 * tau      : reflexive, symetric (equality) relation on Sigma
 * A        : finitie multiset of arrows (s,t,o) s.t. s,t : Sigma U K U rect
 * lambda_s : labels Spider(s) as OWL individuals or literals (V_I U V_LT)
 * lambda_c : labels Curve(s) as OWL concepts or datatypes (V_C U V_DT)
 * lambda_a : labels Arrow(s) as OWL object property expressions (V_OP U V{^-}_{OP})
 * lambda_# : labels Arrow(s) with <=, =, =< for OWL cardinality constraints
 *
 * the lambda and eta and tau are implemented in the Spider, Arrow and Curve classes
 *
 * TODO : for the moment the lambda functions aren't implemented until the code is hooked up wth WebProtege
 */
public abstract class LabelledDiagram extends AbstractDiagram<LabelledMultiDiagram> {

    // TODO
    // missingZones
    // lambda functions

    // TODO : may also have functions in the class to access the underlying functions for eta and tau and lambdas??
    //      : programatically I don' think it matters as if we have the spider we have access to the function
    //      : but would allow writing expressions like in the syntax ... just seems that implementing it as part of
    //      : spider is simpler than recording some sort of lookup table here.


    // TODO : implement the functions as Sigma etc

    private BoundaryRectangle boundaryRectangle;
    private AbstractSet<Spider> spiders;
    private AbstractSet<Curve> curves;
    private AbstractSet<Zone> zones;
    private AbstractSet<Arrow> arrows;

    // Shading is part of the zone in the implementation, so it's computed and cached here.
    private AbstractSet<Zone> shadedZones;
    private boolean shadedZonesUpToDate;

    private FastCurveSet curvesInUse;

    // one for each diagram
    private IDGenerator curveIDgenerator = new IDGenerator();


    LabelledDiagram() {
        initialiseLabelledDiagram();
    }

    LabelledDiagram(LabelledMultiDiagram parent) {
        super(parent);
        initialiseLabelledDiagram();
    }

    private void initialiseLabelledDiagram() {
        spiders = new HashSet<Spider>();
        curves = new HashSet<Curve>();
        zones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        shadedZonesUpToDate = false;
        arrows = new HashSet<Arrow>();
        curvesInUse = new FastCurveSet();
        boundaryRectangle = new BoundaryRectangle();
    }

    protected Integer nextCurveID() {
        return curveIDgenerator.getIDasNum();
    }

    public Boolean isEmptyDiagram() {
        return spiders.size() == 0 && curves.size() == 0 && arrows.size() == 0;
    }

    public BoundaryRectangle boundaryRectangle() {
        return boundaryRectangle;
    }

    public AbstractSet<Spider> Sigma() {
        return spiders;
    }

    public AbstractSet<Curve> K() {
        return curves;
    }

    public AbstractSet<Zone> Z() {
        return zones;
    }

    public AbstractSet<Zone> Zstar() {
        return getShadedZones();
    }

    public AbstractSet<Zone> getShadedZones() {
        if (!shadedZonesUpToDate) {
            computeShadedZones();
        }
        return shadedZones;
    }

    private void computeShadedZones() {
        shadedZones = new HashSet<Zone>();
        for(Zone z : zones) {
            if(z.isShaded()) {
                shadedZones.add(z);
            }
        }
        shadedZonesUpToDate = true;
    }

//    // Don't actually need an implementation??
//    public AbstractSet<Zone> MZ() {
//        return null;
//    }
//
//    // Dont' actually need an implementation??
//    public AbstractSet<Zone> EZ() {
//        return null;
//    }

    public AbstractSet<Zone> eta(Spider s) {
        return s.eta_fn();
    }

    public Boolean tau(Spider s1, Spider s2) {
        // should probably only return true if :
        //      s1 is in this diagram
        //      s2 is in this diagram
        //      they are related by their tau relations
        // bt I don't think they can be related in the implementation unless they are in the same diagram.
        return s1.tau_fn(s2);
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        AbstractSet<DiagramElement> result = new HashSet<DiagramElement>();
        result.add(boundaryRectangle);
        result.addAll(spiders);
        result.addAll(curves);
        result.addAll(zones);
        result.addAll(arrows);
        return result;
    }


    public AbstractCollection<Curve> labelledCurves() {
        return LK();
    }

    // FIXME should cache these
    public AbstractCollection<Curve> LK() {
        AbstractSet<Curve> result = new HashSet<Curve>();
        for(Curve c : K()) {
            if(c.isLabelled()) {
                result.add(c);
            }
        }
        return result;
    }

    public AbstractCollection<Curve> unlabelledCurves() {
        return UK();
    }

    public AbstractCollection<Curve> UK() {
        AbstractSet<Curve> result = new HashSet<Curve>();
        for(Curve c : K()) {
            if(c.isUnLabelled()) {
                result.add(c);
            }
        }
        return result;
    }

    public AbstractSet<Zone> Z(Curve k) {
        return k.zones();
    }

    // FIXME .. might move these into the construction sequence states

//    /*
//    Equiv(k) = {k' : K() . Z(k)\Z^* = Z(k')\Z^*}
//     */
//    public AbstractSet<Curve> Equiv(Curve K) {
//        AbstractSet<Curve> result = new HashSet<Curve>();
//
//        return result;
//    }

//    /*
//    Subs(k) = {k' : K() . Z(k)\Z^* < Z(k')\Z^*}
//     */
//    public AbstractSet<Curve> SubS(Curve K) {
//        AbstractSet<Curve> result = new HashSet<Curve>();
//
//        return result;
//    }



    // Equiv
    // SubS
    // SupS
    // Disj
    // require fast zone sets
}
