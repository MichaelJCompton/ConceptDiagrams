package org.ontologyengineering.conceptdiagrams.web.client.ui;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;

/**
 * the canvas handles all the whole canvas issues, the painter is responsible for drawing the curves etc
 */
public abstract class DiagramPainter {

    protected final DiagramCanvas theCanvas;


    DiagramPainter(DiagramCanvas theCanvas) {
        this.theCanvas = theCanvas;
    }

    protected DiagramCanvas getCanvas() {
        return theCanvas;
    }



    public abstract void drawCurve(ConcreteCurve curve);
    public abstract void removeCurve(ConcreteCurve curve);

    public abstract void drawZone(ConcreteZone zone);
    public abstract void drawIntersectionZone(ConcreteIntersectionZone zone);
    public abstract void drawAllZones(ConcreteCurve curve);
    public abstract void shadeZone(ConcreteZone zone);

    public abstract void drawSpider(ConcreteSpider spider);
    public abstract void removeSpider(ConcreteSpider spider);

    public abstract void drawRectangle(ConcreteBoundaryRectangle rectangle);

    public abstract void drawArrow(ConcreteArrow arrow);

    // FIXME : to add
    // drawCurve(ConcreteCurve )
    // drawBoundaryRectangle ...
    // etc
}
