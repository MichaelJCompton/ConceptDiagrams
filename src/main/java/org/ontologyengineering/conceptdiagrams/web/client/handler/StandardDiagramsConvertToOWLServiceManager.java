package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Author: Michael Compton<br>
 * Date: June 2016<br>
 * See license information in base directory.
 */

public class StandardDiagramsConvertToOWLServiceManager extends ConvertToOWLServiceManager {

    private ConvertAllToOWLServiceAsync convertToOWL;

    private ClientContext context;

    public StandardDiagramsConvertToOWLServiceManager(ClientContext context) {
        this.context = context;

        convertToOWL = GWT.create(ConvertAllToOWLService.class);
    }

    public void convertAllToOWL(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams) { //}, DiagramSet diagrams) {


//        if (convertToOWL == null) {
//            convertToOWL = GWT.create(ConvertAllToOWLService.class);
//        }


        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                int i = 10;
                i++;
            }

            public void onSuccess(String result) {
                // result is the name of the file returned ... so save it

                String url = GWT.getModuleBaseURL() + "ontologyDownloadService?filename=" + result;
                Window.open(url, "_blank", ""); //status=0,toolbar=0,menubar=0,location=0
            }
        };


        //TestSerializationAsync test = GWT.create(TestSerialization.class);
        //test.testSerialization(new Curve(), callback);

        //
        convertToOWL.convertAllToOWL(histories, diagrams, context, callback); // diagrams
    }

    public ClientContext getContext() {
        return context;
    }
}
