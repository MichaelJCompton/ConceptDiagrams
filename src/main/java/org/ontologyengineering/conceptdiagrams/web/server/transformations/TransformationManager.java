package org.ontologyengineering.conceptdiagrams.web.server.transformations;


/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;


public class TransformationManager {


    private AbstractMap<ConcreteDiagram, LabelledMultiDiagramConstructionSequence> constructionSequences;

    private AbstractMap<ConcreteDiagram, Boolean> isValidConstructionSequence;

    // assuming that the edit history starts at the beginning
    // assuming the edit history is for these diagrams
    //public TransformationManager(DiagramSet diagrams, ArrayList<Command> editHistory) {  // ArrayList cause of the indexed access
    public TransformationManager(ArrayList<Command> editHistory) {  // ArrayList cause of the indexed access
        constructionSequences = new HashMap<ConcreteDiagram, LabelledMultiDiagramConstructionSequence>();
        isValidConstructionSequence = new HashMap<ConcreteDiagram, Boolean>();

        CommandTransformer transformer = new CommandTransformer();

        for (int i = editHistory.size() - 1; i >= 0; i--) {
            Command c = editHistory.get(i);
            if (c.leadsToValid()) {
                LabelledMultiDiagramTransformation trans = transformer.makeTransformationFromCommand(c, editHistory, i);
                ConcreteDiagramElement e = c.getDiagram();
                if (constructionSequences.get(c.getDiagram()) == null) {
                    constructionSequences.put(c.getDiagram(), new LabelledMultiDiagramConstructionSequence(c.getDiagram()));
                }
                if (trans != null) {  // some commands don't result in transformations
                    constructionSequences.get(c.getDiagram()).addTransformation(trans);
                }
                isValidConstructionSequence.put(c.getDiagram(), true);  // FIXME : looks suspicious , should be or with prev value?
            } else {
                isValidConstructionSequence.put(c.getDiagram(), false);
            }
        }
    }

    public void translateAll(OWLOutputBuilder outputter) {

        for (ConcreteDiagram d : constructionSequences.keySet()) {
            translateDiagram(d, outputter);
        }
    }

    public void translateDiagram(ConcreteDiagram diagram, OWLOutputBuilder outputter) {

        if (isValidConstructionSequence.get(diagram)) {
            constructionSequences.get(diagram).translate(outputter);
        }
    }

}
