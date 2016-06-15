package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.HashMap;

/**
 * The generics type here isn't totally correct, because a ConceptDiagram just can't have a diagram, but it's fine in
 * practise.
 */
public abstract class AbstractDiagram<DiagramType extends AbstractDiagram, ConcreteType extends ConcreteDiagramElement> extends DiagramElement<DiagramType, ConcreteType> {

    private HashMap<ConcreteDiagramElement, DiagramElement> concreteAbstractMap = new HashMap<ConcreteDiagramElement, DiagramElement>();


    public AbstractDiagram() {
        super();
    }

    //
    AbstractDiagram(DiagramType parent) {
        super(parent);
    }

    protected void setConcreteAbstractMap(ConcreteDiagramElement conc, DiagramElement abs) {
        concreteAbstractMap.put(conc, abs);
    }

    public DiagramElement toAbstract(ConcreteDiagramElement conc) {
        return concreteAbstractMap.get(conc);
    }

}
