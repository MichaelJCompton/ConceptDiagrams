package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.CommandManager;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.MoveCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.ResizeCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;


public class LienzoSpider extends LienzoDiagramShape<ConcreteSpider, Circle> {

    private Circle spiderRubberBand;

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
        return getRepresentation().getBoundingBox();
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        double deltaX = getDiagramElement().getX() - newBoundingBox.getX();
        double deltaY = getDiagramElement().getY() - newBoundingBox.getY();

        getCanvas().getPresenter().moveElement(getDiagramElement(), new Point(newBoundingBox.getX(), newBoundingBox.getY()));

        if(hasLabel()) {
            getLabel().getRepresentation().setX(getLabel().getRepresentation().getX() - deltaX);
            getLabel().getRepresentation().setY(getLabel().getRepresentation().getY() - deltaY);
        }
    }

    private void makeRepresentation() {
        representation = new Circle(getDiagramElement().getRadius());
        representation.setX(getDiagramElement().centre().getX()).setY(getDiagramElement().centre().getY());
        representation.setFillColor(spiderColour);
        representation.setStrokeColor(spiderColour);
        setNotDragable();

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    public void setDragable() {
        getRepresentation().setDraggable(true);
    }

    public void setNotDragable() {
        getRepresentation().setDraggable(false);
    }

    @Override
    public void redraw() {
        getRepresentation().setX(getDiagramElement().centre().getX());
        getRepresentation().setY(getDiagramElement().centre().getY());
        batch();
    }

    protected void makedragRubberBand() {
        if(getDragRubberBand() == null) {
            setDragRubberBand(new LienzoDragSpiderRubberBand(getCanvas(), this));
        }
    }


//    public void drawRubberBand() {
//        makedragRubberBand();
//        getLayer().getViewport().getDragLayer().add(spiderRubberBand);
//        getLayer().getViewport().getDragLayer().batch();
//    }
//
//    public void drawDragRepresentation() {
//        makedragRubberBand();
//        drawRubberBand();
//    }

    @Override
    public void setAsSelected() {
        //setDragable();
        if (getLayer() != null) {
            setLineColour(getSelectedLineColour());
            setFillColour(getSelectedLineColour());
            getRepresentation().setStrokeColor(getSelectedLineColour());
            getRepresentation().setFillColor(getSelectedLineColour());
            batch();
        }
    }


    public void unDrawDragRepresentation() {
        getLayer().getViewport().getDragLayer().remove(spiderRubberBand);
        getLayer().getViewport().getDragLayer().batch();
    }

    @Override
    public void setAsUnSelected() {
        //setNotDragable();
        if(getRepresentation() != null && getLayer() != null) {
            setLineColour(spiderColour);
            setFillColour(spiderColour);
            getRepresentation().setStrokeColor(getLineColour());
            getRepresentation().setFillColor(getFillColour());
            batch();
        }
    }

}
