package org.ontologyengineering.conceptdiagrams.web.server.handler;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.ConvertAllToOWLService;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.ConvertDiagramsToOWL;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLAPIutils;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.StandardClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;


/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */

public class ConvertAllToOWLServiceImpl extends RemoteServiceServlet implements ConvertAllToOWLService {


    //private static ConvertAllToOWLServiceImpl theInstance;

    public ConvertAllToOWLServiceImpl() {

    }


    // FIXME : probably should be that this is just the service interface and it calls another class to do the work??
    public void convertAllToOWL(ArrayList<Command> history, DiagramSet diagrams, ClientContext context){

        // FIXME: looks like it's not necessary to transport the diagramsSet

        // set up the testing
//        GsonClassSerializer gsonSerialiser = new GsonClassSerializer();
//        gsonSerialiser.serializeCommandHistory(history);

        JacksonClassSerializer jacksonSerializer = new JacksonClassSerializer();
        jacksonSerializer.serializeCommandHistory(history);

//        OWLOntology ontology = OWLAPIutils.makeFreshOntology(((StandardClientContext) context).getIri());
//
//        ConvertDiagramsToOWL converter = new ConvertDiagramsToOWL();
//        converter.convertToOWL(history, ontology);
//
//        OWLAPIutils.writeOntology(ontology, "/tmp/" + ((StandardClientContext) context).getFileName());
    }


}