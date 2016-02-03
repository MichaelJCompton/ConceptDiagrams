package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 13
 */
public class TransformAClassAndObjectPropertyDiagram extends LabelledMultiDiagramTransformation {

    private LabelledDiagramTransformation theTransformation;

    public TransformAClassAndObjectPropertyDiagram(LabelledDiagramTransformation theTransformation) {
        this.theTransformation = theTransformation;
    }


    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
