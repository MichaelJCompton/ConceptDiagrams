package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br> Date: September 2015<br> See license information in base directory.
 */

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.ontologyengineering.conceptdiagrams.web.client.ui.DiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class LienzoArrow extends LienzoDiagramShape<ConcreteArrow, Spline> {

    private Point2DArray controlPoints;
    private Rectangle[] drawnControlPoints;
    private Spline rubberBandSpline;
    private Triangle arrowHead;

    private static double headAngle = 25;
    private static double headSize = ConcreteDiagramElement.spiderRadius * 4;


    public LienzoArrow(ConcreteArrow elementToRepresent, LienzoDiagramCanvas canvas) {
        super(elementToRepresent, canvas);

        setLineColour(arrowColour);
        setFillColour(arrowColour);
        setMouseOverLineColour(arrowMouseOverColour);
        setMouseOverFillColour(arrowMouseOverColour);
        setSelectedLineColour(arrowSelectedColour);
        setSelectedFillColour(arrowSelectedColour);

        drawnControlPoints = new Rectangle[pointsInArrowLine + 2];
        for (int i = 0; i < pointsInArrowLine + 2; i++) {
            drawnControlPoints[i] = new Rectangle(dragBoxSize, dragBoxSize);
            drawnControlPoints[i].setStrokeColor(dragBoxColour);
            drawnControlPoints[i].setFillColor(dragBoxColour);
            drawnControlPoints[i].setDraggable(true);
        }
        makeRepresentation();
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getRepresentation() != null) {
            return getRepresentation().getBoundingBox();
        }
        return null;
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        // shouldn't be called for this - it's moved through the control points
    }


    private void makeRepresentation() {
        if (representation == null) {
            double[] xpoints = new double[pointsInArrowLine + 2];  // +2 for start and end points
            double[] ypoints = new double[pointsInArrowLine + 2];
            double xdiff = Math.max(getDiagramElement().getStartPoint().getX(), getDiagramElement().getEndPoint().getX()) - Math.min(getDiagramElement().getStartPoint().getX(), getDiagramElement().getEndPoint().getX());
            double ydiff = Math.max(getDiagramElement().getStartPoint().getY(), getDiagramElement().getEndPoint().getY()) - Math.min(getDiagramElement().getStartPoint().getY(), getDiagramElement().getEndPoint().getY());

            if (getDiagramElement().getSource().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE) {
                xpoints[0] = getDiagramElement().getSource().centre().getX();
                ypoints[0] = getDiagramElement().getSource().centre().getY();
            } else {
                xpoints[0] = getDiagramElement().getStartPoint().getX();
                ypoints[0] = getDiagramElement().getStartPoint().getY();
            }
            for (int i = 1; i <= pointsInArrowLine; i++) {
                if (getDiagramElement().getStartPoint().getX() < getDiagramElement().getEndPoint().getX()) {
                    xpoints[i] = getDiagramElement().getStartPoint().getX() + ((xdiff / (pointsInArrowLine + 1)) * i);
                } else {
                    xpoints[i] = getDiagramElement().getStartPoint().getX() - ((xdiff / (pointsInArrowLine + 1)) * i);
                }
                if (getDiagramElement().getStartPoint().getY() < getDiagramElement().getEndPoint().getY()) {
                    ypoints[i] = getDiagramElement().getStartPoint().getY() + ((ydiff / (pointsInArrowLine + 1)) * i);
                } else {
                    ypoints[i] = getDiagramElement().getStartPoint().getY() - ((ydiff / (pointsInArrowLine + 1)) * i);
                }
            }
            xpoints[pointsInArrowLine + 1] = getDiagramElement().getEndPoint().getX();
            ypoints[pointsInArrowLine + 1] = getDiagramElement().getEndPoint().getY();

            controlPoints = new Point2DArray(xpoints, ypoints);

            representation = new Spline(controlPoints);
        } else {
            if (getDiagramElement().getSource().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE) {
                controlPoints.set(0, new Point2D(getDiagramElement().getSource().centre().getX(), getDiagramElement().getSource().centre().getY()));
            } else {
                controlPoints.set(0, new Point2D(getDiagramElement().getStartPoint().getX(), getDiagramElement().getStartPoint().getY()));
            }
            controlPoints.set(controlPoints.size() - 1, new Point2D(getDiagramElement().getEndPoint().getX(), getDiagramElement().getEndPoint().getY()));
            representation.setControlPoints(controlPoints);
        }

        makeArrowHead();

        representation.setStrokeColor(getLineColour());
        representation.setStrokeWidth(arrowLineWidth);
        representation.setDraggable(false);

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    private void makeArrowHead() {
        // find a line between the last two points and use this to make the head.
        Point2D p1 = controlPoints.get(controlPoints.size() - 2);
        Point2D p2 = getDiagramElement().getEndPoint().asLienzoPoint2D();

        double theta1_m, theta2_m;

        // in radians
        double m = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        if (m != 0) {
            double theta = Math.atan(m);
            theta1_m = Math.tan(theta + headAngle + Math.PI);
            theta2_m = Math.tan((theta - headAngle) + Math.PI);
        } else {
            // vertical line
            if (p2.getY() > p1.getY()) {
                theta1_m = Math.tan(((3 / 4) * Math.PI) + headAngle);
                theta2_m = Math.tan(((3 / 4) * Math.PI) - headAngle);
            } else {
                theta1_m = Math.tan(((1 / 4) * Math.PI) - headAngle);
                theta2_m = Math.tan(((1 / 4) * Math.PI) + headAngle);
            }
        }

        double line1x;
        if (m > 0) {
            line1x = p2.getX() + (headSize / Math.sqrt(1 + (theta1_m * theta1_m)));
        } else {
            line1x = p2.getX() + (headSize / Math.sqrt(1 + (theta1_m * theta1_m)));
        }
        double line1y = (theta1_m * (line1x - p2.getX())) + p2.getY();


        double line2x;
        if (m > 0) {
            line2x = p2.getX() + (headSize / Math.sqrt(1 + (theta2_m * theta2_m)));
        } else {
            line2x = p2.getX() + (headSize / Math.sqrt(1 + (theta2_m * theta2_m)));
        }
        double line2y = (theta2_m * (line2x - p2.getX())) + p2.getY();

        if (arrowHead == null) {
            arrowHead = new Triangle(p2, new Point2D(line1x, line1y), new Point2D(line2x, line2y));
            arrowHead.setStrokeColor(getLineColour());
            arrowHead.setFillColor(getLineColour());
            arrowHead.setDraggable(false);
        } else {
            arrowHead.setPoints(p2, new Point2D(line1x, line1y), new Point2D(line2x, line2y));
        }


    }

    // FIXME the end points are different!!! they should be snapping to the other shapes on the canvas ...
    // FIXME ...should use some sort of command so that moves can be undone.
    private void drawDragRepresentation() {

        if (getRepresentation() != null & getLayer() != null) {
            Point2DArray controlPointsT = getRepresentation().getControlPoints();
            for(int i = 0; i < controlPointsT.size(); i++) {
                Point2D p = controlPointsT.get(i);
                getLayer().getViewport().getTransform().transform(p, p);
                controlPointsT.set(i, p);
            }
            if (rubberBandSpline == null) {
                rubberBandSpline = new Spline(controlPointsT);
            } else {
                rubberBandSpline.setControlPoints(controlPointsT);
            }
            rubberBandSpline.setStrokeColor(rubberBandColour);
            rubberBandSpline.setStrokeWidth(arrowLineWidth);
            rubberBandSpline.setDraggable(false);

            for (int i = 0; i < controlPoints.size(); i++) {
                Point2D p = controlPoints.get(i);
                drawnControlPoints[i].setX(p.getX() - (dragBoxSize / 2)).setY(p.getY() - (dragBoxSize / 2));
                getLayer().add(drawnControlPoints[i]);

                addHandlersToControlBoxes(i);
            }
        }
    }

    private void addHandlersToControlBoxes(final int i) {

        // no rubber band for select on the canvas ... probably bad way to handle this, the canvas should be in control
        drawnControlPoints[i].addNodeMouseDownHandler(new NodeMouseDownHandler() {
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                // FIXME ouch bad!!! changes the mode need a better way
                getCanvas().removeRubberBandRectangle();
                getCanvas().setMode(DiagramCanvas.ModeTypes.SELECTION);
                getLayer().getViewport().getDragLayer().add(rubberBandSpline);
            }
        });

        drawnControlPoints[i].addNodeDragEndHandler(new NodeDragEndHandler() {
            public void onNodeDragEnd(NodeDragEndEvent nodeDragEndEvent) {
                // FIXME : no should be the final point of the rectange that we care about!!
                Point2D mouseUp = new Point2D(nodeDragEndEvent.getX() + (dragBoxSize / 2), nodeDragEndEvent.getY() + (dragBoxSize / 2));
                getLayer().getViewport().getTransform().getInverse().transform(mouseUp, mouseUp);
                controlPoints.set(i, mouseUp);
                getRepresentation().setControlPoints(controlPoints);
                makeArrowHead();
                if (getLayer() != null && rubberBandSpline != null) {
                    getLayer().getViewport().getDragLayer().remove(rubberBandSpline);
                    getLayer().getViewport().getDragLayer().batch();
                }
                batch();
            }
        });

        drawnControlPoints[i].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D mousePos = new Point2D(nodeDragMoveEvent.getX() + (dragBoxSize / 2), nodeDragMoveEvent.getY() + (dragBoxSize / 2));
                getLayer().getViewport().getTransform().getInverse().transform(mousePos, mousePos);
                Point2DArray rubberBandControlPoints = rubberBandSpline.getControlPoints();
                // FIXME these need to be convereted to get right coords
                rubberBandControlPoints.set(i, mousePos);
                rubberBandSpline.setControlPoints(rubberBandControlPoints);
                getLayer().getViewport().getDragLayer().batch();
            }
        });
    }

    @Override
    public void redraw() {
        makeRepresentation();
    }

    // FIXME make group
    public void draw(Layer layer) {
        super.draw(layer);
        layer.add(arrowHead);
        layer.batch();
    }

    @Override
    public void setAsSelected() {
        drawDragRepresentation();

        getRepresentation().setStrokeColor(getSelectedLineColour());
        getRepresentation().setListening(false);
    }

    @Override
    public void setAsUnSelected() {
        if(getLayer() != null) {
            for (int i = 0; i < pointsInArrowLine + 2; i++) {
                if (drawnControlPoints[i] != null) {
                    getLayer().remove(drawnControlPoints[i]);
                }
            }

            if(rubberBandSpline != null) {
                getLayer().getViewport().getDragLayer().remove(rubberBandSpline);
            }
        }
        getRepresentation().setStrokeColor(getLineColour());
        getRepresentation().setListening(true);
    }


    public void drawRubberBand() {
        makedragRubberBand();
    }

}
