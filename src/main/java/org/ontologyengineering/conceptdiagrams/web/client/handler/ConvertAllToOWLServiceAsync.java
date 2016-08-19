package org.ontologyengineering.conceptdiagrams.web.client.handler;


import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */


public interface ConvertAllToOWLServiceAsync {

    //
    void convertAllToOWL(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams, ClientContext context, AsyncCallback<String> callback); //DiagramSet diagrams,
}
