package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class LienzoArrow extends LienzoDiagramShape<ConcreteArrow, Spline> {

    public LienzoArrow(ConcreteArrow elementToRepresent, LienzoDiagramCanvas canvas) {
        super(elementToRepresent, canvas);

        setLineColour(arrowColour);
        setFillColour(arrowColour);
        setMouseOverLineColour(arrowMouseOverColour);
        setMouseOverFillColour(arrowMouseOverColour);
        setSelectedLineColour(arrowSelectedColour);
        setSelectedFillColour(arrowSelectedColour);

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
        double[] xpoints = new double[pointsInArrowLine + 2];  // +2 for start and end points
        double[] ypoints = new double[pointsInArrowLine + 2];
        double xdiff = (getDiagramElement().getX() < getDiagramElement().getEndPoint().getX()) ? getDiagramElement().getEndPoint().getX() - getDiagramElement().getX() : getDiagramElement().getX() - getDiagramElement().getEndPoint().getX();
        double ydiff = (getDiagramElement().getY() < getDiagramElement().getEndPoint().getY()) ? getDiagramElement().getEndPoint().getY() - getDiagramElement().getY() : getDiagramElement().getY() - getDiagramElement().getEndPoint().getY();

        if(getDiagramElement().getSource().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE) {
            xpoints[0] = getDiagramElement().getSource().centre().getX();
            ypoints[0] = getDiagramElement().getSource().centre().getY();
        } else {
            xpoints[0] = getDiagramElement().getX();
            ypoints[0] = getDiagramElement().getY();
        }
        for (int i = 1; i <= pointsInArrowLine; i++) {
            if (getDiagramElement().getX() < getDiagramElement().getEndPoint().getX()) {
                xpoints[i] = getDiagramElement().getX() + (xdiff / (pointsInArrowLine + 1)) * i;
            } else {
                xpoints[i] = getDiagramElement().getX() - (xdiff / (pointsInArrowLine + 1)) * i;
            }
            if (getDiagramElement().getY() < getDiagramElement().getEndPoint().getY()) {
                ypoints[i] = getDiagramElement().getY() + (ydiff / (pointsInArrowLine + 1)) * i;
            } else {
                ypoints[i] = getDiagramElement().getY() - (ydiff / (pointsInArrowLine + 1)) * i;
            }
        }
        xpoints[pointsInArrowLine + 1] = getDiagramElement().getEndPoint().getX();
        ypoints[pointsInArrowLine + 1] = getDiagramElement().getEndPoint().getY();

        Point2DArray controlPoint = new Point2DArray(xpoints, ypoints);

        representation = new Spline(controlPoint);
        representation.setStrokeColor(getLineColour());
        representation.setStrokeWidth(arrowLineWidth);
        representation.setDraggable(false);

        stdMouseEnterHandler(representation);
        stdMouseExitHandler(representation);
    }


    @Override
    public void redraw() {
        // FIXME
    }

    @Override
    public void setAsSelected() {
        getRepresentation().setStrokeColor(getSelectedLineColour());
        getRepresentation().setListening(false);
    }

    @Override
    public void setAsUnSelected() {
        getRepresentation().setStrokeColor(getLineColour());
        getRepresentation().setListening(true);
    }

}
