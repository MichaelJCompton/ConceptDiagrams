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
 * Transformation 17
 */
public class AddACardinalityConstraint extends LabelledMultiDiagramTransformation {

    private ConcreteArrow constrainedArrow;

    public AddACardinalityConstraint(ConcreteArrow constrainedArrow) {
        this.constrainedArrow = constrainedArrow;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        constrainedArrow.getAbstractSyntaxRepresentation().setCardinalityConstraint(constrainedArrow.getCardinalityConstraint(), constrainedArrow.getCardinality());

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {

        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

        if(constrainedArrow.isObjectProperty()) {
            if (!constrainedArrow.isInverse()) {
                outputter.addFunctionalObjectProperty(constrainedArrow.getAbstractSyntaxRepresentation());
            } else {
                outputter.addInverseFunctionalObjectProperty(constrainedArrow.getAbstractSyntaxRepresentation());
            }
        } else {
            outputter.addFunctionalDataProperty(constrainedArrow.getAbstractSyntaxRepresentation());
        }
    }
}

