package org.ontologyengineering.conceptdiagrams.web.server.owlOutput;

import org.ontologyengineering.conceptdiagrams.web.server.transformations.TransformationManager;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.ArrayList;

/**
 * Author: Michael Compton<br>
 * Date: May 2016<br>
 * See license information in base directory.
 */

public class ConvertDiagramsToOWL {


    public ConvertDiagramsToOWL() {

    }

    // affects the ontology passed in
    public void convertToOWL(ArrayList<Command> history, OWLOntology ontology) {
        TransformationManager manger = new TransformationManager(history);

        OWLOntologyManager owlManager = ontology.getOWLOntologyManager();
        OWLAPIOutputBuilder OWLbuilder = new OWLAPIOutputBuilder(ontology, owlManager.getOWLDataFactory());

        manger.translateAll(OWLbuilder);

        // apply all those changes
        OWLbuilder.applyAllChanges(owlManager);
    }
}
