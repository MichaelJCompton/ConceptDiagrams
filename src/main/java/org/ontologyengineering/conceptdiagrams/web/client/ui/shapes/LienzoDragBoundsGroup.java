package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.ontologyengineering.conceptdiagrams.web.shared.presenter.DiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */
public class LienzoDragBoundsGroup extends LienzoDiagramShape<ConcreteDiagramElement, Node> {

    private AbstractSet<LienzoDiagramShape> boundedElements;
    private Point2D topLeftP, bottomRightP;     // canvas coords

    private Rectangle rubberband;               // drawn on drag layer - screen coords
    private Rectangle[] dragBoxes;
    private static final int topLeft = 0;   // corners
    private static final int topRight = 1;
    private static final int botRight = 2;
    private static final int botLeft = 3;
    private static final int top = 4;       // sides for selection boxes
    private static final int right = 5;
    private static final int bot = 6;
    private static final int left = 7;

    private Point2D unitTest;                   // canvas unit

    public LienzoDragBoundsGroup(LienzoDiagramCanvas canvas) {
        super(canvas);
        boundedElements = new HashSet<LienzoDiagramShape>();
        topLeftP = new Point2D(0,0);
        bottomRightP = new Point2D(0,0);


        rubberband = new Rectangle(1, 1);
        rubberband.setStrokeColor(rubberBandColour);
        rubberband.setDraggable(false);
        rubberband.setListening(false);

        representation = rubberband;

        dragBoxes = new Rectangle[8];
        for (int i = 0; i < 8; i++) {
            dragBoxes[i] = new Rectangle(dragBoxSize, dragBoxSize);
            dragBoxes[i].setStrokeColor(dragBoxColour);
            dragBoxes[i].setFillColor(dragBoxColour);
            dragBoxes[i].setDraggable(true);
        }
        addHandlers();

        unitTest = new Point2D();
    }

    public void addElement(LienzoDiagramShape element) {
        boundedElements.add(element);

        element.setAsSelected();
        element.drawRubberBand();

        if(element.getDiagramElement().getType() != ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEARROW) {
            if (boundedElements.size() == 1) {
                topLeftP.setX(element.getDiagramElement().topLeft().getX());
                topLeftP.setY(element.getDiagramElement().topLeft().getY());
                bottomRightP.setX(element.getDiagramElement().bottomRight().getX());
                bottomRightP.setY(element.getDiagramElement().bottomRight().getY());
            } else {
                // see if the new element extends the bounds in any way
                if (element.getDiagramElement().topLeft().getX() < topLeftP.getX()) {
                    topLeftP.setX(element.getDiagramElement().topLeft().getX());
                }
                if (element.getDiagramElement().topLeft().getY() < topLeftP.getY()) {
                    topLeftP.setY(element.getDiagramElement().topLeft().getY());
                }
                if (element.getDiagramElement().bottomRight().getX() > bottomRightP.getX()) {
                    bottomRightP.setX(element.getDiagramElement().bottomRight().getX());
                }
                if (element.getDiagramElement().bottomRight().getY() > bottomRightP.getY()) {
                    bottomRightP.setY(element.getDiagramElement().bottomRight().getY());
                }
            }
        }
    }

    public void clearAndUndraw() {
        undraw();

        for(LienzoDiagramShape s : boundedElements) {
            s.setAsUnSelected();
            s.getDragRubberBand().undraw();
        }

        boundedElements.clear();
    }

    public Point2D topRight() {
        return new Point2D(bottomRightP.getX(), topLeftP.getY());
    }

    public Point2D bottomLeft() {
        return new Point2D(topLeftP.getX(), bottomRightP.getY());
    }

    // canvas width
    public double getWidth() {
        return bottomRightP.getX() - topLeftP.getX();
    }

    // canvas height
    public double getHeight() {
        return bottomRightP.getY() - topLeftP.getY();
    }

