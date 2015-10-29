package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 3
 */
public class AddUnlabelledCurve extends LabelledDiagramTransformation {

    private ConcreteCurve addedCurve;

    public AddUnlabelledCurve(ConcreteCurve addedCurve) {
        this.addedCurve = addedCurve;
    }

    @Override
    public void executeTransformation(LabelledDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        transformedDiagram.addUnlabelledCurve(addedCurve);

        setAsExecuted();
    }

    @Override
    public void translate(LabelledDiagram transformedDiagram, OWLOutputter outputter) {
        executeTransformation(transformedDiagram);

        // otherwise no-op
    }
}
