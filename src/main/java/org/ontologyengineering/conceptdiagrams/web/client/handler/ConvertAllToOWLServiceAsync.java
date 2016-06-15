package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.CommandManager;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */


public interface ConvertAllToOWLServiceAsync {

    //
    void convertAllToOWL(ArrayList<Command> history, DiagramSet diagrams, ClientContext context, AsyncCallback<Void> callback);
}
