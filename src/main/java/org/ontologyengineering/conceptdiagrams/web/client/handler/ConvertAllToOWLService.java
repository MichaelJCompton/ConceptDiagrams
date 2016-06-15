package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
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
    void convertAllToOWL(ArrayList<Command> history, DiagramSet diagrams, ClientContext context);
}
