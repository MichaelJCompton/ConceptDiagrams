package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

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
    public void translate(T transformedDiagram, OWLOutputBuilder outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }


    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        // FIXME : not in the current version, so no worries for now

        // what the???
        //translate((T)constrainedArrow.getAbstractSyntaxRepresentation().diagram(), outputter);
    }
}
