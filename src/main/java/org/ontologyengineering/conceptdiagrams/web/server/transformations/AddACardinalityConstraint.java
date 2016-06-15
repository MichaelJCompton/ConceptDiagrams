package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.Arrow;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;


/**
 * Transformation 17
 */
public class AddACardinalityConstraint extends LabelledMultiDiagramTransformation {

    private ConcreteArrow constrainedArrow;

    private Arrow constrainedArrowAbstract;

    public AddACardinalityConstraint(ConcreteArrow constrainedArrow) {
        this.constrainedArrow = constrainedArrow;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        constrainedArrowAbstract = ((Arrow) transformedDiagram.toAbstract(constrainedArrow));
        constrainedArrowAbstract.setCardinalityConstraintFromConcrete();

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {

        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting


        // Def 45 & 46


        if(constrainedArrow.isObjectProperty()) {

            // Definition 45
            if (!constrainedArrow.isInverse()) {
                outputter.addFunctionalObjectProperty(constrainedArrowAbstract);
            } else {
                outputter.addInverseFunctionalObjectProperty(constrainedArrowAbstract);
            }
        } else {

            // Definition 46
            outputter.addFunctionalDataProperty(constrainedArrowAbstract);
        }
    }
}

