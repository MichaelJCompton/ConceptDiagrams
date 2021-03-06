package org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.*;

/**
 * While in the abstract syntax all the bits of a diagram are 'owned' by the diagram itself (i.e. from the definitions a
 * diagram is a tuple with boundary rectangle, curves, arrows etc), in the concrete syntax things are a bit more dynamic
 * and thus a bit different.  We don't quite know what's being drawn at the start, or where the bits should live, so
 * it's all owned by the boundary rectangle, or concrete diagram, and underneath they sort out the correct abstract
 * representation.
 */
public class ConcreteBoundaryRectangle extends ConcreteRectangularElement {

    // grid to keep intersection checking speedy
    private ArrayList<ArrayList<HashSet<ConcreteCurve>>> intersectionGrid;
    private final int initialSquares = 4;
    private double gridSquareSize;

    private ConcreteZone mainZone;  // the only zone in a boundary rectangle

    private ConcreteDiagram myDiagram;

    // Which zones are 'on top' for spider zone detection and also drawing.  Taking advantage of Lienzo's drawing and
    // picking algorithms to draw zones in order such that we can find and pick the visible parts of a zone.  This is
    // partly because I'm not drawing all the different shaped zones we could get, just (possibly-)curved cornered
    // rectangles and then using the drawing to occlude with covering zones to make the shapes.
    private ArrayList<HashSet<ConcreteZone>> zoneHeights;

    //private AbstractSet<ConcreteSyntaxElement> myChildren;
    private HashSet<ConcreteSpider> mySpiders;
    private HashSet<ConcreteCurve> myCurves;
    private HashSet<ConcreteArrow> myArrows;

    // just for serialization
    public ConcreteBoundaryRectangle() {
        //this(new Point(), new Point());
    }

    public ConcreteBoundaryRectangle(Point topLeft, Point bottomRight) {
        super(topLeft, bottomRight, ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE);

        initialise();
    }

    public ConcreteBoundaryRectangle(Point topLeft, Point bottomRight, ConcreteDiagramElement_TYPES type) {
        super(topLeft, bottomRight, type);

        initialise();
    }

    private void initialise() {
        setBorderWidth(boundaryRectangleBorderWidth);

        zoneHeights = new ArrayList<HashSet<ConcreteZone>>();
        mySpiders = new HashSet<ConcreteSpider>();
        myCurves = new HashSet<ConcreteCurve>();
        myArrows = new HashSet<ConcreteArrow>();

        setBoundaryRectangle(this);

        createIntersectionGrid(initialSquares);

        makeMainZone();
    }

    // tells us if this boundary rectangle is empty, but it's the diagrams job to know if there are arrows in or out
    public boolean isEmpty() {
        return myArrows.isEmpty() && myCurves.isEmpty() && mySpiders.isEmpty();
    }

    public boolean isStarRectangle() {
        return false;
    }

    protected void makeMainZone() {
        mainZone = new ConcreteZone(
                new Point(getX() + getBorderWidth(), getY() + getBorderWidth()),
                new Point(bottomRight().getX() - getBorderWidth(), bottomRight().getY() - getBorderWidth()));
        getMainZone().setLevel(0);
        getMainZone().setBoundaryRectangle(this);
        getMainZone().setCornerRadius(0);
    }

    public void setDiagram(ConcreteDiagram parentDiagram) {
        myDiagram = parentDiagram;
    }

    public ConcreteDiagram getParentDiagram() {
        return myDiagram;
    }

    protected AbstractSet<ConcreteDiagramElement> getAllChildren() {
        AbstractSet<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();
        result.addAll(getCurves());
        result.addAll(getSpiders());
        result.addAll(getArrows());
        result.addAll(getZones());
        return result;
    }

    public Set<ConcreteSpider> getSpiders() {
        return mySpiders;
    }

    public Set<ConcreteCurve> getCurves() {
        return myCurves;
    }

    public Set<ConcreteArrow> getArrows() {
        return myArrows;
    }

