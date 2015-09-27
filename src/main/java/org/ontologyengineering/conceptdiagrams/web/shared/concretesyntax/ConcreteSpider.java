package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

//import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
//import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
//import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
//import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
//import com.ait.lienzo.client.core.shape.Circle;
//import com.ait.lienzo.client.core.shape.Layer;
//import com.ait.lienzo.client.core.types.Point2D;
//import michael.com.Spider;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.Spider;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 * A concrete spider is a filled circle.
 */
public class ConcreteSpider extends ConcreteDiagramElement {


    // This is the centre point of the spider ... breaks the abstraction a bit cause everything else is top left
    public ConcreteSpider(Point centre) {
        super(new Point(centre.getX() - spiderRadius, centre.getY() - spiderRadius), ConcreteDiagramElement_TYPES.CONCRETESPIDER);
        //setFillColour(spiderColour);
        //setBorderColour(spiderColour);
    }

    public Point centre() {
        return new Point(getX() + spiderRadius, getY() + spiderRadius);
        //return topLeft();
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addSpider(this);
    }


    public double getRadius() {
        return spiderRadius;
    }


    @Override
    public void makeAbstractRepresentation() {
        if (! isAbstractRepresentationSyntaxUpToDate()) {
            Spider result = new Spider();
            if (hasLabel()) {
                result.setLabel(labelText());
            }
            setAbstractSyntaxRepresentation(result);
        }
    }

//    @Override
//    public void makeConcreteRepresentation() {
//        if(hasChangedOnScreen()) {
//            final Circle theSpider = new Circle(spiderRadius);
//            theSpider.setX(centre().getX()).setY(centre().getY());
//            theSpider.setFillColor(getFillColour());
//            theSpider.setStrokeColor(getBorderColour());
//            theSpider.setDraggable(false);
//
//            theSpider.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
//                @Override
//                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
//                    theSpider.setStrokeColor(spiderSelectedColor);
//                    theSpider.setFillColor(spiderSelectedColor);
//                    setIsUnderMouse();
//                    theSpider.getLayer().batch();
//                }
//            });
//            theSpider.addNodeMouseExitHandler(new NodeMouseExitHandler() {
//                @Override
//                public void onNodeMouseExit(NodeMouseExitEvent event) {
//                    theSpider.setStrokeColor(getBorderColour());
//                    theSpider.setFillColor(getFillColour());
//                    theSpider.getLayer().batch();
//                }
//            });
//
//            setConcreteRepresentation(theSpider);
//        }
//    }
//
//    @Override
//    public void drawOnLayer(Layer layer) {
//        layer.add(getConcreteRepresentation());
//    }

    public void setAsSelected() {}

    public void deleteMe() {
//        getConcreteRepresentation().setListening(false);  // seems this is required otherwise it crashes trying to
//        // respond to events while deleting
//
//        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
//        getBoundaryRectangle().removeSpider(this);
    }
}
