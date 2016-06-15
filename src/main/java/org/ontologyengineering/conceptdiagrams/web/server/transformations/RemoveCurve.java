package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


/**
 * Transformation 9
 * <p/>
 * Don't think this one will be used in practise.  It's implemented (only part of it is implemented) because the defn.
 * is there, but it will be much faster to do the real defn in RemoveCurves (Transformation 10).
 */
public class RemoveCurve<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {


    private ConcreteCurve removedCurve;

    public RemoveCurve(ConcreteCurve removedCurve) {
        this.removedCurve = removedCurve;
    }

    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // never called
    }

    @Override
    public void translate(T transformedDiagram, OWLOutputBuilder outputter) {
        // should never be called
    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
    }
}
