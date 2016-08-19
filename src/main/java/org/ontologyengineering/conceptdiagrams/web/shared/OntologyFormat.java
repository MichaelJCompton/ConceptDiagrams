package org.ontologyengineering.conceptdiagrams.web.shared;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

// Couldn't see anything in OWLAPI for this.  There are classes for each, but nothing like this.
public enum OntologyFormat {
    OWLXML("OWLXML"),
    RDFXML("RDFXML"),
    TURTLE("TURTLE"),
    MANCHESTER("MANCHESTER");

    private final String name;

    OntologyFormat(String name) {
        this.name = name;
    }
}
