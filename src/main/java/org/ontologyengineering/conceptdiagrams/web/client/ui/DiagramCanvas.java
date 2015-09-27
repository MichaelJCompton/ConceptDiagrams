package org.ontologyengineering.conceptdiagrams.web.client.ui;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.gwt.user.client.ui.Panel;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Abstract class for all canvases for drawing diagrams.
 * <p/>
 * At the moment we have only Lienzo in GWT, but trying this to keep the rest of the code stable as/if we add more.
 * <p/>
 * <p/>
 * The Canvas has the responsibility for the placement and drawing of the concrete elements (deferred to
 * DiagramPainter). It controls the zoom and pan, and also draws the miniature map representation.  The canvas also
 * catches application relevant events (such as an added curve) and defers the drawing of them to the painter.
 * <p/>
 * Each Canvas could have one diagram or could have multiple diagrams (multiple boundary rectangles) on it.  In turn
 * these could be individual diagrams or connected concept diagrams with arrows between the boundary rectangles.
 * <p/>
 * There could be multiple canvases in an application, each drawing some different aspect of the ontology.  Each canvas
 * is independent of the others.
 */
public abstract class DiagramCanvas {


    protected static final double initBoundaryRectangleXoffset = 20;
    protected static final double initBoundaryRectangleYoffset = 20;

    protected final double initBoundaryRectangleWidth;
    protected final double initBoundaryRectangleHeight;

    private AbstractSet<ConcreteDiagramElement> selectedElements;


    // should be a diagram set ???
    private AbstractSet<ConcreteDiagram> diagramsOnCanvas;


    public enum ModeTypes {
        NONE,
        SELECTION, DRAGSELECT, PAN, PANNING, ZOOM, ZOOMING,
        DRAWCURVE, DRAWINGCRVE, DRAWSPIDER, DRAWINGSPIDER, SHADE,
        DRAWARROW, DRAWINGARROW, DRAGINGARROW,
        DRAWBOUNDARYRECTANGLE, DRAWINGBOUNDARYRECTANGLE,
        DRAWSTARRECTANGLE, DRAWINGSTARRECTANGLE,
        DELETE
    }

    private ModeTypes mode;

    // seems easiest way to find what is under the mouse at any point is to keep a record in the mouse handlers
    private ConcreteDiagramElement underMouse;


    private int width, height;
    private Panel parentPanel;

    public DiagramCanvas(int width, int height, Panel addToThis) {
        this.width = width;
        this.height = height;
        parentPanel = addToThis;

        initBoundaryRectangleWidth = width - (2 * initBoundaryRectangleXoffset);
        initBoundaryRectangleHeight = height - (2 * initBoundaryRectangleYoffset);

        diagramsOnCanvas = new HashSet<ConcreteDiagram>();
        mode = ModeTypes.SELECTION;
        selectedElements = new HashSet<ConcreteDiagramElement>();
        clearSelection();
    }


    public void setIsUnderMouse(ConcreteDiagramElement element) {
        underMouse = element;
    }

    protected ConcreteDiagramElement underMouse() {
        return underMouse;
    }

    protected void addDiagram(ConcreteDiagram diagram) {
        diagramsOnCanvas.add(diagram);
    }

    protected void removeDiagram(ConcreteDiagram diagram) {
        diagramsOnCanvas.remove(diagram);
    }

    private AbstractSet<ConcreteDiagram> getDiagramsOnCanvas() {
        return diagramsOnCanvas;
    }

    protected ConcreteBoundaryRectangle boundaryRectangleAtPoint(Point p) {
        for (ConcreteDiagram d : getDiagramsOnCanvas()) {
            for (ConcreteBoundaryRectangle rectangle : d.getRectangles()) {
                if (rectangle.containsPoint(p)) {
                    return rectangle;
                }
            }
        }
        return null;
    }


    protected void clearSelection() {
        for (ConcreteDiagramElement elmnt : getSelectedElements()) {
            setNotSelected(elmnt);
        }

        // is it faster to clear or make new ??
        selectedElements.clear();
    }

    protected abstract void setNotSelected(ConcreteDiagramElement elmnt);

    protected abstract void setSelected(ConcreteDiagramElement elmnt);

    public AbstractSet<ConcreteDiagramElement> getSelectedElements() {
        return selectedElements;
    }

    // only works if there's just one
    public ConcreteDiagramElement getSelectedElement() {
        if(getSelectedElements().size() == 1) {
            for(ConcreteDiagramElement e : getSelectedElements()) {
                return e;
            }
        }
        return null;
    }


    public void addSelectedElement(ConcreteDiagramElement elmnt) {
        getSelectedElements().add(elmnt);
    }

    public void setAsSelectedElement(ConcreteDiagramElement elmnt) {
        clearSelection();
        getSelectedElements().add(elmnt);
        setSelected(elmnt);
    }

    public abstract void createCanvas();

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected Panel getParentPanel() {
        return parentPanel;
    }

    public void setMode(ModeTypes mode) {
        this.mode = mode;
    }

    protected ModeTypes getMode() {
        return mode;
    }

    // FIXME : needs to use the command infrastructure
    protected void addCurve(Point topLeft, Point bottomRight, ConcreteBoundaryRectangle parentRectangle) {

        CommandManager.get().executeCommand(new AddCurveCommand(topLeft, bottomRight, parentRectangle));

//
        // FIXME now happens in the painter
//        //elementsOnCanvas.add(curve);
//        //elementsOnCanvas.add(curve.getMainZone());
//


        // FIXME now in painter
//        curve.drawOnLayer(curveLayer);
//        curve.drawZonesOnLayer(curveLayer);
//        // redraw the whole boundary rectangle that this is on ... maybe with connected components or something could make nicer
//        // FIXME : need to pick the rectangle here once we have many
//        curve.getBoundaryRectangle().drawOnLayer(curveLayer);

    }

    protected void addSpider(Point centre, ConcreteBoundaryRectangle parentRectangle) {
        CommandManager.get().executeCommand(new AddSpiderCommand(centre, parentRectangle));
    }

    protected void addBoundaryRectangle(Point topLeft, Point bottomRight) {
        CommandManager.get().executeCommand(new AddBoundaryRectangleCommand(topLeft, bottomRight));


        // FIXME called from elsewhere
//        initialBoundaryRectangle =
//                new ConcreteBoundaryRectangle(initBoundaryRectangleXoffset, initBoundaryRectangleYoffset);
//        initialBoundaryRectangle.setWidth(initBoundaryRectangleWidth);
//        initialBoundaryRectangle.setHeight(initBoundaryRectangleHeight);
//
//        initialBoundaryRectangle.makeConcreteRepresentation();
//        //elementsOnCanvas.add(initialBoundaryRectangle);
//        initialBoundaryRectangle.setZoneLayer(zoneLayer);
//        initialBoundaryRectangle.setBoundaryRectangleLayer(boundaryRectangleLayer);
//        initialBoundaryRectangle.drawOnLayer(curveLayer);
    }

    protected void addStarRectangle(Point topLeft, Point bottomRight) {
        CommandManager.get().executeCommand(new AddStarRectangleCommand(topLeft, bottomRight));
    }


    public void compileAllToAbstractSyntax() {
        //for(ConcreteSyntaxElement e : elementsOnCanvas) {
        //  e.makeAbstractRepresentation();
        //}
    }
}
