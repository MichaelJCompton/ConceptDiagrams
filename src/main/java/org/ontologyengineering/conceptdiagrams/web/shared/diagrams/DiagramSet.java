package org.ontologyengineering.conceptdiagrams.web.shared.diagrams;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * A set of diagrams - roughly corresponds to an ontology
 * <p/>
 * The set of diagrams that make up some descriptive unit: such as the set of diagrams used to express an ontology (or
 * part of the ontology if some of the ontology is expressed otherwise), or the diagrams for some project etc if not
 * being used to express an ontology.
 *
 */
public class DiagramSet {

    private AbstractSet<ConcreteDiagram> theDiagrams;

    public DiagramSet() {
        theDiagrams = new HashSet<ConcreteDiagram>();
    }

    private AbstractSet<ConcreteDiagram> getDiagrams() {
        return theDiagrams;
    }

    public void addDiagram(ConcreteDiagram diagram) {
        getDiagrams().add(diagram);
    }

    public void removeDiagram(ConcreteDiagram diagram) {
        getDiagrams().remove(diagram);
    }

}
