package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.DatatypeDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 12
 */
public class AddEmptyDatatypeDiagram extends LabelledMultiDiagramTransformation {

    private ConcreteBoundaryRectangle addedDiagram;

    public AddEmptyDatatypeDiagram(ConcreteBoundaryRectangle addedDiagram) {
        this.addedDiagram = addedDiagram;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        transformedDiagram.addDatatypeDiagram(new DatatypeDiagram(transformedDiagram));

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {
        executeTransformation(transformedDiagram);

        // and nothing else
    }

}
