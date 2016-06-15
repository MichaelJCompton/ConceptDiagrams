package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.DatatypeDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;

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
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {
        executeTransformation(transformedDiagram);

        // and nothing else
    }

}
