package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

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
    private HashSet<Spider> spiders;
    //private AbstractSet<Curve> curves;   // should get from the Map
    private HashSet<Zone> zones;
    private HashSet<Zone> coveredZones; // zones that were on the diagram but have been covered and so become missing
                                        // see description in Zones
    private HashSet<Arrow> arrows;
    private HashMap<Integer, Curve> curveMap;  // could probably just be an array, but not sure if it will be dense in the end

    private FastCurveSet unlabelledCurvesFast, labelledCurvesFast;

    // Shading is part of the zone in the implementation, so it's computed and cached here.
    private HashSet<Zone> shadedZones;
    private boolean shadedZonesUpToDate;

    private FastCurveSet curvesInUse;  // should just be intersection of unlabelledCurvesFast & labelledCurvesFast

    // one for each diagram
    private IDGenerator curveIDgenerator = new IDGenerator();

    private HashMap<Arrow, HashSet<ZonalRegion>> SCcache;  // only really counts for one step, but saves making it multiple times in a single iteration - e.g. in add curve label
    private HashMap<Curve, HashSet<Arrow>> INTcache;
    private HashMap<Curve, HashSet<Curve>> equivCache;
    private HashMap<Curve, HashSet<Curve>> subsCache;
    private HashMap<Curve, HashSet<Curve>> supsCache;
    private HashMap<Curve, HashSet<Curve>> disjCache;
    private HashMap<Arrow, HashSet<Arrow>> TEA;
    private HashMap<Arrow, HashSet<Arrow>> ITEA;
    private HashMap<Arrow, HashSet<Arrow>> ITCA;
    private HashMap<Arrow, HashSet<Arrow>> ITDA;

    public LabelledDiagram() {
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
        coveredZones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        shadedZonesUpToDate = false;
        arrows = new HashSet<Arrow>();
        curvesInUse = new FastCurveSet();
        boundaryRectangle = new BoundaryRectangle(this);

        SCcache = new HashMap<Arrow, HashSet<ZonalRegion>>();
        INTcache = new HashMap<Curve, HashSet<Arrow>>();

        equivCache = new HashMap<Curve, HashSet<Curve>>();
        subsCache = new HashMap<Curve, HashSet<Curve>>();
        supsCache = new HashMap<Curve, HashSet<Curve>>();
        disjCache = new HashMap<Curve, HashSet<Curve>>();

        TEA = new HashMap<Arrow, HashSet<Arrow>>();
        ITEA = new HashMap<Arrow, HashSet<Arrow>>();
        ITCA = new HashMap<Arrow, HashSet<Arrow>>();
        ITDA = new HashMap<Arrow, HashSet<Arrow>>();
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

    private AbstractSet<Zone> coveredZones() {
        return coveredZones;
    }

    private void checkAndCoverZone(Zone z) {
        if(z.covered()) {
            Z().remove(z);
            coveredZones().add(z);
            z.cover();
        }
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

    // is z shaded if we did the minus specified by the mask
    // there can be other shaded zone, but this really just checks if this, or zones revealed by the removal of
    // curves that makes this is shaded
    // So if I pick any point in a concrete representation of this zone and remove all the minuses, will that point
    /// still be shaded
    public boolean shadedAfterMinus(Zone z, FastCurveSet subtractMask) {
        if(z.diagram() == this) {
            if(z.isShaded()) {
                // need to check that the revealed zone isn't there already ... if it isn't, is it shaded
                for(Zone other : Z()) {
                    if(z.inAsFastCurveSet().logicalEQ(other.inAsFastCurveSet(), subtractMask)) {
                        if(!other.isShaded()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
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

    // is z in this diagram and empty
    private boolean EZ(Zone z) {
        if(z != null && z.diagram() == this) {
            if(!z.isShaded()) {
                return false;
            } else {
                // so it's shaded, but are there any spiders in there
                // FIXME : we don't have any spiders yet - but eventually might be worth storing the spiders in the zone as well, but can't expect too many spiders, right?
                for(Spider s : Sigma()) {
                    if(eta(s).contains(z)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // are all the zones of curve1 that aren't in curve2 shaded?  (and not contain a spider)
    private boolean allZonesOutsideShadedAndEmpty(Curve curve1, Curve curve2) {
        for (Zone z : curve1.zones()) {
            if(!z.INcontainsCurve(curve2)) {
                if(!EZ(z)) {
                    return false;
                }
            }
        }
        return true;
    }

    // are all the zones of in the intersection shaded? (and not contain a spider)
    private boolean allZonesIntersectionShadedAndEmpty(Curve curve1, Curve curve2) {
        for (Zone z : curve1.zones()) {
            if(z.INcontainsCurve(curve2)) {
               if(!EZ(z)) {
                   return false;
               }
            }
        }
        return true;
    }

    private void ensureInCache(Curve c, HashMap<Curve, HashSet<Curve>> cache) {
        if (!cache.containsKey(c)) {
            cache.put(c, new HashSet<Curve>());
        }
    }

    private void addToCahce(Curve curve, Curve other, HashMap<Curve, HashSet<Curve>> cache) {
        ensureInCache(curve, cache);
        cache.get(curve).add(other);
    }

    private void recalcEquivSubsCachesForCurve(Curve curve) {
        // make sure it's in all the caches
        ensureInCache(curve, equivCache);
        ensureInCache(curve, subsCache);
        ensureInCache(curve, supsCache);
        ensureInCache(curve, disjCache);

        for (Curve other : K()) {
            if (curve != other) {
                if (curve.getConcreteRepresentation().getCompletelyContainedZones().contains(other.getConcreteRepresentation().getMainZone())) {
                    if (allZonesOutsideShadedAndEmpty(curve, other)) {
                        // equiv
                        addToCahce(curve, other, equivCache);
                        addToCahce(other, curve, equivCache);
                    } else {
                        addToCahce(curve, other, subsCache);
                        addToCahce(other, curve, supsCache);
                    }
                } else if (other.getConcreteRepresentation().getCompletelyContainedZones().contains(curve.getConcreteRepresentation().getMainZone())) {
                    if (allZonesOutsideShadedAndEmpty(other, curve)) {
                        addToCahce(curve, other, equivCache);
                        addToCahce(other, curve, equivCache);
                    } else {
                        addToCahce(other, curve, subsCache);
                        addToCahce(curve, other, supsCache);
                    }
                } else if (other.getConcreteRepresentation().getIntersectingCurves().contains(curve.getConcreteRepresentation())) {
                    boolean zonesOutsideOther = allZonesOutsideShadedAndEmpty(curve, other);
                    boolean zonesOutsideCurve = allZonesOutsideShadedAndEmpty(other, curve);
                    if (allZonesIntersectionShadedAndEmpty(curve, other)) {
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

    // these could go into DiagramElement
    private Zone getAbstractForZone(ConcreteZone z) {
        Zone result;
        try {
            result = (Zone) getAbstract(z);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    private Curve getAbstractForCurve(ConcreteCurve c) {
        Curve result;
        try {
            result = (Curve) getAbstract(c);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    private void recalcEquivSubsCachesZonesShaded(AbstractSet<ConcreteZone> zones) {
        Set<Curve> affectedCurves = new HashSet<Curve>();
        for (ConcreteZone z : zones) {
            if(getAbstractForZone(z) != null) {  // we can't shade zones that aren't in the abstract syntax yet so
                                                // this shouldn't ever fail.
                affectedCurves.addAll(getAbstractForZone(z).IN());
            }
        }

        // To this I need to add any equivalent curves.  Consider for example three curves one inside the other
        // if the zone between the outer two is already shaded, a new shading between the inner two should result in
        // the outer and inner becoming equiv, but neither are affected, so that wouldn't happen.
        Set<Curve> equivsToAdd = new HashSet<Curve>();
        for (Curve c : affectedCurves) {
            equivsToAdd.addAll(equivCache.get(c));
        }
        affectedCurves.addAll(equivsToAdd);

        // now remove them from everything and recalc

        // the addition of shading can't make things that were equivalent not equivalent
        // (would be different if we could remove shading!)


        for (Curve c : affectedCurves) {
            if (subsCache.containsKey(c)) {
                Set<Curve> subsCurves = subsCache.get(c);
                for (Curve k : subsCurves) {
                    supsCache.get(k).remove(c);
                }
                subsCache.get(c).clear();
            }
//            if (supsCache.containsKey(c)) {
//                Set<Curve> supsCurves = supsCache.get(c);
//                for (Curve k : supsCurves) {
//                    subsCache.get(k).remove(c);
//                }
//                supsCache.get(c).clear();
//            }
        }

        // again addition of shading can't make two disjoint things not disjoint

        for (Curve c : affectedCurves) {
            recalcEquivSubsCachesForCurve(c);
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
        return disjCache.get(k);
    }


    // FIXME these arrow caches are meaningless at the moment because there are no local arrows.  But Once we
    // move to the next stage I'll have to decide how this is going to work and if I redo all the caches when
    // any arrow is added ... or is that just about required anyway?
    public AbstractSet<Arrow> TEA(Arrow a) {
        // FIXME ... check the calls of this,
        // Cause if a is not in this diagram (i.e. sourced elsewhere), then it won't be in the cache
        //
        // yeah the TEA stored here is just the ones in this diagram
        // could fix this up whenever I add an arrow to a diagram, or calc as extra here
        AbstractSet<Arrow> result = new HashSet<Arrow>();

        DiagramArrowSourceOrTarget target = a.getTarget();
        if(target.diagram() == this && target instanceof Curve) {
            Curve arrowTarget = a.targetAsCurve();
            Set<Curve> equivs = Equiv(arrowTarget);

            for(Arrow a_op : this.diagram().getObjectPropertyArrows()) {
                if(equivs.contains(a_op.getTarget())) {
                    result.add(a_op);
                }
            }
            for(Arrow a_dtp : this.diagram().getDatatypePropertyArrows()) {
                if(equivs.contains(a_dtp.getTarget())) {
                    result.add(a_dtp);
                }
            }

            // FIXME : for when we have local arrows
//            if (a.getSource().diagram() == this) {
//                result.addAll(TEA.get(a));
//            }
        }

        return result;
        // should I cache it?
    }

    public HashMap<Arrow, HashSet<Arrow>> TEA() {
        return TEA;
    }

    private HashMap<Arrow, AbstractSet<Arrow>> arrowMapClone(HashMap<Arrow, HashSet<Arrow>> map) {
        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();
        for(Arrow a : map.keySet()) {
            result.put(a, new HashSet<Arrow>());
            result.get(a).addAll(map.get(a));
        }
        return result;
    }

    public HashMap<Arrow, AbstractSet<Arrow>> TEAclone() {
        //return arrowMapClone(TEA);

        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();

        for (ObjectPropertyArrow a : diagram().getObjectPropertyArrows()) {
            result.put(a, TEA(a));
        }

        return result;
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITEAclone() {
        //return arrowMapClone(ITEA);

        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();

        for (ObjectPropertyArrow a : diagram().getObjectPropertyArrows()) {
            result.put(a, ITEA(a));
        }

        return result;
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITCAclone() {
        //return arrowMapClone(ITCA);

        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();

        for (ObjectPropertyArrow a : diagram().getObjectPropertyArrows()) {
            result.put(a, ITCA(a));
        }

        return result;
    }

    public HashMap<Arrow, AbstractSet<Arrow>> ITDAclone() {
        //return arrowMapClone(ITDA);


        HashMap<Arrow, AbstractSet<Arrow>> result = new HashMap<Arrow, AbstractSet<Arrow>>();

        for (ObjectPropertyArrow a : diagram().getObjectPropertyArrows()) {
            result.put(a, ITDA(a));
        }

        return result;
    }

    // nearestITEA gets changed here
    private void refineITEA(AbstractSet<Arrow> nearestITEA, Curve curve, AbstractSet<? extends Arrow> arrowsToCheck) {
        for(Arrow a : arrowsToCheck) {
            if(a.getTarget().diagram() == this) {
                if (SupS(curve).contains(a.getTarget())) {
                    Boolean keepthis = true;
                    for (Arrow itea : nearestITEA) {
                        if (SubS(itea.targetAsCurve()).contains(a.getTarget())) {
                            nearestITEA.remove(itea);
                        } else if (SubS(a.targetAsCurve()).contains(itea.targetAsCurve())) {
                            keepthis = false;
                        }
                    }
                    if (keepthis) {
                        nearestITEA.add(a);
                    }
                }
            }
        }
    }

    // nearestITCA gets changed here
    private void refineITCA(AbstractSet<Arrow> nearestITCA, Curve curve, AbstractSet<? extends Arrow> arrowsToCheck) {
        for(Arrow a : arrowsToCheck) {
            if(a.getTarget().diagram() == this) {
                if (SubS(curve).contains(a.getTarget())) {
                    Boolean keepthis = true;
                    for (Arrow itca : nearestITCA) {
                        if (SupS(itca.targetAsCurve()).contains(a.getTarget())) {
                            nearestITCA.remove(itca);
                        } else if (SupS(a.targetAsCurve()).contains(itca.targetAsCurve())) {
                            keepthis = false;
                        }
                    }
                    if (keepthis) {
                        nearestITCA.add(a);
                    }
                }
            }
        }
    }

    public AbstractSet<Arrow> ITEA(Arrow a) {
        AbstractSet<Arrow> result = new HashSet<Arrow>();

        DiagramArrowSourceOrTarget target = a.getTarget();
        if(target.diagram() == this && target instanceof Curve) {
            Curve arrowTarget = a.targetAsCurve();
            AbstractSet<Arrow> nearestITEA = ITEA.get(a);  // best we know so far for ITEA

            if(nearestITEA == null) {
                nearestITEA = new HashSet<Arrow>();
            }

            // now see if anything squeezes in between

            refineITEA(nearestITEA, arrowTarget, this.diagram().getObjectPropertyArrows());
            refineITEA(nearestITEA, arrowTarget, this.diagram().getDatatypePropertyArrows());
            result.addAll(nearestITEA);
        }
        return result;
    }

    public HashMap<Arrow, HashSet<Arrow>> ITEA() {
        return ITEA;
    }

    public AbstractSet<Arrow> ITCA(Arrow a) {
        AbstractSet<Arrow> result = new HashSet<Arrow>();
        DiagramArrowSourceOrTarget target = a.getTarget();
        if(target.diagram() == this && target instanceof Curve) {
            Curve arrowTarget = a.targetAsCurve();
            AbstractSet<Arrow> nearestITCA = ITCA.get(a);  // best we know so far for ITEA

            if(nearestITCA == null) {
                nearestITCA = new HashSet<Arrow>();
            }

            // now see if anything squeezes in between

            // FIXME really should know the type here, so just use one
            refineITCA(nearestITCA, arrowTarget, this.diagram().getObjectPropertyArrows());
            refineITCA(nearestITCA, arrowTarget, this.diagram().getDatatypePropertyArrows());
            result.addAll(nearestITCA);
        }
        return result;
    }

    public HashMap<Arrow, HashSet<Arrow>> ITCA() {
        return ITCA;
    }

    public AbstractSet<Arrow> ITDA(Arrow a) {
        return calcITDA(a);
    }

    // Definition 29, pt 4
    public AbstractSet<Arrow> calcITDA(Arrow a) {
        HashSet<Arrow> result = new HashSet<Arrow>();

        if(a.targetIsCurve() && a.getTarget().diagram() == this) {
            Curve k = a.targetAsCurve();

            HashSet<Arrow> arrowsToCheck = new HashSet<Arrow>();
            arrowsToCheck.addAll(diagram().arrowsTov(this));
            arrowsToCheck.addAll(A());

            for (Arrow a_dash : arrowsToCheck) {
                if(a_dash.targetIsCurve()) {  // don't need to check the target cause we only put ones in to this diagram
                    Curve k_dash = a_dash.targetAsCurve();

                    if(Disj(k).contains(k_dash)) {
                        boolean keep = true;
                        for (Arrow a_dash_dash : arrowsToCheck) {
                            if (a_dash_dash.targetIsCurve()) {
                                Curve k_dash_dash = a_dash_dash.targetAsCurve();

                                if(SupS(k).contains(k_dash_dash) && Disj(k_dash).contains(k_dash_dash)) {
                                    keep = false;
                                }

                                if(SupS(k_dash).contains(k_dash_dash) && Disj(k).contains(k_dash_dash)) {
                                    keep = false;
                                }
                            }
                        }
                        if(keep) {
                            result.add(a_dash);
                        }
                    }
                }
            }
        }
        return result;
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
                        // FIXME can this part really be right - see calcITDA
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


    // no adding of arrows to labelleddiagrams at this point, so this is never called.
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
            Set<Curve> curves = getAbstractForZone(z).IN();
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

    public void ensureInArrowCache(Arrow a, HashMap<Arrow, HashSet<Arrow>> cache) {
        if (!cache.containsKey(a)) {
            cache.put(a, new HashSet<Arrow>());
        }
    }

    public void clearInArrowCache(Arrow a, HashMap<Arrow, HashSet<Arrow>> cache) {
        ensureInArrowCache(a, cache);
        cache.get(a).clear();
    }

    public void addToArrowCahce(Arrow arrow, Arrow other, HashMap<Arrow, HashSet<Arrow>> cache) {
        ensureInArrowCache(arrow, cache);
        cache.get(arrow).add(other);
    }

    // really should be better interfaces for this
    // other code shouldn't be just adding zones
    // ... I need it for now for the boundary rectangle to add its zone
    protected void addZone(Zone z) {
        Z().add(z);
    }


    private boolean sameDiagram(ConcreteCurve curve) {
        return getAbstractForCurve(curve).diagram() == this;
    }

    // Transformation 3
    // Implements the same intention as that in the description.  But through the intersection code in the concrete
    // syntax we already have everything we need, so no need to make the IN sets, etc., can just do from what we
    // know from the concrete.
    public void addUnlabelledCurve(ConcreteCurve curve) {
//        if (!sameDiagram(curve)) {
//            return;
//        }

        Curve newCurve = new Curve(this);
        curveMap.put(newCurve.getCurveID(), newCurve);
        curvesInUse.set(newCurve);
        unlabelledCurvesFast.set(newCurve);

        newCurve.setConcreteRepresentation(curve);

        // Make all the intersecting zones where necessary
        for (ConcreteZone z : curve.getAllZones()) {
            if (getAbstractForZone(z) == null) {  // should always be true.  The zones of this curve are a result of it's intersections with others, so they can't be there yet
                boolean allCurvesPresent = true;  // the zone isn't there till the intersections that make it are present
                for (ConcreteCurve c : z.getCurves()) {
                    if (getAbstractForCurve(c) == null) {
                        allCurvesPresent = false;
                    }
                }
                if (allCurvesPresent) {
                    Zone newZone = new Zone(this, false);  // false = no shading
                    newZone.setConcreteRepresentation(z);
                    newCurve.addZone(newZone);
                    addZone(newZone);

                    // Now make the area for this zone.  Could build up an area like is done with the
                    // intersection zones, but can also make just be intersection - using the awt.geom fns
                    newZone.setRemainingArea(curve.getMainZone());
                    for (ConcreteCurve c : z.getCurves()) {
                        if(getAbstractForCurve(c) != null && getAbstractForZone(c.getMainZone()) != null) {
                            // The only curve that could have abstrace != null, but main == to null is the one that's
                            // currently being put on, and for that we have already used its main zone to set the
                            // original area.
                            newZone.intersectMainZoneOriginal(getAbstractForZone(c.getMainZone()));
                        }
                    }


                    HashSet<Curve> inSet = new HashSet<Curve>();
                    inSet.add(newCurve);
                    for (ConcreteCurve c : z.getCurves()) {
                        if(getAbstractForCurve(c) != null) {
                            inSet.add(getAbstractForCurve(c));
                            getAbstractForCurve(c).addZone(newZone);
                        }
                    }
                    for (ConcreteCurve c : z.getCompletelyEnclosingCurves()) {
                        if(getAbstractForCurve(c) != null) {
                            inSet.add(getAbstractForCurve(c));
                            getAbstractForCurve(c).addZone(newZone);
                        }
                    }
                    newZone.setInSet(inSet);
                }
            } else {
                // should never come here
            }
        }

        // if this curve completely enclosed things, now add it to them
        for(ConcreteZone z : curve.getCompletelyContainedZones()) {
            if(getAbstractForZone(z) != null) {
                getAbstractForZone(z).addToInSet(newCurve);
                newCurve.addZone(getAbstractForZone(z));
            }
        }

        // check for any zones that have become covered
        for(ConcreteCurve c : curve.getIntersectingCurves()) {
            if(getAbstractForCurve(c) != null) {
                calculateCoveringForCurve(getAbstractForCurve(c));
            }
        }
        calculateCoveringForCurve(newCurve);



        // FIXME Spider part not implemented - leave until we do ConceptDiagrams


        clearINTcache();
        clearSCcache();  // not needed here? ... but don't think it helps not to
        recalcEquivSubsCachesForCurve(newCurve);
    }

    private void calculateCoveringForCurve(Curve c) {
        for(Zone z : c.zones()) {
            for(Zone z1 : c.zones()) {
                if (z != z1) {
                    if (z.getConcreteRepresentation().getLevel() < z1.getConcreteRepresentation().getLevel()) {
                        z.subtractZone(z1);
                    } else {
                        z1.subtractZone(z);
                    }
                }
            }
            checkAndCoverZone(z);
        }
    }


    // Transformation 4
    public void labelCurve(ConcreteCurve curve) {
        if (!sameDiagram(curve) || getAbstractForCurve(curve) == null) {
            return;
        }

        Curve k = getAbstractForCurve(curve);

        k.setLabel(curve.labelText());
        unlabelledCurvesFast.clear(k);
        labelledCurvesFast.set(k);

        clearINTcache();
        clearSCcache();
    }

    protected Integer nextCurveID() {
        return curveIDgenerator.getIDasNum();
    }


    // Transformation 5
    public void shadeZones(AbstractSet<ConcreteZone> zonesToShade) {
        for (ConcreteZone z : zonesToShade) {
            if (getAbstractForZone(z) != null) {
                if (z.shaded()) {
                    getAbstractForZone(z).shade();
                }
            }
        }

        recalcEquivSubsCachesZonesShaded(zonesToShade);
        shadedZonesUpToDate = false;


        clearINTcache();
        clearSCcache();
    }


    // Definition 22
    //
    // in general I'm ignoring '-' or at least deffering dealing with it, it just removes curves from zones, so squashes
    // some together, but for the moment we can safely ignore it and the copies will be removed later.
    public AbstractSet<Zone> ISC(Arrow a) {
        HashSet<Zone> result = new HashSet<Zone>();

        if (a.getTarget().diagram() == this) {
            if (a.getTarget().getClass() == BoundaryRectangle.class) {
                result.add(boundaryRectangle().getBRZone());
            } else if (a.getTarget().getClass() == Curve.class) {
                // could do the set based method, but again we have all the info in the concrete syntax
                // the ISC is the zones of labelled curves that enclose non shaded zones of the arrow target
                ConcreteCurve c = ((Curve) a.getTarget()).getConcreteRepresentation();

                for (ConcreteZone z : c.getAllZonesAndCompleletlyContained()) {
                    if (!getAbstractForZone(z).isShaded() && !getAbstractForZone(z).covered()) {
                        result.add(getAbstractForZone(z));
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
    public HashSet<ZonalRegion> SZR(AbstractSet<Zone> Zdash, FastCurveSet curveMask) {
        HashSet<ZonalRegion> result = new HashSet<ZonalRegion>();

        FastCurveSet curveMaskAltered = new FastCurveSet(curveMask);

        for (Zone z_dash : Zdash) {
            boolean existingAnswer = false;
            for (ZonalRegion zr : result) {
                if (zr.IN().subseteqOF(z_dash.inAsFastCurveSet(), curveMask) &&
                        zr.OUT().subseteqOF(z_dash.outAsFastCurveSet(), curveMask)) {
                    existingAnswer = true;
                }
            }

            if (!existingAnswer) {
                FastCurveSet IN = (new FastCurveSet(z_dash.inAsFastCurveSet())).mask(curveMask);
                //IN.logicalXOR(curveMask);
                FastCurveSet OUT = (new FastCurveSet(z_dash.outAsFastCurveSet())).mask(curveMask);
                //OUT.logicalXOR(curveMask);

                // FIXME : could probably make an approx just once outside the loop (see also below)
                // remove containing curves from IN
                for(int i : IN) {
                    // does this curve contain any other
                    for (int j = 0; j < IN.numBits(); j++) {
                        if(IN.isSet(j)) {
                            if (i != j) { // && getCurve(j) != null) { // that null bit shouldn't be needed
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
                if(IN.numBitsSet() > 0) {
                    for (int i : OUT) {
                        // is this curve disjoint from all things in IN
                        boolean isDisjoint = true;
                        for (int j : IN) {
                            if (getCurve(j).getConcreteRepresentation().getIntersectingCurves().contains(getCurve(i).getConcreteRepresentation()) ||
                                    getCurve(i).getConcreteRepresentation().getAllEnclosingCurves().contains(getCurve(j).getConcreteRepresentation())) {
                                isDisjoint = false;
                                break;
                            }
                        }
                        if (isDisjoint) {
                            OUT.clear(i);
                            removedCurves.set(i);
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

                        // twidle bits to get the right behaviour for IN\{K} etc given this mask
                        boolean iSet = curveMaskAltered.isSet(i);
                        curveMaskAltered.set(i);

                        for (Zone z : zonesToTest) {
                            //(IN\{k} \subseteq z.in and OUT\{k} \subseteq z.out)
                            if (IN.subseteqOF(z.inAsFastCurveSet(), curveMaskAltered) &&
                                    OUT.subseteqOF(z.outAsFastCurveSet(), curveMaskAltered)) {
                                removable = false;
                            }
                        }
                        if(!iSet) {
                            curveMaskAltered.clear(i);
                        }

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

    private void cacheSC(Arrow a, HashSet<ZonalRegion> result) {
        SCcache.put(a, result);
    }

    private HashSet<ZonalRegion> cachedSCAnswer(Arrow a) {
        return SCcache.get(a);
    }

    // Definition 25
    public AbstractSet<ZonalRegion> SC(Arrow arrow) {
        if (arrow.getTarget().diagram() == this) {
            HashSet<ZonalRegion> cachedAnswer = cachedSCAnswer(arrow);
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

    private void cacheINT(Curve c, HashSet<Arrow> a) {
        INTcache.put(c, a);
    }

    private AbstractSet<Arrow> cacehdINT(Curve c) {
        return INTcache.get(c);
    }

    // Definition 36
    public AbstractSet<Arrow> INT(Curve c) {
        AbstractSet<Arrow> result = new HashSet<Arrow>();

        if (c.diagram() == this) {
            if (cacehdINT(c) == null) {
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
            } else {
                result = cacehdINT(c);
            }
        }

        return result;
    }


}
