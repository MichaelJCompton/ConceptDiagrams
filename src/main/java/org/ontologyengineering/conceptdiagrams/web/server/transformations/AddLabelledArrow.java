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
 * Transformation 7
 */
public class AddLabelledArrow<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {

    private ConcreteArrow addedArrow;

    public AddLabelledArrow(ConcreteArrow addedArrow) {
        this.addedArrow = addedArrow;
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
        // FIXME : not in the translation so far

        // what the???
        //translate((T)addedArrow.getAbstractSyntaxRepresentation().diagram(), outputter);
    }
}
