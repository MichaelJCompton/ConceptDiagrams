package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * A diagram in the concrete syntax.
 * <p/>
 * May be a single diagram enclosed in a boundary rectangle, or a set of diagrams joined because there is are arrows
 * between them.  In either case take it as a unit that needs to be considered together because of the joining.
 * <p/>
 * These (when valid) represent either ConceptDiagrams or PropertyDiagrams (those with a star)
 */
public class ConcreteDiagram extends ConcreteDiagramElement {

    private HashSet<ConcreteBoundaryRectangle> myRectangles;
    private HashSet<ConcreteArrow> myArrows;

    private boolean isConceptDiagram = true;

    private DiagramSet diagramSet;

    // just for serialization
    private ConcreteDiagram() {
        // ?????
    }

    public ConcreteDiagram(ConcreteBoundaryRectangle initalRectangle, DiagramSet diagramSet) {
        super(ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEDIAGRAM);
        this.diagramSet = diagramSet;
        myRectangles = new HashSet<ConcreteBoundaryRectangle>();
        myRectangles.add(initalRectangle);
        myArrows = new HashSet<ConcreteArrow>();
        initalRectangle.setDiagram(this);
    }


    // assume that the arrows go between these rectangles
    protected ConcreteDiagram(HashSet<ConcreteBoundaryRectangle> rectangles, HashSet<ConcreteArrow> arrows, boolean concept, DiagramSet diagramSet) {
        super(ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEDIAGRAM);
        this.diagramSet = diagramSet;
        myRectangles = rectangles;
        myArrows = arrows;
        isConceptDiagram = concept;

        ensureInThisDiagram(rectangles);
    }

    // if there is only 1 rectangle then there can't be arrows in or out
    public boolean isEmpty() {
        if(getRectangles().size() == 1) {
            return getRectangles().iterator().next().isEmpty();
        }
        return false;
    }

    public Set<ConcreteBoundaryRectangle> getRectangles() {
        return myRectangles;
    }

    protected Set<ConcreteArrow> getArrows() {
        return myArrows;
    }

    protected void addArrow(ConcreteArrow arrow) {
        if(isInDiagram(arrow.getBoundaryRectangle())) {
            getArrows().add(arrow);

            // check if we need to merge diagrams
            getDiagramSet().addArrowBetween(arrow);
        }
    }

    private void addArrows(Set<ConcreteArrow> arrows) {
        for(ConcreteArrow a : arrows) {
            addArrow(a);
        }
    }

    protected void removeArrow(ConcreteArrow arrow) {
        if(isInDiagram(arrow.getBoundaryRectangle())) {
            getArrows().remove(arrow);

            getDiagramSet().removeArrowBetween(arrow);
        }
    }

    // simply removes the arrow and doesn't check if this diagram is now disjoint
    // mainly for use when refactoring a diagram that has become disjoint, so we can split it and move arrows to new
    // diagram without calling the removeArrow
    protected void simpleRemoveArrow(ConcreteArrow arrow) {
        getArrows().remove(arrow);
    }



    public DiagramSet getDiagramSet() {
        return diagramSet;
    }

    private void ensureInThisDiagram(Collection<ConcreteBoundaryRectangle> rectangles) {
        for(ConcreteBoundaryRectangle r : rectangles) {
            r.setDiagram(this);
        }
    }

    protected void mergeWith(ConcreteDiagram other, ConcreteArrow arrow) {
        // assumes arrow goes between this diagram and other
        //addArrow(arrow);
        addArrows(other.getArrows());

        getRectangles().addAll(other.getRectangles());
        ensureInThisDiagram(other.getRectangles());
    }

    protected void removeRectangle(ConcreteBoundaryRectangle rectangle) {
        getRectangles().remove(rectangle);
    }

    public boolean isInDiagram(ConcreteBoundaryRectangle rectangle) {
        return getRectangles().contains(rectangle);
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {

    }


    public boolean isConceptDiagram() {
        return isConceptDiagram;
    }

    public boolean isPropertyDiagram() {
        return !isConceptDiagram();
    }

    @Override
    public void checkValidity() {
        inferType();
    }


    public void inferType() {
        boolean validity = true;
        boolean containsAStar = false;

        for(ConcreteBoundaryRectangle r : getRectangles()) {
            r.checkValidity();
            if(! r.isValid()) {
                validity = false;
            }
            if(r.isStarRectangle()) {
                containsAStar = true;
            }
        }

        // we've inferred the types of the rectangles, now what am I?
        if(containsAStar) {
            isConceptDiagram = false;
        }

        for(ConcreteArrow a : getArrows()) {
            a.checkValidity();
            if(! a.isValid()) {
                validity = false;
            }
        }

        setValid(validity);
    }


    @Override
    public Point centre() {
        // don't think I need this method, but might want to implement it as the center of a bounding box of all the stuff in here
        return new Point(0,0);
    }

    public Point bottomRight() {
        return new Point(0,0);
    }


    public AbstractSet<ConcreteDiagramElement> elementsInBoundingBox(Point topLeft, Point botRight) {
        AbstractSet<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();

        for(ConcreteBoundaryRectangle rec : getRectangles()) {
            result.addAll(rec.elementsInBoundingBox(topLeft, botRight));
        }

        return result;
    }


}
