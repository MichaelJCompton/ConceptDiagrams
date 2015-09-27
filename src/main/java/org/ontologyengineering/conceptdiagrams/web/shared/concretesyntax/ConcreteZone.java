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
//import com.ait.lienzo.client.core.types.Point2D;

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashSet;

/**
 * This superclass represents the regular zone that is inside each curve.  The zones arising from intersections are
 * dealt with in the subclass ConcreteIntersectionZone.
 * <p/>
 * Hence the zones here are the same shape as the curves, just a bit smaller to account for the border of the square.
 * There are no borders on zones.
 */
public class ConcreteZone extends ConcreteRectangularElement {

    // means curves that make up the intersection that results in this zone.  For example, completely enclosing a zone
    // won't get the curve in this list.  A main zone, there for, can only be in one curve.
    private AbstractSet<ConcreteCurve> curvesImIn;

    private Boolean isShaded;
    Integer drawingLevel;

    ConcreteZone(Point topLeft, Point bottomRight, ConcreteDiagramElement_TYPES type) {
        super(topLeft, bottomRight, type);

        initialise();
    }

    ConcreteZone(Point topLeft, Point bottomRight) {
        // This is the actual x,y of the zone the offset from the enclosing curves has already been taken away
        super(topLeft, bottomRight, ConcreteDiagramElement_TYPES.CONCRETEZONE);

        initialise();
    }

    private void initialise() {
        setNOTShaded();
        //setBorderSelectedColour(zoneSelectedColor);
        //setFillSelectedColour(zoneSelectedColor);
        setCornerRadius(zoneCornerRadius);
        setBorderWidth(0);
        drawingLevel = 1;
        curvesImIn = new HashSet<ConcreteCurve>();
        topLeftIsCircle = botLeftIsCircle = topRightIsCircle = botRightIsCircle = true;
    }

    public void swapShading() {
        if (shaded()) {
            setNOTShaded();
        } else {
            setShaded();
        }
    }

    public void setShaded() {
        isShaded = true;
//        setBorderColour(zoneShadedColor);
//        setFillColour(zoneShadedColor);
    }

    public void setNOTShaded() {
        isShaded = false;
//        setBorderColour(zoneStandardColour);
//        setFillColour(zoneStandardColour);
    }

    public boolean shaded() {
        return isShaded;
    }

    public void resize(Point topLeft, Point botRight) {}        // ignore

    @Override
    public AbstractCollection<ConcreteZone> getAllZones() {
        AbstractCollection<ConcreteZone> result = new HashSet<ConcreteZone>();
        result.add(this);
        return result;
    }

    public Integer getLevel() {
        return drawingLevel;
    }

    public AbstractSet<ConcreteCurve> getCurves() {
        return curvesImIn;
    }

    protected void addEnclosingCurve(ConcreteCurve curve) {
        getCurves().add(curve);
    }

    protected void removeEnclosingCurve(ConcreteCurve curve) {
        getCurves().remove(curve);
    }

    public void removeFromAllEnclosingCurves() {
        for (ConcreteCurve curve : getCurves()) {
            curve.removeEnclosedZone(this);
        }
    }

    /**
     * Same as removeFromAllEnclosingCurves, but keeps the passed in curve.
     * <p/>
     * Needed because we often want to remove a curve fom a boundary rectangle, and remove all traces of it from what
     * remains in the rectangle, but keep the curve as an image of what it once was.
     *
     * @param curveToKeep The curve we don't remove
     */
    public void disassociateFromCurves(ConcreteCurve curveToKeep) {
        HashSet<ConcreteCurve> copy = new HashSet<ConcreteCurve>();
        copy.addAll(getCurves());
        for (ConcreteCurve curve : copy) {
            // could implement with calling removeFromAllEnclosingCurves and then re-add, but if more things
            // happen behind the scences that might have ripple effects.
            if (curve != curveToKeep) {
                curve.removeEnclosedZone(this);
                removeEnclosingCurve(curve);
            }
        }
    }


    // should only be called from the boundary rectangle
    protected void increaseLevel() {
        drawingLevel++;
    }

    protected void decreaseLevel() {
        drawingLevel--;
    }

