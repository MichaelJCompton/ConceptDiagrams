package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */



public class DatatypePropertyArrow extends Arrow {


    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        super(source, target);
    }

    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                          String label) {
        super(source, target, label);
    }

    DatatypePropertyArrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
                          String label, AbstractDiagram parent) {
        super(source, target, label, parent);
    }
}
