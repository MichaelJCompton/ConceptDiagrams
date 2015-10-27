package org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.types.Point2D;

/**
 * Points for curves.
 *
 * At the moment this just hides a Lienzo Point2D, but may want to change, so hide that from the rest of the code.
 */
public class Point {

    Point2D thePoint;

    public Point(double x, double y) {
        thePoint = new Point2D(x,y);
    }

    public Point(Point2D point) {
        thePoint = point;
    }

    public double distance(Point other) {
        return asLienzoPoint2D().distance(other.asLienzoPoint2D());
    }

    public Point2D asLienzoPoint2D() {
        return new Point2D(thePoint);
    }

    public void setX(double x) {
        thePoint.setX(x);
    }

    public void setY(double y) {
        thePoint.setY(y);
    }

    public double getX() {
        return thePoint.getX();
    }

    public double getY() {
        return thePoint.getY();
    }

    public boolean equals(Point other) {
        return getX() == other.getX() && getY() == other.getY();
    }

}
