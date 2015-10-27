package org.ontologyengineering.conceptdiagrams.web.shared.diagrams;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * A set of diagrams - roughly corresponds to an ontology
 * <p/>
 * The set of diagrams that make up some descriptive unit: such as the set of diagrams used to express an ontology (or
 * part of the ontology if some of the ontology is expressed otherwise), or the diagrams for some project etc if not
 * being used to express an ontology, or the set of diagrams drawn on a single canvas.
 *
 */
public class DiagramSet {

    private AbstractSet<ConcreteDiagram> theDiagrams;

    public DiagramSet() {
        theDiagrams = new HashSet<ConcreteDiagram>();
    }

    public AbstractSet<ConcreteDiagram> getDiagrams() {
        return theDiagrams;
    }

    public void addDiagram(ConcreteDiagram diagram) {
        getDiagrams().add(diagram);
    }

    public void removeDiagram(ConcreteDiagram diagram) {
        getDiagrams().remove(diagram);
    }

    public AbstractSet<ConcreteDiagramElement> elementsInBoundingBox(Point topLeft, Point botRight) {
        AbstractSet<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();

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


}
