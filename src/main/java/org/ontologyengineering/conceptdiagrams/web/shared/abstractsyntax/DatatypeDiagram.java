package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


/**
 * A LabelledDiagram where
 *
 * A = \emptyset
 * lambda_s has range V_I
 * lambda_c has range V_C
 */
public class DatatypeDiagram extends LabelledDiagram {

    public DatatypeDiagram() {
        super();
    }

    public DatatypeDiagram(LabelledMultiDiagram parent) {
        super(parent);
    }

    // TODO : datatype diagram constraints to add
    // - no arrows
}
