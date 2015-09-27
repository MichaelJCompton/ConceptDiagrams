package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;

/**
 * The space inside the boundary rectangle
 */
public class LienzoBoundaryRectangleZone extends LienzoDiagramShape<ConcreteBoundaryRectangle, Rectangle> {

    public LienzoBoundaryRectangleZone(ConcreteBoundaryRectangle elementToRepresent, LienzoDiagramCanvas canvas) {
        super(elementToRepresent, canvas);

        setLineColour(zoneStandardColour);
        setFillColour(zoneStandardColour);
        setMouseOverLineColour(zoneMouseOverColor);
        setMouseOverFillColour(zoneMouseOverColor);
        setSelectedLineColour(zoneSelectedColor);
        setSelectedFillColour(zoneSelectedColor);

        makeRepresentation();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }


    private void makeRepresentation() {
        representation = new Rectangle(getDiagramElement().getWidth() - (2 * getDiagramElement().getBorderWidth()),
                getDiagramElement().getHeight() - (2 * getDiagramElement().getBorderWidth()));
        representation.setX(getDiagramElement().getX() + getDiagramElement().getBorderWidth());
        representation.setY(getDiagramElement().getY() + getDiagramElement().getBorderWidth());
        representation.setFillColor(getFillColour());
        representation.setStrokeColor(getLineColour());
        representation.setDraggable(false);

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }

    public void shade() {
        setFillColour(zoneShadedColor);
        setLineColour(zoneShadedColor);
    }

    public void unShade() {
        setFillColour(zoneStandardColour);
        setLineColour(zoneStandardColour);
    }


    @Override
    public void redraw() {

    }

    @Override
    public void setAsSelected() {

    }

    @Override
    public void setAsUnSelected() {

    }
}
