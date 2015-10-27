package org.ontologyengineering.conceptdiagrams.web.shared.transformations;


/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;


public class TransformationManager {


    private AbstractMap<ConcreteDiagram, LabelledMultiDiagramConstructionSequence> constructionSequences;

    private AbstractMap<ConcreteDiagram, Boolean> isValidConstructionSequence;

    // assuming that the edit history starts at the beginning
    // assuming the edit history is for these diagrams
    public TransformationManager(DiagramSet diagrams, ArrayList<Command> editHistory) {  // ArrayList cause of the indexed access
        constructionSequences = new HashMap<ConcreteDiagram, LabelledMultiDiagramConstructionSequence>();
        isValidConstructionSequence = new HashMap<ConcreteDiagram, Boolean>();

        for(int i = 0; i < editHistory.size(); i++) {
            Command c = editHistory.get(i);
            if(c.leadsToValid()) {
                LabelledMultiDiagramTransformation trans = c.asMultiDiagramTransformation(editHistory, i);
                if(constructionSequences.get(c.getDiagram()) == null) {
                    constructionSequences.put(c.getDiagram(), new LabelledMultiDiagramConstructionSequence(c.getDiagram()));
                }
                if(trans != null) {  // some commands don't result in transformations
                    constructionSequences.get(c.getDiagram()).addTransformation(trans);
                }
                isValidConstructionSequence.put(c.getDiagram(), true);
            } else {
                isValidConstructionSequence.put(c.getDiagram(), false);
            }
        }
    }

    public void translateAll(OWLOutputter outputter) {
        for(ConcreteDiagram d : constructionSequences.keySet()) {
            translateDiagram(d, outputter);
        }
    }

    public void translateDiagram(ConcreteDiagram diagram, OWLOutputter outputter) {
        if(isValidConstructionSequence.get(diagram)) {
            constructionSequences.get(diagram).translate(outputter);
        }
    }

}
