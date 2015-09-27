package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


/**
 * A Labelled diagram with some constraints
 */
public class DatatypeDiagram extends LabelledDiagram {

    DatatypeDiagram() {
        super();
    }

    DatatypeDiagram(ConceptDiagram parent) {
        super(parent);
    }

    // TODO : datatype diagram constraints to add
    // - no arrows
}
