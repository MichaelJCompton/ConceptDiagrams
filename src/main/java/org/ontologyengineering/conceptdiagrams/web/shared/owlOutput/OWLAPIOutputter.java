package org.ontologyengineering.conceptdiagrams.web.shared.owlOutput;

import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */


public class OWLAPIOutputter extends OWLOutputter {


    // needs to be a bit more sophisticated once we have multiple ontologies, inports, existing classes etc!

    private OWLOntologyManager manager;
    private IRI ontologyIRI;
    private OWLOntology ontology;
    private OWLDataFactory factory;


    // everything from scratch
    public OWLAPIOutputter(String iri) {
        manager = OWLManager.createOWLOntologyManager();

        try {
            ontologyIRI = IRI.create(iri);

            ontology = manager.createOntology(ontologyIRI);

            factory = manager.getOWLDataFactory();
        } catch (Exception e) {
            // OUCH!
        }
    }

    private Set<OWLClass> FastCurveSetAsClasses(FastCurveSet curves, LabelledDiagram v) {
        Set<OWLClass> classes = new HashSet<OWLClass>();
        for (int i : curves) {
            classes.add(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(i))));
        }
        return classes;
    }

    private Set<OWLDatatype> FastCurveSetAsDatatype(FastCurveSet curves, LabelledDiagram v) {
        Set<OWLDatatype> classes = new HashSet<OWLDatatype>();
        for (int i : curves) {
            classes.add(factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(i))));
        }
        return classes;
    }


    // ---------------------------------------------------------------------------------------
    //                          Objects
    // ---------------------------------------------------------------------------------------

    // Definition 20
    private OWLClassExpression Tobject(ZonalRegion zr, ClassAndObjectPropertyDiagram v) {
        OWLClassExpression result = null;
        if (zr == null) {
            // ??? shouldn't happen
        } else if (zr.IN().isZero() && zr.OUT().isZero()) {
            // 1.  OWL:Thing

            result = factory.getOWLThing();

        } else if (zr.IN().isZero() && zr.OUT().numBitsSet() == 1) {
            // 2.  ObjectComplementOf(CL(OUT,v))

            result = factory.getOWLObjectComplementOf(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next()))));

        } else if (zr.IN().isZero() && zr.OUT().numBitsSet() >= 2) {
            // 3.  ObjectComplementOf(ObjectUnionOf(CL(OUT,v)))

            result = factory.getOWLObjectComplementOf(factory.getOWLObjectUnionOf(FastCurveSetAsClasses(zr.OUT(), v)));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().isZero()) {
            // 4.  CL(IN,v)

            result = factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() == 1) {
            // 5.  ObjectIntersectionOf(CL(IN,v), ObjectComplementOf(CL(OUT,v)))

            result =
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.IN().iterator().next()))),
                            factory.getOWLObjectComplementOf(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())))));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() >= 2) {
            // 6.  ObjectIntersectionOf(CL(IN,v), ObjectComplementOf(ObjectUnionOf(CL(OUT,v))))

            result =
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.IN().iterator().next()))),
                            factory.getOWLObjectComplementOf(factory.getOWLObjectUnionOf(FastCurveSetAsClasses(zr.OUT(), v))));

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().isZero()) {
            // 7.  ObjectIntersectionOf(CL(IN,v))

            result = factory.getOWLObjectIntersectionOf(FastCurveSetAsClasses(zr.IN(), v));

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() == 1) {
            // 8.  ObjectIntersectionOf(CL(IN,v) ObjectComplementOf(CL(OUT,v)))

            Set<OWLClass> classes = FastCurveSetAsClasses(zr.IN(), v);
            Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
            expressions.addAll(classes);
            expressions.add(factory.getOWLObjectComplementOf(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())))));

            result = factory.getOWLObjectIntersectionOf(expressions);

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() >= 2) {
            // 9.  ObjectIntersectionOf(CL(IN,v) ObjectComplementOf(ObjectUnionOf(CL(OUT,v))))

            Set<OWLClass> classes = FastCurveSetAsClasses(zr.IN(), v);
            Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
            expressions.addAll(classes);
            expressions.add(factory.getOWLObjectComplementOf(factory.getOWLObjectUnionOf(FastCurveSetAsClasses(zr.OUT(), v))));

            result = factory.getOWLObjectIntersectionOf(expressions);
        }
        return result;
    }


    // Definition 26
    private OWLClassExpression T_SC(AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v) {
        OWLClassExpression result = null;

        if (SC == null) {
            // as below??
        } else if (SC.size() == 0) {
            // 1.  OWL:Nothing

            result = factory.getOWLNothing();

        } else if (SC.size() == 1) {
            // 2.  T(<IN,OUT,v>)

            result = Tobject(SC.iterator().next(), v);

        } else {
            // 3.  ObjectUnionOf(T(<IN_1,OUT_1,v>), ..., T(<IN_n,OUT_n,v>))

            Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
            for (ZonalRegion zr : SC) {
                OWLClassExpression expression = Tobject(zr, v);
                if (expression != null) {
                    expressions.add(expression);
                }
            }
            result = factory.getOWLObjectUnionOf(expressions);

        }

        return result;
    }


    @Override
    public void addGlobalContradiction() {
        manager.addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(factory.getOWLThing(), factory.getOWLNothing()));
    }

    @Override
    public void addEquivalentUnionThing(FastCurveSet curves, LabelledDiagram v) {
        manager.addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(
                makeUnion(curves, v),
                factory.getOWLThing()));
    }

    @Override
    public void addEquivalentNoThing(Curve curve, LabelledDiagram v) {
        manager.addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve))), factory.getOWLNothing()));
    }

    private OWLClassExpression makeIntersection(FastCurveSet intCurves, LabelledDiagram v) {
        if (intCurves.numBitsSet() == 1) {
            return factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(intCurves.iterator().next())));
        } else {
            return factory.getOWLObjectIntersectionOf(FastCurveSetAsClasses(intCurves, v));
        }
    }

    private OWLClassExpression makeUnion(FastCurveSet unionCurves, LabelledDiagram v) {
        if (unionCurves.numBitsSet() == 1) {
            return factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(unionCurves.iterator().next())));
        } else {
            return factory.getOWLObjectIntersectionOf(FastCurveSetAsClasses(unionCurves, v));
        }
    }

    @Override
    public void addSubClassAxiomIntUnion(FastCurveSet intCurves, FastCurveSet unionCurves, LabelledDiagram v) {
        manager.addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(makeIntersection(intCurves, v), makeUnion(unionCurves, v)));
    }

    @Override
    public void addDisjointClasses(Curve curve1, Curve curve2, LabelledDiagram v) {
        Set<OWLClassExpression> classes = new HashSet<OWLClassExpression>();
        classes.add(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve1))));
        classes.add(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve2))));
        manager.addAxiom(ontology, factory.getOWLDisjointClassesAxiom(classes));
    }

    @Override
    public void addDisjointClasses(Curve curve, FastCurveSet intCurves, LabelledDiagram v) {
        Set<OWLClassExpression> disj = new HashSet<OWLClassExpression>();
        disj.add(factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve))));
        disj.add(makeIntersection(intCurves, v));
        manager.addAxiom(ontology, factory.getOWLDisjointClassesAxiom(disj));
    }

    // ---------------------------------------------------------------------------------------
    //                          Object Properties
    // ---------------------------------------------------------------------------------------




    private OWLObjectPropertyExpression OM(Arrow a) {
        // FIXME need OPE-
        return factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + a.getLabel()));
    }

    private Set<OWLObjectPropertyExpression> OMset(Set<Arrow> arrows) {
        Set<OWLObjectPropertyExpression> result = new HashSet<OWLObjectPropertyExpression>();
        for(Arrow a : arrows) {
            result.add(OM(a));
        }
        return result;
    }


    @Override
    public void addObjectPropertyDomain(ObjectPropertyArrow arrow, Curve curve, LabelledDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLObjectPropertyDomainAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),  // FIXME is this OM
                        factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve)))));
    }

    @Override
    public void addObjectPropertyDomainTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLObjectPropertyDomainAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),  // FIXME again OM???
                        T_SC(SC, v)));
    }

    @Override
    public void addObjectPropertyRange(ObjectPropertyArrow arrow, Curve curve, LabelledDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLObjectPropertyRangeAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        factory.getOWLClass(IRI.create(ontologyIRI + "#" + v.CL(curve)))));
    }

    @Override
    public void addObjectPropertyRangeTSC(ObjectPropertyArrow arrow, AbstractSet<ZonalRegion> SC, ClassAndObjectPropertyDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLObjectPropertyRangeAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        T_SC(SC, v)));
    }

    public void addObjectPropertyEquivTop(ObjectPropertyArrow arrow, LabelledDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLEquivalentObjectPropertiesAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        factory.getOWLTopObjectProperty()));
    }

    public void addObjectPropertyEquivBot(ObjectPropertyArrow arrow, LabelledDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLEquivalentObjectPropertiesAxiom(
                        factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        factory.getOWLBottomObjectProperty()));
    }

    public void addEquivalentObjectProperties(Set<Arrow> arrows) {
        manager.addAxiom(ontology, factory.getOWLEquivalentObjectPropertiesAxiom(OMset(arrows)));
    }

    public void addInverseObjectProperties(Arrow a1, Arrow a2) {  // FIXME again check the OM
        manager.addAxiom(ontology, factory.getOWLInverseObjectPropertiesAxiom(factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + a1.getLabel())), factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + a2.getLabel()))));
    }

    public void addSubObjectProperties(Arrow a1, Arrow a2) {
        manager.addAxiom(ontology, factory.getOWLSubObjectPropertyOfAxiom(OM(a1), OM(a2)));
    }

    public void addDisjointObjectProperties(Arrow a1, Arrow a2) {
        manager.addAxiom(ontology, factory.getOWLDisjointObjectPropertiesAxiom(OM(a1), OM(a2)));
    }

    // ---------------------------------------------------------------------------------------
    //                          Data
    // ---------------------------------------------------------------------------------------

    // Definition 21
    private OWLDataRange Tdata(ZonalRegion zr, DatatypeDiagram v) {
        OWLDataRange result = null;

        if (zr == null) {
            // ??? shouldn't happen
        } else if (zr.IN().isZero() && zr.OUT().isZero()) {
            // 1.  DataUnionOf(DT_c DataComplementOF(DT_C)) if V_DT != 0
            //     undefined                                   otherwise

            // FIXME : how to write this case???  IMPORTANT!!

        } else if (zr.IN().isZero() && zr.OUT().numBitsSet() == 1) {
            // 2. DataComplementOf(CL(OUT,v))

            result = factory.getOWLDataComplementOf(factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next()))));

        } else if (zr.IN().isZero() && zr.OUT().numBitsSet() >= 2) {
            // 3.  DataComplementOf(DataUnionOf(CL(OUT,v)))

            result = factory.getOWLDataComplementOf(factory.getOWLDataUnionOf(FastCurveSetAsDatatype(zr.OUT(), v)));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().isZero()) {
            // 4.  CL(IN, v)

            result = factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() == 1) {
            // 5. DataIntersectionOf(CL(IN,v) DataComplementOf(CL(OUT,v)))

            result =
                    factory.getOWLDataIntersectionOf(
                            factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.IN().iterator().next()))),
                            factory.getOWLDataComplementOf(factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())))));

        } else if (zr.IN().numBitsSet() == 1 && zr.OUT().numBitsSet() >= 2) {
            // 6.  DataIntersectionOf(CL(IN,v), DataComplementOf(DataUnionOf(CL(OUT,v))))

            result =
                    factory.getOWLDataIntersectionOf(
                            factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.IN().iterator().next()))),
                            factory.getOWLDataComplementOf(factory.getOWLDataUnionOf(FastCurveSetAsDatatype(zr.OUT(), v))));

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().isZero()) {
            // 7.  DataIntersectionOf(CL(IN,v))

            result = factory.getOWLDataIntersectionOf(FastCurveSetAsDatatype(zr.IN(), v));

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() == 1) {
            // 8.  DataIntersectionOf(CL(IN,v) DataComplementOf(CL(OUT,v)))

            Set<OWLDatatype> datatypes = FastCurveSetAsDatatype(zr.IN(), v);
            Set<OWLDataRange> dataranges = new HashSet<OWLDataRange>();
            dataranges.addAll(datatypes);
            dataranges.add(factory.getOWLDataComplementOf(factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(zr.OUT().iterator().next())))));

            result = factory.getOWLDataIntersectionOf(dataranges);

        } else if (zr.IN().numBitsSet() >= 2 && zr.OUT().numBitsSet() >= 2) {
            // 9.  DataIntersectionOf(CL(IN,v) DataComplementOf(DataUnionOf(CL(OUT,v))))

            Set<OWLDatatype> datatypes = FastCurveSetAsDatatype(zr.IN(), v);
            Set<OWLDataRange> dataranges = new HashSet<OWLDataRange>();
            dataranges.addAll(datatypes);
            dataranges.add(factory.getOWLDataComplementOf(factory.getOWLDataUnionOf(FastCurveSetAsDatatype(zr.OUT(), v))));

            result = factory.getOWLDataIntersectionOf(dataranges);
        }

        return result;
    }

    // Definition 27
    private OWLDataRange T_SCdata(AbstractSet<ZonalRegion> SC, DatatypeDiagram v) {
        OWLDataRange result = null;

        if (SC == null) {
            // as below??
        } else if (SC.size() == 0) {
            // 1.  DataIntersectionOf(DT_c DataComplementOF(DT_C)) if V_DT != 0
            //     undefined                                   otherwise

            // FIXME : how to write this case???  IMPORTANT!!

        } else if (SC.size() == 1) {
            // 2.  T(<IN,OUT,v>)

            result = Tdata(SC.iterator().next(), v);

        } else {
            // 3.  DataUnionOf(T(<IN_1,OUT_1,v>), ..., T(<IN_n,OUT_n,v>))

            Set<OWLDataRange> expressions = new HashSet<OWLDataRange>();
            for (ZonalRegion zr : SC) {
                OWLDataRange expression = Tdata(zr, v);
                if (expression != null) {
                    expressions.add(expression);
                }
            }
            result = factory.getOWLDataUnionOf(expressions);

        }

        return result;
    }

    // ---------------------------------------------------------------------------------------
    //                          Data Properties
    // ---------------------------------------------------------------------------------------




    private Set<OWLDataProperty> dataPropertySet(Set<Arrow> arrows) {
        Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
        for(Arrow a : arrows) {
            result.add(factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + a.getLabel())));
        }
        return result;
    }


    @Override
    public void addDataPropertyRange(DatatypePropertyArrow arrow, Curve curve, LabelledDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLDataPropertyRangeAxiom(
                        factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        factory.getOWLDatatype(IRI.create(ontologyIRI + "#" + v.CL(curve)))));
    }

    @Override
    public void addDataPropertyRangeTSC(DatatypePropertyArrow arrow, AbstractSet<ZonalRegion> SC, DatatypeDiagram v) {
        manager.addAxiom(ontology,
                factory.getOWLDataPropertyRangeAxiom(
                        factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + arrow.getLabel())),
                        T_SCdata(SC, v)));
    }



    public void addEquivalentDataProperties(Set<Arrow> arrows) {
        manager.addAxiom(ontology, factory.getOWLEquivalentDataPropertiesAxiom(dataPropertySet(arrows)));
    }


    public void addSubDataProperties(Arrow a1, Arrow a2) {
        manager.addAxiom(ontology,
                factory.getOWLSubDataPropertyOfAxiom(
                        factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + a1.getLabel())),
                        factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + a2.getLabel()))));
    }


    public void addDisjointDataProperties(Arrow a1, Arrow a2) {
        manager.addAxiom(ontology,
                factory.getOWLDisjointDataPropertiesAxiom(
                    factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + a1.getLabel())),
                    factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + a2.getLabel()))));
    }

}
