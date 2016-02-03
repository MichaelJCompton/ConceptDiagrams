package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br> Date: November 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// For defs 35 & 37
public class AddCurveLabelToClassPropertyDiagram extends AddCurveLabel<ClassAndObjectPropertyDiagram> {


    public AddCurveLabelToClassPropertyDiagram(ConcreteCurve labelledCurve) {
        super(labelledCurve);
    }


    // Definition 37
    @Override
    public void translate(ClassAndObjectPropertyDiagram transformedDiagram, OWLOutputter outputter) {
        // no caching required
        executeTransformation(transformedDiagram);

        // IT(PD_2, PD_1) = EC U SCO U DC U DOM U RAN

        Set<ZonalRegion> CACS = CACS();
        for (ZonalRegion zr : CACS) {

            // 1.  EC

            if (zr.IN().isZero() && zr.OUT().numBitsSet() == 1 && zr.OUTcontainsCurve(labelledCurve.getAbstractSyntaxRepresentation())) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.IN().isZero() && zr.OUT().numBitsSet() >= 2) {
                outputter.addEquivalentUnionThing(zr.OUT(), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() == 1 && zr.INcontainsCurve(labelledCurve.getAbstractSyntaxRepresentation())) {
                outputter.addEquivalentNoThing(transformedDiagram.getCurve(zr.OUT().iterator().next()), transformedDiagram);
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

            if (zr.OUT().isZero() && zr.IN().numBitsSet() == 2 && zr.INcontainsCurve(labelledCurve.getAbstractSyntaxRepresentation())) {
                int curve1, curve2;
                Iterator<Integer> iter = zr.OUT().iterator();
                curve1 = iter.next();
                curve2 = iter.next();
                outputter.addDisjointClasses(transformedDiagram.getCurve(curve1), transformedDiagram.getCurve(curve2), transformedDiagram);
            } else if (zr.OUT().isZero() && zr.IN().numBitsSet() > 2) {
                FastCurveSet curves = new FastCurveSet(zr.IN());
                curves.clear(labelledCurve.getAbstractSyntaxRepresentation());
                outputter.addDisjointClasses(labelledCurve.getAbstractSyntaxRepresentation(), curves, transformedDiagram);
            }
        }
        // 4.  DOM

        AbstractSet<Arrow> INT = transformedDiagram.INT(labelledCurve.getAbstractSyntaxRepresentation());
        // we've already done the transformation, so this is v+(k,\lambda)

        AbstractSet<ObjectPropertyArrow> domainArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> domainArrowsTSC = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrowsTSC = new HashSet<ObjectPropertyArrow>();
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.getTarget() == labelledCurve.getAbstractSyntaxRepresentation()
                    && a.isInverse()) {
                domainArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    a.isInverse() &&
                    INT.contains(a)) {
                domainArrowsTSC.add(a);
            }
            if (a.getTarget() == labelledCurve.getAbstractSyntaxRepresentation()
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
            outputter.addObjectPropertyDomain(a, labelledCurve.getAbstractSyntaxRepresentation(), transformedDiagram);
        }
        for (ObjectPropertyArrow a : domainArrowsTSC) {
            outputter.addObjectPropertyDomainTSC(a, transformedDiagram.SC(a), transformedDiagram);
        }


        // 5. RAN

        for (ObjectPropertyArrow a : rangeArrows) {
            outputter.addObjectPropertyRange(a, labelledCurve.getAbstractSyntaxRepresentation(), transformedDiagram);
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
        LabelledDiagram diagram = getCurve().getAbstractSyntaxRepresentation().diagram();

        // CACS(v, k, \lambda) = CACS_I, CACS_O, CACS_*


        FastCurveSet UED_mask = new FastCurveSet();
        for(Curve c : diagram.UK()) {
            UED_mask.set(c);
        }
        // just to make sure
        UED_mask.set(getCurve().getAbstractSyntaxRepresentation());


        // 1. CACS_I
        // I think looking at the definition that this is the set of zones in curves completely contained in k
        Set<ZonalRegion> CACS_I = new HashSet<ZonalRegion>();

        AbstractSet<Zone> Z_I = new HashSet<Zone>();
        for(ConcreteZone z : getCurve().getCompletelyContainedZones()) {
            if(z.getAbstractSyntaxRepresentation() != null) {
                Z_I.add(z.getAbstractSyntaxRepresentation());
            }
        }

        CACS_I.addAll(diagram.SZR(Z_I, UED_mask));

        result.addAll(CACS_I);


        // 2. CACS_O
        // This one seems to be the zones of curves that are disjoint from k
        Set<ZonalRegion> CACS_O = new HashSet<ZonalRegion>();

        AbstractSet<Zone> disjoints = new HashSet<Zone>();
        for(Curve c : diagram.K()) {
            if(c != getCurve().getAbstractSyntaxRepresentation()) {
                if(!getCurve().getIntersectingCurves().contains(c.getConcreteRepresentation())) {
                    disjoints.addAll(c.zones());
                }
            }
        }

        CACS_O.addAll(diagram.SZR(disjoints, UED_mask));

        result.addAll(CACS_O);


        // 3. CACS_*
        Set<ZonalRegion> CACS_star = new HashSet<ZonalRegion>();

        AbstractSet<Zone> Zstar = new HashSet<Zone>();

        for(Zone z : diagram.Zstar()) {
            // Copy the fast zones just to make sure
            FastCurveSet in = new FastCurveSet(z.inAsFastCurveSet());
            FastCurveSet out = new FastCurveSet(z.outAsFastCurveSet());
            if(z.INcontainsCurve(getCurve().getAbstractSyntaxRepresentation())) {
                // OK, this (in,out) - UK is in Z*(UED(v)), so only way we keep it is if
                // (in,out) - UK is not able to be made from CACS_o
                //

                // can mask out in subsetcheck
                //in.mask(UED_mask);
                //out.mask(UED_mask);

                Boolean isIn = true;
                for(ZonalRegion zr : CACS_O) {
                    if(zr.IN().subseteqOF(in, UED_mask) && zr.OUT().subseteqOF(out, UED_mask)) {
                        // do nothing - this means it is in Z_OUT (in Z* - def), so not in rhs of def 36 pt 3, so stays in LHS
                    } else {
                        // if we are here then this (in,out) doesn't get removed in def Z* -, so it does in def 36 pt 3
                        isIn = false;
                        break;
                    }
                }
                if(isIn) {
                    Zstar.add(z);
                }
            } else {
                // OK k is in OUT, so same test, except with CACS_I
                Boolean isIn = true;
                for(ZonalRegion zr : CACS_I) {
                    if(zr.IN().subseteqOF(in, UED_mask) && zr.OUT().subseteqOF(out, UED_mask)) {

                    } else {
                        isIn = false;
                        break;
                    }
                }
                if(isIn) {
                    Zstar.add(z);
                }
            }
        }

        CACS_star.addAll(diagram.SZR(Zstar, UED_mask));

        result.addAll(CACS_star);

        return result;
    }
}
