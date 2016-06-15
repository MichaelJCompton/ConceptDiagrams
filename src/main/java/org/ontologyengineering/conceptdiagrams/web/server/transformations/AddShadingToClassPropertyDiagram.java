package org.ontologyengineering.conceptdiagrams.web.server.transformations;


/**
 * Author: Michael Compton<br> Date: November 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


import java.util.*;

// Definitions 39 & 40
public class AddShadingToClassPropertyDiagram extends AddShading<ClassAndObjectPropertyDiagram> {

    private ClassAndObjectPropertyDiagram theDiagram;

    public AddShadingToClassPropertyDiagram(AbstractSet<ConcreteZone> shadedZones) {
        super(shadedZones);
    }


    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        if (shadedZones.size() > 0) {
            translate((ClassAndObjectPropertyDiagram) diagram.toAbstract(shadedZones.iterator().next()).diagram() , outputter);
        } else {
            // if there are none, then there is nothing to do ... but should never have gotten here if there are none
        }
    }
    @Override
    public void translate(ClassAndObjectPropertyDiagram transformedDiagram, OWLOutputBuilder outputter) {

        theDiagram = transformedDiagram;

        // cache TEA, ITEA, ITDA, SC for all arrows sourved on * targeted to curves
        HashMap<Arrow, AbstractSet<Arrow>> TEA_v = transformedDiagram.TEAclone();
        HashMap<Arrow, AbstractSet<Arrow>> ITEA_v = transformedDiagram.ITEAclone();
        HashMap<Arrow, AbstractSet<Arrow>> ITDA_v = transformedDiagram.ITDAclone();

        HashMap<Arrow, AbstractSet<ZonalRegion>> SC_v = new HashMap<Arrow, AbstractSet<ZonalRegion>>();
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.targetIsCurve() && a.getTarget().isUnLabelled() && a.getTarget().diagram() == transformedDiagram) {
                SC_v.put(a, new HashSet<ZonalRegion>());
                SC_v.get(a).addAll(transformedDiagram.SC(a));
            }
        }

        executeTransformation(transformedDiagram);

        // IT(PD_2, PD_1) = EC U SCO U DC U EOP_T U EOP_bot U EOP U IOP U SOP U DOP U DOM U RAN

        Set<ZonalRegion> CACS = CACS(transformedDiagram);
        for (ZonalRegion zr : CACS) {

            // 1.  EC

            if (zr.IN().isZero() && zr.OUT().isZero()) {
                outputter.addGlobalContradiction();
            } else if (zr.IN().isZero() && zr.OUT().numBitsSet() == 1) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.IN().isZero() && zr.OUT().numBitsSet() >= 2) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() == 1) {
                Curve c = transformedDiagram.getCurve(zr.IN().iterator().next());
                outputter.addEquivalentNoThing(c, transformedDiagram);
            }

            // 2.  SCO

            if ((zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() == 1) ||
                    (zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() >= 2) ||
                    (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() == 1) ||
                    (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() >= 2)) {
                // all the cases are covered by the call
                outputter.addSubClassAxiomIntUnion(zr.IN(), zr.OUT(), transformedDiagram);
            }

            // 3.  DC

            if (zr.OUT().isZero() && zr.IN().numBitsSet() == 2) {
                int curve1, curve2;
                Iterator<Integer> iter = zr.IN().iterator();
                curve1 = iter.next();
                curve2 = iter.next();
                outputter.addDisjointClasses(transformedDiagram.getCurve(curve1), transformedDiagram.getCurve(curve2), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() > 2) {
                FastCurveSet curves = new FastCurveSet(zr.IN());

                for (int i : zr.IN()) {
                    curves.clear(i);
                    outputter.addDisjointClasses(transformedDiagram.getCurve(i), curves, transformedDiagram);
                    curves.set(i);
                }
            }
        }

        // 4.  EOP_T

        // computed slightly differently to the document ... here I'm assuming just one shading event and doing all
        // the calculations off the final diagram v+Z'
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.getTarget().getClass() == Curve.class) {
                Curve target = (Curve) a.getTarget();
                boolean isThing = true;
                for (Zone z : transformedDiagram.Z()) {
                    if (z.OUTcontainsCurve(target)) {
                        if (z.isShaded()) {

                        } else {
                            isThing = false;
                            break;
                        }
                    }
                }
                if (isThing) {
                    boolean setThisRound = false;
                    for (ConcreteZone z : shadedZones) {
                        Zone zone = (Zone) transformedDiagram.toAbstract(z);
                        if (zone.OUTcontainsCurve(target)) {
                            setThisRound = true;
                            break;
                        }
                    }
                    if (setThisRound) {
                        // so this target was made equiv to thing in this shading
                        outputter.addObjectPropertyEquivTop(a, transformedDiagram);
                    }
                }
            }
        }

        // 5.  EOP_bot

        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.getTarget().getClass() == Curve.class) {
                Curve target = (Curve) a.getTarget();
                boolean isNoThing = true;
                for (Zone z : transformedDiagram.Z()) {
                    if (z.INcontainsCurve(target)) {
                        if(z.isShaded()) {

                        } else {
                            isNoThing = false;
                            break;
                        }
                    }
                }
                if (isNoThing) {
//                    boolean setThisRound = false;
//                    for (ConcreteZone z : shadedZones) {
//                        Zone zone = (Zone) transformedDiagram.toAbstract(z);
//                        if (zone.INcontainsCurve(target)) {
//                            setThisRound = true;
//                            break;
//                        }
//                    }
//                    if (setThisRound) {
//                        // so this target was made equiv to thing in this shading
                        outputter.addObjectPropertyEquivBot(a, transformedDiagram);
//                    }
                }
            }
        }


        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            AbstractSet<Arrow> inInv = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.TEA(a)) {
                if (!TEA_v.get(a).contains(arrow)) {
                    if (transformedDiagram.diagram().DE(a).contains((arrow))) {
                        inDiff.add(arrow);
                    } else {
                        inInv.add(arrow);
                    }
                }
            }
            // 6.  EOP
            if (inDiff.size() >= 2) {
                outputter.addEquivalentObjectProperties(inDiff);
            }

            // 7.  IOP
            for (Arrow arrow : inInv) {
                outputter.addInverseObjectProperties(a, arrow);
            }
        }

        // 8. SOP
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.ITEA(a)) {
                if (ITEA_v.get(a) != null && !ITEA_v.get(a).contains(arrow)) {
                    inDiff.add(arrow);
                }
            }
            for (Arrow arrow : inDiff) {
                outputter.addSubObjectProperties(a, arrow);
            }
        }

        // 9. DOP
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.ITDA(a)) {
                if (ITDA_v.get(a) != null && !ITDA_v.get(a).contains(arrow)) {
                    inDiff.add(arrow);
                }
            }
            for (Arrow arrow : inDiff) {
                outputter.addDisjointObjectProperties(a, arrow);
            }
        }

        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.targetIsCurve() && a.getTarget().isUnLabelled() && a.getTarget().diagram() == transformedDiagram ) {
                AbstractSet<ZonalRegion> SC = transformedDiagram.SC(a); // I think this is cached in the diagram, but no harm here
                //if(!(SC_v.get(a).containsAll(SC) && SC.containsAll(SC_v.get(a)))) {
                if(!(ZonalRegion.zonalRegionSetsEQ(SC_v.get(a), SC))) {
                    if(a.isInverse()) {
                        // 10. DOM
                        outputter.addObjectPropertyDomainTSC(a, SC, transformedDiagram);
                    } else {
                        // 11. RAN
                        outputter.addObjectPropertyRangeTSC(a, SC, transformedDiagram);
                    }
                }
            }
        }
    }

    // Definition 39
    private Set<ZonalRegion> CACS(ClassAndObjectPropertyDiagram transformedDiagram) {

        FastCurveSet UK = new FastCurveSet();
        for(Curve c : transformedDiagram.UK()) {
            UK.set(c);
        }

        AbstractSet<Zone> shading = new HashSet<Zone>();
        for(ConcreteZone z : shadedZones) {
            if(transformedDiagram.shadedAfterMinus((Zone) transformedDiagram.toAbstract(z), UK)) {
                shading.add((Zone) transformedDiagram.toAbstract(z));
            }
        }

        return theDiagram.SZR(shading, UK);
    }
}

