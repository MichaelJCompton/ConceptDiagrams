package org.ontologyengineering.conceptdiagrams.web.server.handler;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.LoadDiagramService;
import org.ontologyengineering.conceptdiagrams.web.client.handler.SaveDiagramService;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
public class LoadDiagramServiceImpl  extends RemoteServiceServlet implements LoadDiagramService {


    public LoadDiagramServiceImpl() {

    }


    // but what about the context ????
    public HashSet<ArrayList<Command>> loadCommandHistory(String file) {

        HashSet<ArrayList<Command>> result;

        String prefix = "/tmp/org.ontologyengineering.conceptdiagrams.";

        String path = prefix + file;


        JacksonClassSerializer jacksonSerialiser = new JacksonClassSerializer();
        result = jacksonSerialiser.readSerializedCommandHistoryToHashSet(path.toString());

        return result;
    }
}
