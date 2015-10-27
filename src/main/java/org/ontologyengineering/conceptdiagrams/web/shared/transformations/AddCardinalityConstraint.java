package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 8
 */
public class AddCardinalityConstraint extends LabelledDiagramTransformation {

    private ConcreteArrow constrainedArrow;

    public AddCardinalityConstraint(ConcreteArrow constrainedArrow) {
        this.constrainedArrow = constrainedArrow;
    }

    @Override
    public void executeTransformation(LabelledDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(LabelledDiagram transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
