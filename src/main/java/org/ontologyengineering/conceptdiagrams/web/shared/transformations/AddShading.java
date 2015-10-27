package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

import java.util.AbstractSet;

/**
 * Transformation 5
 */
public class AddShading extends LabelledDiagramTransformation {

    private AbstractSet<ConcreteZone> shadedZones;

    public AddShading(AbstractSet<ConcreteZone> shadedZones) {

        for(ConcreteZone z : shadedZones) {
            if(z.getAbstractSyntaxRepresentation() != null) {
                if(!z.getAbstractSyntaxRepresentation().isShaded()) {
                    this.shadedZones.add(z);
                }
            }
        }
    }

    @Override
    public void executeTransformation(LabelledDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(LabelledDiagram transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
