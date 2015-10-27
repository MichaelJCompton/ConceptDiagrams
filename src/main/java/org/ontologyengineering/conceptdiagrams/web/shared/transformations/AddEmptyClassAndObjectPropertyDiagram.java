package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

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

        transformedDiagram.addClassAndObjectPropertyDiagram(new ClassAndObjectPropertyDiagram(transformedDiagram));

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {
        executeTransformation(transformedDiagram);

        // and nothing else
    }
}
