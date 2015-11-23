package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br> Date: November 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
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

        // FIXME : to be done --- need V_op-

        AbstractSet<Arrow> INT = transformedDiagram.INT(labelledCurve.getAbstractSyntaxRepresentation());
        // we've already don the transformation, so this is v+(k,\lambda)

        AbstractSet<ObjectPropertyArrow> domainArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> domainArrowsTSC = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrows = new HashSet<ObjectPropertyArrow>();
        AbstractSet<ObjectPropertyArrow> rangeArrowsTSC = new HashSet<ObjectPropertyArrow>();
        for (ObjectPropertyArrow a : transformedDiagram.diagram().getObjectPropertyArrows()) {
            if (a.getTarget() == labelledCurve.getAbstractSyntaxRepresentation()
                // FIXME : && OPE-
                    ) {
                domainArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    // FIXME : OPE-
                    INT.contains(a)) {
                domainArrowsTSC.add(a);
            }
            if (a.getTarget() == labelledCurve.getAbstractSyntaxRepresentation()
                // FIXME : && OPE
                    ) {
                rangeArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    // FIXME : OPE-
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


    // Definition 35
    private Set<ZonalRegion> CACS() {
        Set<ZonalRegion> result = new HashSet<ZonalRegion>();

        // FIXME : can't do till work out how SZR(EZ....)  works

        return result;
    }
}