    public void removeElement(LienzoDiagramShape element) {
        if(boundedElements.contains(element)) {
            boundedElements.remove(element);
            element.setAsUnSelected();
            element.getDragRubberBand().undraw();

            if(boundedElements.size() == 0) {
                topLeftP.setX(0);
                topLeftP.setY(0);
                bottomRightP.setX(0);
                bottomRightP.setY(0);
            } else {
                double minX = bottomRightP.getX();
                double minY = bottomRightP.getY();
                double maxX = topLeftP.getX();
                double maxY = topLeftP.getY();

                for(LienzoDiagramShape e : boundedElements) {
                    if(e.getDiagramElement().topLeft().getX() < minX) {
                        minX = e.getDiagramElement().topLeft().getX();
                    }
                    if(e.getDiagramElement().topLeft().getY() < minY) {
                        minY = e.getDiagramElement().topLeft().getY();
                    }
                    if(e.getDiagramElement().bottomRight().getX() > maxX) {
                        maxY = e.getDiagramElement().bottomRight().getX();
                    }
                    if(e.getDiagramElement().bottomRight().getY() > maxY) {
                        maxY = e.getDiagramElement().bottomRight().getY();
                    }
                }

                topLeftP.setX(minX);
                topLeftP.setY(minY);
                bottomRightP.setX(maxX);
                bottomRightP.setY(maxY);
            }
        }

    }

    public Layer getBoxLayer() {
        if(boundedElements.size() != 0) {
            return ((LienzoDiagramShape) boundedElements.toArray()[0]).getLayer();
        }
        return null;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }

    public void draw(Layer layer) {
        if(boundedElements.size() > 0) {
            if(boundedElements.size() == 1 &&
                    (((LienzoDiagramShape) boundedElements.toArray()[0]).getDiagramElement().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER ||
                            ((LienzoDiagramShape) boundedElements.toArray()[0]).getDiagramElement().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEARROW)) {
                // don't draw things if it's a single spider or an arrow
                // FIXME needs to be made cleaner ... why special cases
            } else {
                Point2D unit = getUnitTest();

                Point2D widthHeight = new Point2D();
                getBoxLayer().getViewport().getTransform().transform(new Point2D(getWidth(), getHeight()), widthHeight);

                Point2D xy = new Point2D();
                getBoxLayer().getViewport().getTransform().transform(new Point2D(topLeftP.getX(), topLeftP.getY()), xy);

                makeRubberband();

                getBoxLayer().getViewport().getDragLayer().add(rubberband);

                redrawDragBoxes();

                for (int i = 0; i < 8; i++) {
                    getBoxLayer().add(dragBoxes[i]);
                }
            }
            //addZoomPanHandlers();
        }
        batch();
    }


    // FIXME ... can't ever get this to register????
//    private void addZoomPanHandlers() {
//        if (getLayer() != null) {
//            getLayer().getScene().getViewport().addViewportTransformChangedHandler(new ViewportTransformChangedHandler() {
//                public void onViewportTransformChanged(ViewportTransformChangedEvent viewportTransformChangedEvent) {
//                    redraw();
//                }
//            });
//        }
//    }


    @Override
    public void setAsSelected() {

    }

    @Override
    public void setAsUnSelected() {

    }


    // from the new location of the rubber band (Vs the top left and bot right of the bounds)
    private void notifyBoundedRubberBands() {
        Point2D topLeft = new Point2D();  // of the bounds as screen coords
        getBoxLayer().getViewport().getTransform().transform(topLeftP, topLeft);

        Point2D botRight = new Point2D();  // of the bounds as screen coords
        getBoxLayer().getViewport().getTransform().transform(bottomRightP, botRight);

        double widthChange;// = 1;
        double heightChange;// = 1;
//        if(rubberband.getWidth() == (botRight.getX() - topLeft.getX()) &&
//                rubberband.getHeight() == (botRight.getY() - topLeft.getY())) {
//            // MOVE ... nothing to do
//        } else {
//            // RESIZE

            // Calculate the change ratio and adjust each element by that
            widthChange = rubberband.getWidth() / (botRight.getX() - topLeft.getX());
            heightChange = rubberband.getHeight() / (botRight.getY() - topLeft.getY());
       // }

        for(LienzoDiagramShape s : boundedElements) {
            // placement of the drag rectangle has to be in screen coords
            Point2D shapeTL = new Point2D();  // of the bounds as screen coords
            getBoxLayer().getViewport().getTransform().transform(s.getDiagramElement().topLeft().asLienzoPoint2D(), shapeTL);
            Point2D shapeBR = new Point2D();  // of the bounds as screen coords
            getBoxLayer().getViewport().getTransform().transform(s.getDiagramElement().bottomRight().asLienzoPoint2D(), shapeBR);

            double newWidth = (shapeBR.getX() - shapeTL.getX()) * widthChange;
            double newHeight = (shapeBR.getY() - shapeTL.getY()) * heightChange;

            // calculate the old top left distance and set the new one based on the percentage change
            double xoffset = shapeTL.getX() - topLeft.getX();
            double yoffset = shapeTL.getY() - topLeft.getY();

            // but the placement has to be relative to the drag rectangle
            Point2D topL = new Point2D(rubberband.getX() + (xoffset * widthChange), rubberband.getY() + (yoffset * heightChange));
            Point2D botR = new Point2D(topL.getX() + newWidth, topL.getY() + newHeight);

            s.getDragRubberBand().dragBoundsMoved(new BoundingBox(topL, botR));
        }
    }

