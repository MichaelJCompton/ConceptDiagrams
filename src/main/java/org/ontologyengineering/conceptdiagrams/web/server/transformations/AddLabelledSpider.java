package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;


/**
 * Transformation 1
 */
public class AddLabelledSpider<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {
    // why can't T just be LabelledDiagram???

    private ConcreteSpider addedSpider;


    public AddLabelledSpider(ConcreteSpider addedSpider) {
        this.addedSpider = addedSpider;
    }


    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // then the rest
    }

    @Override
    public void translate(T transformedDiagram, OWLOutputBuilder outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        // FIXME : not in the translation so far
        //
        //translate((T)addedSpider.getAbstractSyntaxRepresentation().diagram(), outputter);
    }
}
