package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


/**
 * Transformation 3
 */
public class AddUnlabelledCurve extends LabelledDiagramTransformation<LabelledDiagram> {

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
    public void translate(LabelledDiagram transformedDiagram, OWLOutputBuilder outputter) {
        executeTransformation(transformedDiagram);

        // otherwise no-op
    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        translate((LabelledDiagram) diagram.toAbstract(addedCurve.getBoundaryRectangle()).diagram(), outputter);
    }
}
