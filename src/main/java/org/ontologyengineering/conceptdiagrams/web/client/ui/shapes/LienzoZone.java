package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

/**
 *
 */
public class LienzoZone extends LienzoDiagramShape<ConcreteZone, Rectangle> {

    public LienzoZone(ConcreteZone elementToRepresent, LienzoDiagramCanvas canvas) {
        super(elementToRepresent, canvas);

        setLineColour(zoneStandardColour);
        setFillColour(zoneStandardColour);
        setMouseOverLineColour(zoneMouseOverColor);
        setMouseOverFillColour(zoneMouseOverColor);
        setSelectedLineColour(zoneSelectedColor);
        setSelectedFillColour(zoneSelectedColor);

        if(elementToRepresent.shaded()) {
            shade();
        }

        makeRepresentation();
    }

    @Override
    public BoundingBox getBoundingBox() {
        if(getRepresentation() != null) {
            return getRepresentation().getBoundingBox();
        }
        return null;
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }


    private void makeRepresentation() {
        representation = new Rectangle(getDiagramElement().getWidth(), getDiagramElement().getHeight(), getDiagramElement().getCornerRadius());
        representation.setX(getDiagramElement().getX());
        representation.setY(getDiagramElement().getY());
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
        getRepresentation().setWidth(getDiagramElement().getWidth());
        getRepresentation().setHeight(getDiagramElement().getHeight());
        getRepresentation().setX(getDiagramElement().getX());
        getRepresentation().setY(getDiagramElement().getY());
    }


    public void setAsSelected() {
        getRepresentation().setStrokeColor(getSelectedLineColour());
        getRepresentation().setFillColor(getSelectedFillColour());
        getRepresentation().setListening(false);        // don't do mouse overs while selected
    }


    public void setAsUnSelected() {
        getRepresentation().setStrokeColor(getLineColour());
        getRepresentation().setFillColor(getLineColour());
        getRepresentation().setListening(true);
    }



    public void addLabel(String labelText) {
        // no labels
    }

}
