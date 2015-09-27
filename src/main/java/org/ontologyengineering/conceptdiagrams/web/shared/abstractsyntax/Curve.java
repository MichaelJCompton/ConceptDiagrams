package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


public class Curve extends DiagramArrowSourceOrTarget {

    public Curve() {
        super();
    }

    public Curve(LabelledDiagram parent) {
        super(parent);
    }

    public Curve(String label) {
        super(label);
    }

    public Curve(String label, LabelledDiagram parent) {
        super(label, parent);
    }


}
