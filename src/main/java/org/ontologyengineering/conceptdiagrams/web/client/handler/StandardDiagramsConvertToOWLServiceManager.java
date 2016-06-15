package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;

import java.util.ArrayList;


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

    public void convertAllToOWL(ArrayList<Command> history, DiagramSet diagrams) {


//        if (convertToOWL == null) {
//            convertToOWL = GWT.create(ConvertAllToOWLService.class);
//        }


        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }

            public void onSuccess(Void result) {
                // nothing to do??  Maybe eventually I have to update something,
                // or put up a waiting window, block out any input and then indicate success/failure
            }
        };


        //TestSerializationAsync test = GWT.create(TestSerialization.class);
        //test.testSerialization(new Curve(), callback);

        //
        convertToOWL.convertAllToOWL(history, diagrams, context, callback);
    }
}
