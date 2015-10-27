package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import java.util.AbstractCollection;
import java.util.HashSet;

/**
 * Typing for those things that can be the source and targets of arrows.  Also provides a common interface for functions
 * related to that.
 */
public abstract class DiagramArrowSourceOrTarget<DiagramType extends AbstractDiagram> extends DiagramElement<DiagramType> {

    DiagramArrowSourceOrTarget() {
        super();
    }

    DiagramArrowSourceOrTarget(DiagramType parent) {
        super(parent);
    }

    DiagramArrowSourceOrTarget(String label) {
        super(label);
    }

    DiagramArrowSourceOrTarget(String label, DiagramType parent) {
        super(label, parent);
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }
}
