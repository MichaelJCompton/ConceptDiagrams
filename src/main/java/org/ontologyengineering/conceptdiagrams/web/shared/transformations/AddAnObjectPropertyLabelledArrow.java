package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 15
 */
public class AddAnObjectPropertyLabelledArrow extends LabelledMultiDiagramTransformation {

    private ConcreteArrow addedArrow;

    public AddAnObjectPropertyLabelledArrow(ConcreteArrow addedArrow) {
        this.addedArrow = addedArrow;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
