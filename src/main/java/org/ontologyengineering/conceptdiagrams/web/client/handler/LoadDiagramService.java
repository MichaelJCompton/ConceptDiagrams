package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */

@RemoteServiceRelativePath("LoadDiagram")
public interface LoadDiagramService extends RemoteService {

    HashSet<ArrayList<Command>> loadCommandHistory(String file);

}


