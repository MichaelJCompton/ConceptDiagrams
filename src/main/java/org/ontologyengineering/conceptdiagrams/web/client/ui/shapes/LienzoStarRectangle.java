package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Star;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;


public class LienzoStarRectangle extends LienzoBoundaryRectangle {

    private Star theStar;

    public LienzoStarRectangle(ConcreteBoundaryRectangle rectangle, LienzoDiagramCanvas canvas) {
        super(rectangle, canvas);
    }

    protected void makeRepresentation() {
        super.makeRepresentation();

        theStar = new Star(20, 1, 2, 0);
        remakeStar();
    }

    protected void remakeStar() {
        double minDim = Math.min(getDiagramElement().getHeight(), getDiagramElement().getWidth());

        theStar.setInnerRadius(minDim / 9);
        theStar.setOuterRadius(minDim / 3);
        theStar.setX(getDiagramElement().getX() + (getDiagramElement().getWidth() / 2));
        theStar.setY(getDiagramElement().getY() + (getDiagramElement().getHeight() / 2));
        theStar.setStrokeColor(starColour);
        theStar.setFillColor(starColour);
    }

    public void draw(Layer layer) {
        super.draw(layer);



        // FIXME ... should be some sort of multi object in the representation
        layer.add(theStar);
        layer.batch();




    }

    public void redraw() {
        super.redraw();
        remakeStar();
        batch();
    }

    public void undraw() {
        Layer layer = getLayer();
        super.undraw();
        layer.remove(theStar);
    }
}
