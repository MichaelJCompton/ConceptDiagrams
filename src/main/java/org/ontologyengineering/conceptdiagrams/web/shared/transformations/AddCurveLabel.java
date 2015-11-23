package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;
import org.semanticweb.owlapi.model.OWLClassAxiom;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Transformation 4
 */
public abstract class AddCurveLabel <T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {

    protected ConcreteCurve labelledCurve;

    public AddCurveLabel(ConcreteCurve labelledCurve) {
        this.labelledCurve = labelledCurve;
    }

    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // somewhere along the line I should be checking that the labels are legal
        transformedDiagram.labelCurve(labelledCurve);

        setAsExecuted();
    }



}
