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
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.tooling.common.api.java.util.function.Predicate;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteIntersectionZone;

/**
 * a representation with four overlapping rectangles ... just round the corners where necessary
 */
public class LienzoIntersectionZone extends LienzoDiagramShape<ConcreteIntersectionZone, Group> {

    private Rectangle rectangleTopLeft, rectangleBotLeft, rectangleTopRight, rectangleBotRight;

    public LienzoIntersectionZone(ConcreteIntersectionZone elementToRepresent, LienzoDiagramCanvas canvas) {
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
        return null;
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {

    }

    private void makeRepresentation() {

        rectangleTopLeft = new Rectangle(1, 1);
        rectangleBotLeft = new Rectangle(1, 1);
        rectangleTopRight = new Rectangle(1, 1);
        rectangleBotRight = new Rectangle(1, 1);
        representation = new Group();

        makeRectangles();

        // what's right with the locations of groups??
        representation.setX(0).setY(0);
        representation.add(rectangleTopLeft);
        representation.add(rectangleBotLeft);
        representation.add(rectangleTopRight);
        representation.add(rectangleBotRight);
        representation.setListening(true);
        representation.setDraggable(false);

        paintColourOnRepresentation(getFillColour());

        attachHandlers();
    }


    private void attachHandlers() {
        for (Node n : representation.find(new Predicate<Node<?>>() {
            public boolean test(Node<?> node) {
                return node.getNodeType() == NodeType.SHAPE;
            }
        })) {
            if (n != null && n.asShape() != null) {
                n.asShape().addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                    public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                        paintColourOnRepresentation(getMouseOverFillColour());
                        getCanvas().setIsUnderMouse(getDiagramElement());
                        representation.getLayer().batch();
                    }
                });
            }
        }

        for (Node n : representation.find(new Predicate<Node<?>>() {
            public boolean test(Node<?> node) {
                return node.getNodeType() == NodeType.SHAPE;
            }
        })) {
            if (n != null && n.asShape() != null) {
                n.asShape().addNodeMouseExitHandler(new NodeMouseExitHandler() {
                    public void onNodeMouseExit(NodeMouseExitEvent event) {
                        paintColourOnRepresentation(getFillColour());
                        representation.getLayer().batch();
                    }
                });
            }
        }
    }

    private void paintColourOnRepresentation(Color newColor) {
        for (Node n : representation.find(new Predicate<Node<?>>() {
            public boolean test(Node<?> node) {
                return (node.getNodeType() == NodeType.SHAPE);
            }
        })) {
            if (n != null && n.asShape() != null) {
                n.asShape().setFillColor(newColor);
                n.asShape().setStrokeColor(newColor);
            }
        }
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
        if(getRepresentation() != null) {
            makeRectangles();
            getRepresentation().getLayer().batch();
        }
    }

    private void makeRectangles() {
        rectangleTopLeft.setX(getDiagramElement().getX());
        rectangleTopLeft.setY(getDiagramElement().getY());
        rectangleTopLeft.setWidth(getDiagramElement().getWidth() - getDiagramElement().getCornerRadius());
        rectangleTopLeft.setHeight(getDiagramElement().getHeight() - getDiagramElement().getCornerRadius());
        if (getDiagramElement().topLeftIsCircle()) {
            rectangleTopLeft.setCornerRadius(getDiagramElement().getCornerRadius());
        }

        rectangleBotLeft.setX(getDiagramElement().getX());
        rectangleBotLeft.setY(getDiagramElement().getY() + getDiagramElement().getCornerRadius());
        rectangleBotLeft.setWidth(getDiagramElement().getWidth() - getDiagramElement().getCornerRadius());
        rectangleBotLeft.setHeight(getDiagramElement().getHeight() - getDiagramElement().getCornerRadius());
        if (getDiagramElement().bottomLeftIsCircle()) {
            rectangleBotLeft.setCornerRadius(getDiagramElement().getCornerRadius());
        }

        rectangleTopRight.setX(getDiagramElement().getX() + getDiagramElement().getCornerRadius());
        rectangleTopRight.setY(getDiagramElement().getY());
        rectangleTopRight.setWidth(getDiagramElement().getWidth() - getDiagramElement().getCornerRadius());
        rectangleTopRight.setHeight(getDiagramElement().getHeight() - getDiagramElement().getCornerRadius());
        if (getDiagramElement().topRightIsCircle()) {
            rectangleTopRight.setCornerRadius(getDiagramElement().getCornerRadius());
        }

        rectangleBotRight.setX(getDiagramElement().getX() + getDiagramElement().getCornerRadius());
        rectangleBotRight.setY(getDiagramElement().getY() + getDiagramElement().getCornerRadius());
        rectangleBotRight.setWidth(getDiagramElement().getWidth() - getDiagramElement().getCornerRadius());
        rectangleBotRight.setHeight(getDiagramElement().getHeight() - getDiagramElement().getCornerRadius());
        if (getDiagramElement().bottomRightIsCircle()) {
            rectangleBotRight.setCornerRadius(getDiagramElement().getCornerRadius());
        }
    }


    public void setAsSelected() {
        paintColourOnRepresentation(getSelectedLineColour());
        getRepresentation().setListening(false);        // don't do mouse overs while selected
    }


    public void setAsUnSelected() {
        paintColourOnRepresentation(getLineColour());
        getRepresentation().setListening(true);
    }


}