    public Set<ConcreteZone> getZones() {
        Set<ConcreteZone> result = new HashSet<ConcreteZone>();
        for (Set<ConcreteZone> zones : getZoneHeights()) {
            result.addAll(zones);
        }
        return result;
    }

    public List<ConcreteZone> getSortedZones() {
        List<ConcreteZone> result = new ArrayList<ConcreteZone>();
        for (Set<ConcreteZone> zones : getZoneHeights()) {
            for(ConcreteZone zone : zones) {
                result.add(zone);
            }
        }
        return result;
    }

    public Set<ConcreteZone> getShadedZones() {
        Set<ConcreteZone> result = new HashSet<ConcreteZone>();
        for(ConcreteZone z : getZones()) {
            if(z.shaded()) {
                result.add(z);
            }
        }
        return result;
    }

    public void addCurve(ConcreteCurve curve) {
        // can the cure fit inside the rectangle?
        if (completelyEncloses(curve)) {
            curve.refresh();
            getCurves().add(curve);
            addZone(curve.getMainZone());

            addCurveToIntersectionGrid(curve);

            // work out the intersections with existing curves and add the resulting zones
            HashSet<ConcreteCurve> intersectingCurves = new HashSet<ConcreteCurve>();
            for (int i = 0; i < intersectionGrid.size(); i++) {
                for (int j = 0; j < intersectionGrid.get(i).size(); j++) {
                    if (curveIsInIntersectionGridSquare(curve, j, i)) {
                        for (ConcreteCurve other : intersectionGrid.get(i).get(j)) {
                            if (other != curve) {
                                if (curve.intersectsRectangularElement(other)) {
                                    intersectingCurves.add(other);
                                    curve.addIntersectingCurve(other);
                                    other.addIntersectingCurve(curve);
                                }
                            }
                        }

                    }
                }
            }
            curve.computeAllIntersections(intersectingCurves);
        }
    }


    // removes the curve from this boundary rectangle and from any curves that it intersected
    // but all the zones of the curve etc are left untoched, so the curve looks the same externally, but the
    // rest of the diagram has 'forgotten' about it.
    //
    // Really curves should only be removed if they aren't the source or target of an arrow ... but this also works
    // during resize, cause the curve remains and adds itself back, so the arrow is ok.
    public void removeCurve(ConcreteCurve curve) {
            getCurves().remove(curve);
            removeCurveFromIntersectionGrid(curve);

            // remove this curve from the lists of zones it contains
            curve.disassociateCompletelyContainedZones();


            // now remove zones from intersecting curves
            //
            // But can't just remove everything because we really want the curve to be an image of what it once was,
            // just having been disassociated from all the rest, so this removes it but keeps this curve.
            for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
                zone.disassociateFromCurves(curve);
                //if (zone.getCurves().size() == 1) {
                removeZone(zone);

                for (ConcreteCurve c : zone.getCompletelyEnclosingCurves()) {
                    c.removeEnclosedZone(zone);
                }

            }
            removeZone(curve.getMainZone());
            for (ConcreteCurve c : curve.getMainZone().getCompletelyEnclosingCurves()) {
                c.removeEnclosedZone(curve.getMainZone());
            }

