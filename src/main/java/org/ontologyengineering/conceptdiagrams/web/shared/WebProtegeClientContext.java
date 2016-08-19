package org.ontologyengineering.conceptdiagrams.web.shared;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class WebProtegeClientContext extends ClientContext {


    @Override
    public String getIRI() {
        return null;

        // should get it from webprotee
    }

    @Override
    public void setIRI(String IRI) {
        // no-op
    }

    @Override
    public OntologyFormat getOntologyFormat() {
        return null;
        // probably doesn't mean anything is this instance??
    }

    @Override
    public void setOntologyFormat(OntologyFormat format) {
        // no-op
    }
}
