package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Transformation 5
 */
public abstract class AddShading <T extends LabelledDiagram> extends LabelledDiagramTransformation <T>  {

    protected AbstractSet<ConcreteZone> shadedZones;

    public AddShading(AbstractSet<ConcreteZone> shadedZones) {

        this.shadedZones = new HashSet<ConcreteZone>();

        for(ConcreteZone z : shadedZones) {
            // This is all done before the abstract representation is built, so just add the zones?
            //if(z.getAbstractSyntaxRepresentation() != null) {
            //    if(!z.getAbstractSyntaxRepresentation().isShaded()) {
                    this.shadedZones.add(z);
            //    }
            //}
        }
    }

    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        transformedDiagram.shadeZones(shadedZones);

        setAsExecuted();
    }

    @Override
    public void translate(T transformedDiagram, OWLOutputBuilder outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }


}
