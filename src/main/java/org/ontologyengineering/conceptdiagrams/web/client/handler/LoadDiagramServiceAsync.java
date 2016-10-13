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
 * Date: August 2016<br>
 * See license information in base directory.
 */

public interface LoadDiagramServiceAsync {

    void loadCommandHistory(String file, AsyncCallback<HashSet<ArrayList<Command>>> callback);

}
