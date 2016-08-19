package org.ontologyengineering.conceptdiagrams.web.shared.presenter;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;

/**
 * A DiagramPainter just knows how to paint (and remove) the diagram bits.  Sometimes one would have a really dumb
 * painter (or display), in terms of the data structures it interacts with,
 * here I think to do so would need another abstraction over the concrete syntax.  Shouldn't
 * be a real problem here, basically the painter shouldn't fiddle with the diagrams only display them.  I'll try
 * to keep the interface such that the painter only accesses things, doesn't change them.
 *
 * The painter doesn't do any real checking, just draws things where it's told to and removes them when told to.
 * If it doesn't make sense to do so, some other part of the code should have checked.
 */
public interface DiagramPainter {

    void drawCurve(ConcreteCurve curve);
    void removeCurve(ConcreteCurve curve);

    void drawZone(ConcreteZone zone);
    void removeZone(ConcreteZone zone);
    void drawIntersectionZone(ConcreteIntersectionZone zone);
    void drawAllZones(ConcreteCurve curve);
    void shadeZone(ConcreteZone zone);
    void unShadeZone(ConcreteZone zone);

    void drawSpider(ConcreteSpider spider);
    void removeSpider(ConcreteSpider spider);

    void drawRectangle(ConcreteBoundaryRectangle rectangle);
    void drawStarRectangle(ConcreteStarRectangle rectangle);
    void removeRectangle(ConcreteBoundaryRectangle rectangle);

    void drawArrow(ConcreteArrow arrow);
    void removeArrow(ConcreteArrow arrow);

    void changeLabel(ConcreteDiagramElement element);

    void redraw(ConcreteDiagramElement element);

    void clearAll();
}
