package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.ontologyengineering.conceptdiagrams.web.client.ui.DiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Draws a bounding box around the shape with small drag boxes at the corners.
 * <p/>
 * As the boxes are moved or the external bounding box is draged, it calls back to the internal shape. (at the moment
 * these call backs are largely ignored - one option is to redo the underlying concrete and abstract syntax on each drag
 * end; the option taken at the moment is to allow all the drags and move, but not change the real onscreen
 * representation or the underlying representations until the shape is un selected)
 */
public class LienzoDragBoundsBoxes extends LienzoDiagramShape<ConcreteDiagramElement, Node> {

    private LienzoDiagramShape boundedShape;

    private Rectangle rubberband;
    private Rectangle[] dragBoxes;
    private static final int topLeft = 0;   // corners
    private static final int topRight = 1;
    private static final int botRight = 2;
    private static final int botLeft = 3;
    private static final int top = 4;       // sides for selection boxes
    private static final int right = 5;
    private static final int bot = 6;
    private static final int left = 7;

    private static final double dragBoxSize = 6;

    private Point2D unitTest;


    // FIXME : this should probably catch zoom events and resize the lines etc apropriately
    // and maybe move things if they are on say drag layer

    public LienzoDragBoundsBoxes(LienzoDiagramCanvas canvas, LienzoDiagramShape shapeToBound) {
        super(canvas);
        boundedShape = shapeToBound;

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

    @Override
    public BoundingBox getBoundingBox() {
        // should not be called also not quite right because the drag boxes will extend beyond this.
        return boundedShape.getBoundingBox();
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }

    public void draw(Layer layer) {
        BoundingBox boundBox = boundedShape.getBoundingBox();
        Point2D unit = getUnitTest();

        Point2D widthHeight = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundBox.getWidth(), boundBox.getHeight()), widthHeight);