    public void setLevel(Integer newLevel) {
        drawingLevel = newLevel;
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        // but should only be called by the enclosing curve, so that will add the zone itself
        myBoundaryRectangle = rect;
        rect.addZone(this);
    }




    @Override
    public void makeAbstractRepresentation() {

    }


//    @Override
//    public void makeConcreteRepresentation() {
//
//        if(hasChangedOnScreen()) {
//            final Rectangle concreteZone = new Rectangle(getWidth(), getHeight(), getCornerRadius());
//            setConcreteRepresentation(concreteZone);
//            setupConcreteRepresentation();
//
//            concreteZone.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
//                @Override
//                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
//                    paintSelectedColoursOnConcreteRepresentation();
//                    setIsUnderMouse();
//                    concreteZone.getLayer().draw();
//                }
//            });
//            concreteZone.addNodeMouseExitHandler(new NodeMouseExitHandler() {
//                @Override
//                public void onNodeMouseExit(NodeMouseExitEvent event) {
//                    paintColoursOnConcreteRepresentation();
//                    concreteZone.getLayer().batch();
//                }
//            });
//        }
//    }

//    @Override
//    public void drawOnLayer(Layer layer) {
//        layer.add(getConcreteRepresentation());
//    }
//

    public void deleteMe() {
//        getConcreteRepresentation().setListening(false);
//        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
//        getBoundaryRectangle().removeZone(getLevel(), this);
    }


    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------



    // FIXME ... some of this geometry code needs to be refactored into CONCRETERECTANGLESYNTAXELEMENT


    public boolean intersectsZone(ConcreteZone other) {

        // assumes no small curves constraint
        if (completelyEncloses(other) || other.completelyEncloses(this)) {
            return true;
        }

        // still true for zones???
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


    // Computes the zone arising from the intersection.  and adds it
    //
    // Assumes
    // no small intersections constraint
    // that the zones do actually intersect
    public void computeIntersection(ConcreteZone other) {
        Point topLeft, bottomRight;

        // are any of the intersection corners half circles
        Boolean leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle;


        if (completelyEncloses(other)) {
            other.getBoundaryRectangle().increaseLevel(other);
            return;     // no new zones to add
        }

        if (other.completelyEncloses(this)) {
            getBoundaryRectangle().increaseLevel(this);
            return;
        }

        // figure out the corners ... call a zone to make itself
        // Just going to do it corner by corner to calculate the extent of the intersection and work out what's
        // round and what's square.

        // these are points in the original curve.  To make them the top left of the zone, need to subtract the
        // line widths.
        topLeft = new Point(Math.max(getX(), other.getX()) + getBorderWidth(), Math.max(getY(), other.getY()) + getBorderWidth());
        bottomRight = new Point(Math.min(getX() + getWidth(), other.getX() + other.getWidth()) - getBorderWidth(),
                Math.min(getY() + getHeight(), other.getY() + other.getHeight()) - getBorderWidth());

        // what's round and what's square?

        leftTopIsCircle = (containsPoint(other.topLeftCentre()) && other.topLeftIsCircle)
                || (other.containsPoint(topLeftCentre()) && topLeftIsCircle);
        leftBotIsCircle = (containsPoint(other.bottomLeftCentre()) && other.botLeftIsCircle)
                || (other.containsPoint(bottomLeftCentre()) && botLeftIsCircle);
        rightTopIsCircle = (containsPoint(other.topRightCentre()) && other.topRightIsCircle)
                || (other.containsPoint(topRightCentre()) && topRightIsCircle);
        rightBotIsCircle = (containsPoint(other.bottomRightCentre()) && other.botRightIsCircle)
                || (other.containsPoint(bottomRightCentre()) && botRightIsCircle);

        ConcreteIntersectionZone z = new ConcreteIntersectionZone(topLeft, bottomRight, leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle);

        z.setLevel(Math.max(getLevel(), other.getLevel()) + 1);

        z.setBoundaryRectangle(getBoundaryRectangle());

        for (ConcreteCurve curve : getCurves()) {
            curve.addEnclosedZone(z);
        }
        for (ConcreteCurve curve : other.getCurves()) {
            curve.addEnclosedZone(z);
        }

        // FIXME : I need to sanitise these enclosed zones.  As more zones are added the list starts to get completely enclosed things as well as intersections ... how to check?

        //z.makeConcreteRepresentation();
    }
}
