package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */



public class ObjectPropertyArrow extends Arrow {


    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        super(source, target);
    }

    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                        String label) {
        super(source, target, label);
    }

    ObjectPropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                        String label, AbstractDiagram parent) {
        super(source, target, label, parent);
    }
}
