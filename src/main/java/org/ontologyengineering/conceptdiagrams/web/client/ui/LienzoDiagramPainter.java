package org.ontologyengineering.conceptdiagrams.web.client.ui;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.LienzoPanel;
import org.ontologyengineering.conceptdiagrams.web.client.ui.shapes.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.presenter.DiagramPainter;

/**
 * Just a drawer and decorator - doesn't check much in the way of sensibleness of what it draws, assumes something else
 * is checking if the curves etc are in proper places and of reasonable size.
 */
public class LienzoDiagramPainter implements DiagramPainter {

    // global drawing properties

    private Rectangle rubberbandRectangle;
    private Circle rubberbandSpider;
    private Line rubberbandArrow;

    private Point rubberbandStart;

    private LienzoDiagramCanvas theCanvas;

    public LienzoDiagramPainter(LienzoDiagramCanvas theCanvas) {
        this.theCanvas = theCanvas;
    }

    public LienzoDiagramCanvas getCanvas() {
        return theCanvas;
    }


    public void redraw(ConcreteDiagramElement element) {
        if(getCanvas().isOnCanvas(element)) {
            getCanvas().getRepresentation(element).redraw();
        }
    }

    public void changeLabel(ConcreteDiagramElement element) {
        if(getCanvas().isOnCanvas(element)) {
            getCanvas().getRepresentation(element).addLabel();
        }
    }


    @Override
    public void drawRectangle(ConcreteBoundaryRectangle rectangle) {
        if (!getCanvas().isOnCanvas(rectangle)) {
            getCanvas().drawOnCanvas(rectangle, new LienzoBoundaryRectangle(rectangle, getCanvas()), getCanvas().getBoundaryRectangleLayer());
        }
    }

    public void drawStarRectangle(ConcreteStarRectangle rectangle) {
        if (!getCanvas().isOnCanvas(rectangle)) {
            getCanvas().drawOnCanvas(rectangle, new LienzoStarRectangle(rectangle, getCanvas()), getCanvas().getBoundaryRectangleLayer());
        }
    }


    public void removeRectangle(ConcreteBoundaryRectangle rectangle) {
        getCanvas().removeFromCanvas(rectangle);
    }



    public void drawCurve(final ConcreteCurve curve) {
        if (!getCanvas().isOnCanvas(curve)) {
            getCanvas().drawOnCanvas(curve, new LienzoCurve(curve, getCanvas()), getCanvas().getCurveLayer());
            getCanvas().addToCurveToZoneMap(curve);
        }
    }

    public void removeCurve(final ConcreteCurve curve) {
        getCanvas().removeFromCanvas(curve);
        getCanvas().removeFromCurveToZoneMap(curve);
    }


