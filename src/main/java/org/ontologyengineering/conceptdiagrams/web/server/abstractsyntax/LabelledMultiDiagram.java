package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.*;

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
    private HashSet<ClassAndObjectPropertyDiagram> classAndObjectPropertyDiagrams;
    private HashSet<DatatypeDiagram> datatypeDiagrams;
    private HashSet<ObjectPropertyArrow> objectPropertyArrows;
    private HashSet<DatatypePropertyArrow> datatypePropertyArrows;

    public LabelledMultiDiagram() {
        classAndObjectPropertyDiagrams = new HashSet<ClassAndObjectPropertyDiagram>();
        datatypeDiagrams = new HashSet<DatatypeDiagram>();
        objectPropertyArrows = new HashSet<ObjectPropertyArrow>();
        datatypePropertyArrows = new HashSet<DatatypePropertyArrow>();
    }

    @Override
    public DiagramElement toAbstract(ConcreteDiagramElement conc) {
        DiagramElement result = super.toAbstract(conc);
        if(result == null) {
            // ok it's (probably in one of my child diagrams)
            for(LabelledDiagram d : getAllDiagrams()) {
                result = d.toAbstract(conc);
                if(result != null) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        AbstractSet<DiagramElement> result = new HashSet<DiagramElement>(classAndObjectPropertyDiagrams);
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
        classAndObjectPropertyDiagrams.add(newDiagram);
    }

    public AbstractSet<ClassAndObjectPropertyDiagram> getObjectDiagrams() {
        return classAndObjectPropertyDiagrams;
    }

    public void addDatatypeDiagram(DatatypeDiagram newDiagram) {
        datatypeDiagrams.add(newDiagram);
    }

    public AbstractSet<DatatypeDiagram> getDatatypeDiagrams() {
        return datatypeDiagrams;
    }

    public AbstractSet<LabelledDiagram> getAllDiagrams() {
        HashSet<LabelledDiagram> result = new HashSet<LabelledDiagram>();
        result.addAll(getObjectDiagrams());
        result.addAll(getDatatypeDiagrams());
        return result;
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
                //  how to do this without the casts?  need another type in the concrete??
                new ObjectPropertyArrow((DiagramArrowSourceOrTarget) getAbstract(arrow.getSource()),
                        (DiagramArrowSourceOrTarget) getAbstract(arrow.getTarget()),
                        arrow.labelText());

        if(arrow.isInverse()) {
            result.setAsInverse();
        }

        getObjectPropertyArrows().add(result);

        result.setDiagram(this);
        result.setConcreteRepresentation(arrow);

        return result;
    }

    public DatatypePropertyArrow addDataPropertyArrow(ConcreteArrow arrow) {
        DatatypePropertyArrow result =
                // ouch how to do this without the casts?  need another type in the concrete??
                new DatatypePropertyArrow((DiagramArrowSourceOrTarget) getAbstract(arrow.getSource()),
                        (DiagramArrowSourceOrTarget) getAbstract(arrow.getTarget()),
                        arrow.labelText());

        getDatatypePropertyArrows().add(result);
        return result;
    }


    // Definition 30
    public Set<Arrow> DE(Arrow arrow) {

        // FIXME --- Cache these and just build up???

        HashSet<Arrow> result = new HashSet<Arrow>();
        for(ObjectPropertyArrow a : getObjectPropertyArrows()) {
            if(a.isInverse() == arrow.isInverse()) {
                result.add(a);
            }
        }

        for(ClassAndObjectPropertyDiagram opd : getObjectDiagrams()) {
            result.addAll(opd.DE(arrow));
        }

        return result;
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
