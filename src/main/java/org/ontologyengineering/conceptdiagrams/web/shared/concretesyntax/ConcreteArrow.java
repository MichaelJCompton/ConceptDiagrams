package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

//import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
//import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
//import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
//import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
//import com.ait.lienzo.client.core.shape.Layer;
//import com.ait.lienzo.client.core.shape.Rectangle;
//import com.ait.lienzo.client.core.shape.Spline;
//import com.ait.lienzo.client.core.types.Point2D;
//import com.ait.lienzo.client.core.types.Point2DArray;
//import com.ait.lienzo.shared.core.types.Color;

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;



public class ConcreteArrow extends ConcreteDiagramElement {

    private ConcreteDiagramElement source;
    private ConcreteDiagramElement target;

    //private Point startPoint, endPoint;  // startpoint is in the super

    // start and end points are calculated from the ratios below

    // how far from the top left of the element the start/end point is - as a proportion of width/height.
    // So that on moves and resizes we can keep the arrow in the right spot
    private double xRatioSource, yRatioSource, xRatioTarget, yRatioTarget;


    private boolean isObjectProperty = true;
    private boolean isInverse = false;

    private boolean typeUnknown = true;  // if we don't know the type, we are free to infer it (once)

    public enum CardinalityConstraint {NONE, EQ, LEQ, GEQ}
    private CardinalityConstraint cardinalityConstraint = CardinalityConstraint.NONE;
    private Integer cardinality;

    // just for serialization
    public ConcreteArrow() {
        //????
    }

    // Assumes that the points are correct
    public ConcreteArrow(Point startPoint, Point endPoint, ConcreteDiagramElement source, ConcreteDiagramElement target) {
        super(startPoint, ConcreteDiagramElement_TYPES.CONCRETEARROW);  // wrong start point!  but need something

        setTopLeft(new Point(Math.min(startPoint.getX(), endPoint.getX()), Math.min(startPoint.getY(), endPoint.getY())));

        setSource(source);
        setStartPoint(startPoint);
        setTarget(target);
        setEndPoint(endPoint);

        // FIXME : maybe this should set the boundary rectangle as the sourcce/target?
    }

    // really set the ratio to the source top left
    private void setStartPoint(Point startPoint) {
        xRatioSource = (startPoint.getX() - source.topLeft().getX()) / source.getWidth();
        yRatioSource = (startPoint.getY() - source.topLeft().getY()) / source.getHeight();
    }

    public void setSource(ConcreteDiagramElement newSource) {
        if(source != null) {
            source.removeAsArrowSource(this);
        }
        source = newSource;
        if(newSource != null) {
            source.setAsArrowSource(this);
        }

        // FIXME also if the source is a data curve/rectangle setAsDataProperty
    }

    public ConcreteDiagramElement getSource() {
        return source;
    }

    private void setEndPoint(Point endPoint) {
        xRatioTarget = (endPoint.getX() - target.topLeft().getX()) / target.getWidth();
        yRatioTarget = (endPoint.getY() - target.topLeft().getY()) / target.getHeight();
    }

    public void setTarget(ConcreteDiagramElement newTarget) {
        if(target != null) {
            target.removeAsArrowTarget(this);
        }
        target = newTarget;
        if(newTarget != null) {
            target.setAsArrowTarget(this);
        }
    }

    public ConcreteDiagramElement getTarget() {
        return target;
    }

    public boolean isInverse() {
        return isInverse;
    }

    public void setAsInverse() {
        isInverse = true;
    }

    public void setAsNotInverse() {
        isInverse = false;
    }

    public void swapInverse() {
        isInverse = !isInverse;
    }


    public void setCardinalityConstraint(CardinalityConstraint constraint, Integer cardinality) {
        cardinalityConstraint = constraint;
        this.cardinality = cardinality;
    }

    public Boolean hasCardinalityConstraint() {
        return cardinalityConstraint != CardinalityConstraint.NONE;
    }

    public CardinalityConstraint getCardinalityConstraint() {
        return cardinalityConstraint;
    }

    public Integer getCardinality() {
        return cardinality;
    }


    // ignore moves, they happen through source and dest
    public void move(Point topLeft) {}

    public boolean singleRectangle() {
        return getSource().getBoundaryRectangle() == getTarget().getBoundaryRectangle();
    }

    public boolean spansRectangles() {
        return ! singleRectangle();
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        // done by the command that adds the arrow
        //rect.addArrow(this);
    }

//    @Override
//    public void makeAbstractRepresentation() {
//
//    }

    public void setAsObjectProperty() {
        setAsObject();
    }

    public void setAsDataProperty() {
        setAsData();
    }

    public boolean isObjectProperty() {
        return isObject();
    }

    public boolean isDataProperty() {
        return isData();
    }

    @Override
    public void checkValidity() {
        inferType();
    }

    public void inferType() {
        boolean validity;

        // source must be object
        validity = getSource().isObject();

        // target and my type must agree
        if(getTarget().typeIsKnown()) {
            if(typeIsKnown()) {
                validity &= (isObjectProperty == getTarget().isObject());
            } else { // if it's not known then it's just valid
                if(getTarget().isObject()) {
                    setAsObjectProperty();
                } else {
                    setAsDataProperty();
                }
            }
        }

        if(typeIsKnown() && isDataProperty() && isInverse()) {
            validity = false;
        }

        setValid(validity);

    }

    public void deleteMe() {
        // FIXME : in flux

        getBoundaryRectangle().removeArrow(this);
    }


    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------


    public Point centre() {
        return new Point((getX() + getEndPoint().getX()) / 2, (getY() + getEndPoint().getY()) / 2);
    }

    public Point bottomRight() {
        return new Point(Math.max(getStartPoint().getX(), getEndPoint().getX()), Math.max(getStartPoint().getY(), getEndPoint().getY()));
    }

    public Point getStartPoint() {
        return new Point(getSource().topLeft().getX() + (getSource().getWidth() * xRatioSource),
                getSource().topLeft().getY() + (getSource().getHeight() * yRatioSource));
    }

    public Point getEndPoint() {
        return new Point(getTarget().topLeft().getX() + (getTarget().getWidth() * xRatioTarget),
                getTarget().topLeft().getY() + (getTarget().getHeight() * yRatioTarget));
    }

    public boolean intersectsBox(Point topLeft, Point botRight) {
        return ConcreteRectangularElement.rectangleContainment(getStartPoint(), topLeft, botRight) ||
                ConcreteRectangularElement.rectangleContainment(getEndPoint(), topLeft, botRight);
    }
}
