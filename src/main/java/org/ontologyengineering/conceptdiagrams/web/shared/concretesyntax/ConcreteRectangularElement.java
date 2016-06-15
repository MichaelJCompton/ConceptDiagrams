package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;

/**
 * Brings together all the rectangular shaped elements (boundary rectangles, curves and zones) which share things such
 * as width and height, and also curved corners.
 */
public abstract class ConcreteRectangularElement extends ConcreteDiagramElement {

    private Point bottomRight;

    private double borderWidth;
    private double cornerRadius;

    protected Boolean topLeftIsCircle, botLeftIsCircle, topRightIsCircle, botRightIsCircle;

    // just for serialization
    public ConcreteRectangularElement() {
        //this(new Point(), new Point(), ConcreteDiagramElement_TYPES.CONCRETERECTANGULARELEMENT);
    }

    public ConcreteRectangularElement(Point topLeft, Point bottomRight, ConcreteDiagramElement_TYPES type) {
        super(topLeft, type);
        setBottomRight(bottomRight);
        topLeftIsCircle = botLeftIsCircle = topRightIsCircle = botRightIsCircle = false;
    }

    protected void setBottomRight(Point newBottomLeft) {
        bottomRight = newBottomLeft;
    }

    public Point bottomRight() {
        return bottomRight;
    }


    public abstract void resize(Point topLeft, Point botRight);
    public void move(Point topLeft) {
        resize(topLeft, bottomRight());
    }

    public abstract AbstractCollection<ConcreteZone> getAllZones();

//    public void setWidth(double width) {
//        this.width = width;
//    }
//
//    public void setHeight(double height) {
//        this.height = height;
//    }

    public double getWidth() {
        return bottomRight().getX() - topLeft().getX();
    }

    public double getHeight() {
        return bottomRight().getY() - topLeft().getY();
    }


