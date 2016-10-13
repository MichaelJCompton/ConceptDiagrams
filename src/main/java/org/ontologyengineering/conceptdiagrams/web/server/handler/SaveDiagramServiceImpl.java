package org.ontologyengineering.conceptdiagrams.web.server.handler;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.SaveDiagramService;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.ConvertDiagramsToOWL;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLAPIutils;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */

public class SaveDiagramServiceImpl  extends RemoteServiceServlet implements SaveDiagramService {


    public SaveDiagramServiceImpl() {

    }



    public String saveCommandHistory(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams, ClientContext context){

        // should be able to rebuild the diagram set exactly from the history

        UUID ontologyID = UUID.randomUUID();


        // FIXME needs to go into properties or similar
        String prefix = "/tmp/org.ontologyengineering.conceptdiagrams.";

        String filename = "diagrams_2_OWL_savefile" + ontologyID + ".cd.hist";

        JacksonClassSerializer jacksonSerializer = new JacksonClassSerializer();
        jacksonSerializer.serializeCommandHistory(histories, prefix + filename);

        return filename;
    }


}
