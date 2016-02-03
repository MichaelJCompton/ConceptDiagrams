package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 8
 */
public class AddCardinalityConstraint<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {


    private ConcreteArrow constrainedArrow;

    public AddCardinalityConstraint(ConcreteArrow constrainedArrow) {
        this.constrainedArrow = constrainedArrow;
    }


    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }



    @Override
    public void translate(T transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
