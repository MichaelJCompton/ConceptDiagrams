package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: November 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


import java.util.AbstractSet;
import java.util.HashSet;


// Definition 38
public class AddCurveLabelToDataPropertyDiagram extends AddCurveLabel <DatatypeDiagram> {

    private Curve labelledCurveAbstract;

    public AddCurveLabelToDataPropertyDiagram(ConcreteCurve labelledCurve) {
        super(labelledCurve);
    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        labelledCurveAbstract = (Curve) diagram.toAbstract(labelledCurve);
        if(diagram.toAbstract(labelledCurve).diagram() instanceof DatatypeDiagram) {
            translate((DatatypeDiagram) diagram.toAbstract(labelledCurve).diagram(), outputter);
        } else {
            // something bad
        }
    }

    // Definition 38
    @Override
    public void translate(DatatypeDiagram transformedDiagram, OWLOutputBuilder outputter) {
        // no caching required
        executeTransformation(transformedDiagram);

        // IT(PD_2, PD_1) = RAN

        AbstractSet<Arrow> INT = transformedDiagram.INT(labelledCurveAbstract);
        // we've already don the transformation, so this is v+(k,\lambda)

        AbstractSet<DatatypePropertyArrow> rangeArrows = new HashSet<DatatypePropertyArrow>();
        AbstractSet<DatatypePropertyArrow> rangeArrowsTSC = new HashSet<DatatypePropertyArrow>();
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            if (a.getTarget() == labelledCurveAbstract) {
                rangeArrows.add(a);
            }
            if (a.getTarget().getClass() == Curve.class &&
                    a.getTarget().isUnLabelled() &&
                    INT.contains(a)) {
                rangeArrowsTSC.add(a);
            }
        }


        // RAN

        for(DatatypePropertyArrow a : rangeArrows) {
            outputter.addDataPropertyRange(a, labelledCurveAbstract, transformedDiagram);
        }
        for(DatatypePropertyArrow a : rangeArrowsTSC) {
            outputter.addDataPropertyRangeTSC(a, transformedDiagram.SC(a), transformedDiagram);
        }
    }
}