    // assume we've been setting their rubber bands
    private void notifyBoundedShapes() {
        for(LienzoDiagramShape s : boundedElements) {
            Point2D shapeTL = new Point2D();
            getBoxLayer().getViewport().getTransform().getInverse().transform(s.getDragRubberBand().getRepresentation().getLocation(), shapeTL);
            Point2D shapeBR = new Point2D();
//            getBoxLayer().getViewport().getTransform().getInverse().transform(new Point2D(s.getDragRubberBand().getRepresentation().getX() + s.getDragRubberBand().getRepresentation().getWidth(),
//                    s.getDragRubberBand().getRepresentation().getY() + s.getDragRubberBand().getRepresentation().getHeight()), shapeBR);

            getBoxLayer().getViewport().getTransform().getInverse().transform(
                    new Point2D(s.getDragRubberBand().getRepresentation().getX() + s.getDragRubberBand().getWidth(),
                            s.getDragRubberBand().getRepresentation().getY() + s.getDragRubberBand().getHeight()), shapeBR);


            s.dragBoundsMoved(new BoundingBox(shapeTL, shapeBR));
        }
    }

    private void addHandlers() {

        // no rubber band for select on the canvas ... probably bad way to handle this, the canvas should be in control
        for (int i = 0; i < 8; i++) {
            dragBoxes[i].addNodeMouseDownHandler(new NodeMouseDownHandler() {
                public void onNodeMouseDown(NodeMouseDownEvent event) {



                    // FIXME !!!!
                    //getCanvas().removeRubberBandRectangle();




                    //getCanvas().setMode(DiagramCanvas.ModeTypes.SELECTION);
                    getCanvas().turnOffDragSelect();
                }
            });
        }


        for (int i = 0; i < 8; i++) {
            dragBoxes[i].addNodeDragEndHandler(new NodeDragEndHandler() {
                public void onNodeDragEnd(NodeDragEndEvent nodeDragEndEvent) {
                    Point2D newTopLeft = new Point2D(rubberband.getX(), rubberband.getY());
                    Point2D newbotRight = new Point2D(rubberband.getX() + rubberband.getWidth(), rubberband.getY() + rubberband.getHeight());
                    getBoxLayer().getViewport().getTransform().getInverse().transform(newTopLeft, topLeftP);
                    getBoxLayer().getViewport().getTransform().getInverse().transform(newbotRight, bottomRightP);
                    notifyBoundedShapes(); //newTopLeft, newbotRight);
                    //boundedShape.dragBoundsMoved(new BoundingBox(newTopLeft, newbotRight));

                    // not sure if this is necessary ... shouldn't be, but there might a race condition between the
                    // mouse down message above and the one in diagram canvas, so just to make sure, I'm ensuring the
                    // state I want at the end of the move too.
                    getCanvas().turnOffDragSelect();
                }
            });
        }


        // all these are screen coords ... we just care about the rubberband rectangle

        dragBoxes[topLeft].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D botRight = new Point2D();  // of the on screen shape

                getBoxLayer().getViewport().getTransform().transform(bottomRightP, botRight);

                double newX = rubberband.getX();
                double newY = rubberband.getY();
                double newWidth = rubberband.getWidth();
                double newHeight = rubberband.getHeight();

                double width = Math.max(nodeDragMoveEvent.getX(), botRight.getX()) - Math.min(nodeDragMoveEvent.getX(), botRight.getX());
                double height = Math.max(nodeDragMoveEvent.getY(), botRight.getY()) - Math.min(nodeDragMoveEvent.getY(), botRight.getY());

                if (width >= ConcreteDiagramElement.curveMinWidth) {
                    newWidth = width;
                    newX = Math.min(nodeDragMoveEvent.getX(), botRight.getX());
                }

                if (height >= ConcreteDiagramElement.curveMinHeight) {
                    newHeight = height;
                    newY = Math.min(nodeDragMoveEvent.getY(), botRight.getY());
                }

                setRubberband(newX, newY, newWidth, newHeight);
                notifyBoundedRubberBands();
                redraw();
            }
        });


        dragBoxes[topRight].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                getBoxLayer().getViewport().getTransform().transform(topLeftP, topLeft);

                Point2D botRight = new Point2D();  // of the on screen shape
                getBoxLayer().getViewport().getTransform().transform(bottomRightP, botRight);

                double newX = rubberband.getX();
                double newY = rubberband.getY();
                double newWidth = rubberband.getWidth();
                double newHeight = rubberband.getHeight();

                double width = Math.max(nodeDragMoveEvent.getX(), topLeft.getX()) - Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
                double height = Math.max(nodeDragMoveEvent.getY(), botRight.getY()) - Math.min(nodeDragMoveEvent.getY(), botRight.getY());

                if (width >= ConcreteDiagramElement.curveMinWidth) {
                    newWidth = width;
                    newX = Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
                }

                if (height >= ConcreteDiagramElement.curveMinHeight) {
                    newHeight = height;
                    newY = Math.min(nodeDragMoveEvent.getY(), botRight.getY());
                }

                setRubberband(newX, newY, newWidth, newHeight);
                notifyBoundedRubberBands();
                redraw();
            }
        });

        dragBoxes[botLeft].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                getBoxLayer().getViewport().getTransform().transform(topLeftP, topLeft);

                Point2D botRight = new Point2D();  // of the on screen shape
                getBoxLayer().getViewport().getTransform().transform(bottomRightP, botRight);

                double newX = rubberband.getX();
                double newY = rubberband.getY();
                double newWidth = rubberband.getWidth();
                double newHeight = rubberband.getHeight();

                double width = Math.max(nodeDragMoveEvent.getX(), botRight.getX()) - Math.min(nodeDragMoveEvent.getX(), botRight.getX());
                double height = Math.max(nodeDragMoveEvent.getY(), topLeft.getY()) - Math.min(nodeDragMoveEvent.getY(), topLeft.getY());

                if (width >= ConcreteDiagramElement.curveMinWidth) {
                    newWidth = width;
                    newX = Math.min(nodeDragMoveEvent.getX(), botRight.getX());
                }

                if (height >= ConcreteDiagramElement.curveMinHeight) {
                    newHeight = height;
                    newY = Math.min(nodeDragMoveEvent.getY(), topLeft.getY());
                }

                setRubberband(newX, newY, newWidth, newHeight);
                notifyBoundedRubberBands();
                redraw();
            }
        });

        dragBoxes[botRight].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                getBoxLayer().getViewport().getTransform().transform(topLeftP, topLeft);

                double newX = rubberband.getX();
                double newY = rubberband.getY();
                double newWidth = rubberband.getWidth();
                double newHeight = rubberband.getHeight();

                double width = Math.max(nodeDragMoveEvent.getX(), topLeft.getX()) - Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
                double height = Math.max(nodeDragMoveEvent.getY(), topLeft.getY()) - Math.min(nodeDragMoveEvent.getY(), topLeft.getY());

                if (width >= ConcreteDiagramElement.curveMinWidth) {
                    newWidth = width;
                    newX = Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
                }

                if (height >= ConcreteDiagramElement.curveMinHeight) {
                    newHeight = height;
                    newY = Math.min(nodeDragMoveEvent.getY(), topLeft.getY());
                }

                setRubberband(newX, newY, newWidth, newHeight);
                notifyBoundedRubberBands();
                redraw();
            }
        });

        dragBoxes[top].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                dragByDelta(nodeDragMoveEvent.getDragContext().getDx(), nodeDragMoveEvent.getDragContext().getDy());
            }
        });

        dragBoxes[right].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                dragByDelta(nodeDragMoveEvent.getDragContext().getDx(), nodeDragMoveEvent.getDragContext().getDy());
            }
        });

        dragBoxes[bot].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                dragByDelta(nodeDragMoveEvent.getDragContext().getDx(), nodeDragMoveEvent.getDragContext().getDy());
            }
        });

        dragBoxes[left].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                dragByDelta(nodeDragMoveEvent.getDragContext().getDx(), nodeDragMoveEvent.getDragContext().getDy());
            }
        });
    }

    // all relative to the rubber band, not the original shape
    private void redrawDragBoxes() {

        // hmmm don't quite understand bounding boxes ... sometimes they seem to be local, othertimes global?
        // BoundingBox bbox = rubberband.getBoundingBox();


        Point2D topleftXY = new Point2D();
        Point2D widthHeight = new Point2D();
        getBoxLayer().getViewport().getTransform().getInverse().transform(new Point2D(rubberband.getX(), rubberband.getY()), topleftXY);
        getBoxLayer().getViewport().getTransform().getInverse().transform(new Point2D(rubberband.getWidth(), rubberband.getHeight()), widthHeight);

        Point2D unit = getUnitTest();       // no need to keep calculating


        // FIXME should be transforms for this right??  ok as is - cause we have done the translation to get a unit size?
        double dboxLeftX = topleftXY.getX() - (unit.getX() * dragBoxSize);
        double dboxTopY = topleftXY.getY() - (unit.getX() * dragBoxSize);
        double dboxCentreX = topleftXY.getX() + ((widthHeight.getX() / 2) - (unit.getX() * dragBoxSize / 2));
        double dboxCentreY = topleftXY.getY() + ((widthHeight.getY() / 2) - (unit.getX() * dragBoxSize / 2));

        setDragBoxSizes(unit);

        // FIXME need to set the drag bounds

        dragBoxes[topLeft].setX(dboxLeftX);
        dragBoxes[topLeft].setY(dboxTopY);

        dragBoxes[topRight].setX((topleftXY.getX() + widthHeight.getX()));
        dragBoxes[topRight].setY(dboxTopY);

        dragBoxes[botRight].setX(topleftXY.getX() + widthHeight.getX());
        dragBoxes[botRight].setY(topleftXY.getY() + widthHeight.getY());

        dragBoxes[botLeft].setX(dboxLeftX);
        dragBoxes[botLeft].setY(topleftXY.getY() + widthHeight.getY());


        dragBoxes[top].setX(dboxCentreX);
        dragBoxes[top].setY(dboxTopY);

        dragBoxes[right].setX((topleftXY.getX() + widthHeight.getX()));
        dragBoxes[right].setY(dboxCentreY);

        dragBoxes[bot].setX(dboxCentreX);
        dragBoxes[bot].setY(topleftXY.getY() + widthHeight.getY());

        dragBoxes[left].setX(dboxLeftX);
        dragBoxes[left].setY(dboxCentreY);


    }


    // does a test for what the point (1,1) translates to in the current viewport
    private Point2D getUnitTest() {
        if (getBoxLayer() != null) {
            Point2D unit = new Point2D(1, 1);
            getBoxLayer().getViewport().getTransform().getInverse().transform(unit, unitTest);
        }
        return unitTest;
    }


    private void makeRubberband() {
        Point2D unit = getUnitTest();

        Point2D widthHeight = new Point2D();
        getBoxLayer().getViewport().getTransform().transform(new Point2D(getWidth(), getHeight()), widthHeight);

        Point2D xy = new Point2D();
        getBoxLayer().getViewport().getTransform().transform(new Point2D(topLeftP.getX(), topLeftP.getY()), xy);

        setRubberband(xy.getX(), xy.getY(), widthHeight.getX(), widthHeight.getY());

        rubberband.setStrokeWidth(unit.getX() * rubberbandLineWidth);
    }

    private void setRubberband(double x, double y, double width, double height) {
        rubberband.setWidth(width);
        rubberband.setHeight(height);
        rubberband.setX(x);
        rubberband.setY(y);
    }

    @Override
    public void redraw() {
        redrawDragBoxes();
        //redrawRubberband();
        batch();
    }

    private void setDragBoxSizes(Point2D unit) {
        for (int i = 0; i < 8; i++) {
            dragBoxes[i].setWidth(unit.getX() * dragBoxSize).setHeight(unit.getX() * dragBoxSize);
        }
    }


    protected void batch() {
        if (getLayer() != null) {
            getLayer().batch();
        }
        if (getBoxLayer() != null) {
            getBoxLayer().batch();
        }
    }


    // screen deltas
    private void dragByDelta(double dx, double dy) {
        Point2D topLeft = new Point2D();  // of the group as screen coords
        getBoxLayer().getViewport().getTransform().transform(topLeftP, topLeft);

        setRubberband(topLeft.getX() + dx, topLeft.getY() + dy, rubberband.getWidth(), rubberband.getHeight());
        notifyBoundedRubberBands();
        redraw();
    }


    public void undraw() {
        if (getLayer() != null) {
            Layer rubberbandLayer = getLayer();
            rubberbandLayer.remove(rubberband);
            rubberbandLayer.batch();
        }

        if (getBoxLayer() != null) {
            Layer boxesLayer = getBoxLayer();
            for (Rectangle r : dragBoxes) {
                boxesLayer.remove(r);
            }
            boxesLayer.batch();
        }
    }


    public void addLabel(String labelText) {
        // no labels
    }
}
