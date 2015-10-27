package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.AbstractDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * The transformations.
 * <p/>
 * In the implementation these are transformations using the already built concrete syntax.  So the parameters as
 * described in the document are derived here from the existing concrete diagram.
 */
public abstract class Transformation <T extends AbstractDiagram> {

    protected boolean executed = false;

    private T transformedDiagram;

    //protected OWLOutputter outputter;

    public Transformation() {

    }

    public void executeTransformation(T transformedDiagram) {
        this.transformedDiagram = transformedDiagram;

        // subclasses should call setAsExecuted once done.
    }

    // takes the current state, executes the transformation and uses the outputter to produce the resultant OWL
    public abstract void translate(T transformedDiagram, OWLOutputter outputter);

    // this could be pre- or post-execution ... or after some other number of transformations.  It's really only
    // valid for the step being taken.
    public T diagram() {
        return transformedDiagram;
    }

    protected void setAsExecuted() {
        executed = true;
    }

    public boolean isExecuted() {
        return executed;
    }

}
