package org.ontologyengineering.conceptdiagrams.web.shared.presenter;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */



import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Abstract class for all canvases for drawing diagrams.
 * <p/>
 * The idea is to separate and abstract the painting (DiagramPainter) the UI and interface state (DiagramCanvas) and
 * the logic of what can be painted, how to keep a history etc (DiagramPresenter).
 * At the moment we have only Lienzo in GWT, but trying this to keep the rest of the code stable as/if we add more.
 * <p/>
 * <p/>
 * The Canvas has the responsibility for the user interface and state of the drawing application.
 * It controls the zoom and pan, and also draws the miniature map representation.
 * <p/>
 * Each Canvas could have one diagram or could have multiple diagrams (multiple boundary rectangles) on it.  In turn
 * these could be individual diagrams or connected concept diagrams with arrows between the boundary rectangles.
 * <p/>
 * There could be multiple canvases in an application, each drawing some different aspect of the ontology.  Each canvas
 * is independent of the others.
 */
public abstract class DiagramCanvas {

    public enum ModeTypes {
        NONE,
        SELECTION, DRAGSELECT, PAN, PANNING, ZOOM, ZOOMING,
        DRAWCURVE, DRAWINGCURVE, DRAWSPIDER, DRAWINGSPIDER, SHADE,
        DRAWARROW, DRAWINGARROW, DRAGINGARROW,
        DRAWBOUNDARYRECTANGLE, DRAWINGBOUNDARYRECTANGLE,
        DRAWSTARRECTANGLE, DRAWINGSTARRECTANGLE,
        DELETE
    }

    private ModeTypes mode = ModeTypes.NONE;

    private AbstractSet<ConcreteDiagramElement> selectedElements = new HashSet<ConcreteDiagramElement>();;

    protected DiagramPresenter presenter;     // could be done by firing messages, but this is simpler at this stage

    // seems easiest way to find what is under the mouse at any point is to keep a record in the mouse handlers
    private ConcreteDiagramElement underMouse;


    public DiagramCanvas() {

    }


    protected void setPresenter(DiagramPresenter presenter) {
        this.presenter = presenter;
    }

    protected void setAsFreshCanvas() {
        clearUnderMouse();
        mode = ModeTypes.NONE;
        clearSelection();
    }

    protected ModeTypes getMode() {
        return mode;
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

    public AbstractSet<ConcreteDiagramElement> getSelectedElements() {
        return selectedElements;
    }

    // only works if there's just one
    public ConcreteDiagramElement getSelectedElement() {
        if(getSelectedElements().size() == 1) {
            return getSelectedElements().iterator().next();
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

    protected void clearSelection() {
        selectedElements.clear();
    }

    protected abstract void drawSelectedRepresentation();

    protected ConcreteBoundaryRectangle boundaryRectangleAtPoint(String ID, Point p) {
        return presenter.boundaryRectangleAtPoint(ID, p);
    }


    // transition from the current mode to the newly given mode.
    // Responsible for setting and correcting any state, leftover bits from the old mode and windowing changes,
    // highlights, context etc in the change of mode.
    public void setMode(ModeTypes mode) {
        this.mode = mode;
    }


    public abstract void setAsFocusDiagram(String ID);

    public DiagramPresenter getPresenter() {
        return presenter;
    }

    public abstract DiagramPainter getPainter();
}