            // now remove all intersections
            curve.disassociateFromAllIntersectingCurves();
    }


    // readd a curve that was dissassociatied from this diagram ... should only be used in undo
    public void unremoveCurve(ConcreteCurve curve) {
        if(curve.getBoundaryRectangle() == this) {
            getCurves().add(curve);

            addCurveToIntersectionGrid(curve);

            // now got to add back in all those zones and the intersecting curves
            curve.reassociateCompletelyContainedZones();

            for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
                zone.reassociateToCurves(curve);

                addZone(zone);

                for (ConcreteCurve c : zone.getCompletelyEnclosingCurves()) {
                    c.addCompletelyContainedZone(zone);
                }

            }
            addZone(curve.getMainZone());
            for (ConcreteCurve c : curve.getMainZone().getCompletelyEnclosingCurves()) {
                c.addCompletelyContainedZone(curve.getMainZone());
            }

            curve.reassocitateToAllIntersectingCurves();
        }
    }

    public void addSpider(ConcreteSpider spider) {
        getSpiders().add(spider);
    }

    public void removeSpider(ConcreteSpider spider) {
        getSpiders().remove(spider);
    }

    public void addArrow(ConcreteArrow arrow) {
        if(arrow.getSource().getBoundaryRectangle() == this &&
                arrow.getTarget().getBoundaryRectangle() == this) {
            getArrows().add(arrow);
        } else if(arrow.getSource().getBoundaryRectangle() == this) {
            getDiagram().addArrow(arrow);
        } else {
            // not my arrow
        }
    }

    public void removeArrow(ConcreteArrow arrow) {
        if(arrow.getSource().getBoundaryRectangle() == this &&
                arrow.getTarget().getBoundaryRectangle() == this) {
            getArrows().remove(arrow);
            arrow.unlinkSourceAndTarget();
        } else if(arrow.getSource().getBoundaryRectangle() == this) {
            getDiagram().removeArrow(arrow);
        } else {

        }

    }


    private List<HashSet<ConcreteZone>> getZoneHeights() {
        return zoneHeights;
    }

    public void addZone(ConcreteZone zone) {
        if (getZoneHeights().size() <= zone.getLevel()) {
            // need to extend the list to accommodate
            for (int i = getZoneHeights().size(); i <= zone.getLevel(); i++) {
                getZoneHeights().add(new HashSet<ConcreteZone>());
            }
        }

        getZoneHeights().get(zone.getLevel()).add(zone);
    }

    public void removeZone(ConcreteZone zone) {
        if (zone.getLevel() < getZoneHeights().size()) {
            getZoneHeights().get(zone.getLevel()).remove(zone);
        }
    }

    public void increaseLevel(ConcreteZone zone) {
        removeZone(zone);
        zone.increaseLevel();
        addZone(zone);
    }

    public void decreaseLevel(ConcreteZone zone) {
        removeZone(zone);
        zone.decreaseLevel();
        addZone(zone);
    }

    public void setLevel(ConcreteZone zone, int newLevel) {
        removeZone(zone);
        zone.setLevel(newLevel);
        addZone(zone);
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = this;
    }

    @Override
    public void checkValidity() {
        inferType();
    }

    private void checkElementType(ConcreteDiagramElement elmnt) {
        if (elmnt.typeIsKnown()) {
            if(typeIsKnown() && (isObject() != elmnt.isObject())) {
                setValid(false);
            }
            if(!typeIsKnown()) {
                if(elmnt.isObject()) {
                    setAsObject();
                } else {
                    setAsData();
                }
                setTypeKnown();
            }
        }
//            if(!c.typeIsKnown() && typeIsKnown()) {
//                c.setTypeKnown();
//                // ...
//            }
    }

    public void inferType() {

        if(!typeIsKnown()) {
            setAsObject();
        }
        setValid(true);

        // *** at the moment in the interface can just set the boundry rectangles
        // will need something like this if we allow forexample arrow types

//        boolean isObject = isObject();
//        boolean typeknown = typeIsKnown();
//        setValid(true);  /// start with this and see if it changes

//        // firstly try to infer the type of this rectangle ... basically see if anything is data
//        for (ConcreteCurve c : getCurves()) {
//            if (c.typeIsKnown()) {
//                if(typeknown && (isObject != c.isObject())) {
//                    setValid(false);
//                }
//                if(!typeIsKnown()) {
//                    isObject = c.isObject();
//                    setTypeKnown();
//                }
//            }
////            if(!c.typeIsKnown() && typeIsKnown()) {
////                c.setTypeKnown();
////                // ...
////            }
//        }
//        for (ConcreteSpider s : getSpiders()) {
//            if (s.typeIsKnown()) {
//                if(typeknown && (isObject != s.isObject())) {
//                    setValid(false);
//                }
//                isObject = s.isObject();
//                typeknown = true;
//            }
//        }
//        for (ConcreteArrow a : getArrows()) {
//            if(a.isValid()) {
//                if (a.typeIsKnown()) {
//                    if (typeknown && (isObject != a.isObject())) {
//                        setValid(false);
//                    }
//                    isObject = a.isObject();
//                    typeknown = true;
//                }
//            } else {
//                setValid(false);
//            }
//        }
//
        if(isValid()) {
//            if (isObject) {
//                setAsObject();
//            } else {
//                setAsData();
//            }

            // no go through and set all those inferred types
            for (ConcreteCurve c : getCurves()) {
                if (isObject()) {
                    c.setAsObject();
                } else {
                    c.setAsData();
                }
            }
            for (ConcreteSpider s : getSpiders()) {
                if (isObject()) {
                    s.setAsObject();
                } else {
                    s.setAsData();
                }
            }
            for (ConcreteArrow a : getArrows()) {
                if (isObject()) {
                    a.setAsObject();
                } else {
                    a.setAsData();
                }
            }
        }
    }