        Point2D xy = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundedShape.getDiagramElement().getX(), boundedShape.getDiagramElement().getY()), xy);

        redrawRubberband();

        boundedShape.getLayer().getViewport().getDragLayer().add(rubberband);

        redrawDragBoxes();
        for (int i = 0; i < 8; i++) {
            boundedShape.getLayer().add(dragBoxes[i]);
        }

        addZoomPanHandlers();

        batch();
    }


    // FIXME ... can't ever get this to register????
    private void addZoomPanHandlers() {
        if (getLayer() != null) {
            getLayer().getScene().getViewport().addViewportTransformChangedHandler(new ViewportTransformChangedHandler() {
                public void onViewportTransformChanged(ViewportTransformChangedEvent viewportTransformChangedEvent) {
                    redraw();
                }
            });
        }
    }

    // does a test for what the point (1,1) translates to in the current viewport
    private Point2D getUnitTest() {
        if (boundedShape != null && boundedShape.getLayer() != null) {
            Point2D unit = new Point2D(1, 1);
            boundedShape.getLayer().getViewport().getTransform().getInverse().transform(unit, unitTest);
        }
        return unitTest;
    }


    private void redrawRubberband() {
        BoundingBox boundBox = boundedShape.getBoundingBox();
        Point2D unit = getUnitTest();

        Point2D widthHeight = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundBox.getWidth(), boundBox.getHeight()), widthHeight);

        Point2D xy = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundedShape.getDiagramElement().getX(), boundedShape.getDiagramElement().getY()), xy);

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
            dragBoxes[i].setWidth(unit.getX() * dragBoxSize).
                    setHeight(unit.getX() * dragBoxSize);
        }
    }


    protected void batch() {
        if (getLayer() != null) {
            getLayer().batch();
        }
        if (dragBoxes[0] != null && dragBoxes[0].getLayer() != null) {
            dragBoxes[0].getLayer().batch();
        }
    }


    private void dragByDelta(double dx, double dy) {
        Point2D topLeft = new Point2D();  // of the on screen shape
        boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);

        double newX = topLeft.getX() + dx;
        double newY = topLeft.getY() + dy;
        double newWidth = rubberband.getWidth();
        double newHeight = rubberband.getHeight();

        setRubberband(newX, newY, newWidth, newHeight);

        redraw();
    }

    private void addHandlers() {

        // no rubber band for select on the canvas ... probably bad way to handle this, the canvas should be in control
        for (int i = 0; i < 8; i++) {
            dragBoxes[i].addNodeMouseDownHandler(new NodeMouseDownHandler() {
                public void onNodeMouseDown(NodeMouseDownEvent event) {
                    getCanvas().removeRubberBandRectangle();
                    getCanvas().setMode(DiagramCanvas.ModeTypes.SELECTION);
                }
            });
        }


        for (int i = 0; i < 8; i++) {
            dragBoxes[i].addNodeDragEndHandler(new NodeDragEndHandler() {
                public void onNodeDragEnd(NodeDragEndEvent nodeDragEndEvent) {
                    Point2D newTopLeft = new Point2D(rubberband.getX(), rubberband.getY());
                    Point2D newbotRight = new Point2D(rubberband.getX() + rubberband.getWidth(), rubberband.getY() + rubberband.getHeight());
                    boundedShape.getLayer().getViewport().getTransform().getInverse().transform(newTopLeft, newTopLeft);
                    boundedShape.getLayer().getViewport().getTransform().getInverse().transform(newbotRight, newbotRight);
                    boundedShape.dragBoundsMoved(new BoundingBox(newTopLeft, newbotRight));
                }
            });
        }


        // all these are screen coords ... we just care about the rubberband rectangle

        dragBoxes[topLeft].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D botRight = new Point2D();  // of the on screen shape
                BoundingBox bbox = boundedShape.getBoundingBox();
                boundedShape.getLayer().getViewport().getTransform().transform(
                        new Point2D(boundedShape.getDiagramElement().topLeft().getX() + bbox.getWidth(),
                                boundedShape.getDiagramElement().topLeft().getY() + bbox.getHeight()), botRight);

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

                redraw();
            }
        });


        dragBoxes[topRight].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);

                Point2D botRight = new Point2D();  // of the on screen shape
                BoundingBox bbox = boundedShape.getBoundingBox();
                boundedShape.getLayer().getViewport().getTransform().transform(
                        new Point2D(boundedShape.getDiagramElement().topLeft().getX() + bbox.getWidth(),
                                boundedShape.getDiagramElement().topLeft().getY() + bbox.getHeight()), botRight);

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

                redraw();
            }
        });

        dragBoxes[botLeft].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);

                Point2D botRight = new Point2D();  // of the on screen shape
                BoundingBox bbox = boundedShape.getBoundingBox();
                boundedShape.getLayer().getViewport().getTransform().transform(
                        new Point2D(boundedShape.getDiagramElement().topLeft().getX() + bbox.getWidth(),
                                boundedShape.getDiagramElement().topLeft().getY() + bbox.getHeight()), botRight);

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

                redraw();
            }
        });

        dragBoxes[botRight].addNodeDragMoveHandler(new NodeDragMoveHandler() {
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                Point2D topLeft = new Point2D();  // of the on screen shape
                boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);

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
        boundedShape.getLayer().getViewport().getTransform().getInverse().transform(new Point2D(rubberband.getX(), rubberband.getY()), topleftXY);
        boundedShape.getLayer().getViewport().getTransform().getInverse().transform(new Point2D(rubberband.getWidth(), rubberband.getHeight()), widthHeight);

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

    @Override
    public void drawDragRepresentation() {

    }

    @Override
    public void setAsSelected() {

    }

    public void undraw() {
        unDrawDragRepresentation();
    }

    @Override
    public void unDrawDragRepresentation() {
        if (getLayer() != null) {
            Layer rubberbandLayer = getLayer();
            rubberbandLayer.remove(rubberband);
            rubberbandLayer.batch();
        }

        if (dragBoxes[0] != null && dragBoxes[0].getLayer() != null) {
            Layer boxesLayer = dragBoxes[0].getLayer();
            for (Rectangle r : dragBoxes) {
                boxesLayer.remove(r);
            }
            boxesLayer.batch();
        }
    }

    @Override
    public void setAsUnSelected() {

    }


    public void addLabel(String labelText) {
        // no labels
    }
}


