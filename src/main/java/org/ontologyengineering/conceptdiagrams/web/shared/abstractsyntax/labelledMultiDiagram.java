package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Set;

/**
 * (sample of comments - in the end what these to be consistent with the text (as well as the code being consistent),
 * but must also talk about the implementation choices and tradeoffs made)
 *
 *
 * A Concept Diagram is a tuple (C, D, A_o, A_d, lambda_o, lambda_d, lambda_#)
 *
 * where:
 *
 * C        : is a finite set of ClassAndObjectPropertyDiagram(s)
 * D        : is a finite set of DatatypeDiagram(s)
 * A_o      : is a finite multiset of ObjectPropertyArrow(s)
 * A_d      : is a finite multiset of DatatypePropertyArrow(s)
 * lambda_o : labels the A_o
 * lambda_d : labels the A_d
 * lambda_# : may add <=, = or >= constraints to the A_o/A_d
 *
 * The labels are implemented as part of Arrow classes
 *
 */
public class LabelledMultiDiagram extends AbstractDiagram<AbstractDiagram, ConcreteDiagram> {


    // TODO : implement the constraints

    // might change the types of these as we go along ... trying for a pretty direct translation
    // of the syntax and semantics document at this stage.
    private AbstractSet<ClassAndObjectPropertyDiagram> classAndObectPropertyDiagrams;
    private AbstractSet<DatatypeDiagram> datatypeDiagrams;
    private AbstractSet<ObjectPropertyArrow> objectPropertyArrows;
    private AbstractSet<DatatypePropertyArrow> datatypePropertyArrows;

    LabelledMultiDiagram() {
        classAndObectPropertyDiagrams = new HashSet<ClassAndObjectPropertyDiagram>();
        datatypeDiagrams = new HashSet<DatatypeDiagram>();
        objectPropertyArrows = new HashSet<ObjectPropertyArrow>();
        datatypePropertyArrows = new HashSet<DatatypePropertyArrow>();
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        AbstractSet<DiagramElement> result = new HashSet<DiagramElement>(classAndObectPropertyDiagrams);
        result.addAll(datatypeDiagrams);
        result.addAll(objectPropertyArrows);
        result.addAll(datatypePropertyArrows);
        return result;
    }

    public AbstractSet<ObjectPropertyArrow> getObjectPropertyArrows() {
        return objectPropertyArrows;
    }

    public AbstractSet<DatatypePropertyArrow> getDatatypePropertyArrows() {
        return datatypePropertyArrows;
    }


    // after this operation the diagram will be a bit inconsistent because the new diagram isn't yet jnoined to the others
    public void addClassAndObjectPropertyDiagram(ClassAndObjectPropertyDiagram newDiagram) {
        classAndObectPropertyDiagrams.add(newDiagram);
    }

    public void addDatatypeDiagram(DatatypeDiagram newDiagram) {
        datatypeDiagrams.add(newDiagram);
    }


    public AbstractSet<Arrow> arrowsTov(LabelledDiagram v) {
        AbstractSet<Arrow> result = new HashSet<Arrow>();
        result.addAll(objectArrowsTov(v));
        result.addAll(dataArrowsTov(v));
        return result;
    }

    public AbstractSet<ObjectPropertyArrow> objectArrowsTov(LabelledDiagram v) {
        AbstractSet<ObjectPropertyArrow> result = new HashSet<ObjectPropertyArrow>();
        for(ObjectPropertyArrow a : getObjectPropertyArrows()) {
            if(a.getTarget().diagram() == v) {
                result.add(a);
            }
        }
        return result;
    }

    public AbstractSet<DatatypePropertyArrow> dataArrowsTov(LabelledDiagram v) {
        AbstractSet<DatatypePropertyArrow> result = new HashSet<DatatypePropertyArrow>();
        for(DatatypePropertyArrow a : getDatatypePropertyArrows()) {
            if(a.getTarget().diagram() == v) {
                result.add(a);
            }
        }
        return result;
    }


    // ooohhh bad style, but otherwise I think I have a cast on getAbstractRep
    public ObjectPropertyArrow addObjectPropertyArrow(ConcreteArrow arrow) {
        // should check this has different source and target diagrams
        ObjectPropertyArrow result =
                // ouch how to do this without the casts?  need another type in the concrete??
                new ObjectPropertyArrow((DiagramArrowSourceOrTarget) arrow.getSource().getAbstractSyntaxRepresentation(),
                        (DiagramArrowSourceOrTarget) arrow.getTarget().getAbstractSyntaxRepresentation(),
                        arrow.labelText());

        getObjectPropertyArrows().add(result);
        return result;
    }

    public DatatypePropertyArrow addDataPropertyArrow(ConcreteArrow arrow) {
        DatatypePropertyArrow result =
                // ouch how to do this without the casts?  need another type in the concrete??
                new DatatypePropertyArrow((DiagramArrowSourceOrTarget) arrow.getSource().getAbstractSyntaxRepresentation(),
                        (DiagramArrowSourceOrTarget) arrow.getTarget().getAbstractSyntaxRepresentation(),
                        arrow.labelText());

        getDatatypePropertyArrows().add(result);
        return result;
    }


    public Set<Arrow> DE(Arrow a) {
        // FIXME !!!
        // Cache these
        return new HashSet<Arrow>();
    }



    // Definition 25
    public AbstractSet<ZonalRegion> SC(Arrow arrow) {
        if(arrow.diagram() == this) {
            LabelledDiagram v = (LabelledDiagram) arrow.getTarget().diagram();  // FIXME : why cast required ... maybe I'm wrong and it will crash at some point??
            return v.SZR(v.ISC(arrow), v.getISCcurveMask(arrow));
        }

        return null;
    }

}
