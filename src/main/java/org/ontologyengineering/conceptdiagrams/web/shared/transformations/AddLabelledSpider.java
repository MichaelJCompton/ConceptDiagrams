package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;


/**
 * Transformation 1
 */
public class AddLabelledSpider<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {

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
    public void translate(T transformedDiagram, OWLOutputter outputter) {
        // do some preamble

        executeTransformation(transformedDiagram);

        // do some outputting

    }
}
