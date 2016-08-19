package org.ontologyengineering.conceptdiagrams.web.shared;


/**
 * Author: Michael Compton<br>
 * Date: February 2015<br>
 * See license information in base directory.
 */

public class StandardClientContext extends ClientContext{

    // private String fileName;  // the place where the generated ontology will be saved.
    // return a file instead

    private String iri;
    private OntologyFormat format;

    public StandardClientContext() {

    }

//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }

    public String getIRI() {
        return iri;
    }

    public void setIRI(String iri) {
        this.iri = iri;
    }

    @Override
    public void setOntologyFormat(OntologyFormat format) {
        this.format = format;
    }

    @Override
    public OntologyFormat getOntologyFormat() {
        return format;
    }
}

