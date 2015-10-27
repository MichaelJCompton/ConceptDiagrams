package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


/**
 *
 * The generics type here isn't totally correct, because a ConceptDiagram just can't have a diagram, but it's fine
 * in practise.
 */
public abstract class AbstractDiagram<DiagramType extends AbstractDiagram> extends DiagramElement<DiagramType> {

    AbstractDiagram() {
        super();
    }

    AbstractDiagram(DiagramType parent) {
        super(parent);
    }


}
