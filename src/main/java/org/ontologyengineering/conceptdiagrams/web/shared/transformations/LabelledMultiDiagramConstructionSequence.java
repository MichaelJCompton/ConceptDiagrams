package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ConceptDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.PropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

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

    public void translate(OWLOutputter outputter) {
        if(diagram.isConceptDiagram()) {
            constructedAbstractDiagram = new ConceptDiagram();
        } else {
            constructedAbstractDiagram = new PropertyDiagram();
        }

        for(LabelledMultiDiagramTransformation t : constructionSequence) {
            // at each iteration the constructed diagram changes until we get to the final
            t.translate(constructedAbstractDiagram, outputter);
        }
    }

    public LabelledMultiDiagram getConstructedAbstractDiagram () {
        return constructedAbstractDiagram;
    }

}
