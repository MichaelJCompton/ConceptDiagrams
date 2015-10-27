package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import org.ontologyengineering.conceptdiagrams.web.client.ui.DiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class LienzoDragRubberBand extends LienzoDiagramShape<ConcreteDiagramElement, Rectangle> {


    private LienzoDiagramShape boundedShape;

    private Rectangle rubberband;           // drawn on drag layer - screen coords

    private Point2D unitTest;


    // FIXME : this should probably catch zoom events and resize the lines etc apropriately
    // and maybe move things if they are on say drag layer

    public LienzoDragRubberBand(LienzoDiagramCanvas canvas, LienzoDiagramShape shapeToBound) {
        super(canvas);
        boundedShape = shapeToBound;

        rubberband = new Rectangle(1, 1);
        //rubberband.setStrokeColor(rubberBandColour);
        rubberband.setStrokeColor(ColorName.RED);
        rubberband.setDraggable(false);
        rubberband.setListening(false);

        representation = rubberband;

        unitTest = new Point2D();
    }

    @Override
    public BoundingBox getBoundingBox() {
        // should not be called
        return boundedShape.getBoundingBox();
    }


    // assume in screen coords
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        setRubberband(newBoundingBox.getX(), newBoundingBox.getY(), newBoundingBox.getWidth(), newBoundingBox.getHeight());
    }


    // draw from the bounded shape
    public void draw(Layer layer) {
        BoundingBox boundBox = boundedShape.getBoundingBox();
        Point2D unit = getUnitTest();

        Point2D widthHeight = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundBox.getWidth(), boundBox.getHeight()), widthHeight);

        Point2D xy = new Point2D();
        boundedShape.getLayer().getViewport().getTransform().transform(new Point2D(boundedShape.getDiagramElement().getX(), boundedShape.getDiagramElement().getY()), xy);

        setRubberband(xy.getX(), xy.getY(), widthHeight.getX(), widthHeight.getY());

        rubberband.setStrokeWidth(unit.getX() * rubberbandLineWidth);

        boundedShape.getLayer().getViewport().getDragLayer().add(rubberband);

        batch();
    }


    // does a test for what the point (1,1) translates to in the current viewport
    private Point2D getUnitTest() {
        if (boundedShape != null && boundedShape.getLayer() != null) {
            Point2D unit = new Point2D(1, 1);
            boundedShape.getLayer().getViewport().getTransform().getInverse().transform(unit, unitTest);
        }
        return unitTest;
    }

    private void setRubberband(double x, double y, double width, double height) {
        rubberband.setWidth(width);
        rubberband.setHeight(height);
        rubberband.setX(x);
        rubberband.setY(y);
    }

    @Override
    public void redraw() {
        //redrawRubberband();
        batch();
    }

    protected void batch() {
        if (getLayer() != null) {
            getLayer().batch();
        }
    }



    public void undraw() {
        if (getLayer() != null) {
            Layer rubberbandLayer = getLayer();
            rubberbandLayer.remove(rubberband);
            rubberbandLayer.batch();
        }
    }

    @Override
    public void setAsUnSelected() {

    }

    @Override
    public void setAsSelected() {

    }

    public void addLabel(String labelText) {
        // no labels
    }

}
