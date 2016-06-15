package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;



/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */



public class PropertyDiagram extends LabelledMultiDiagram {


    public PropertyDiagram() {
        super();
        setDiagram(this);  // a property diagram can't be a component of something
    }

    // FIXME : constraints if needed

}
