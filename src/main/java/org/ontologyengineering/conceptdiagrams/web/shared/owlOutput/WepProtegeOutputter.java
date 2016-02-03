package org.ontologyengineering.conceptdiagrams.web.shared.owlOutput;

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;

import java.util.AbstractSet;
import java.util.Set;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */



public class WepProtegeOutputter extends OWLOutputter {

    @Override
    public void addGlobalContradiction() {

    }

    @Override
    public void addEquivalentUnionThing(FastCurveSet curves, LabelledDiagram v) {

    }

    @Override
    public void addEquivalentNoThing(Curve curve, LabelledDiagram v) {

    }

    @Override
    public void addSubClassAxiomIntUnion(FastCurveSet intCurves, FastCurveSet unionCurves, LabelledDiagram v) {

    }

    @Override
    public void addDisjointClasses(Curve curve1, Curve curve2, LabelledDiagram v) {

    }

    @Override
    public void addDisjointClasses(Curve curve, FastCurveSet intCurves, LabelledDiagram v) {

    }

    @Override
    public void addObjectPropertyDomain(ObjectPropertyArrow arrow, Curve c, LabelledDiagram v) {

    }

    @Override
    public void addObjectPropertyDomainTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v) {

    }

    @Override
    public void addObjectPropertyRange(ObjectPropertyArrow arrow, Curve c, LabelledDiagram v) {

    }

    @Override
    public void addObjectPropertyRangeTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v) {

    }

    @Override
    public void addObjectPropertyEquivTop(ObjectPropertyArrow arrow, LabelledDiagram v) {

    }

    @Override
    public void addObjectPropertyEquivBot(ObjectPropertyArrow arrow, LabelledDiagram v) {

    }

    @Override
    public void addEquivalentObjectProperties(Set<Arrow> arrows) {

    }

    @Override
    public void addInverseObjectProperties(Arrow a1, Arrow a2) {

    }

    @Override
    public void addSubObjectProperties(Arrow a1, Arrow a2) {

    }

    @Override
    public void addDisjointObjectProperties(Arrow a1, Arrow a2) {

    }

    @Override
    public void addFunctionalObjectProperty(Arrow a) {

    }

    @Override
    public void addInverseFunctionalObjectProperty(Arrow a) {

    }

    @Override
    public void addDataPropertyRange(DatatypePropertyArrow arrow, Curve c, LabelledDiagram v) {

    }

    @Override
    public void addDataPropertyRangeTSC(DatatypePropertyArrow arrow, AbstractSet<ZonalRegion> SC, DatatypeDiagram v) {

    }

    @Override
    public void addEquivalentDataProperties(Set<Arrow> arrows) {

    }

    @Override
    public void addDisjointDataProperties(Arrow a1, Arrow a2) {

    }

    @Override
    public void addSubDataProperties(Arrow a1, Arrow a2) {

    }

    @Override
    public void addFunctionalDataProperty(Arrow a) {

    }
}
