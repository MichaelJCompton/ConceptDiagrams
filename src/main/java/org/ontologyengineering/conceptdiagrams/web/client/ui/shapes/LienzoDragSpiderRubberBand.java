package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
public class LienzoDragSpiderRubberBand extends LienzoDragRubberBand {


    public LienzoDragSpiderRubberBand(LienzoDiagramCanvas canvas, LienzoSpider spiderToBound) {
        super(canvas, spiderToBound);

        representation = new Circle(spiderToBound.getDiagramElement().getRadius());
        representation.setFillColor(rubberbandRetangleColor);
        representation.setStrokeColor(rubberbandRetangleColor);
        representation.setAlpha(0.2);     // partly invisible

        representation.setDraggable(true);
        representation.setListening(true);

        representation.addNodeDragEndHandler(new NodeDragEndHandler() {
            public void onNodeDragEnd(NodeDragEndEvent nodeDragEndEvent) {
                // get the movement as canvas coords
//                Point2D dragSize = new Point2D();  // of the bounds as screen coords
//
//                getLayer().getViewport().getTransform().getInverse().transform(
//                        new Point2D(nodeDragEndEvent.getDragContext().getDx(), nodeDragEndEvent.getDragContext().getDy()), dragSize);
//
//                getBoundedShape().dragBoundsMoved(new BoundingBox(new Point2D(getDiagramElement().getX() + dragSize.getX(), getDiagramElement().getY() + dragSize.getY()),
//                        new Point2D(getDiagramElement().bottomRight().getX() + dragSize.getX(), getDiagramElement().bottomRight().getY() + dragSize.getY())));

                // dragBoundsMoved is in top left coords, so adjust

                // - ConcreteDiagramElement.spiderRadius
                getBoundedShape().dragBoundsMoved(new BoundingBox(
                        new Point2D(getBoundedShape().getDiagramElement().getX() + nodeDragEndEvent.getDragContext().getDx(),
                                getBoundedShape().getDiagramElement().getY() + nodeDragEndEvent.getDragContext().getDy()),
                        new Point2D(getBoundedShape().getDiagramElement().bottomRight().getX() + nodeDragEndEvent.getDragContext().getDx(),
                                getBoundedShape().getDiagramElement().bottomRight().getY() + nodeDragEndEvent.getDragContext().getDy())));
            }
        });

        representation.addNodeDragMoveHandler(new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove(NodeDragMoveEvent nodeDragMoveEvent) {
                getCanvas().turnOffDragSelect();
                getCanvas().turnOffDrawingSpider();
            }
        });
    }

    @Override
    public BoundingBox getBoundingBox() {
        // should not be called
        return getBoundedShape().getBoundingBox();
    }


    // assume in screen coords
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        setRubberband(newBoundingBox.getX(), newBoundingBox.getY(),
                newBoundingBox.getWidth(), newBoundingBox.getHeight());
    }


    // Different to the other drag rubberbands cause this one is clickable and so on the normal canvas, not the drag layer
    // so no need for transforms
    public void draw(Layer layer) {
        //super.draw(layer);

        setRubberband(getBoundedShape().getDiagramElement().getX(), getBoundedShape().getDiagramElement().getY(), 0, 0);

        getBoundedShape().getLayer().add(representation);
        getBoundedShape().getLayer().batch();
    }

    protected double getWidth() {
        return ConcreteDiagramElement.spiderRadius;
    }

    protected double getHeight() {
        return ConcreteDiagramElement.spiderRadius;
    }


    protected void setRubberband(double x, double y, double width, double height) {
        representation.setX(x + ConcreteDiagramElement.spiderRadius);
        representation.setY(y + ConcreteDiagramElement.spiderRadius);
    }


}
