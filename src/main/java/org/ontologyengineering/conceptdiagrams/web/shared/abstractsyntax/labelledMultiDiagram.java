package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

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
 * The labels are implemented as part of *Arrow classes
 *
 */
public class LabelledMultiDiagram extends AbstractDiagram {


    // might change the types of these as we go along ... trying for a pretty direct translation
    // of the syntax and semantics document at this stage.
    private AbstractSet<LabelledDiagram> labelledDiagrams; // FIXME : Class and object diagrams ???
    private AbstractSet<DatatypeDiagram> datatypeDiagrams;
    private AbstractSet<ObjectPropertyArrow> objectPropertyArrows;
    private AbstractSet<DatatypePropertyArrow> datatypePropertyArrows;

    LabelledMultiDiagram() {
        labelledDiagrams = new HashSet<LabelledDiagram>();
        datatypeDiagrams = new HashSet<DatatypeDiagram>();
        objectPropertyArrows = new HashSet<ObjectPropertyArrow>();
        datatypePropertyArrows = new HashSet<DatatypePropertyArrow>();
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        AbstractSet<DiagramElement> result = new HashSet<DiagramElement>(labelledDiagrams);
        result.addAll(datatypeDiagrams);
        result.addAll(objectPropertyArrows);
        result.addAll(datatypePropertyArrows);
        return result;
    }

    // TODO : implement the constraints
}
