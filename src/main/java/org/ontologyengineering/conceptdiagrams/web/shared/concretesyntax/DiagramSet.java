package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.BoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * A set of diagrams - can roughly correspond to an ontology if all the diagrams relevant to an ontology are say drawn
 * in one diagram.  But it doesn't have to: an ontology can be made up of a number of diagram sets.
 * <p/>
 * The set of diagrams that make up some descriptive unit: such as the set of diagrams used to express an ontology (or
 * part of the ontology if some of the ontology is expressed otherwise), or the diagrams for some project etc if not
 * being used to express an ontology, or the set of diagrams drawn on a single canvas.
 * </p>
 * Practially it's just a set of diagrams that form some unit and might at some stage be joined if an arrow is drawn
 * between two diagrams.
 */
public class DiagramSet implements Serializable {

    private HashSet<ConcreteDiagram> theDiagrams;
    private String uniqueID;    // allocated by the system (Document.get().createUniqueId() as UUID can't be reached)
    private String label;       // given by the user

    public enum Diagram_TYPE {
        MIXED, CONCEPT, PROPERTY
    }
    private Diagram_TYPE type;

    public DiagramSet() {
        theDiagrams = new HashSet<ConcreteDiagram>();
        if(GWT.isScript()) {  // only run this bit on the client side.
            uniqueID = Document.get().createUniqueId();
        }   // Otherwise it's because this code is being executed on the server, so it's probably deserialization,
            // so ok not to allocated ID - it will be given in the serialized object
        type = Diagram_TYPE.MIXED;
    }

    public boolean isEmpty() {
        return getDiagrams().isEmpty();
    }

    public boolean containsOnlyStar() {
        boolean result = true;
        for(ConcreteDiagram d : getDiagrams()) {
            for(ConcreteBoundaryRectangle br : d.getRectangles()) {
                if(!(br instanceof ConcreteStarRectangle)) {
                    result = false;
                }
            }
        }
        return result;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String newLabel) {
        label = newLabel;
    }

    public boolean isConceptDiagramSet() {
        return type == Diagram_TYPE.CONCEPT;
    }

    public boolean isPropertyDiagramSet() {
        return type == Diagram_TYPE.PROPERTY;
    }

    public void setAsConceptDiagramSet() {
        type = Diagram_TYPE.CONCEPT;
    }

    public void setAsPropertyDiagramSet() {
        type = Diagram_TYPE.PROPERTY;
    }

    public void setAsMixedDiagramSet() {
        type = Diagram_TYPE.MIXED;
    }

    public void trySetAsType(Diagram_TYPE newType) {
        // should be checked
        type = newType;
    }


    public String getUniqueID() {
        return uniqueID;
    }

    public Set<ConcreteDiagram> getDiagrams() {
        return theDiagrams;
    }

    public void addDiagram(ConcreteDiagram diagram) {
        getDiagrams().add(diagram);
    }

    public void removeDiagram(ConcreteDiagram diagram) {
        if(diagram.isEmpty()) {
            getDiagrams().remove(diagram);
        }
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


    public Set<ConcreteCurve> curvesAtPoint(Point p) {
        Set<ConcreteCurve> result = new HashSet<ConcreteCurve>();
        ConcreteBoundaryRectangle br = boundaryRectangleAtPoint(p);
        if(br != null) {
            result.addAll(br.curvesAtPoint(p));
        }
        return result;
    }


    // this arrow has been added to the diagram set and (maybe) thus joins two diagrams into a single diagram
    protected void addArrowBetween(ConcreteArrow arrow) {
        if (getDiagrams().contains(arrow.getTarget().getDiagram()) &&
                getDiagrams().contains(arrow.getSource().getDiagram())) {

            if (arrow.getSource().getDiagram() != arrow.getTarget().getDiagram()) {
                ConcreteDiagram toRemove = arrow.getTarget().getDiagram();
                arrow.getSource().getDiagram().mergeWith(arrow.getTarget().getDiagram(), arrow);
                getDiagrams().remove(toRemove);
            }
        }
    }


    protected void removeArrowBetween(ConcreteArrow arrow) {
        if (getDiagrams().contains(arrow.getSource().getDiagram()) &&
                arrow.getSource().getDiagram() == arrow.getTarget().getDiagram()) {  // this should be an invariant of the diagram set

            ConcreteDiagram sourceDiag = arrow.getDiagram();
            ConcreteBoundaryRectangle sourceRect = arrow.getSource().getBoundaryRectangle();

            // remove the arrow source and target, this unlinks it from the graph
            arrow.unlinkSourceAndTarget();

            // now traverse the graph to check if all the boundary rectangles are still reachable
            HashSet<ConcreteBoundaryRectangle> reachable = new HashSet<ConcreteBoundaryRectangle>();
            reachableFrom(sourceRect, reachable);

            if (!reachable.containsAll(sourceDiag.getRectangles())) {
                // only removing one arrow, so split into two diagrams
                // the ones in reachable are one already, then grab the rest
                // only issue is splitting the arrows
                HashSet<ConcreteBoundaryRectangle> rectangles = new HashSet<>();
                HashSet<ConcreteArrow> arrows = new HashSet<>();

                for(ConcreteBoundaryRectangle br : sourceDiag.getRectangles()) {
                    if(!reachable.contains(br)) {
                        sourceDiag.removeRectangle(br);
                        rectangles.add(br);
                    }
                }

                for(ConcreteArrow a : sourceDiag.getArrows()) {
                    if(!reachable.contains(a.getSource().getBoundaryRectangle())) {
                        sourceDiag.simpleRemoveArrow(a);
                        arrows.add(a);
                    }
                }

                getDiagrams().add(new ConcreteDiagram(rectangles, arrows, sourceDiag.isConceptDiagram(), this));
            }
        }
    }

    private void reachableFrom(ConcreteBoundaryRectangle br, HashSet<ConcreteBoundaryRectangle> reached) {
        HashSet<ConcreteBoundaryRectangle> tocheck = new HashSet<ConcreteBoundaryRectangle>();

        reached.add(br);

        // this is the arrows out
        for(ConcreteArrow a : br.getBoundaryRectangle().getSourcedArrows()) {
            tocheck.add(a.getTarget().getBoundaryRectangle());
        }

        // the arrows in
        for(ConcreteArrow a : br.getBoundaryRectangle().getTargetedArrows()) {
            tocheck.add(a.getSource().getBoundaryRectangle());
        }

        // recurse through these
        for(ConcreteBoundaryRectangle rect : tocheck) {
            if(!reached.contains(rect)) {
                reachableFrom(rect, reached);
            }
        }
    }


    public boolean containsElement(ConcreteDiagramElement elmnt) {
        return getDiagrams().contains(elmnt.getDiagram());
    }

}
