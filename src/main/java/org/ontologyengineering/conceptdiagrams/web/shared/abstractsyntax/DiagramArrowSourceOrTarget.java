package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.AbstractCollection;
import java.util.HashSet;

/**
 * Typing for those things that can be the source and targets of arrows.  Also provides a common interface for functions
 * related to that.
 */
public abstract class DiagramArrowSourceOrTarget<ConcreteType extends ConcreteDiagramElement> extends DiagramElement<LabelledDiagram, ConcreteType> {

    DiagramArrowSourceOrTarget() {
        super();
    }

    DiagramArrowSourceOrTarget(LabelledDiagram parent) {
        super(parent);
    }

    DiagramArrowSourceOrTarget(String label) {
        super(label);
    }

    DiagramArrowSourceOrTarget(String label, LabelledDiagram parent) {
        super(label, parent);
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }
}
