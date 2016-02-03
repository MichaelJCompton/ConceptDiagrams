package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import java.util.ArrayList;

/**
 * Author: Michael Compton<br>
 * Date: January 2016<br>
 * See license information in base directory.
 */


public interface AddAxiomsHandlerAsync {

    void createManager(String iri, AsyncCallback<OWLDataFactory> callback);
    void AddAxioms(ArrayList<OWLAxiom> newAxioms, AsyncCallback<Void> callback);

}
