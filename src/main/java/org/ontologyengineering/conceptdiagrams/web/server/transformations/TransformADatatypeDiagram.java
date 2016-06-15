package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;


/**
 * Transformation 14
 */
public class TransformADatatypeDiagram extends LabelledMultiDiagramTransformation {

    private LabelledDiagramTransformation theTransformation;

    public TransformADatatypeDiagram(LabelledDiagramTransformation theTransformation) {
        this.theTransformation = theTransformation;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}

