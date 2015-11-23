package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br> Date: September 2015<br> See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.*;

/**
 * A LabelledDiagram is a tuple (rect, Sigma, K, Z, Z*, eta, tau, A, lambda_s, lambda_c, lambda_a, lambda_#)
 * <p/>
 * where
 * <p/>
 * rect     : the boundry rectangle of the diagram Sigma    : finite set of Spider(s) K        : finite set of Curve(s)
 * Z        : set of Zone(s) s.t. z \subseteq {(in, K-in) : in \subseteq K} Z*       : set of shaded Zone(s) s.t. Z*
 * \subseteq Z eta      : function giving the set of Zone(s) a Spider resides in tau      : reflexive, symetric
 * (equality) relation on Sigma A        : finitie multiset of arrows (s,t,o) s.t. s,t : Sigma U K U rect lambda_s :
 * labels Spider(s) as OWL individuals or literals (V_I U V_LT) lambda_c : labels Curve(s) as OWL concepts or datatypes
 * (V_C U V_DT) lambda_a : labels Arrow(s) as OWL object property expressions (V_OP U V{^-}_{OP}) lambda_# : labels
 * Arrow(s) with <=, =, =< for OWL cardinality constraints
 * <p/>
 * the lambda and eta and tau are implemented in the Spider, Arrow and Curve classes
 * <p/>
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

    private AbstractMap<Arrow, AbstractSet<ZonalRegion>> SCcache;  // only really counts for one step, but saves making it multiple times in a single iteration - e.g. in add curve label
    private AbstractMap<Curve, AbstractSet<Arrow>> INTcache;
    private AbstractMap<Curve, AbstractSet<Curve>> equivCache;
    private AbstractMap<Curve, AbstractSet<Curve>> subsCache;
    private AbstractMap<Curve, AbstractSet<Curve>> supsCache;
    private AbstractMap<Curve, AbstractSet<Curve>> disjCache;
    private HashMap<Arrow, AbstractSet<Arrow>> TEA;
    private HashMap<Arrow, AbstractSet<Arrow>> ITEA;
    private HashMap<Arrow, AbstractSet<Arrow>> ITCA;
    private HashMap<Arrow, AbstractSet<Arrow>> ITDA;

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

        SCcache = new HashMap<Arrow, AbstractSet<ZonalRegion>>();
        INTcache = new HashMap<Curve, AbstractSet<Arrow>>();

        equivCache = new HashMap<Curve, AbstractSet<Curve>>();
        subsCache = new HashMap<Curve, AbstractSet<Curve>>();
        supsCache = new HashMap<Curve, AbstractSet<Curve>>();
        disjCache = new HashMap<Curve, AbstractSet<Curve>>();

        TEA = new HashMap<Arrow, AbstractSet<Arrow>>();
        ITEA = new HashMap<Arrow, AbstractSet<Arrow>>();
        ITCA = new HashMap<Arrow, AbstractSet<Arrow>>();
        ITDA = new HashMap<Arrow, AbstractSet<Arrow>>();
    }


    public FastCurveSet getCurvesInUse() {
        return curvesInUse;
    }

    public Curve getCurve(int i) {
        return curveMap.get(i);
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
        for (Zone z : zones) {
            if (z.isShaded()) {
                shadedZones.add(z);
            }
        }
        shadedZonesUpToDate = true;
    }


    public AbstractSet<Arrow> A() {
        return arrows;
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
        for (Curve c : K()) {
            if (c.isLabelled()) {
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
        for (Curve c : K()) {
            if (c.isUnLabelled()) {
                result.add(c);
            }
        }
        return result;
    }

    public AbstractSet<Zone> Z(Curve k) {
        return k.zones();
    }


    public String CL(Curve c) {
        if (c != null) {
            return c.getLabel();
        } else {
            return null;
        }
    }

    public String CL(int i) {
        return CL(getCurve(i));
    }


    // are all the zones of curve1 that aren't in curve2 shaded?
    private boolean allZonesOutsideShaded(Curve curve1, Curve curve2) {
        boolean result = true;
        for (Zone z : curve1.zones()) {
            if (!z.INcontainsCurve(curve2) && !z.isShaded()) {
                result = false;
                break;
            }
        }
        return result;
    }

    // are all the zones of in the intersection shaded?
    private boolean allZonesIntersectionShaded(Curve curve1, Curve curve2) {
        boolean result = true;
        for (Zone z : curve1.zones()) {
            if (z.INcontainsCurve(curve2) && !z.isShaded()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void ensureInCache(Curve c, AbstractMap<Curve, AbstractSet<Curve>> cache) {
        if (!cache.containsKey(c)) {
            cache.put(c, new HashSet<Curve>());
        }
    }

    private void addToCahce(Curve curve, Curve other, AbstractMap<Curve, AbstractSet<Curve>> cache) {
        ensureInCache(curve, cache);
        cache.get(curve).add(other);
    }

    private void recalcEquivSubsCachesCurveAdded(Curve curve) {
        for (Curve other : K()) {
            if (curve != other) {
                if (curve.getConcreteRepresentation().getCompletelyContainedZones().contains(other.getConcreteRepresentation().getMainZone())) {
                    if (allZonesOutsideShaded(other, curve)) {
                        // equiv
                        addToCahce(curve, other, equivCache);
                        addToCahce(other, curve, equivCache);
                    } else {
                        addToCahce(curve, other, subsCache);
                        addToCahce(other, curve, supsCache);
                    }
                } else if (other.getConcreteRepresentation().getCompletelyContainedZones().contains(curve.getConcreteRepresentation().getMainZone())) {
                    if (allZonesOutsideShaded(other, curve)) {
                        addToCahce(curve, other, equivCache);
                        addToCahce(other, curve, equivCache);
                    } else {
                        addToCahce(other, curve, subsCache);
                        addToCahce(curve, other, supsCache);
                    }
                } else if (other.getConcreteRepresentation().getIntersectingCurves().contains(curve.getConcreteRepresentation())) {
                    boolean zonesOutsideOther = allZonesOutsideShaded(curve, other);
                    boolean zonesOutsideCurve = allZonesOutsideShaded(other, curve);
                    if (allZonesIntersectionShaded(curve, other)) {
                        addToCahce(curve, other, disjCache);
                        addToCahce(other, curve, disjCache);
                    } else {
                        if (zonesOutsideCurve && zonesOutsideOther) {
                            addToCahce(curve, other, equivCache);
                            addToCahce(other, curve, equivCache);
                        } else if (zonesOutsideOther) {
                            addToCahce(other, curve, subsCache);
                            addToCahce(curve, other, supsCache);
                        } else if (zonesOutsideCurve) {
                            addToCahce(curve, other, subsCache);
                            addToCahce(other, curve, supsCache);
                        } // otherwise they are just nothing
                    }
                } else {
                    // must be disj
                    addToCahce(curve, other, disjCache);
                    addToCahce(other, curve, disjCache);
                }
            }
        }
    }


    private void recalcEquivSubsCachesZonesShaded(AbstractSet<ConcreteZone> zones) {
        Set<Curve> affectedCurves = new HashSet<Curve>();
        for (ConcreteZone z : zones) {
            affectedCurves.addAll(z.getAbstractSyntaxRepresentation().IN());
        }

        // now remove them from everything and recalc

        // no! the addition of shading can't make things that were equivalent not equivalent (would be different if we could remove shading!)
//        for(Curve c : affectedCurves) {
//            if(equivCache.containsKey(c)) {
//                Set<Curve> equivCurves = equivCache.get(c);
//                for(Curve k : equivCurves) {
//                    equivCache.get(k).remove(c);
//                }
//                equivCache.get(c).clear();
//            }
//        }

        for (Curve c : affectedCurves) {
            if (subsCache.containsKey(c)) {
                Set<Curve> subsCurves = subsCache.get(c);
                for (Curve k : subsCurves) {
                    supsCache.get(k).remove(c);
                }
                subsCache.get(c).clear();
            }
            if (supsCache.containsKey(c)) {
                Set<Curve> supsCurves = supsCache.get(c);
                for (Curve k : supsCurves) {
                    subsCache.get(k).remove(c);
                }
                supsCache.get(c).clear();
            }
        }

        // again addition of shading can't make two disjoint things not disjoint

        for (Curve c : affectedCurves) {
            recalcEquivSubsCachesCurveAdded(c);
        }
    }

    /*
    Equiv(k) = {k' : K() . Z(k)\Z^* = Z(k')\Z^*}
     */
    public AbstractSet<Curve> Equiv(Curve k) {
        return equivCache.get(k);
    }

    /*
    SubS(k) = {k' : K() . Z(k)\Z^* < Z(k')\Z^*}
     */
    public AbstractSet<Curve> SubS(Curve k) {
        return subsCache.get(k);
    }

    /*
    SupS(k) = {k' : K() . Z(k)\Z^* > Z(k')\Z^*}
    */
    public AbstractSet<Curve> SupS(Curve k) {
        return supsCache.get(k);
    }

    /*
    Disj(k) = {k' : K() . Z(k)\Z^* \int Z(k')\Z^* = \emptyset}
    */
    public AbstractSet<Curve> Disj(Curve k) {
        return subsCache.get(k);
    }


    public AbstractSet<Arrow> TEA(Arrow a) {
        return TEA.get(a);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> TEA() {
        return TEA;
    }

    private HashMap<Arrow, AbstractSet<Arrow>> arrowMapClone(HashMap<Arrow, AbstractSet<Arrow>> map) {
        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();
        for(Arrow a : map.keySet()) {
            result.put(a, new HashSet<Arrow>());
            result.get(a).addAll(map.get(a));
        }
        return result;
    }

    public HashMap<Arrow, AbstractSet<Arrow>> TEAclone() {
        return arrowMapClone(TEA);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITEAclone() {
        return arrowMapClone(ITEA);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITCAclone() {
        return arrowMapClone(ITCA);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITDAclone() {
        return arrowMapClone(ITDA);
    }

    public AbstractSet<Arrow> ITEA(Arrow a) {
        return ITEA.get(a);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITEA() {
        return ITEA;
    }


    public AbstractSet<Arrow> ITCA(Arrow a) {
        return ITCA.get(a);
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITCA() {
        return ITCA;
    }

    public AbstractSet<Arrow> ITDA(Arrow a) {
        return ITDA.get(a);
    }

    // Definition 29
    public void calculateArrowCaches(Arrow a) {
        if (a.targetIsCurve()) {
            AbstractSet<Arrow> arrowsToCheck = diagram().arrowsTov(this);
            arrowsToCheck.addAll(A());

            Set<Arrow> nearestITEA = new HashSet<Arrow>();
            Set<Arrow> nearestITCA = new HashSet<Arrow>();
            Set<Arrow> nearestITDA = new HashSet<Arrow>();

            for (Arrow a_dash : arrowsToCheck) {
                if (a != a_dash && a_dash.targetIsCurve()) {
                    if (Equiv(a_dash.targetAsCurve()).contains(a.targetAsCurve())) {
                        // I think we would be ok to handle this like the others as all the TEA arrows should be
                        // affected for an added arrow
                        for (Arrow arrow : TEA(a_dash)) {
                            addToArrowCahce(a, arrow, TEA);
                            addToArrowCahce(arrow, a, TEA);
                        }
                        addToArrowCahce(a, a_dash, TEA);
                        addToArrowCahce(a_dash, a, TEA);
                    } else if (SupS(a.targetAsCurve()).contains(a_dash.targetAsCurve())) {
                        // is it a closest so far?
                        boolean add = true;
                        for (Arrow a_dash_dash : nearestITEA) {
                            if (SubS(a_dash.targetAsCurve()).contains(a_dash_dash.targetAsCurve())) {
                                add = false;
                            } else if (SubS(a_dash_dash.targetAsCurve()).contains(a_dash.targetAsCurve())) {
                                nearestITEA.remove(a_dash_dash);
                                // I think this is enough.  Otherwise, we'd need to keep the list and remove at the end
                                // if we are adding it.  But I think if what we are thinking of adding is subs of something
                                // we have added in the past, then we are definitetly adding this one.
                            }
                        }
                        if (add) {
                            nearestITEA.add(a_dash);
                        }
                    } else if (SubS(a.targetAsCurve()).contains(a_dash.targetAsCurve())) {
                        boolean add = true;
                        for (Arrow a_dash_dash : nearestITCA) {
                            if (SupS(a_dash.targetAsCurve()).contains(a_dash_dash.targetAsCurve())) {
                                add = false;
                            } else if (SupS(a_dash_dash.targetAsCurve()).contains(a_dash.targetAsCurve())) {
                                nearestITCA.remove(a_dash_dash);
                            }
                        }
                        if (add) {
                            nearestITCA.add(a_dash);
                        }
                    } else if (Disj(a.targetAsCurve()).contains(a_dash.targetAsCurve())) {
                        boolean add = true;
                        for (Arrow a_dash_dash : nearestITDA) {
                            if ((SupS(a.targetAsCurve()).contains(a_dash_dash.targetAsCurve()) && Disj(a_dash.targetAsCurve()).contains(a_dash_dash.targetAsCurve())) ||
                                    (SupS(a_dash.targetAsCurve()).contains(a_dash_dash.targetAsCurve()))) { // must already have k'' : Disj(v,k)
                                add = false;
                            } else if ((SupS(a.targetAsCurve()).contains(a_dash.targetAsCurve()) && Disj(a_dash_dash.targetAsCurve()).contains(a_dash.targetAsCurve())) ||
                                    (SupS(a_dash_dash.targetAsCurve()).contains(a_dash.targetAsCurve()))) {
                                nearestITDA.remove(a_dash_dash);
                            }
                        }
                        if (add) {
                            nearestITDA.add(a_dash);
                        }
                    }

                }
            }
            for (Arrow a_dash : nearestITEA) {
                addToArrowCahce(a, a_dash, ITEA);
            }
            for (Arrow a_dash : nearestITCA) {
                addToArrowCahce(a, a_dash, ITCA);
            }
            for (Arrow a_dash : nearestITDA) {
                addToArrowCahce(a, a_dash, ITDA);
                //addToArrowCahce(a_dash, a, ITDA);
            }
        }
    }


    public void addArrowToCaches(Arrow a) {
        calculateArrowCaches(a);
        // ... but we have invalidated some other arrows caches here, so recalculate them
        // (that will also put a into the ITEA etc of those arrows)
        Set<Arrow> arrowsToRecalc = new HashSet<Arrow>();
        arrowsToRecalc.addAll(ITEA(a));
        arrowsToRecalc.addAll(ITCA(a));
        arrowsToRecalc.addAll(ITDA(a));
        for (Arrow arrow : arrowsToRecalc) {
            clearInArrowCache(arrow, ITEA);
            clearInArrowCache(arrow, ITCA);
            clearInArrowCache(arrow, ITDA);
        }
        for (Arrow arrow : arrowsToRecalc) {
            calculateArrowCaches(arrow);
        }
    }

    public void recalculateArrowCachesShading(AbstractSet<ConcreteZone> zones) {
        Set<Curve> affectedCurves = new HashSet<Curve>();
        for (ConcreteZone z : zones) {
            Set<Curve> curves = z.getAbstractSyntaxRepresentation().IN();
            for (Curve c : curves) {
                for (Zone zone : c.zones()) {
                    affectedCurves.addAll(zone.IN());
                }
            }
        }

        AbstractSet<Arrow> arrowsToCheck = diagram().arrowsTov(this);
        arrowsToCheck.addAll(A());
        for (Arrow a : arrowsToCheck) {
            if (a.targetIsCurve()) {
                if (affectedCurves.contains(a.targetAsCurve())) {

                }
            }
        }
    }

    public void ensureInArrowCache(Arrow a, AbstractMap<Arrow, AbstractSet<Arrow>> cache) {
        if (!cache.containsKey(a)) {
            cache.put(a, new HashSet<Arrow>());
        }
    }

    public void clearInArrowCache(Arrow a, AbstractMap<Arrow, AbstractSet<Arrow>> cache) {
        ensureInArrowCache(a, cache);
        cache.get(a).clear();
    }

    public void addToArrowCahce(Arrow arrow, Arrow other, AbstractMap<Arrow, AbstractSet<Arrow>> cache) {
        ensureInArrowCache(arrow, cache);
        cache.get(arrow).add(other);
    }


    // Transformation 3
    // Implements the same intention as that in the description.  But through the intersection code in the concrete
    // syntax we already have everything we need, so no need to make the IN sets, etc., can just do from what we
    // know from the concrete.
    public void addUnlabelledCurve(ConcreteCurve curve) {
        if (curve.getBoundaryRectangle().getAbstractSyntaxRepresentation().diagram() != this) {
            return;
        }

        Curve newCurve = new Curve(this);
        newCurve.setID(nextCurveID());
        curveMap.put(newCurve.getCurveID(), newCurve);
        curvesInUse.set(newCurve);
        unlabelledCurvesFast.set(newCurve);


        for (ConcreteZone z : curve.getAllZones()) {
            if (z.getAbstractSyntaxRepresentation() == null) {
                boolean allCurvesPresent = true;  // the zone isn't there till the intersections that make it are present
                for (ConcreteCurve c : z.getCurves()) {
                    if (c.getAbstractSyntaxRepresentation() == null ||
                            c.getAbstractSyntaxRepresentation().diagram() != this) {
                        allCurvesPresent = false;
                    }
                }
                if (allCurvesPresent) {
                    Zone newZone = new Zone(this, false);  // no shading at the start

                    HashSet<Curve> inSet = new HashSet<Curve>();
                    inSet.add(newCurve);
                    newCurve.addZone(newZone);
                    for (ConcreteCurve c : z.getCurves()) {
                        inSet.add(c.getAbstractSyntaxRepresentation());
                        c.getAbstractSyntaxRepresentation().addZone(newZone);
                    }
                    for (ConcreteCurve c : z.getCompletelyEnclosingCurves()) {
                        inSet.add(c.getAbstractSyntaxRepresentation());
                        c.getAbstractSyntaxRepresentation().addZone(newZone);
                    }
                    newZone.setInSet(inSet);

                    Z().add(newZone);

                    z.setAbstractSyntaxRepresentation(newZone);
                    newZone.setConcreteRepresentation(z);
                }
            } else {
                // should never come here
            }

        }

        curve.setAbstractSyntaxRepresentation(newCurve);
        newCurve.setConcreteRepresentation(curve);

        // Spider part not implemented - leave until we do ConceptDiagrams

        clearSCcache();  // not needed here? ... but don't think it helps not to
        recalcEquivSubsCachesCurveAdded(newCurve);
    }

    // Transformation 4
    public void labelCurve(ConcreteCurve curve) {
        if (curve.getBoundaryRectangle().getAbstractSyntaxRepresentation().diagram() != this ||
                curve.getAbstractSyntaxRepresentation() == null) {
            return;
        }

        curve.getAbstractSyntaxRepresentation().setLabel(curve.labelText());
        unlabelledCurvesFast.clear(curve.getAbstractSyntaxRepresentation());
        labelledCurvesFast.set(curve.getAbstractSyntaxRepresentation());

        clearSCcache();
    }

    protected Integer nextCurveID() {
        return curveIDgenerator.getIDasNum();
    }


    // Transformation 5
    public void shadeZones(AbstractSet<ConcreteZone> zonesToShade) {
        for (ConcreteZone z : zonesToShade) {
            if (z.getAbstractSyntaxRepresentation() != null) {
                if (z.shaded()) {
                    z.getAbstractSyntaxRepresentation().shade();
                }
            }
        }

        recalcEquivSubsCachesZonesShaded(zonesToShade);
        shadedZonesUpToDate = false;
    }


    // Definition 22
    //
    // in general I'm ignoring - or at least deffering dealing with it, it just removes curves from zones, so squashes
    // some together, but for the moment we can safely ignore it and the copies will be removed later.
    public AbstractSet<Zone> ISC(Arrow a) {
        HashSet<Zone> result = new HashSet<Zone>();

        if (a.getTarget().diagram() == this) {
            if (a.getTarget().getClass() == BoundaryRectangle.class) {
                result.add(new Zone());
            } else if (a.getTarget().getClass() == Curve.class) {
                // could do the set based method, but again we have all the info in the concrete syntax
                // the ISC is the zones of labelled curves that enclose non shaded zones of the arrow target
                ConcreteCurve c = ((Curve) a.getTarget()).getConcreteRepresentation();

                for (ConcreteZone z : c.getAllZones()) {
                    if (!z.shaded()) {
                        result.add(z.getAbstractSyntaxRepresentation());
                    }
                }

            }
        }
        return result;
    }

    public FastCurveSet getISCcurveMask(Arrow a) {
        FastCurveSet result = null;
        if (a.getTarget().diagram() == this) {
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
                for (int i = 0; i < IN.numBits(); i++) {
                    if (IN.isSet(i)) {
                        // does this curve contain any other
                        for (int j = 0; j < IN.numBits(); j++) {
                            if (IN.isSet(j) && i != j) {
                                if (getCurve(j).getConcreteRepresentation().getAllEnclosingCurves().contains(getCurve(i).getConcreteRepresentation())) {
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
                for (int i = 0; i < OUT.numBits(); i++) {
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

    private void clearSCcache() {
        SCcache.clear();
    }

    private void cacheSC(Arrow a, AbstractSet<ZonalRegion> result) {
        SCcache.put(a, result);
    }

    private AbstractSet<ZonalRegion> cachedSCAnswer(Arrow a) {
        return SCcache.get(a);
    }

    // Definition 25
    public AbstractSet<ZonalRegion> SC(Arrow arrow) {
        if (arrow.diagram() == this) {
            AbstractSet<ZonalRegion> cachedAnswer = cachedSCAnswer(arrow);
            if (cachedAnswer == null) {
                cachedAnswer = SZR(ISC(arrow), getISCcurveMask(arrow));
                cacheSC(arrow, cachedAnswer);
            }
            return cachedAnswer;
        }
        return null;
    }


    private void clearINTcache() {
        INTcache.clear();
    }

    private void cacheINT(Curve c, AbstractSet<Arrow> a) {
        INTcache.put(c, a);
    }

    private AbstractSet<Arrow> cacehdINT(Curve c) {
        return INTcache.get(c);
    }

    // Definition 36
    public AbstractSet<Arrow> INT(Curve c) {
        AbstractSet<Arrow> result = new HashSet<Arrow>();

        if (c.diagram() == this) {
            result = cacehdINT(c);
            if (result == null) {
                AbstractSet<Arrow> arrowsToCheck = diagram().arrowsTov(this);
                arrowsToCheck.addAll(A());
                for (Arrow a : arrowsToCheck) {
                    AbstractSet<ZonalRegion> sc = SC(a);
                    for (ZonalRegion zr : sc) {
                        if (zr.INcontainsCurve(c) || zr.OUTcontainsCurve(c)) {
                            result.add(a);
                        }
                    }
                }
            }
        }

        return result;
    }


}
