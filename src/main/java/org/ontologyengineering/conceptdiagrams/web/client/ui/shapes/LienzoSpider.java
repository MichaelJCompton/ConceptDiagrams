package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;


public class LienzoSpider extends LienzoDiagramShape<ConcreteSpider, Circle> {

    public LienzoSpider(ConcreteSpider elementToRepresent, LienzoDiagramCanvas canvas) {
        super(elementToRepresent, canvas);

        setLineColour(spiderColour);
        setFillColour(spiderColour);
        setMouseOverLineColour(spiderMouseOverColor);
        setMouseOverFillColour(spiderMouseOverColor);
        setSelectedLineColour(spiderSelectedColor);
        setSelectedFillColour(spiderSelectedColor);

        makeRepresentation();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null; // shouldn't be called
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }

    private void makeRepresentation() {
        representation = new Circle(getDiagramElement().getRadius());
        representation.setX(getDiagramElement().centre().getX()).setY(getDiagramElement().centre().getY());
        representation.setFillColor(spiderColour);
        representation.setStrokeColor(spiderColour);
        representation.setDraggable(false);

        // FIXME - spiders should be dragable and have dragbounds equal to their boundary rectangle - minus a bit so they don't go into the sides

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    @Override
    public void redraw() {
        getRepresentation().setX(getDiagramElement().centre().getX());
        getRepresentation().setY(getDiagramElement().centre().getY());
    }

    @Override
    public void drawDragRepresentation() {
        setAsSelected();
    }

    @Override
    public void setAsSelected() {
        getRepresentation().setStrokeColor(getSelectedLineColour());
    }

    @Override
    public void unDrawDragRepresentation() {

    }

    @Override
    public void setAsUnSelected() {

    }


}
