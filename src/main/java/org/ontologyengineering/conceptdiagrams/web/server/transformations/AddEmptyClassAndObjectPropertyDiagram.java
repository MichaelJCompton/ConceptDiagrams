package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.ClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;


/**
 * Transformation 11
 */
public class AddEmptyClassAndObjectPropertyDiagram extends LabelledMultiDiagramTransformation {

    // must be one that is a going to be a class and object property diagram
    private ConcreteBoundaryRectangle addedDiagram;

    public AddEmptyClassAndObjectPropertyDiagram(ConcreteBoundaryRectangle addedDiagram) {
        this.addedDiagram = addedDiagram;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        ClassAndObjectPropertyDiagram newDiagram = new ClassAndObjectPropertyDiagram(transformedDiagram);
        transformedDiagram.addClassAndObjectPropertyDiagram(newDiagram);
        newDiagram.boundaryRectangle().setConcreteRepresentation(addedDiagram);
        newDiagram.boundaryRectangle().makeBRZone();

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {
        executeTransformation(transformedDiagram);

        // and nothing else
    }
}
