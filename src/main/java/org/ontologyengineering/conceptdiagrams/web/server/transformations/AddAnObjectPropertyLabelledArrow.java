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
 * Transformation 15
 */
public class AddAnObjectPropertyLabelledArrow extends LabelledMultiDiagramTransformation {

    private ConcreteArrow addedArrow;
    private ObjectPropertyArrow addedArrowAbstract;

    public AddAnObjectPropertyLabelledArrow(ConcreteArrow addedArrow) {
        this.addedArrow = addedArrow;
    }

    @Override
    public void executeTransformation(LabelledMultiDiagram transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        addedArrowAbstract = transformedDiagram.addObjectPropertyArrow(addedArrow);

        //addedArrow.setAbstractSyntaxRepresentation(addedArrowAbstract);
        //addedArrowAbstract.setConcreteRepresentation(addedArrow);

        setAsExecuted();
    }



    @Override
    public void translate(LabelledMultiDiagram transformedDiagram, OWLOutputBuilder outputter) {

        // do some preamble

        // FIXME --- not sure about the ITEA(a,v) etc below.
        // I've implemented as ITEA(a, v + ...)

        executeTransformation(transformedDiagram);


        // Definition 43

        // 1.

        if(addedArrowAbstract.targetIsBoundaryRectangle()) {
            outputter.addObjectPropertyEquivTop(addedArrowAbstract, (LabelledDiagram) addedArrowAbstract.getTarget().diagram());
        } else {

            Curve k = addedArrowAbstract.targetAsCurve();

            // 2.
            // IT(PD_2, PD_1) = EOP_T U EOP_bot U EOP U IOP U SOP_1 U SOP_2 U DOP U DOM U RAN


            // FIXME : these two only work because of my assumption to do all shading at the end.
            // should probably make it more like the test in the document

            // (a) EOP_T
            boolean allZstar = true;
            for(Zone z : k.diagram().Z()) {
                if(z.OUTcontainsCurve(k) && !z.isShaded()) {
                    allZstar = false;
                    break;
                }
            }
            if(allZstar) {
                outputter.addObjectPropertyEquivTop(addedArrowAbstract, k.diagram());
            } else {
                // EOP_T = \empty
            }

            // (b) EOP_bot
            allZstar = true;
            for(Zone z : k.zones()) {
                if(!z.isShaded()) {
                    allZstar = false;
                    break;
                }
            }
            if(allZstar) {
                outputter.addObjectPropertyEquivBot(addedArrowAbstract, k.diagram());
            } else {
                // EOP_bot = \empty
            }

            // (c) EOP
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
                outputter.addEquivalentObjectProperties(DEarrows);
            }

            // (d) IOP
            for(Arrow a : notDEarrows) {
                outputter.addInverseObjectProperties(addedArrowAbstract, a);
            }

            // (e) SOP_1
            for(Arrow a : k.diagram().ITEA(addedArrowAbstract)) {
                outputter.addSubObjectProperties(addedArrowAbstract, a);
            }

            // (f) SOP_2
            for(Arrow a : k.diagram().ITCA(addedArrowAbstract)) {
                outputter.addSubObjectProperties(a, addedArrowAbstract);
            }

            // (g) DOP
            for(Arrow a : k.diagram().ITDA(addedArrowAbstract)) {
                outputter.addDisjointObjectProperties(addedArrowAbstract, a);
            }

            // (h) DOM
            if(!addedArrow.isInverse()) {
                // DOM = \empty
            } else if (k.diagram().LK().contains(k)) {
                outputter.addObjectPropertyDomain(addedArrowAbstract, k, k.diagram());
            } else {
                outputter.addObjectPropertyDomainTSC(addedArrowAbstract, k.diagram().SC(addedArrowAbstract), (ClassAndObjectPropertyDiagram) k.diagram()); // I think the type has to be this
            }

            // (i) RAN
            if(addedArrow.isInverse()) {
                // RAN = \empty
            } else if (k.diagram().LK().contains(k)) {
                outputter.addObjectPropertyRange(addedArrowAbstract, k, k.diagram());
            } else {
                outputter.addObjectPropertyRangeTSC(addedArrowAbstract, k.diagram().SC(addedArrowAbstract), (ClassAndObjectPropertyDiagram) k.diagram()); // I think the type has to be this
            }
        }
    }
}
