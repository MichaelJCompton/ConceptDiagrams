package org.ontologyengineering.conceptdiagrams.web.client.handler;

/**
 * Author: Michael Compton<br>
 * Date: January 2016<br>
 * See license information in base directory.
 */

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;


import java.util.ArrayList;

@RemoteServiceRelativePath("addOWLAxioms")
public interface AddAxiomsHandler extends RemoteService {

    OWLDataFactory createManager(String iri);
    void AddAxioms(ArrayList<OWLAxiom> newAxioms);

}
