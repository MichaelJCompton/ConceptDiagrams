package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 4
 */
public class AddCurveLabel extends LabelledDiagramTransformation {

    private ConcreteCurve labelledCurve;

    public AddCurveLabel(ConcreteCurve labelledCurve) {
        this.labelledCurve = labelledCurve;
    }

    @Override
    public void executeTransformation(LabelledDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // somewhere along the line I should be checking that the labels are legal
        transformedDiagram.labelCurve(labelledCurve);
    }

    @Override
    public void translate(LabelledDiagram transformedDiagram, OWLOutputter outputter) {
        // no caching required
        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
