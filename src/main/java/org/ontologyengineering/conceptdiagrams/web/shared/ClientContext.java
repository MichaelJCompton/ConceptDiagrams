package org.ontologyengineering.conceptdiagrams.web.shared;

import java.io.Serializable;


/**
 * Author: Michael Compton<br>
 * Date: February 2015<br>
 * See license information in base directory.
 */

public abstract class ClientContext implements Serializable {

    // is there any context common to both WebProtege and not ??

    public ClientContext() {

    }

    public abstract String getIRI();

    public abstract void setIRI(String IRI);

    public abstract OntologyFormat getOntologyFormat();

    public abstract void setOntologyFormat(OntologyFormat format);

}
