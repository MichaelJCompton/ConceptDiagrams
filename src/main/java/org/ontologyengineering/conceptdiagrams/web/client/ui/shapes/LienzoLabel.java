package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class LienzoLabel extends LienzoDiagramShape<ConcreteDiagramElement, Text>  {


    public LienzoLabel(ConcreteDiagramElement attachedTo, LienzoDiagramCanvas canvas, String labelText) {
        super(attachedTo, canvas);

        representation = new Text(labelText);
        representation.setDraggable(true);
        representation.setFontSize(13);
        representation.setFillColor(labelColour);
        representation.setFillColor(labelColour);
        representation.setX(attachedTo.getX());
        representation.setY(attachedTo.getY() - (representation.getBoundingBox().getHeight() / 2));
    }

    @Override
    public BoundingBox getBoundingBox() {
        return getRepresentation().getBoundingBox();
    }

    @Override
    public void dragBoundsMoved(BoundingBox newBoundingBox) {
        // nothing to do? unless I have a drag resize option
    }

    @Override
    public void redraw() {
        batch();
    }

    @Override
    public void setAsSelected() {
        // maybe have resize to adjust text size??
    }

    @Override
    public void setAsUnSelected() {

    }

    public String getLabelText() {
        return getRepresentation().getText();
    }

    public void addLabel(String labelText) {
        // no labels
    }
}