//    @Override
//    public void makeAbstractRepresentation() {
////        if (!isAbstractRepresentationSyntaxUpToDate()) {
////            BoundaryRectangle result = new BoundaryRectangle();
////            setAbstractSyntaxRepresentation(result);
////        }
//    }

    // so it's been moved on the canvas, need to refresh the intersection grid.  All the curves are just where they
    // were as far as this call is concerned
    public void resize(Point topLeft, Point botRight) {
        // let's keep about the same grid square size
        double maxDim = Math.max(getWidth(), getHeight());

        setTopLeft(topLeft);
        setBottomRight(botRight);

        makeMainZone();

        createIntersectionGrid((int) Math.ceil(maxDim / gridSquareSize));

        for(ConcreteCurve c : getCurves()) {
            addCurveToIntersectionGrid(c);
        }
    }

    public ConcreteZone getMainZone() {
        return mainZone;
    }

    @Override
    public AbstractCollection<ConcreteZone> getAllZones() {
        AbstractCollection<ConcreteZone> result = new HashSet<ConcreteZone>();
        result.add(getMainZone());
        return result;
    }



    private void calculateRectangleType() {
        // sets isObjectRectangle and isValidRectangle
    }



    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------


    private void createIntersectionGrid(int numSquares) {
        // if it's exact we'll end up with (initialSquares - 1) in the min dimension, otherwise we'll end up with
        // (initialSquares - 1) full sized squares and then some bits at the end
        double minDimension = (bottomRight().getX() - topLeft().getX() > bottomRight().getY() - topLeft().getY())
                ? bottomRight().getY() - topLeft().getY() : bottomRight().getX() - topLeft().getX();
        gridSquareSize = minDimension / (numSquares - 1);

        intersectionGrid = new ArrayList<ArrayList<HashSet<ConcreteCurve>>>();
        for (int i = 0; i < (int) Math.ceil(getHeight() / gridSquareSize); i++) {
            intersectionGrid.add(new ArrayList<HashSet<ConcreteCurve>>());
            for (int j = 0; j < (int) Math.ceil(getWidth() / gridSquareSize); j++) {
                intersectionGrid.get(i).add(new HashSet<ConcreteCurve>());
            }
        }
    }


    private void addCurveToIntersectionGrid(ConcreteCurve curve) {
        // could just calculate this, do that if we get slow
        for (int i = 0; i < intersectionGrid.size(); i++) {
            for (int j = 0; j < intersectionGrid.get(i).size(); j++) {
                if (curveIsInIntersectionGridSquare(curve, j, i)) {
                    intersectionGrid.get(i).get(j).add(curve);
                }
            }
        }
    }

    private void removeCurveFromIntersectionGrid(ConcreteCurve curve) {
        for (int i = 0; i < intersectionGrid.size(); i++) {
            for (int j = 0; j < intersectionGrid.get(i).size(); j++) {
                intersectionGrid.get(i).get(j).remove(curve);
            }
        }
    }

    private boolean curveIsInIntersectionGridSquare(ConcreteCurve curve, int across, int down) {
        return rectanglesIntersect(curve.topLeft(), bottomRight(), gridSquareTopLeft(across, down), gridSquareBottomRight(across, down));
    }

    private int intersectionGridAcross(Point p) {
        return (int) Math.floor((p.getX() - getX()) / gridSquareSize);
    }

    private int intersectionGridDown(Point p) {
        return (int) Math.floor((p.getY() - getY()) / gridSquareSize);
    }

    public Set<ConcreteCurve> curvesAtPoint(Point p) {
        Set<ConcreteCurve> result = new HashSet<ConcreteCurve>();
        int i = intersectionGridAcross(p);
        int j = intersectionGridDown(p);
        if(i > 0 && j > 0 && i < intersectionGrid.size() && j < intersectionGrid.get(i).size()) {
            for(ConcreteCurve c : intersectionGrid.get(i).get(j)) {
                if(c.containsPoint(p)) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    public boolean pointInGridSquare(Point p, int across, int down) {
        return ConcreteRectangularElement.rectangleContainment(p,
                gridSquareTopLeft(across, down),
                gridSquareSize, gridSquareSize);
    }

    public Point gridSquareTopLeft(int across, int down) {
        return new Point(getX() + (across * gridSquareSize), getY() + (down * gridSquareSize));
    }

    // this might be outside the boundary rectangle itself, but for the purposes of intersection checking it doesn't
    // matter, because we are only looking at things in the rectangle.
    public Point gridSquareBottomRight(int across, int down) {
        return gridSquareTopLeft(across + 1, down + 1);
    }


    // hmmm this currently includes zones ... but often we might not want them.  For the moment
    // clients will have to remove themselves
    public AbstractSet<ConcreteDiagramElement> elementsInBoundingBox(Point topLeft, Point botRight) {
        AbstractSet<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();

        if(completelyEnclosed(topLeft, botRight)) {
            // we are completely inside the given bounds, so return everything, including this boundary rectangle
            result.add(this);
            result.addAll(getAllChildren());
        } else if(completelyEncloses(topLeft, botRight)) {
            // the bound is completely inside me
            result.addAll(elementsInBoundingBoxHelper(topLeft, botRight));
        } else {
            if(ConcreteRectangularElement.rectanglesIntersect(topLeft(), bottomRight(), topLeft, botRight)) {
                // must intersect the rectangle so add that too
                result.add(this);
                result.addAll(elementsInBoundingBoxHelper(topLeft, botRight));
            }
        }
        return result;
    }

    private AbstractSet<ConcreteDiagramElement> elementsInBoundingBoxHelper(Point topLeft, Point botRight) {
        AbstractSet<ConcreteDiagramElement> result = new HashSet<ConcreteDiagramElement>();

        for(ConcreteSpider s : getSpiders()) {
            if(ConcreteRectangularElement.rectangleContainment(s.centre(), topLeft, botRight)) {
                result.add(s);
            }
        }

        for(ConcreteArrow a : getArrows()) {
            if (a.intersectsBox(topLeft, botRight)) {
                result.add(a);
            }
        }

        // NOT quite the right test.  But should do as the only error is if  the rounded corner doens't make it into
        // the bounding box ... need to think about this for the things that call this function
        for(ConcreteCurve c : getCurves()) {
            if(ConcreteRectangularElement.rectanglesIntersect(c.topLeft(), c.bottomRight(), topLeft, botRight)) {
                result.add(c);
            }
        }

        for (ConcreteZone z : getZones()) {
            if(ConcreteRectangularElement.rectanglesIntersect(z.topLeft(), z.bottomRight(), topLeft, botRight)) {
                result.add(z);
            }
        }

        return result;
    }

}