    public void removeAllZones(ConcreteCurve curve) {
        removeZone(curve.getMainZone());

        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            removeZone(zone);
        }
    }

    public void removeZone(ConcreteZone zone) {
        getCanvas().removeFromCanvas(zone);
    }

    @Override
    public void drawAllZones(ConcreteCurve curve) {
        drawZone(curve.getMainZone());

        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            drawIntersectionZone(zone);
        }

        redrawAllZones(curve.getBoundaryRectangle());
    }



    // something has changed in the boundary rectangle, like some zones added, so maybe the drawn zones are overlapping
    // each other in the wrong ways, so redraw.
    // ...
    // But instead of redrawing, let's just try to reorder them on the layer.
    public void redrawAllZones(ConcreteBoundaryRectangle rectangle) {
        for(ConcreteZone z : rectangle.getSortedZones()) {
            if(getCanvas().getRepresentation(z) != null) {
                getCanvas().getRepresentation(z).moveToTop();
            }
            //getCanvas().getRepresentation(z).getLayer().moveToTop(getCanvas().getRepresentation(z).getRepresentation().asPrimitive());
        }
        getCanvas().getZoneLayer().batch();
    }

    // doesn't care about order, just draws it now
    @Override
    public void drawZone(final ConcreteZone zone) {
        if (!getCanvas().isOnCanvas(zone)) {
            if(zone.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEZONE) {
                getCanvas().drawOnCanvas(zone, new LienzoZone(zone, getCanvas()), getCanvas().getZoneLayer());
            } else {
                getCanvas().drawOnCanvas(zone, new LienzoIntersectionZone((ConcreteIntersectionZone) zone, getCanvas()), getCanvas().getZoneLayer());
            }
//
            redrawAllZones(zone.getBoundaryRectangle());
//            LienzoZone lienzoZone = new LienzoZone(zone, getCanvas());
//
//            getCanvas().addToElementsOnCanvas(zone, lienzoZone);
//            getCanvas().addToZoneLayer(lienzoZone.getRepresentation());
        }
    }

    public void drawIntersectionZone(ConcreteIntersectionZone zone) {
        if (!getCanvas().isOnCanvas(zone)) {
            getCanvas().drawOnCanvas(zone, new LienzoIntersectionZone(zone, getCanvas()), getCanvas().getZoneLayer());

            //LienzoIntersectionZone intersectionZone = new LienzoIntersectionZone(zone, getCanvas());

            //getCanvas().addToElementsOnCanvas(zone, intersectionZone);
            //getCanvas().addToZoneLayer(intersectionZone);
        }
    }


    public void shadeZone(ConcreteZone zone) {
        if (getCanvas().isOnCanvas(zone)) {
            //getCanvas().resetMapForElement(zone, new LienzoShadeDecorator((LienzoZone) getCanvas().getRepresentation(zone), getCanvas()));
            //getCanvas().getRepresentation(zone).draw(getCanvas().getZoneLayer());
            getCanvas().getRepresentation(zone).shade();
            getCanvas().getZoneLayer().batch();
        }
    }

    public void unShadeZone(ConcreteZone zone) {
        if (getCanvas().isOnCanvas(zone)) {
            //getCanvas().resetMapForElement(zone, new LienzoShadeDecorator((LienzoZone) getCanvas().getRepresentation(zone), getCanvas()));
            //getCanvas().getRepresentation(zone).draw(getCanvas().getZoneLayer());
            getCanvas().getRepresentation(zone).unShade();
            getCanvas().getZoneLayer().batch();
        }
    }


    @Override
    public void drawSpider(final ConcreteSpider spider) {
        if (!getCanvas().isOnCanvas(spider)) {
            getCanvas().drawOnCanvas(spider, new LienzoSpider(spider, getCanvas()), getCanvas().getCurveLayer());
//
//            LienzoSpider lienzoSpider = new LienzoSpider(spider, getCanvas());
//
//            getCanvas().addToElementsOnCanvas(spider, lienzoSpider);
//            getCanvas().addToCurveLayer(lienzoSpider.getRepresentation());
        }
        getCanvas().setEnabledButtons();
    }

    @Override
    public void removeSpider(ConcreteSpider spider) {
        getCanvas().removeFromCanvas(spider);
//        getCanvas().removeFromCurveLayer(getCanvas().getRepresentation(spider).asPrimitive());
//        getCanvas().removeFromElementsOnCanvas(spider);
    }

    @Override
    public void drawArrow(final ConcreteArrow arrow) {
        if (!getCanvas().isOnCanvas(arrow)) {
            getCanvas().drawOnCanvas(arrow, new LienzoArrow(arrow, getCanvas()), getCanvas().getCurveLayer());
//
//            LienzoArrow lienzoArrow = new LienzoArrow(arrow, getCanvas());
//
//            getCanvas().addToElementsOnCanvas(arrow, lienzoArrow);
//            getCanvas().addToCurveLayer(lienzoArrow.getRepresentation());
        }
    }

    @Override
    public void removeArrow(ConcreteArrow arrow) {
        getCanvas().removeFromCanvas(arrow);
    }

    @Override
    public void clearAll() {

    }


    private LienzoPanel getPanel() {
        return getCanvas().getCurrentPanel();
    }

    // --------------------------------
    // rubberbanding
    // --------------------------------

    // screen coords  for drag layer


    protected void startRubberBandRectangle(Point topLeft) {
        rubberbandRectangle = new Rectangle(0, 0);
        rubberbandRectangle.setX(topLeft.getX()).setY(topLeft.getY());
        rubberbandStart = topLeft;
        getPanel().getDragLayer().add(rubberbandRectangle);
        getPanel().getDragLayer().batch();
    }


    protected void startRubberBandSpider(Point p) {
        // FIXME : how about a little rubber banding spider with legs for fun?
        // even more fun would be to animate it, running around the screen
        rubberbandSpider = new Circle(ConcreteDiagramElement.spiderRadius);
        rubberbandSpider.setX(p.getX()).setY(p.getY());
        rubberbandStart = p;
        getPanel().getDragLayer().add(rubberbandSpider);
        getPanel().getDragLayer().batch();
    }


    protected void startRubberBandArrow(Point p) {
        rubberbandArrow = new Line(new Point2D(p.getX(), p.getY()), new Point2D(p.getX(), p.getY()));
        rubberbandStart = p;
        getPanel().getDragLayer().add(rubberbandArrow);
        getPanel().getDragLayer().batch();
    }


    protected void dragRubberBandRectangle(Point newPoint) {

        double newWidth = Math.abs(newPoint.getX() - rubberbandStart.getX());   //(newPoint.getX() >= rubberbandStart.getX()) ? (newPoint.getX() - rubberbandStart.getX()) : (rubberbandStart.getX() - newPoint.getX());
        double newHeight = Math.abs(newPoint.getY() - rubberbandStart.getY());  //newPoint.getY() >= rubberbandStart.getY()) ? (newPoint.getY() - rubberbandStart.getY()) : (rubberbandStart.getY() - newPoint.getY());
        rubberbandRectangle.setHeight(newHeight);
        rubberbandRectangle.setWidth(newWidth);

        rubberbandRectangle.setX((newPoint.getX() >= rubberbandStart.getX()) ? rubberbandStart.getX() : newPoint.getX());
        rubberbandRectangle.setY((newPoint.getY() >= rubberbandStart.getY()) ? rubberbandStart.getY() : newPoint.getY());

        getPanel().getDragLayer().batch();
    }


    protected void dragRubberBandSpider(Point newPoint) {
        rubberbandSpider.setX(newPoint.getX()).setY(newPoint.getY());
        getPanel().getDragLayer().batch();
    }


    protected void dragRubberBandArrow(Point newPoint) {
        rubberbandArrow.setPoints(new Point2DArray(new Point2D(rubberbandStart.getX(), rubberbandStart.getY()),
                new Point2D(newPoint.getX(), newPoint.getY())));
        getPanel().getDragLayer().batch();
    }


    protected void removeRubberBandRectangle() {
        removeRubberBand(rubberbandRectangle);
    }

    protected void removeRubberBandSpider() {
        removeRubberBand(rubberbandSpider);
    }

    protected void removeRubberBandArrow() {
        removeRubberBand(rubberbandArrow);
    }

    private void removeRubberBand(Shape s) {
        if(s != null) {
            getPanel().getDragLayer().remove(s);
            s = null;
            rubberbandStart = null;
            getPanel().getDragLayer().batch();
        }
    }

    // --------------------------------
    // draw and undraw shapes
    // --------------------------------

    protected void draw(LienzoDiagramShape shape, Layer layer) {
        shape.draw(layer);
    }

    protected void unDraw(LienzoDiagramShape shape) {
        shape.undraw();
    }
}