// old code for resize on those corner boxes
//
//dragBoxes[top].addNodeDragMoveHandler(new NodeDragMoveHandler() {
//public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
//        Point2D topLeft = new Point2D();  // of the on screen shape
//        boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);
//
//        Point2D botRight = new Point2D();  // of the on screen shape
//        BoundingBox bbox = boundedShape.getBoundingBox();
//        boundedShape.getLayer().getViewport().getTransform().transform(
//        new Point2D(boundedShape.getDiagramElement().topLeft().getX() + bbox.getWidth(),
//        boundedShape.getDiagramElement().topLeft().getY() + bbox.getHeight()), botRight);
//
//        double newX = rubberband.getX();
//        double newY = rubberband.getY();
//        double newWidth = rubberband.getWidth();
//        double newHeight = rubberband.getHeight();
//
//        double height = Math.max(nodeDragMoveEvent.getY(), botRight.getY()) - Math.min(nodeDragMoveEvent.getY(), botRight.getY());
//
//        if (height >= ConcreteDiagramElement.curveMinHeight) {
//        newHeight = height;
//        newY = Math.min(nodeDragMoveEvent.getY(), botRight.getY());
//        }
//
//        setRubberband(newX, newY, newWidth, newHeight);
//
//        redraw();
//        }
//        });
//
//        dragBoxes[right].addNodeDragMoveHandler(new NodeDragMoveHandler() {
//public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
//        Point2D topLeft = new Point2D();  // of the on screen shape
//        boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);
//
//        double newX = rubberband.getX();
//        double newY = rubberband.getY();
//        double newWidth = rubberband.getWidth();
//        double newHeight = rubberband.getHeight();
//
//        double width = Math.max(nodeDragMoveEvent.getX(), topLeft.getX()) - Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
//
//        if (width >= ConcreteDiagramElement.curveMinWidth) {
//        newWidth = width;
//        newX = Math.min(nodeDragMoveEvent.getX(), topLeft.getX());
//        }
//
//        setRubberband(newX, newY, newWidth, newHeight);
//
//        redraw();
//        }
//        });
//
//        dragBoxes[bot].addNodeDragMoveHandler(new NodeDragMoveHandler() {
//public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
//        Point2D topLeft = new Point2D();  // of the on screen shape
//        boundedShape.getLayer().getViewport().getTransform().transform(boundedShape.getDiagramElement().topLeft().asLienzoPoint2D(), topLeft);
//
//        double newX = rubberband.getX();
//        double newY = rubberband.getY();
//        double newWidth = rubberband.getWidth();
//        double newHeight = rubberband.getHeight();
//
//        double height = Math.max(nodeDragMoveEvent.getY(), topLeft.getY()) - Math.min(nodeDragMoveEvent.getY(), topLeft.getY());
//
//        if (height >= ConcreteDiagramElement.curveMinHeight) {
//        newHeight = height;
//        newY = Math.min(nodeDragMoveEvent.getY(), topLeft.getY());
//        }
//
//        setRubberband(newX, newY, newWidth, newHeight);
//
//        redraw();
//        }
//        });
//
//        dragBoxes[left].addNodeDragMoveHandler(new NodeDragMoveHandler() {
//public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
//
//        Point2D botRight = new Point2D();  // of the on screen shape
//        BoundingBox bbox = boundedShape.getBoundingBox();
//        boundedShape.getLayer().getViewport().getTransform().transform(
//        new Point2D(boundedShape.getDiagramElement().topLeft().getX() + bbox.getWidth(),
//        boundedShape.getDiagramElement().topLeft().getY() + bbox.getHeight()), botRight);
//
//        double newX = rubberband.getX();
//        double newY = rubberband.getY();
//        double newWidth = rubberband.getWidth();
//        double newHeight = rubberband.getHeight();
//
//        double width = Math.max(nodeDragMoveEvent.getX(), botRight.getX()) - Math.min(nodeDragMoveEvent.getX(), botRight.getX());
//
//        if (width >= ConcreteDiagramElement.curveMinWidth) {
//        newWidth = width;
//        newX = Math.min(nodeDragMoveEvent.getX(), botRight.getX());
//        }
//
//        setRubberband(newX, newY, newWidth, newHeight);
//
//        redraw();
//        }
//        });