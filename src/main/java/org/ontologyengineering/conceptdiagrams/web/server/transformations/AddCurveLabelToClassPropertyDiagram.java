package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br> Date: November 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// For defs 35 & 37
public class AddCurveLabelToClassPropertyDiagram extends AddCurveLabel<ClassAndObjectPropertyDiagram> {

    private Curve labelledCurveAbstract;

    public AddCurveLabelToClassPropertyDiagram(ConcreteCurve labelledCurve) {
        super(labelledCurve);
    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        labelledCurveAbstract = (Curve) diagram.toAbstract(labelledCurve);
        if (diagram.toAbstract(labelledCurve).diagram() instanceof ClassAndObjectPropertyDiagram) {
            translate((ClassAndObjectPropertyDiagram) diagram.toAbstract(labelledCurve).diagram(), outputter);
        } else {
            // something bad has gone wrong
        }
    }


    // Definition 37
    @Override
    public void translate(ClassAndObjectPropertyDiagram transformedDiagram, OWLOutputBuilder outputter) {
        // no caching required
        executeTransformation(transformedDiagram);


        outputter.assertExistenceCurve(labelledCurveAbstract);

        // IT(PD_2, PD_1) = EC U SCO U DC U DOM U RAN

        Set<ZonalRegion> CACS = CACS();
        for (ZonalRegion zr : CACS) {

            // 1.  EC

            if (zr.IN().isZero() && zr.OUT().numBitsSet() == 1 && zr.OUTcontainsCurve(labelledCurveAbstract)) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.IN().isZero() && zr.OUT().numBitsSet() >= 2) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() == 1 && zr.INcontainsCurve(labelledCurveAbstract)) {
                outputter.addEquivalentNoThing(transformedDiagram.getCurve(zr.IN().iterator().next()), transformedDiagram);
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

            if (zr.OUT().isZero() && zr.IN().numBitsSet() == 2 && zr.INcontainsCurve(labelledCurveAbstract)) {
                int curve1, curve2;
                Iterator<Integer> iter = zr.IN().iterator();
                curve1 = iter.next();
                curve2 = iter.next();
                outputter.addDisjointClasses(transformedDiagram.getCurve(curve1), transformedDiagram.getCurve(curve2), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() > 2) {
                FastCurveSet curves = new FastCurveSet(zr.IN());
                curves.clear(labelledCurveAbstract);
                outputter.addDisjointClasses(labelledCurveAbstract, curves, transformedDiagram);
            }
        }
        // 4.  DOM

        AbstractSet<Arrow> INT = transformedDiagram.INT(labelledCurveAbstract);
        // we've already done the transformation, so this is v+(k,\lambda)

        AbstractSet<ObjectPropertyArrow> domainArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> domainArrowsTSC = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrowsTSC = new HashSet<ObjectPropertyArrow>();
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.getTarget() == labelledCurveAbstract
                    && a.isInverse()) {
                domainArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    a.isInverse() &&
                    INT.contains(a)) {
                domainArrowsTSC.add(a);
            }
            if (a.getTarget() == labelledCurveAbstract
                    && !a.isInverse()) {
                rangeArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    ! a.isInverse() &&
                    INT.contains(a)) {
                rangeArrowsTSC.add(a);
            }
        }


        for (ObjectPropertyArrow a : domainArrows) {
            outputter.addObjectPropertyDomain(a, labelledCurveAbstract, transformedDiagram);
        }
        for (ObjectPropertyArrow a : domainArrowsTSC) {
            outputter.addObjectPropertyDomainTSC(a, transformedDiagram.SC(a), transformedDiagram);
        }


        // 5. RAN

        for (ObjectPropertyArrow a : rangeArrows) {
            outputter.addObjectPropertyRange(a, labelledCurveAbstract, transformedDiagram);
        }
        for (ObjectPropertyArrow a : rangeArrowsTSC) {
            outputter.addObjectPropertyRangeTSC(a, transformedDiagram.SC(a), transformedDiagram);
        }
    }


    // Definition 35 & 36
    //
    // Def 35 is rolled into here - not explicitly calculated, just the maske is carried around and used where necessary
    private Set<ZonalRegion> CACS() {


        Set<ZonalRegion> result = new HashSet<ZonalRegion>();
        LabelledDiagram diagram = labelledCurveAbstract.diagram();

        // CACS(v, k, \lambda) = CACS_I, CACS_O, CACS_*


        FastCurveSet UED_mask = new FastCurveSet();
        for(Curve c : diagram.UK()) {
            UED_mask.set(c);
        }
        // just to make sure
        UED_mask.set(labelledCurveAbstract);


        // 1. CACS_I
        Set<ZonalRegion> CACS_I = new HashSet<ZonalRegion>();

        AbstractSet<Zone> Z_I = new HashSet<Zone>();
        for(Zone z : labelledCurveAbstract.zones()) {
            boolean keep = true;
            for(Zone z_dash : diagram.Z()) {
                if(z.inAsFastCurveSet().logicalEQ(z_dash.inAsFastCurveSet(), UED_mask) &&
                        z_dash.OUTcontainsCurve(labelledCurveAbstract)) {
                    keep = false;
                }
            }
            if(keep) {
                Z_I.add(z);
            }
        }


        // Do first - look at optimisations second
//        // I think looking at the definition that this is the set of zones in curves completely contained in k
//        Set<ZonalRegion> CACS_I = new HashSet<ZonalRegion>();
//
//        AbstractSet<Zone> Z_I = new HashSet<Zone>();
//        for(ConcreteZone z : getCurve().getCompletelyContainedZones()) {
//            if(labelledCurveAbstract.diagram().toAbstract(z) != null) {
//                boolean addThis = true;
//                for(ConcreteCurve c : z.getCurves()) {
//                    if(((Curve) diagram.toAbstract(c)).isUnLabelled()) {
//                        addThis = false;
//                    }
//                }
//                if(addThis) {
//                    Z_I.add((Zone) labelledCurveAbstract.diagram().toAbstract(z));
//                }
//            }
//        }

        CACS_I.addAll(diagram.SZR(Z_I, UED_mask));
        for(ZonalRegion zr : CACS_I) {
            zr.OUT().set(labelledCurveAbstract);
        }

        result.addAll(CACS_I);


        // 2. CACS_O
        Set<ZonalRegion> CACS_O = new HashSet<ZonalRegion>();

        AbstractSet<Zone> Z_o = new HashSet<Zone>();
        for(Zone z : diagram.Z()) {
            if(z.OUTcontainsCurve(labelledCurveAbstract)) {
                boolean keep = true;
                for(Zone z_dash : labelledCurveAbstract.zones()) {
                    // are in and out the same given the mask == is in the same
                    if(z.inAsFastCurveSet().logicalEQ(z_dash.inAsFastCurveSet(), UED_mask)) {
                        keep = false;
                        break;
                    }
                }
                if(keep) {
                    Z_o.add(z);
                }
            }
        }

        CACS_O.addAll(diagram.SZR(Z_o, UED_mask));
        for(ZonalRegion zr : CACS_O) {
            zr.IN().set(labelledCurveAbstract);
        }
        result.addAll(CACS_O);


        // 3. CACS_*
        Set<ZonalRegion> CACS_star = new HashSet<ZonalRegion>();

        AbstractSet<Zone> Zstar = new HashSet<Zone>();

        for(Zone z : diagram.Zstar()) {
            // Copy the fast zones just to make sure
            FastCurveSet in = new FastCurveSet(z.inAsFastCurveSet());
            FastCurveSet out = new FastCurveSet(z.outAsFastCurveSet());

            // need to check if this zone is shaded in the UED(v+(k,lambda))
            UED_mask.clear(labelledCurveAbstract);
            if(diagram.shadedAfterMinus(z, UED_mask)) {

                if (z.INcontainsCurve(labelledCurveAbstract)) {
                    // OK, this (in,out) - UK is in Z*(UED(v)), so only way we keep it is if
                    // (in,out) - UK is not able to be made from CACS_o
                    //

                    // can mask out in subsetcheck
                    //in.mask(UED_mask);
                    //out.mask(UED_mask);

                    // back to UED(v)
                    UED_mask.set(labelledCurveAbstract);

                    Boolean isIn = true;
                    for (ZonalRegion zr : CACS_O) {
                        if (zr.IN().subseteqOF(in, UED_mask) && zr.OUT().subseteqOF(out, UED_mask)) {
                            // do nothing - this means it is in Z_OUT (in Z* - def), so not in rhs of def 36 pt 3, so stays in LHS
                        } else {
                            // if we are here then this (in,out) doesn't get removed in def Z* -, so it does in def 36 pt 3
                            isIn = false;
                            break;
                        }
                    }
                    if (isIn) {
                        Zstar.add(z);
                    }
                } else {
                    // OK k is in OUT, so same test, except with CACS_I
                    Boolean isIn = true;
                    for (ZonalRegion zr : CACS_I) {
                        if (zr.IN().subseteqOF(in, UED_mask) && zr.OUT().subseteqOF(out, UED_mask)) {

                        } else {
                            isIn = false;
                            break;
                        }
                    }
                    if (isIn) {
                        Zstar.add(z);
                    }
                }
            }
        }

        // SZR for  UED(v+(k,lambda))
        UED_mask.clear(labelledCurveAbstract);
        CACS_star.addAll(diagram.SZR(Zstar, UED_mask));

        result.addAll(CACS_star);

        return result;
    }
}
