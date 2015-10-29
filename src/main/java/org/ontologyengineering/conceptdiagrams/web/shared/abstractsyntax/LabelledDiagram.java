package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.*;

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
public abstract class LabelledDiagram extends AbstractDiagram<LabelledMultiDiagram, ConcreteDiagram> {

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
    //private AbstractSet<Curve> curves;   // should get from the Map
    private AbstractSet<Zone> zones;
    private AbstractSet<Arrow> arrows;
    private AbstractMap<Integer, Curve> curveMap;  // could probably just be an array, but not sure if it will be dense in the end

    private FastCurveSet unlabelledCurvesFast, labelledCurvesFast;

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
        //curves = new HashSet<Curve>();
        curveMap = new HashMap<Integer, Curve>();
        unlabelledCurvesFast = new FastCurveSet();
        labelledCurvesFast = new FastCurveSet();
        zones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        shadedZonesUpToDate = false;
        arrows = new HashSet<Arrow>();
        curvesInUse = new FastCurveSet();
        boundaryRectangle = new BoundaryRectangle();
    }


    public FastCurveSet getCurvesInUse() {
        return curvesInUse;
    }

    public Curve getCurve(int i) {
        return curveMap.get(i);
    }

    // Implements the same intention as that in the description.  But through the intersection code in the concrete
    // syntax we already have everything we need, so no need to make the IN sets, etc., can just do from what we
    // know from the concrete.
    public void addUnlabelledCurve(ConcreteCurve curve) {
        if(curve.getBoundaryRectangle().getAbstractSyntaxRepresentation().diagram() != this) {
            return;
        }

        Curve newCurve = new Curve(this);
        newCurve.setID(nextCurveID());
        curveMap.put(newCurve.getCurveID(), newCurve);
        curvesInUse.set(newCurve);
        unlabelledCurvesFast.set(newCurve);


        for(ConcreteZone z : curve.getAllZones()) {
            boolean allCurvesPresent = true;  // the zone isn't there till the intersections that make it are present
            for(ConcreteCurve c : z.getCurves()) {
                if(c.getAbstractSyntaxRepresentation() == null ||
                        c.getAbstractSyntaxRepresentation().diagram() != this) {
                    allCurvesPresent = false;
                }
            }
            if(allCurvesPresent) {
                Zone newZone = new Zone(this, false);  // no shading at the start

                HashSet<Curve> inSet = new HashSet<Curve>();
                inSet.add(newCurve);
                for(ConcreteCurve c : z.getCurves()) {
                    inSet.add(c.getAbstractSyntaxRepresentation());
                }
                for(ConcreteCurve c : z.getCompletelyEnclosingCurves()) {
                    inSet.add(c.getAbstractSyntaxRepresentation());
                }
                newZone.setInSet(inSet);

                Z().add(newZone);

                z.setAbstractSyntaxRepresentation(newZone);
                newZone.setConcreteRepresentation(z);
            }

        }

        curve.setAbstractSyntaxRepresentation(newCurve);
        newCurve.setConcreteRepresentation(curve);

        // Spider part not implemented - leave until we do ConceptDiagrams
    }

    public void labelCurve(ConcreteCurve curve) {
        if(curve.getBoundaryRectangle().getAbstractSyntaxRepresentation().diagram() != this ||
                curve.getAbstractSyntaxRepresentation() == null) {
            return;
        }

        curve.getAbstractSyntaxRepresentation().setLabel(curve.labelText());
        unlabelledCurvesFast.clear(curve.getAbstractSyntaxRepresentation());
        labelledCurvesFast.set(curve.getAbstractSyntaxRepresentation());
    }

    protected Integer nextCurveID() {
        return curveIDgenerator.getIDasNum();
    }

    public Boolean isEmptyDiagram() {
        return spiders.size() == 0 && curveMap.size() == 0 && arrows.size() == 0;
    }

    public BoundaryRectangle boundaryRectangle() {
        return boundaryRectangle;
    }

    public AbstractSet<Spider> Sigma() {
        return spiders;
    }

    public Collection<Curve> K() {
        return curveMap.values();
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
        result.addAll(K());
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

    private FastCurveSet getUnlabelledCurvesFast() {
        return unlabelledCurvesFast;
    }

    private FastCurveSet getLabelledCurvesFast() {
        return labelledCurvesFast;
    }

    public AbstractCollection<Curve> unlabelledCurves() {
        return UK();
    }

    // FIXME can cache too
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


    // Definition 22
    //
    // in general I'm ignoring - or at least deffering dealing with it, it just removes curves from zones, so squashes
    // some together, but for the moment we can safely ignore it and the copies will be removed later.
    public AbstractSet<Zone> ISC(Arrow a) {
        HashSet<Zone> result = new HashSet<Zone>();

        if(a.getTarget().diagram() == this) {
            if(a.getTarget().getClass() == BoundaryRectangle.class) {
                result.add(new Zone());
            } else if(a.getTarget().getClass() == Curve.class) {
                // could do the set based method, but again we have all the info in the concrete syntax
                // the ISC is the zones of labelled curves that enclose non shaded zones of the arrow target
                ConcreteCurve c = ((Curve) a.getTarget()).getConcreteRepresentation();

                for(ConcreteZone z : c.getAllZones()) {
                    if(!z.shaded()) {
                        result.add(z.getAbstractSyntaxRepresentation());
                    }
                }

            }
        }
        return result;
    }

    public FastCurveSet getISCcurveMask(Arrow a) {
        FastCurveSet result = null;
        if(a.getTarget().diagram() == this) {
            if (a.getTarget().getClass() == Curve.class) {
                result = new FastCurveSet(getUnlabelledCurvesFast());
                result.set((Curve) a.getTarget());
            }
        }
        return result;
    }

    // Definition 23 & 24
    // curve mask is any removed curves by a -.  Done this way so I don't have to implement the - transformation.
    // this is the spot where it is used.
    // at this point the mask will remove any duplicates that ignoring the - will have allowed in
    public AbstractSet<ZonalRegion> SZR(AbstractSet<Zone> Zdash, FastCurveSet curveMask) {
        HashSet<ZonalRegion> result = new HashSet<ZonalRegion>();

        FastCurveSet curveMaskAltered = new FastCurveSet(curveMask);

        for (Zone z_dash : Zdash) {
            boolean existingAnswer = false;
            for (ZonalRegion zr : result) {
                if (zr.IN().subseteqOF(z_dash.inAsFastCurveSet(), curveMask) && zr.OUT().subseteqOF(z_dash.outAsFastCurveSet(), curveMask)) {
                    existingAnswer = true;
                }
            }

            if (!existingAnswer) {
                FastCurveSet IN = new FastCurveSet(z_dash.inAsFastCurveSet());
                IN.logicalXOR(curveMask);
                FastCurveSet OUT = new FastCurveSet(z_dash.outAsFastCurveSet());
                OUT.logicalXOR(curveMask);

                // FIXME : could probably make an approx just once outside the loop (see also below)
                // remove containing curves from IN
                for(int i = 0; i < IN.numBits(); i++) {
                    if(IN.isSet(i)) {
                        // does this curve contain any other
                        for(int j = 0; j < IN.numBits(); j++) {
                            if(IN.isSet(j) && i != j) {
                                if(getCurve(j).getConcreteRepresentation().getAllEnclosingCurves().contains(getCurve(i).getConcreteRepresentation())) {
                                    IN.clear(i);
                                    break;
                                }
                            }
                        }
                    }
                }

                FastCurveSet removedCurves = new FastCurveSet();
                // FIXME : again could probably make an approx outside the for
                // remove disjoints from OUT
                for(int i = 0; i < OUT.numBits(); i++) {
                    if (OUT.isSet(i)) {
                        // is this curve disjoint from all things in IN
                        boolean isDisjoint = true;
                        for (int j = 0; j < IN.numBits(); j++) {
                            if (IN.isSet(j)) {
                                if (getCurve(j).getConcreteRepresentation().getIntersectingCurves().contains(getCurve(i).getConcreteRepresentation()) ||
                                        getCurve(j).getConcreteRepresentation().getAllEnclosingCurves().contains(getCurve(i).getConcreteRepresentation())) {
                                    isDisjoint = false;
                                }
                            }
                            if (isDisjoint) {
                                OUT.clear(i);
                                removedCurves.set(i);
                            }
                        }
                    }
                }

                // FIXME : can I make a good approximation of this outside the outer for loop
                AbstractSet<Zone> zonesToTest = getZonesToTest(Zdash, curveMask, IN, OUT, removedCurves);

                FastCurveSet iterationGuide = new FastCurveSet(IN);
                iterationGuide.logicalOR(OUT);
                for (int i = iterationGuide.numBits() - 1; i >= 0; i--) {  // reverse order
                    if (iterationGuide.isSet(i)) {
                        // try removing curve i
                        boolean removable = true;

                        // curve mask cannot contain curve i because of the XOR with IN and OUT, so I can twidle its
                        // bits to get the right behaviour for IN\{K} etc given this mask
                        curveMaskAltered.set(i);

                        for (Zone z : zonesToTest) {
                            //(IN\{k} \subseteq z.in and OUT\{k} \subseteq z.out)
                            if (IN.subseteqOF(z.inAsFastCurveSet(), curveMaskAltered) && OUT.subseteqOF(z.outAsFastCurveSet(), curveMaskAltered)) {
                                removable = false;
                            }
                        }
                        curveMaskAltered.clear(i);

                        if (removable) {
                            IN.clear(i);
                            OUT.clear(i);
                        }
                    }
                }
                result.add(new ZonalRegion(IN, OUT, this));
            }
        }

        return result;
    }

    public abstract AbstractSet<Zone> getZonesToTest(AbstractSet<Zone> Zdash, FastCurveSet curveMask, FastCurveSet IN, FastCurveSet OUT, FastCurveSet removedCurves);


    // Definition 25
    public AbstractSet<ZonalRegion> SC(Arrow arrow) {
        if(arrow.diagram() == this) {
            return SZR(ISC(arrow), getISCcurveMask(arrow));
        }
        return null;
    }



}
