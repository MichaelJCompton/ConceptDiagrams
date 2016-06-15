package org.ontologyengineering.conceptdiagrams.web.server.owlOutput;

import org.ontologyengineering.conceptdiagrams.web.shared.StandardClientContext;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Author: Michael Compton<br>
 * Date: May 2016<br>
 * See license information in base directory.
 */

public class OWLAPIutils {


    public static OWLOntology makeFreshOntology(String iri) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        IRI ontologyIRI;

        try {
            ontologyIRI = IRI.create(iri);

            ontology = manager.createOntology(ontologyIRI);

        } catch (Exception e) {
            // OUCH!
        }

        return ontology;
    }


    public static void writeOntology(OWLOntology ontology, String location) {
        try {
            ontology.getOWLOntologyManager().saveOntology(ontology, new OWLXMLOntologyFormat(), IRI.create("file:" + location));
        } catch (Exception e) {
            // grrrr
        }
    }

}
