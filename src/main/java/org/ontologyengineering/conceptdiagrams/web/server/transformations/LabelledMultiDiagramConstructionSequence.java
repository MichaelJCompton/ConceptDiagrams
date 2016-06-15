package org.ontologyengineering.conceptdiagrams.web.server.transformations;

import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.ConceptDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.PropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */




public class LabelledMultiDiagramConstructionSequence {

    private List<LabelledMultiDiagramTransformation> constructionSequence;
    private ConcreteDiagram diagram;
    private LabelledMultiDiagram constructedAbstractDiagram;

    public LabelledMultiDiagramConstructionSequence(ConcreteDiagram diagram) {
        constructionSequence = new LinkedList<LabelledMultiDiagramTransformation>();
        this.diagram = diagram;
    }

    public void addTransformation(LabelledMultiDiagramTransformation newTransformation) {
        constructionSequence.add(newTransformation);
    }

    public void translate(OWLOutputBuilder outputter) {

        diagram.checkValidity();
        if(diagram.isConceptDiagram()) {
            constructedAbstractDiagram = new ConceptDiagram();
        } else {
            constructedAbstractDiagram = new PropertyDiagram();
        }

        constructedAbstractDiagram.setConcreteRepresentation(diagram);

        for(LabelledMultiDiagramTransformation t : constructionSequence) {
            // at each iteration the constructed diagram changes until we get to the final
            t.translate(constructedAbstractDiagram, outputter);
        }
    }

    public LabelledMultiDiagram getConstructedAbstractDiagram () {
        return constructedAbstractDiagram;
    }

}
