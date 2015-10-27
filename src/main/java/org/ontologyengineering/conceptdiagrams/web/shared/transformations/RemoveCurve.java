package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

/**
 * Transformation 9
 * <p/>
 * Don't think this one will be used in practise.  It's implemented (only part of it is implemented) because the defn.
 * is there, but it will be much faster to do the real defn in RemoveCurves (Transformation 10).
 */
public class RemoveCurve extends LabelledDiagramTransformation {

    private ConcreteCurve removedCurve;

    public RemoveCurve(ConcreteCurve removedCurve) {
        this.removedCurve = removedCurve;
    }

    @Override
    public void executeTransformation(LabelledDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // never called
    }

    @Override
    public void translate(LabelledDiagram transformedDiagram, OWLOutputter outputter) {
        // should never be called
    }
}
