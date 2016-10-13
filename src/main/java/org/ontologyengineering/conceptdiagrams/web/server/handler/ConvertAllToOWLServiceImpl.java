package org.ontologyengineering.conceptdiagrams.web.server.handler;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.ConvertAllToOWLService;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.ConvertDiagramsToOWL;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLAPIutils;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.StandardClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */

public class ConvertAllToOWLServiceImpl extends RemoteServiceServlet implements ConvertAllToOWLService {


    //private static ConvertAllToOWLServiceImpl theInstance;

    public ConvertAllToOWLServiceImpl() {

    }



    public String convertAllToOWL(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams, ClientContext context){ //DiagramSet diagrams,

        // should be able to rebuild the diagram set exactly from the history


        // set up the testing
//        GsonClassSerializer gsonSerialiser = new GsonClassSerializer();
//        gsonSerialiser.serializeCommandHistory(history);

//        JacksonClassSerializer jacksonSerializer = new JacksonClassSerializer();
//        jacksonSerializer.serializeCommandHistory(history);

        OWLOntology ontology = OWLAPIutils.makeFreshOntology(context.getIRI());

        for(ArrayList<Command> history : histories) {
            if(history.size() > 0) {
                // FIXME : just property diagrams for now
                if(history.get(0).getDiagram().getDiagramSet().isPropertyDiagramSet()) {
                    ConvertDiagramsToOWL converter = new ConvertDiagramsToOWL();
                    converter.convertToOWL(history, ontology);
                }
            }
        }

        UUID ontologyID = UUID.randomUUID();

        String filename = "diagrams_2_OWL_output" + ontologyID + ".owl";

        OWLAPIutils.writeOntology(ontology, "/tmp/org.ontologyengineering.conceptdiagrams." + filename);

        return filename;
    }


}