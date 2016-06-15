package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;


public abstract class LabelledDiagramTransformation <T extends LabelledDiagram> extends Transformation <T> {

    public LabelledDiagramTransformation() {
    }

    // I think for all these they have been made with the concrete element and so we can call the
    // other version just fine from inside the class??
    public abstract void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter);

}
