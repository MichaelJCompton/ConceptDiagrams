package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br> Date: November 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;

public class AddShadingToDatatypeDiagram extends AddShading<DatatypeDiagram> {

    public AddShadingToDatatypeDiagram(AbstractSet<ConcreteZone> shadedZones) {
        super(shadedZones);
    }

    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
        if (shadedZones.size() > 0) {
            translate((DatatypeDiagram) diagram.toAbstract(shadedZones.iterator().next()).diagram(), outputter);
        } else {
            // if there are none, then there is nothing to do ... but should never have gotten here if there are none
        }   }

    @Override
    public void translate(DatatypeDiagram transformedDiagram, OWLOutputBuilder outputter) {

        // cache TEA, ITEA, ITDA, SC for all arrows sourved on * targeted to curves
        HashMap<Arrow, AbstractSet<Arrow>> TEA_v = transformedDiagram.TEAclone();
        HashMap<Arrow, AbstractSet<Arrow>> ITEA_v = transformedDiagram.ITEAclone();
        HashMap<Arrow, AbstractSet<Arrow>> ITDA_v = transformedDiagram.ITDAclone();

        HashMap<Arrow, AbstractSet<ZonalRegion>> SC_v = new HashMap<Arrow, AbstractSet<ZonalRegion>>();
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            if (a.targetIsCurve() && a.getTarget().isUnLabelled() && a.getTarget().diagram() == transformedDiagram) {
                SC_v.put(a, new HashSet<ZonalRegion>());
                SC_v.get(a).addAll(transformedDiagram.SC(a));
            }
        }

        executeTransformation(transformedDiagram);

        // IT(PD_2, PD_1) = EDP U SDP U DDP U RAN


        // 1. EDP
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.TEA(a)) {
                if (!TEA_v.get(a).contains(arrow) && transformedDiagram.diagram().DE(a).contains((arrow))) {
                    inDiff.add(arrow);
                }
            }
            if (inDiff.size() >= 2) {
                outputter.addEquivalentDataProperties(inDiff);
            }
        }

        // 2. SDP
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.ITEA(a)) {
                if (!ITEA_v.get(a).contains(arrow)) {
                    inDiff.add(arrow);
                }
            }
            for(Arrow arrow : inDiff) {
                outputter.addSubDataProperties(a, arrow);
            }
        }

        // 3. DDP
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            AbstractSet<Arrow> inDiff = new HashSet<Arrow>();
            for (Arrow arrow : transformedDiagram.ITDA(a)) {
                if (!ITDA_v.get(a).contains(arrow)) {
                    inDiff.add(arrow);
                }
            }
            for(Arrow arrow : inDiff) {
                outputter.addDisjointDataProperties(a, arrow);
            }
        }


        // 4. RAN
        for (DatatypePropertyArrow a : transformedDiagram.diagram().getDatatypePropertyArrows()) {
            if (a.targetIsCurve() && a.getTarget().isUnLabelled() && a.getTarget().diagram() == transformedDiagram ) {
                AbstractSet<ZonalRegion> SC = transformedDiagram.SC(a); // I think this is cached in the diagram, but no harm here
                if(!(SC_v.get(a).containsAll(SC) && SC.containsAll(SC_v.get(a)))) {
                    outputter.addDataPropertyRangeTSC(a, SC, transformedDiagram);
                }
            }
        }

    }


}
