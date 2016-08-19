package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 *
 */
public class ConcreteStarRectangle extends ConcreteBoundaryRectangle {

    // just for serialization
    public ConcreteStarRectangle() {
        //this(new Point(), new Point());
    }

    public ConcreteStarRectangle(Point topLeft, Point bottomRight) {
        super(topLeft, bottomRight, ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE);

        setAsObject();
    }

    public boolean isStarRectangle() {
        return true;
    }

    // no ops ... no curves etc in this rectangle
    public void addCurve(ConcreteCurve curve) { }
    public void removeCurve(ConcreteCurve curve) { }
    public void addZone(ConcreteZone zone) { }
    public void removeZone(ConcreteZone zone) { }
    public void addSpider(ConcreteSpider spider) { }
    public void removeSpider(ConcreteSpider spider) { }
    //public void addArrow(ConcreteArrow arrow) { }       // I think these too as the arrows should be on the diagram
    //public void removeArrow(ConcreteArrow arrow) { }

    public void resize(Point topLeft, Point botRight) {
        super.resize(topLeft, botRight);
    }
}
