package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractSet;
import java.util.HashSet;


/**
 * A diagram in the concrete syntax.
 * <p/>
 * May be a single diagram enclosed in a boundary rectangle, or a set of diagrams joined because there is an arrow
 * between them.  In either case take it as a unit that needs to be considered together because of the joining.
 * <p/>
 * The whole space for diagrams is an infinite 2D space.  The boundary rectangles themselves don't know anything about
 * their location in this space.  The boundary rectangles sort out the curves and arrows within them, but know nothing
 * about their location in the whole space.
 */
public class ConcreteDiagram extends ConcreteDiagramElement {

    private AbstractSet<ConcreteBoundaryRectangle> myRectangles;
    private AbstractSet<ConcreteArrow> myArrows;

    public ConcreteDiagram(ConcreteBoundaryRectangle initalRectangle) {
        super(ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEDIAGRAM);
        myRectangles = new HashSet<ConcreteBoundaryRectangle>();
        myRectangles.add(initalRectangle);
        myArrows = new HashSet<ConcreteArrow>();
        initalRectangle.setDiagram(this);
    }

    public AbstractSet<ConcreteBoundaryRectangle> getRectangles() {
        return myRectangles;
    }

    private AbstractSet<ConcreteArrow> getArrows() {
        return myArrows;
    }

//    public void addBoundaryRectangle(Point topLeft, ConcreteBoundaryRectangle rect) {
//        // FIXME : probably should be checking that it doesn't touch anything else in the space
//        getRectangles().put(rect, topLeft);
//    }


    public void mergeWith(ConcreteDiagram other, ConcreteArrow arrow) {
        // assumes arrow goes between this diagram and other
        getArrows().add(arrow);
        getRectangles().addAll(other.getRectangles());
    }

    public boolean isInDiagram(ConcreteBoundaryRectangle rectangle) {
        return getRectangles().contains(rectangle);
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {

    }

    @Override
    public Point centre() {
        // FIXME ... don't think I need this method, but might want to implement it as the center of a bounding box of all the stuff in here
        return new Point(0,0);
    }

    @Override
    public void makeAbstractRepresentation() {

    }

    @Override
    public void deleteMe() {

    }

    // FIXME : might need to become more with a basic diagram and a complex diagram.

}
