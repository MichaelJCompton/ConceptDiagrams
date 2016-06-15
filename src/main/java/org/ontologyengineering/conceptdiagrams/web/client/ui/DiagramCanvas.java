package org.ontologyengineering.conceptdiagrams.web.client.ui;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.gwt.user.client.ui.Panel;
import org.ontologyengineering.conceptdiagrams.web.client.handler.ConvertToOWLServiceManager;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

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

    private DiagramSet diagramsOnCanvas;


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

    private ConvertToOWLServiceManager converToOWLsrvc;

    // now accessed through the service manager
    //private ClientContext context;

    public DiagramCanvas(int width, int height, Panel addToThis, ConvertToOWLServiceManager converToOWLsrvc) {
        this.width = width;
        this.height = height;
        parentPanel = addToThis;

        //this.context = context;
        this.converToOWLsrvc = converToOWLsrvc;

        initBoundaryRectangleWidth = width - (2 * initBoundaryRectangleXoffset);
        initBoundaryRectangleHeight = height - (2 * initBoundaryRectangleYoffset);

        diagramsOnCanvas = new DiagramSet();
        mode = ModeTypes.SELECTION;
        selectedElements = new HashSet<ConcreteDiagramElement>();
        //clearSelection();
    }

    protected void clearAll() {
        underMouse = null;
        diagramsOnCanvas = new DiagramSet();
        mode = ModeTypes.SELECTION;
        clearSelection();
        CommandManager.get().clearAll();
    }

    public void setIsUnderMouse(ConcreteDiagramElement element) {
        underMouse = element;
    }

    public void clearUnderMouse() {
        underMouse = null;
    }

    protected ConcreteDiagramElement underMouse() {
        return underMouse;
    }

    // FIXME : not happy with this.  Should only ever be one call to this at the very start when we
    // set the first diagram, but really should all be handled by the diagram set and not the canvas.
    // Maybe if we were allowing the first boundary rectangle to be undone it would all be handled by that
    // first command and everything would be ok??
    protected void addDiagram(ConcreteDiagram diagram) {
        diagramsOnCanvas.addDiagram(diagram);
    }

    protected void removeDiagram(ConcreteDiagram diagram) {
        diagramsOnCanvas.removeDiagram(diagram);
    }

    protected DiagramSet getDiagramsOnCanvas() {
        return diagramsOnCanvas;
    }

    protected ConcreteBoundaryRectangle boundaryRectangleAtPoint(Point p) {
        return getDiagramsOnCanvas().boundaryRectangleAtPoint(p);
    }


    protected void clearSelection() {
//        for (ConcreteDiagramElement elmnt : getSelectedElements()) {
//            setNotSelected(elmnt);
//        }

        // is it faster to clear or make new ??
        selectedElements.clear();
    }

//    protected abstract void setNotSelected(ConcreteDiagramElement elmnt);
//
//    protected abstract void setSelected(ConcreteDiagramElement elmnt);



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

    public void removeSelectedElement(ConcreteDiagramElement element) {
        getSelectedElements().remove(element);
    }

    public void setAsSelectedElement(ConcreteDiagramElement elmnt) {
        clearSelection();
        addSelectedElement(elmnt);
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


    protected void addCurve(Point topLeft, Point bottomRight, ConcreteBoundaryRectangle parentRectangle) {
        CommandManager.get().executeCommand(new AddCurveCommand(topLeft, bottomRight, parentRectangle));
    }

    protected void addSpider(Point centre, ConcreteBoundaryRectangle parentRectangle) {
        CommandManager.get().executeCommand(new AddSpiderCommand(centre, parentRectangle));
    }

    protected void addBoundaryRectangle(Point topLeft, Point bottomRight) {
        CommandManager.get().executeCommand(new AddBoundaryRectangleCommand(topLeft, bottomRight, getDiagramsOnCanvas()));
    }

    protected void addStarRectangle(Point topLeft, Point bottomRight) {
        CommandManager.get().executeCommand(new AddStarRectangleCommand(topLeft, bottomRight, getDiagramsOnCanvas()));
    }


    public void compileAllToAbstractSyntax() {
        //for(ConcreteSyntaxElement e : elementsOnCanvas) {
        //  e.makeAbstractRepresentation();
        //}
    }

    // compile everything to the diagrams abstract syntax and on to OWL
    public void compileToOWL() {

        // should have been set to the right service at initialisation
        converToOWLsrvc.convertAllToOWL(CommandManager.get().getUndoList(), diagramsOnCanvas);
    }
}
