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
//import com.ait.lienzo.client.core.shape.Group;
//import com.ait.lienzo.client.core.shape.Layer;
//import com.ait.lienzo.client.core.shape.Node;
//import com.ait.lienzo.client.core.shape.Rectangle;
//import com.ait.lienzo.client.core.types.Point2D;
//import com.ait.lienzo.shared.core.types.Color;

import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

/**
 * An intersection zone is formed by the intersection of two curves and has two rounded corners and two square corners
 *
 * A zone will be drawn as two rectangles for the square edges and two circles for the round edges in a 'blank colour'
 */
public class ConcreteIntersectionZone extends ConcreteZone {


    // just for serialization
    public ConcreteIntersectionZone() {

    }

    public ConcreteIntersectionZone(Point topLeft, Point bottomRight,
                             Boolean topLeftIsCircle, Boolean botLeftIsCircle,
                             Boolean topRightIsCircle, Boolean botRightIsCircle) {

        // This is the actual x,y of the zone : the offset from the enclosing curves has already been taken away
        super(topLeft, bottomRight, ConcreteDiagramElement_TYPES.CONCRETEINTERSECTIONZONE);

        this.topLeftIsCircle = topLeftIsCircle;
        this.botLeftIsCircle = botLeftIsCircle;
        this.topRightIsCircle = topRightIsCircle;
        this.botRightIsCircle = botRightIsCircle;
    }




    public void reassociateToCurves(ConcreteCurve curveBeenKept) {
        for (ConcreteCurve curve : getCurves()) {
            if (curve != curveBeenKept) {
                curve.addEnclosedZone(this);
            }
        }
    }



}