    public Point centre() {
        return new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2));
    }

    protected void setBorderWidth(double width) {
        this.borderWidth = width;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    protected void setCornerRadius(double newRadius) {
        cornerRadius = newRadius;
    }

    public boolean topLeftIsCircle() {
        return topLeftIsCircle;
    }

    public boolean bottomLeftIsCircle() {
        return botLeftIsCircle;
    }

    public boolean topRightIsCircle() {
        return topRightIsCircle;
    }

    public boolean bottomRightIsCircle() {
        return botRightIsCircle;
    }

//    public void setupConcreteRepresentation() {
//        getConcreteRepresentation().setX(getX());
//        getConcreteRepresentation().setY(getY());
//        getConcreteRepresentation().setStrokeWidth(getBorderWidth());
//        paintColoursOnConcreteRepresentation();
//        getConcreteRepresentation().setDraggable(false);
//    }


//    public abstract void makeAbstractRepresentation();
//    public abstract void makeConcreteRepresentation();
//    public abstract void drawOnLayer(Layer layer);

//    public void setAsSelected() {}


    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------


    // Center point of the top left corner curve
    // These centre points are meaningless if that corner isn't curved.
    public Point topLeftCentre() {
        return new Point(getX() + getCornerRadius(), getY() + getCornerRadius());
    }

    public Point topRightCentre() {
        return new Point(getX() + getWidth() - getCornerRadius(), getY() + getCornerRadius());
    }

    public Point bottomLeftCentre() {
        return new Point(getX() + getCornerRadius(), getY() + getHeight() - getCornerRadius());
    }

    public Point bottomRightCentre() {
        return new Point(getX() + getWidth() - getCornerRadius(), getY() + getHeight() - getCornerRadius());
    }


    public Point topRight() {
        return new Point(getX() + getWidth(), getY());
    }

    public Point bottomLeft() {
        return new Point(getX(), getY() + getHeight());
    }


    // Is the point in the given rectangle (axis aligned rectangle)
    // Includes on the lines
    public static boolean rectangleContainment(Point p, Point topLeft, Point bottomRight) {
        if (p.getX() < topLeft.getX() || bottomRight.getX() < p.getX()) {
            return false;
        }

        if (p.getY() < topLeft.getY() || bottomRight.getY() < p.getY()) {
            return false;
        }

        return true;
    }

    public static boolean rectangleContainment(Point p, Point topLeft, double width, double height) {
        return rectangleContainment(p, topLeft, new Point(topLeft.getX() + width, topLeft.getY() + height));
    }

    // Doesn't care about line width or corner curves.
    protected boolean rectangleContainment(Point p) {
        return rectangleContainment(p, topLeft(), bottomRight());
    }

    // FIXME : doesn't account for the corners - does this matter for how it's used??
    // Does this rectangle contain the given point - is it in the area enclosed, not on the line.
    // The point is relative to the enclosing boundary rectangle, not the whole canvas
    // accounts for line thickness and (potentially) rounded corners
    public boolean containsPoint(Point p) {
        // simple rectangle containment
        if (p.getX() <= (getX() + getBorderWidth()) || (getX() + getWidth() - getBorderWidth()) <= p.getX()) {
            return false;
        }

        if (p.getY() <= (getY() + getBorderWidth()) || (getY() + getHeight() - getBorderWidth()) <= p.getY()) {
            return false;
        }
        return true;
    }

    // Does this rectangular element completely enclose the other.
    // Doesn't care about if there is any zone between the two, just is one inside the other
    //
    // Assumes
    // no small curves constraint
    public boolean completelyEncloses(ConcreteRectangularElement other) {
        return rectangleContainment(new Point(other.getX(), other.getY())) &&
                rectangleContainment(new Point(other.getX() + other.getWidth(), other.getY() + other.getHeight()));
    }

    public boolean completelyEncloses(Point topLeft, Point botRight) {
        return rectangleContainment(topLeft) &&
                rectangleContainment(botRight);
    }

    // is this element enclosed by the bounding box
    public boolean completelyEnclosed(Point topLeft, Point botRight) {
        return rectangleContainment(topLeft(), topLeft, botRight) &&
                rectangleContainment(bottomRight(), topLeft, botRight);
    }


    // Do the spaces enclosed by the curves contain any points in common
    // Accounts for the rounded corners and the line thicknesses
    // (hhmmm what about curves exactly overlapping?? FIXME???)
    //
    // This definition ASSUMES the
    // - no small curves constraint
    // - no small intersections constraint
    public boolean intersectsRectangularElement(ConcreteRectangularElement other) {

        //return rectanglesIntersect(topLeft(), bottomRight(), other.topLeft(), other.bottomRight());// assumes no small curves constraint
        if (completelyEncloses(other) || other.completelyEncloses(this)) {
            return true;

            // FIXME : This may not be the end of the problem.  There are little side cases where because of the rounded
            // corners a tiny curve
            // could be completely enclosed, but actually live in the space between the sharp corner and the curve.
            // For the moment, I'm assuming large enough curves for that not to happen.
            // On the front end, I'll enforce anyway that the width/height of a curve has to be greater than 2*radius
            // or similar to make them at least be rectangles with rounded corners.

        }

        // because of the no small intersections constraint, can check the corners just by checking where the
        // centre points of the curves are
        if (containsPoint(other.topLeftCentre()) || other.containsPoint(topLeftCentre())
                || containsPoint(other.bottomLeftCentre()) || other.containsPoint(bottomLeftCentre())
                || containsPoint(other.bottomRightCentre()) || other.containsPoint(bottomRightCentre())) {
            return true;
        }

        // last case is if they slice right through each other
        if (getX() < other.getX() && other.getX() < getX() + getWidth()
                && other.getY() < getY() && getY() < other.getY() + other.getHeight()) {
            return true;
        }
        if (other.getX() < getX() && getX() < other.getX() + other.getWidth()
                && getY() < other.getY() && other.getY() < getY() + getHeight()) {
            return true;
        }

        return false;


    }


    // FIXME : these two can probably be done as one, since the implementation of the curved corner centerpoints would be ok for non curved elements ... only question is the small curves, but if we have the constraints it's ok.

    // do the two rectangles intersect - as rectangles: no curved corners
    public static boolean rectanglesIntersect(Point topLeft_one, Point bottomRight_one, Point topLeft_other, Point bottomRight_other) {

        // is a corner inside
        if (rectangleContainment(topLeft_one, topLeft_other, bottomRight_other) ||
                rectangleContainment(new Point(bottomRight_one.getX(), topLeft_one.getY()), topLeft_other, bottomRight_other) ||
                rectangleContainment(new Point(topLeft_one.getX(), bottomRight_one.getY()), topLeft_other, bottomRight_other) ||
                rectangleContainment(bottomRight_one, topLeft_other, bottomRight_other)) {
            return true;
        }
        // just check two diag opposite
        if (rectangleContainment(topLeft_other, topLeft_one, bottomRight_one) ||
                rectangleContainment(bottomRight_other, topLeft_one, bottomRight_one)) {
            return true;
        }

        // do they cut through each other
        if (topLeft_one.getX() <= topLeft_other.getX() && topLeft_other.getX() < bottomRight_one.getX()
                && topLeft_other.getY() <= topLeft_one.getY() && topLeft_one.getY() < bottomRight_other.getY()) {
            return true;
        }
        if (topLeft_other.getX() <= topLeft_one.getX() && topLeft_one.getX() < bottomRight_other.getX()
                && topLeft_one.getY() <= topLeft_other.getY() && topLeft_other.getY() < bottomRight_one.getY()) {
            return true;
        }

        return false;

    }


}
