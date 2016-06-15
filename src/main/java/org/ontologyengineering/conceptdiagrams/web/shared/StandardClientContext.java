package org.ontologyengineering.conceptdiagrams.web.shared;


/**
 * Author: Michael Compton<br>
 * Date: February 2015<br>
 * See license information in base directory.
 */

public class StandardClientContext extends ClientContext{

    private String fileName;  // the place where the generated ontology will be saved.
    private String iri;


    public StandardClientContext() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }
}

