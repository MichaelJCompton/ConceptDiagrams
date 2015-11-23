package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ObjectPropertyArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 15
 */
public class AddAnObjectPropertyLabelledArrow extends LabelledMultiDiagramTransformation {

    private ConcreteArrow addedArrow;
    private ObjectPropertyArrow addedArrowAbstract;

    public AddAnObjectPropertyLabelledArrow(ConcreteArrow addedArrow) {
        this.addedArrow = addedArrow;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        addedArrowAbstract = transformedDiagram.addObjectPropertyArrow(addedArrow);

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {



        // do some preamble

        executeTransformation(transformedDiagram);


        if(addedArrow.getAbstractSyntaxRepresentation().targetIsBoundaryRectangle()) {
            outputter.addObjectPropertyEquivTop(addedArrow.getAbstractSyntaxRepresentation(), addedArrow.getAbstractSyntaxRepresentation().getTarget().diagram());
        } else {

        }

    }
}
