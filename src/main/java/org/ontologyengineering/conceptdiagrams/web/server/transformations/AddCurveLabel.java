package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


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

    public ConcreteCurve getCurve() {
        return labelledCurve;
    }

}
