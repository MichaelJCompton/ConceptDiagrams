package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;

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
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

        // nothing to do because it should be done by the enclosed labelled diagram transformation??
        // so how should it be called???
        // will it have in it enough to extract the affected diagram??
        //
        // is it that we have the concrete seed, we use that to get the abstract, then the diagram for that
        // then us that in the translate???
        //
        // That way we can just call translate with the outputter???
        theTransformation.translate(transformedDiagram, outputter);

    }
}
