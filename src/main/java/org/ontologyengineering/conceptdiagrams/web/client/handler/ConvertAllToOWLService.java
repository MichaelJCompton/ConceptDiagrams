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
 * Date: February 2016<br>
 * See license information in base directory.
 */


@RemoteServiceRelativePath("ConvertAllToOWL")
public interface ConvertAllToOWLService extends RemoteService {
//    /**
//     * Utility/Convenience class. Use ConvertAllToOWLService.App.getInstance() to access static instance of
//     * ConvertAllToOWLAsync
//     */
//    public static class App {
//        private static final ConvertAllToOWLAsync ourInstance = (ConvertAllToOWLAsync) GWT.create(ConvertAllToOWLService.class);
//
//        public static ConvertAllToOWLAsync getInstance() {
//            return ourInstance;
//        }
//    }

    //

//
    String convertAllToOWL(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams, ClientContext context); //DiagramSet diagrams,
}
