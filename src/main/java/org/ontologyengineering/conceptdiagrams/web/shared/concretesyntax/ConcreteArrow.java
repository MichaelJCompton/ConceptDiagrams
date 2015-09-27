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
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.PointArray;


public class ConcreteArrow extends ConcreteDiagramElement {

    private ConcreteDiagramElement source;
    private ConcreteDiagramElement target;

    private Point endPoint;  // startpoint is in the super
    private PointArray controlPoints;



    // FIXME : also needs to respond to moves of the parents
    // FIXME : need to make attached to it's source and target so if they move, this can move too

    public ConcreteArrow(Point topLeft, Point endPoint) {
        super(topLeft, ConcreteDiagramElement_TYPES.CONCRETEARROW);
        this.endPoint = endPoint;
//
//        setLineColour(arrowColour);
//        setLineSelectedColour(arrowSelectedColour);
//        lineWidth = arrowLineWidth;
    }


    public void setSource(ConcreteDiagramElement newSource) {
        source = newSource;
    }

    public ConcreteDiagramElement getSource() {
        return source;
    }

    public void setTarget(ConcreteDiagramElement newTarget) {
        target = newTarget;
    }

    public ConcreteDiagramElement getTarget() {
        return target;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    // ignore moves, they happen through source and dest
    public void move(Point topLeft) {}


    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addArrow(this);
    }

    @Override
    public void makeAbstractRepresentation() {

    }

//    @Override
//    public void makeConcreteRepresentation() {
//        double[] xpoints = new double[pointsInArrowLine + 2];  // +2 for start and end points
//        double[] ypoints = new double[pointsInArrowLine + 2];
//        double xdiff = (getX() < getEndPoint().getX()) ? getEndPoint().getX() - getX() : getX() - getEndPoint().getX();
//        double ydiff = (getY() < getEndPoint().getY()) ? getEndPoint().getY() - getY() : getY() - getEndPoint().getY();
//
//        xpoints[0] = getX();
//        ypoints[0] = getY();
//        for(int i = 1; i <= pointsInArrowLine; i++) {
//            if(getX() < getEndPoint().getX()) {
//                xpoints[i] = getX() + (xdiff / (pointsInArrowLine+1)) * i;
//            } else {
//                xpoints[i] = getX() - (xdiff / (pointsInArrowLine+1)) * i;
//            }
//            if(getY() < getEndPoint().getY()) {
//                ypoints[i] = getY() + (ydiff / (pointsInArrowLine+1)) * i;
//            } else {
//                ypoints[i] = getY() - (ydiff / (pointsInArrowLine+1)) * i;
//            }
//        }
//        xpoints[pointsInArrowLine+1] = getEndPoint().getX();
//        ypoints[pointsInArrowLine+1] = getEndPoint().getY();
//
//        controlPoint = new Point2DArray(xpoints, ypoints);
//
//        final Spline spline = new Spline(controlPoint);
//        spline.setStrokeColor(getBorderColour());
//        spline.setStrokeWidth(getLineWidth());
//        spline.setDraggable(false);
//
//        spline.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
//
//            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
//                spline.setStrokeColor(getBorderSelectedColour());
//                setIsUnderMouse();
//                spline.getLayer().batch();
//            }
//        });
//        spline.addNodeMouseExitHandler(new NodeMouseExitHandler() {
//
//            public void onNodeMouseExit(NodeMouseExitEvent event) {
//                spline.setStrokeColor(getBorderColour());
//                spline.getLayer().batch();
//            }
//        });
//
//        setConcreteRepresentation(spline);
//    }


//    @Override
//    public void drawOnLayer(Layer layer) {
//        layer.add(getConcreteRepresentation());
//    }
//
//    public void removeFromLayer(Layer layer) {
//        layer.remove(getConcreteRepresentation());
//    }
//
//    public void setAsUnSelected() {
//        //getBoundaryRectangle().getDragLayer(). remove...
//    }

//    public void setAsSelected() {
//        // FIXME : should be done without the concretes knowing about the layers
//        removeFromLayer(getBoundaryRectangle().getCurveLayer());
//
//        getBoundaryRectangle().getDragLayer().add(getConcreteRepresentation());
//
////        Point2DArray splinepoints =
////        controlPoints = new Rectangle[pointsInArrowLine];
////        for(int i = 0; i < pointsInArrowLine; i++) {
////            final Rectangle controlPoint = new
////        }
//
//        // draw the rubberbanding one
//    }



    public Point centre() {
        return new Point((getX() + getEndPoint().getX()) / 2, (getY() + getEndPoint().getY()) / 2);
    }

    public void deleteMe() {
        // FIXME : in flux
        //getConcreteRepresentation().setListening(false);
        //getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
        getBoundaryRectangle().removeArrow(this);
    }
}
