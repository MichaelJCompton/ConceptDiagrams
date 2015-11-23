package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: November 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

import java.util.AbstractSet;
import java.util.HashSet;


// Definition 38
public class AddCurveLabelToDataPropertyDiagram extends AddCurveLabel <DatatypeDiagram> {

    public AddCurveLabelToDataPropertyDiagram(ConcreteCurve labelledCurve) {
        super(labelledCurve);
    }



    // Definition 38
    @Override
    public void translate(DatatypeDiagram transformedDiagram, OWLOutputter outputter) {
        // no caching required
        executeTransformation(transformedDiagram);

        // IT(PD_2, PD_1) = RAN

        AbstractSet<Arrow> INT = transformedDiagram.INT(labelledCurve.getAbstractSyntaxRepresentation());
        // we've already don the transformation, so this is v+(k,\lambda)

        AbstractSet<DatatypePropertyArrow> rangeArrows = new HashSet<DatatypePropertyArrow>();
        AbstractSet<DatatypePropertyArrow> rangeArrowsTSC = new HashSet<DatatypePropertyArrow>();
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            if (a.getTarget() == labelledCurve.getAbstractSyntaxRepresentation()) {
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
            outputter.addDataPropertyRange(a, labelledCurve.getAbstractSyntaxRepresentation(), transformedDiagram);
        }
        for(DatatypePropertyArrow a : rangeArrowsTSC) {
            outputter.addDataPropertyRangeTSC(a, transformedDiagram.SC(a), transformedDiagram);
        }
    }
}
