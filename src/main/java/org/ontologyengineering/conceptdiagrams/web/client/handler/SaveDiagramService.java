package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
@RemoteServiceRelativePath("SaveDiagram")
public interface SaveDiagramService extends RemoteService {

    String saveCommandHistory(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams, ClientContext context);

}
