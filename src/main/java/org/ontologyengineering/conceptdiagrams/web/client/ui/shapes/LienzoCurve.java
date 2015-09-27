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
        // FIXME : need some more hooks here ... if the curve is selected we drag the bounds box.  Also
        // ignore the drage select in the canvas if there is a selected entity under the mouse ... just move it.
        // then we'll have the recieve pan and zoom events to redraw all the drag bounds ... hmmm
        //
        // boxes on curve/br layer, rubber band on the drag layer //
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        CommandManager.get().executeCommand(new ResizeCommand(getDiagramElement(), new Point(newBoundingBox.getX(), newBoundingBox.getY()),
                new Point(newBoundingBox.getX() + newBoundingBox.getWidth(), newBoundingBox.getY() + newBoundingBox.getHeight())));
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
