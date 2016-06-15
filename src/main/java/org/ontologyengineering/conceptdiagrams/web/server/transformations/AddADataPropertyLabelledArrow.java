package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Transformation 16
 */
public class AddADataPropertyLabelledArrow extends LabelledMultiDiagramTransformation {

    private ConcreteArrow addedArrow;
    private DatatypePropertyArrow addedArrowAbstract;


    public AddADataPropertyLabelledArrow(ConcreteArrow addedArrow) {
        this.addedArrow = addedArrow;
    }


    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        addedArrowAbstract = transformedDiagram.addDataPropertyArrow(addedArrow);

        setAsExecuted();
    }

    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {

        executeTransformation(transformedDiagram);

        // Definition 44

        // 1.

        if(addedArrowAbstract.targetIsBoundaryRectangle()) {
            // IT(PD_2, PD_1) = \empty
        } else {

            Curve k = addedArrowAbstract.targetAsCurve();

            // 2.
            // IT(PD_2, PD_1) = EDP U SDP_1 U SDP_2 U DDP U RAN


            // (a) EDP
            AbstractSet<Arrow> DEarrows = new HashSet<Arrow>();
            AbstractSet<Arrow> notDEarrows = new HashSet<Arrow>();
            DEarrows.add(addedArrowAbstract);
            for(Arrow a : k.diagram().TEA(addedArrowAbstract)) {
                if(transformedDiagram.DE(addedArrowAbstract).contains(a)) {
                    DEarrows.add(a);
                } else {
                    notDEarrows.add(a);
                }
            }
            if(DEarrows.size() >= 2) {
                outputter.addEquivalentDataProperties(DEarrows);
            }


            // (b) SDP_1
            for(Arrow a : k.diagram().ITEA(addedArrowAbstract)) {
                outputter.addSubDataProperties(addedArrowAbstract, a);
            }


            // (c) SDP_2
            for(Arrow a : k.diagram().ITCA(addedArrowAbstract)) {
                outputter.addSubDataProperties(a, addedArrowAbstract);
            }


            // (d) DDP
            for(Arrow a : k.diagram().ITDA(addedArrowAbstract)) {
                outputter.addDisjointDataProperties(addedArrowAbstract, a);
            }


            // (e) RAN
            if (k.diagram().LK().contains(k)) {
                outputter.addDataPropertyRange(addedArrowAbstract, k, k.diagram());
            } else {
                outputter.addDataPropertyRangeTSC(addedArrowAbstract, k.diagram().SC(addedArrowAbstract), (DatatypeDiagram) k.diagram()); // I think the type has to be this
            }

        }
    }
}
