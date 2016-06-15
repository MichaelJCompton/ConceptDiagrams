package org.ontologyengineering.conceptdiagrams.web.server.owlOutput;

import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;

import java.util.AbstractSet;
import java.util.Set;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */




public abstract class OWLOutputBuilder {


    // ---------------------------------------------------------------------------------------
    //                          Objects
    // ---------------------------------------------------------------------------------------


    public abstract void addGlobalContradiction();  // EquivalentClasses(Thing, Nothing)
    public abstract void addEquivalentUnionThing(FastCurveSet curves, LabelledDiagram v);  // intersection of classes == thing
    public abstract void addEquivalentNoThing(Curve curve, LabelledDiagram v);

    public abstract void addSubClassAxiomIntUnion(FastCurveSet intCurves, FastCurveSet unionCurves, LabelledDiagram v);

    public abstract void addDisjointClasses(Curve curve1, Curve curve2, LabelledDiagram v);
    public abstract void addDisjointClasses(Curve curve, FastCurveSet intCurves, LabelledDiagram v);

    // just so that all labelled curves in the diagram are asserted in the ontology, even if there isn't anything
    // implied about them in the diagram
    public abstract void assertExistenceCurve(Curve curve);

    // ---------------------------------------------------------------------------------------
    //                          Object Properties
    // ---------------------------------------------------------------------------------------


    public abstract void addObjectPropertyDomain(ObjectPropertyArrow arrow, Curve c, LabelledDiagram v);
    public abstract void addObjectPropertyDomainTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v);

    public abstract void addObjectPropertyRange(ObjectPropertyArrow arrow, Curve c, LabelledDiagram v);
    public abstract void addObjectPropertyRangeTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v);

    public abstract void addObjectPropertyEquivTop(ObjectPropertyArrow arrow, LabelledDiagram v);
    public abstract void addObjectPropertyEquivBot(ObjectPropertyArrow arrow, LabelledDiagram v);

    public abstract void addEquivalentObjectProperties(Set<Arrow> arrows);
    public abstract void addInverseObjectProperties(Arrow a1, Arrow a2);
    public abstract void addSubObjectProperties(Arrow a1, Arrow a2);
    public abstract void addDisjointObjectProperties(Arrow a1, Arrow a2);

    public abstract void addFunctionalObjectProperty(Arrow a);
    public abstract void addInverseFunctionalObjectProperty(Arrow a);


    // ---------------------------------------------------------------------------------------
    //                          Data Properties
    // ---------------------------------------------------------------------------------------


    public abstract void addDataPropertyRange(DatatypePropertyArrow arrow, Curve c, LabelledDiagram v);
    public abstract void addDataPropertyRangeTSC(DatatypePropertyArrow arrow, AbstractSet<ZonalRegion> SC, DatatypeDiagram v);

    public abstract void addEquivalentDataProperties(Set<Arrow> arrows);
    public abstract void addDisjointDataProperties(Arrow a1, Arrow a2);
    public abstract void addSubDataProperties(Arrow a1, Arrow a2);

    public abstract void addFunctionalDataProperty(Arrow a);
}
