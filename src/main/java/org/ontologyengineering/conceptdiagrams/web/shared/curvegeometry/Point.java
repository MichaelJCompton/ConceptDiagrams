package org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.types.Point2D;

import java.io.Serializable;

/**
 * Points for curves.
 *
 * At the moment this just hides a Lienzo Point2D, but may want to change, so hide that from the rest of the code.
 */
public class Point implements Serializable {

    // grr, problem here is that Lienzo can only be used from the client side!
    // so this is great while the app just ran client side, but now we are serializing the
    // whole thing to the server side this won't wash, so I'll keep it all locally and have the interface
    // return the lienzo point on demand when needed
    //private transient Point2D thePoint;
    double x;
    double y;


    public Point() {
        //this(0,0);
    }

    public Point(double x, double y) {
        //thePoint = new Point2D(x,y);
        this.x = x;
        this.y = y;
    }

    public Point(Point2D point) {
        x = point.getX();
        y = point.getY();
        //thePoint = point;
    }

    // only used client side???
    public double distance(Point other) {

        //return Math.sqrt(Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()));

        return asLienzoPoint2D().distance(other.asLienzoPoint2D());
    }

    public Point2D asLienzoPoint2D() {
        return new Point2D(x,y);
    }

//    private void newPoint2D(double x, double y) {
//        thePoint = new Point2D(x,y);
//    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
        //return thePoint.getX();
    }

    public double getY() {
        return y;
        //return thePoint.getY();
    }

    public boolean equals(Point other) {
        return getX() == other.getX() && getY() == other.getY();
    }

    // top left of the rectangle defined by the two points
    public static Point topLeft(Point p1, Point p2) {
        return new Point(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
    }

    public static Point botRight(Point p1, Point p2) {
        return new Point(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
    }

}
