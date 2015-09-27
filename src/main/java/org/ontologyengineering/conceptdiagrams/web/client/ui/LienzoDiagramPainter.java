package org.ontologyengineering.conceptdiagrams.web.client.ui;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.client.ui.shapes.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;

/**
 * Just a drawer and decorator - doesn't check much in the way of sensibleness of what it draws, assumes something else
 * is checking if the curves etc are in proper places and of reasonable size.
 */
public class LienzoDiagramPainter extends DiagramPainter {

    public LienzoDiagramPainter(LienzoDiagramCanvas theCanvas) {
        super(theCanvas);
    }

    public LienzoDiagramCanvas getCanvas() {
        return (LienzoDiagramCanvas) super.getCanvas();
    }

    @Override
    public void drawRectangle(final ConcreteBoundaryRectangle rectangle) {
        if (!getCanvas().isOnCanvas(rectangle)) {
            getCanvas().drawOnCanvas(rectangle, new LienzoBoundaryRectangle(rectangle, getCanvas()), getCanvas().getBoundaryRectangleLayer());
            getCanvas().getBoundaryRectangleLayer().batch();
        }
        getCanvas().setEnabledButtons();
    }

    public void drawStarRectangle(final ConcreteStarRectangle rectangle) {
        if (!getCanvas().isOnCanvas(rectangle)) {

            getCanvas().drawOnCanvas(rectangle, new LienzoStarRectangle(rectangle, getCanvas()), getCanvas().getBoundaryRectangleLayer());
            getCanvas().getBoundaryRectangleLayer().batch();

        }
        getCanvas().setEnabledButtons();
    }


    public void removeRectangle(final ConcreteBoundaryRectangle rectangle) {
        // FIXME ... probably a bug here, because have to deal with the concrete diagrams as well.

        getCanvas().removeFromCanvas(rectangle);

//        getCanvas().removeFromBoundaryRectangleLayer(getCanvas().getRepresentation(rectangle).asPrimitive());
//        getCanvas().removeFromElementsOnCanvas(rectangle);
    }




//            // FIXME : have editable labels, placed sensibly .... maybe allow the user to move them to 'sensible' locations near the curve
//            labelText = new  Text("My Curve").setFillColor(ColorName.BLACK).setX(getX()).setY(getY() - 10).setFontSize(7);
//            labelText.setEditable(true);
    public void drawCurve(final ConcreteCurve curve) {

        if (!getCanvas().isOnCanvas(curve)) {
            getCanvas().drawOnCanvas(curve, new LienzoCurve(curve, getCanvas()), getCanvas().getCurveLayer());
            //drawAllZones(curve);

            getCanvas().addToCurveToZoneMap(curve);
        }
        getCanvas().setEnabledButtons();
    }

    public void removeCurve(final ConcreteCurve curve) {
        getCanvas().removeFromCanvas(curve);
        //removeAllZones(curve);

        getCanvas().removeFromCurveToZoneMap(curve);
    }


    public void redraw(ConcreteDiagramElement element) {
        if(getCanvas().isOnCanvas(element)) {
            getCanvas().getRepresentation(element).redraw();
        }
    }

    public void addLabel(ConcreteDiagramElement element) {
        if(getCanvas().isOnCanvas(element)) {
            getCanvas().getRepresentation(element).addLabel();
        }
    }

    public void removeAllZones(ConcreteCurve curve) {
        removeZone(curve.getMainZone());

        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            removeZone(zone);
        }
    }

    public void removeZone(ConcreteZone zone) {
        getCanvas().removeFromCanvas(zone);
//        getCanvas().removeFromZoneLayer(getCanvas().getRepresentation(zone).asPrimitive());
//        getCanvas().removeFromElementsOnCanvas(zone);
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
}
