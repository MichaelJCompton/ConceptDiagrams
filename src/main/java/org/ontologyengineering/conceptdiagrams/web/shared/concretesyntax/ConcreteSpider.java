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

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 * A concrete spider
 */
public class ConcreteSpider extends ConcreteDiagramElement {

    // just for serialization
    public ConcreteSpider() {
        //this(new Point());
    }

    // This is the centre point of the spider ... breaks the abstraction a bit cause everything else is top left
    public ConcreteSpider(Point centre) {
        super(new Point(centre.getX() - spiderRadius, centre.getY() - spiderRadius), ConcreteDiagramElement_TYPES.CONCRETESPIDER);
        //setFillColour(spiderColour);
        //setBorderColour(spiderColour);
        checkValidity();
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

    @Override
    public void checkValidity() {
        // FIXME : this isn't good enough once there are multiple spider feet
        setValid(true);
    }


    public double getRadius() {
        return spiderRadius;
    }

    public Point bottomRight() {
        return new Point(topLeft().getX() + (2*getRadius()), topLeft().getY() + (2*getRadius()));
    }


    public void setAsSelected() {}

}
