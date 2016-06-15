package org.ontologyengineering.conceptdiagrams.web.shared.diagrams;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of diagrams - roughly corresponds to an ontology
 * <p/>
 * The set of diagrams that make up some descriptive unit: such as the set of diagrams used to express an ontology (or
 * part of the ontology if some of the ontology is expressed otherwise), or the diagrams for some project etc if not
 * being used to express an ontology, or the set of diagrams drawn on a single canvas.
 *
 */
public class DiagramSet implements Serializable {

    private HashSet<ConcreteDiagram> theDiagrams;

    public DiagramSet() {
        theDiagrams = new HashSet<ConcreteDiagram>();
    }

    public Set<ConcreteDiagram> getDiagrams() {
        return theDiagrams;
    }

    public void addDiagram(ConcreteDiagram diagram) {
        getDiagrams().add(diagram);
    }

    public void removeDiagram(ConcreteDiagram diagram) {
        getDiagrams().remove(diagram);
    }

    public Set<ConcreteDiagramElement> elementsInBoundingBox(Point topLeft, Point botRight) {
        Set<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();

        for(ConcreteDiagram d : getDiagrams()) {
            result.addAll(d.elementsInBoundingBox(topLeft, botRight));
        }

        return result;
    }

    public ConcreteBoundaryRectangle boundaryRectangleAtPoint(Point p) {
        for (ConcreteDiagram d : getDiagrams()) {
            for (ConcreteBoundaryRectangle rectangle : d.getRectangles()) {
                if (rectangle.containsPoint(p)) {
                    return rectangle;
                }
            }
        }
        return null;
    }


    // this arrow has been added to the diagram set and (maybe) thus joins two diagrams into a single
    public void addArrowBetween(ConcreteArrow arrow) {
        if(getDiagrams().contains(arrow.getTarget().getDiagram())) {
            if (getDiagrams().contains(arrow.getSource().getDiagram())) {

                if (arrow.getSource().getDiagram() != arrow.getTarget().getDiagram()) {
                    ConcreteDiagram toRemove = arrow.getTarget().getDiagram();
                    arrow.getSource().getDiagram().mergeWith(arrow.getTarget().getDiagram(), arrow);
                    getDiagrams().remove(toRemove);
                }
            }
        }
    }

    // FIXME add way  un-combine diagrams (generally on the removal of an arrow)
    // harder than the above, because we have to find if removing an arrow leaves two components
    // unconnected by any chain of arrows.
    // Important for delete.


}
