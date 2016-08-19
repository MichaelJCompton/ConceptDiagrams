package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class LienzoDragRubberBand extends LienzoDiagramShape<ConcreteDiagramElement, Shape> {


    private LienzoDiagramShape boundedShape;

    // just using representation instead
    //private Shape rubberband;           // drawn on drag layer - screen coords

    private Point2D unitTest;


    public LienzoDragRubberBand(LienzoDiagramCanvas canvas, LienzoDiagramShape shapeToBound) {
        super(canvas);
        boundedShape = shapeToBound;

        representation = new Rectangle(1, 1);
        //rubberband.setStrokeColor(rubberBandColour);
        representation.setStrokeColor(ColorName.RED);
        representation.setDraggable(false);
        representation.setListening(false);

        unitTest = new Point2D();
    }

    @Override
    public BoundingBox getBoundingBox() {
        // should not be called
        return boundedShape.getBoundingBox();
    }

    protected LienzoDiagramShape getBoundedShape() {
        return boundedShape;
    }

    protected double getWidth() {
        return ((Rectangle) representation).getWidth();
    }

    protected double getHeight() {
        return ((Rectangle) representation).getHeight();
    }

    // assume in screen coords
    // top left
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        setRubberband(newBoundingBox.getX(), newBoundingBox.getY(), newBoundingBox.getWidth(), newBoundingBox.getHeight());
    }

    private Shape getRubberband() {
        return getRepresentation();
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

        getRubberband().setStrokeWidth(unit.getX() * rubberbandLineWidth);

        boundedShape.getLayer().getViewport().getDragLayer().add(getRubberband());

        boundedShape.getLayer().getViewport().getDragLayer().batch();
    }


    // does a test for what the point (1,1) translates to in the current viewport
    private Point2D getUnitTest() {
        if (boundedShape != null && boundedShape.getLayer() != null) {
            Point2D unit = new Point2D(1, 1);
            boundedShape.getLayer().getViewport().getTransform().getInverse().transform(unit, unitTest);
        }
        return unitTest;
    }

    // these are top left coords
    protected void setRubberband(double x, double y, double width, double height) {
        ((Rectangle) getRepresentation()).setWidth(width);
        ((Rectangle) getRepresentation()).setHeight(height);
        getRepresentation().setX(x);
        getRepresentation().setY(y);
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



//    public void undraw() {
//        if (getLayer() != null) {
//            Layer rubberbandLayer = getLayer();
//            rubberbandLayer.remove(rubberband);
//            rubberbandLayer.batch();
//        }
//    }

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
