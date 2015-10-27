package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.CommandManager;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.ResizeCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 *
 */
public class LienzoCurve extends LienzoDiagramShape<ConcreteCurve, OrthogonalPolyLine> {

    private static final int topLeft = 0;   // corners
    private static final int topRight = 1;
    private static final int botRight = 2;
    private static final int botLeft = 3;

    public LienzoCurve(ConcreteCurve curve, LienzoDiagramCanvas canvas) {
        super(curve, canvas);

        setLineColour(curveBorderColour);
        setFillColour(curveFillColour);
        setMouseOverLineColour(curveBorderMouseOverColor);
        setMouseOverFillColour(curveMouseOverFillColour);
        setSelectedLineColour(curveBorderSelectedColor);
        setSelectedFillColour(curveSelectedFillColour);

        makeRepresentation();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return getRepresentation().getBoundingBox();
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        double deltaX = newBoundingBox.getX() - getDiagramElement().getX();
        double deltaY = newBoundingBox.getY() - getDiagramElement().getY();
        //double changeRatioX = newBoundingBox.getWidth() / getDiagramElement().getWidth();
        //double changeRatioY = newBoundingBox.getHeight() / getDiagramElement().getHeight();

        CommandManager.get().executeCommand(new ResizeCommand(getDiagramElement(), new Point(newBoundingBox.getX(), newBoundingBox.getY()),
                new Point(newBoundingBox.getX() + newBoundingBox.getWidth(), newBoundingBox.getY() + newBoundingBox.getHeight())));

        // FIXME ... does this mean label locations should be in the concrete elements???
        if(hasLabel()) {
            getLabel().getRepresentation().setX(getLabel().getRepresentation().getX() + (deltaX));// * changeRatioX));
            getLabel().getRepresentation().setY(getLabel().getRepresentation().getY() + (deltaY));// * changeRatioY));
        }
    }


    private void makeRepresentation() {
        Point2DArray points = new Point2DArray(
                getDiagramElement().topLeft().asLienzoPoint2D(),
                getDiagramElement().topRight().asLienzoPoint2D(),
                getDiagramElement().bottomRight().asLienzoPoint2D(),
                getDiagramElement().bottomLeft().asLienzoPoint2D(),
                getDiagramElement().topLeft().asLienzoPoint2D());

        representation = new OrthogonalPolyLine(points);
        representation.setX(0);
        representation.setY(0);

        representation.setStrokeColor(getLineColour());
        representation.setDraggable(false);

        representation.setCornerRadius(getDiagramElement().getCornerRadius());
        representation.setStrokeWidth(getDiagramElement().getBorderWidth());

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    @Override
    public void redraw() {

        Point2DArray points = new Point2DArray(
                getDiagramElement().topLeft().asLienzoPoint2D(),
                getDiagramElement().topRight().asLienzoPoint2D(),
                getDiagramElement().bottomRight().asLienzoPoint2D(),
                getDiagramElement().bottomLeft().asLienzoPoint2D(),
                getDiagramElement().topLeft().asLienzoPoint2D());

        representation.setControlPoints(points);

        getLayer().batch();
    }


    public void setAsSelected() {
        if (getRepresentation() != null && getLayer() != null) {
            setLineColour(getSelectedLineColour());
            getRepresentation().setStrokeColor(getSelectedLineColour());
            getLayer().batch();
        }
    }


    public void setAsUnSelected() {
        if(getRepresentation() != null && getLayer() != null) {
            setLineColour(curveBorderColour);
            getRepresentation().setStrokeColor(getLineColour());
            getLayer().batch();
        }
    }
}
