package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Color;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.CommandManager;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.ResizeCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 *
 */
public class LienzoBoundaryRectangle extends LienzoDiagramShape<ConcreteBoundaryRectangle, Rectangle> {

    // FIXME : this is wrong!!! should be done just like curves, and in the command language
    // the rectangle is a zone + set the radius to 0
    private Rectangle zone;
    protected Color brZoneColour, brZoneMouseOverColour, brZoneSelectedColour;



    public LienzoBoundaryRectangle(ConcreteBoundaryRectangle rectangle, LienzoDiagramCanvas canvas) {
        super(rectangle, canvas);

        setLineColour(boundaryRectangleColour);
        setFillColour(boundaryRectangleFillColour);
        setMouseOverLineColour(boundaryRectangleMouseOverColour);
        setMouseOverFillColour(boundaryRectangleMouseOverFillColour);
        setSelectedLineColour(boundaryRectangleSelectedColour);
        setSelectedFillColour(boundaryRectangleSelectedFillColour);
        makeRepresentation();

        brZoneColour = zoneStandardColour;
        brZoneMouseOverColour = zoneMouseOverColor;
        brZoneSelectedColour = zoneSelectedColor;
        //makeZoneRepresentation();
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
        CommandManager.get().executeCommand(new ResizeCommand(getDiagramElement(), new Point(newBoundingBox.getX(), newBoundingBox.getY()),
                new Point(newBoundingBox.getX() + newBoundingBox.getWidth(), newBoundingBox.getY() + newBoundingBox.getHeight())));
    }

    protected void makeRepresentation() {
        representation = new Rectangle(getDiagramElement().getWidth(), getDiagramElement().getHeight());
        representation.setX(getDiagramElement().getX());
        representation.setY(getDiagramElement().getY());

        representation.setStrokeWidth(getDiagramElement().getBorderWidth());
        representation.setStrokeColor(getLineColour());
        representation.setFillColor(getFillColour());
        representation.setDraggable(false);

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    protected void makeZoneRepresentation() {
        // FIXME : why is there a gap between the boundary rectangle and the zone
        zone = new Rectangle(getDiagramElement().getWidth() - (2 * getDiagramElement().getBorderWidth()),
                getDiagramElement().getHeight() - (2 * getDiagramElement().getBorderWidth()));
        zone.setX(getDiagramElement().getX() + getDiagramElement().getBorderWidth());
        zone.setY(getDiagramElement().getY() + getDiagramElement().getBorderWidth());
        zone.setFillColor(brZoneColour);
        zone.setStrokeColor(brZoneColour);
        zone.setDraggable(false);

        zone.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                getCanvas().setIsUnderMouse(null);
                zone.setStrokeColor(brZoneMouseOverColour);
                zone.setFillColor(brZoneMouseOverColour);
                zone.getLayer().batch();
            }
        });

        zone.addNodeMouseExitHandler(new NodeMouseExitHandler() {
            public void onNodeMouseExit(NodeMouseExitEvent event) {
                zone.setStrokeColor(brZoneColour);
                zone.setFillColor(brZoneColour);
                zone.getLayer().batch();
            }
        });
    }

    public void shade() {
        brZoneColour = zoneShadedColor;
        brZoneColour = zoneShadedColor;
    }

    public void unShade() {
        brZoneColour = zoneStandardColour;
        brZoneColour = zoneStandardColour;
    }

    public void draw(Layer layer) {
        super.draw(layer);
    }

    @Override
    public void redraw() {
        getRepresentation().setWidth(getDiagramElement().getWidth());
        getRepresentation().setHeight(getDiagramElement().getHeight());
        getRepresentation().setX(getDiagramElement().getX());
        getRepresentation().setY(getDiagramElement().getY());
    }

    public void undraw() {
        super.undraw();
        if(getRepresentation() != null && getRepresentation().getLayer() != null) {
            //getRepresentation().getLayer().remove(zone);
            getRepresentation().getLayer().batch();
        }
    }


    public void setAsSelected() {
        if(getRepresentation() != null && getLayer() != null) {
            setLineColour(getSelectedLineColour());
            getRepresentation().setStrokeColor(getSelectedLineColour());
            getLayer().batch();
        }
    }


    public void setAsUnSelected() {
        if(getRepresentation() != null && getLayer() != null) {
            setLineColour(boundaryRectangleColour);
            getRepresentation().setStrokeColor(getLineColour());
            getLayer().batch();
        }
    }

    public void addLabel(String labelText) {
        // no labels
    }
}
