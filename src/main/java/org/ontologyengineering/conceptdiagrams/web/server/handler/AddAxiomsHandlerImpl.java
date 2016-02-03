package org.ontologyengineering.conceptdiagrams.web.server.handler;

/**
 * Author: Michael Compton<br>
 * Date: January 2016<br>
 * See license information in base directory.
 */

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.AddAxiomsHandler;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;


public class AddAxiomsHandlerImpl extends RemoteServiceServlet implements AddAxiomsHandler {


    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private IRI ontologyIRI;
    private OWLDataFactory factory;

    public AddAxiomsHandlerImpl() {}

    public OWLDataFactory createManager(String iri) {
        manager = OWLManager.createOWLOntologyManager();

        try {
            ontologyIRI = IRI.create(iri);

            ontology = manager.createOntology(ontologyIRI);

            factory = manager.getOWLDataFactory();
        } catch (Exception e) {
            // OUCH!
        }
        return factory;
    }


    public void AddAxioms(ArrayList<OWLAxiom> newAxioms) {
        for(OWLAxiom a : newAxioms) {
            manager.addAxiom(ontology, a);
        }
    }

}
