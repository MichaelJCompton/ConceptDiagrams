package org.ontologyengineering.conceptdiagrams.web.shared.presenter;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.ontologyengineering.conceptdiagrams.web.client.events.*;
import org.ontologyengineering.conceptdiagrams.web.client.handler.*;
import org.ontologyengineering.conceptdiagrams.web.shared.OntologyFormat;
import org.ontologyengineering.conceptdiagrams.web.shared.WebProtegeClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A DiagramPresenter is the interface between any UI code and the backend (though some is client side) data strucutres
 * and the user interface.  It's basically the MVP pattern.  The division of responsibilities is:
 *
 * Painter - just draws what it's told
 *
 * Canvas - the bulk of the user interface, the widgets etc (and some panel for the painter to draw on).  It manages
 *  the user interface state and all the display ... but it doesn't do the application logic - when a user clicks and
 *  drags to draw a curve it doesn't decide what to do, it just records the users intention and then asks the presenter
 *  to do the action.
 *
 * Presenter - encodes all the diagrams state and keeps track of constraints etc for the drawing.  If the UI asks to
 *  put a curve at (x,y) the presenter checks all the constraints and either ignores it, or goes away and does all the
 *  creation.  Later it might get an event telling it to ask the painter to display the new curve.
 *
 * This way all the app specific code is here and only the drawing and UI is in the canvas and painter, so no need to
 * write an code other than the specifics of the UI there (no application events etc) ... just implement the interface.
 * Should also be able to keep rubber banding and snap etc in this one class rather than implementing for different
 * interfaces.
 */
public class DiagramPresenter {

    private DiagramCanvas canvas;

    private HashMap<String, DiagramSet> diagrams = new HashMap<String, DiagramSet>();           // uniqueID -> DiagramSet
    private HashMap<String, CommandManager> managers = new HashMap<String, CommandManager>();   // uniqueID -> CommandManager

    private ConvertToOWLServiceManager converToOWLsrvc;


    private OntologyFormat format = OntologyFormat.OWLXML; // default format
    //private String fileName;  // this is the prefix of the filenames
    // e.g: /blaa/blaa/blaa/myontologyname
    // and save will save diagrams to
    // /blaa/blaa/blaa/myontologyname.diag
    // and compile to OWL will save to
    // /blaa/blaa/blaa/myontologyname.owl  (in whatever format was chosen by the user)

    public DiagramPresenter(DiagramCanvas canvas, ConvertToOWLServiceManager converToOWLsrvc) {
        this.canvas = canvas;
        this.converToOWLsrvc = converToOWLsrvc;
        canvas.setPresenter(this);

        registerForDiagramEvents(CommandManager.getEventBus());

        // make sure we were given a clean slate
        canvas.setAsFreshCanvas();
    }

//    public void setFileLocation(String file) {
//        fileName = file;
//    }




//    public String getFileName() {
//        return fileName;
//    }


    public void compileToOWL() {
        HashSet<ArrayList<Command>> histories = new HashSet<ArrayList<Command>>();
        for(CommandManager manager : managers.values()) {
            histories.add(manager.getUndoList());
        }

        converToOWLsrvc.convertAllToOWL(histories, diagrams);
    }


    public void saveALL() {

        SaveDiagramServiceAsync saveAllService;
        saveAllService = GWT.create(SaveDiagramService.class);

        HashSet<ArrayList<Command>> histories = new HashSet<ArrayList<Command>>();
        for(CommandManager manager : managers.values()) {
            histories.add(manager.getUndoList());
        }


        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }

            public void onSuccess(String result) {
                // result is the name of the file returned ... so save it
                String url = GWT.getModuleBaseURL() + "ontologyDownloadService?filename=" + result;
                Window.open(url, "_blank", ""); //status=0,toolbar=0,menubar=0,location=0
            }
        };



        saveAllService.saveCommandHistory(histories, diagrams, converToOWLsrvc.getContext(), callback);
    }

    public void loadFrom(String filename) {

        LoadDiagramServiceAsync loadAllService;
        loadAllService = GWT.create(LoadDiagramService.class);

        AsyncCallback<HashSet<ArrayList<Command>>> callback = new AsyncCallback<HashSet<ArrayList<Command>>>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }

            public void onSuccess(HashSet<ArrayList<Command>> commandHistories) {
                setAllAs(commandHistories);
            }
        };


        loadAllService.loadCommandHistory(filename, callback);
    }

    public void setAllAs(HashSet<ArrayList<Command>> commandSet) {

        // assume somethingelse has checked that this is ok
        // ... just blank everything set the given commands as the right ones and redraw everything


        // wipe everything
        canvas.setAsFreshCanvas();


        // set up the diagram sets and commands
        diagrams = new HashMap<String, DiagramSet>();
        managers = new HashMap<String, CommandManager>();

        for(ArrayList<Command> commands : commandSet) {
            if(commands.size() > 0) {
                Command first = commands.get(0);

                DiagramSet diagSet = first.getDiagram().getDiagramSet();

                diagrams.put(diagSet.getUniqueID(), diagSet);
                managers.put(diagSet.getUniqueID(), new CommandManager(commands));
            }
        }


        // Draw everything on the canvas : setup new canvases, then for each reexecute the commands
        // which should fire anything in the system that draws or is otherwise interested in those events
        for(String diagramID : diagrams.keySet()) {
            canvas.addNewCanvas(diagramID, diagrams.get(diagramID).getLabel());
        }
        for(String diagramID : diagrams.keySet()) {
            canvas.setAsFocusDiagram(diagramID);
            // some of this will be bogus (e.g. the moves, which will always move to the final position
            // - what about delete?) ... but with any luck, it will be ok for drawing
            managers.get(diagramID).reFireAll();
        }
    }




    public OntologyFormat getFormat() {
        return converToOWLsrvc.getContext().getOntologyFormat();
    }

    public void setFormat(OntologyFormat format) {
        converToOWLsrvc.getContext().setOntologyFormat(format);
    }

    // interface needs to have different behaviour in different contexts
    public boolean isWebProtege() {
        return converToOWLsrvc.getContext() instanceof WebProtegeClientContext;
    }

    public DiagramSet getDiagramSetByID(String ID) {
        return diagrams.get(ID);
    }

    public CommandManager getManagerByID(String ID) {
        return managers.get(ID);
    }

    protected ConcreteBoundaryRectangle boundaryRectangleAtPoint(String ID, Point p) {
        DiagramSet set = getDiagramSetByID(ID);
        if(set != null) {
            return set.boundaryRectangleAtPoint(p);
        }
        return null;
    }


    public String newDiagramSet(String label) {
        DiagramSet ds = new DiagramSet();
        ds.setLabel(label);
        diagrams.put(ds.getUniqueID(), ds);
        managers.put(ds.getUniqueID(), new CommandManager());
        return ds.getUniqueID();
    }

    // can't be undone
    public void deleteDiagramSet(String ID) {
        diagrams.remove(ID);
        managers.remove(ID);
    }

    public void setDiagramType(String ID, DiagramSet.Diagram_TYPE newType) {
        DiagramSet ds = getDiagramSetByID(ID);
        if(ds != null) {
            ds.trySetAsType(newType);
        }
    }

    public void setDiagramName(String ID, String newName) {
        DiagramSet ds = getDiagramSetByID(ID);
        if(ds != null) {
            ds.setLabel(newName);
        }
    }

    public void setIRI(String IRI) {
        converToOWLsrvc.getContext().setIRI(IRI);
    }

    public String getIRI() {
        return converToOWLsrvc.getContext().getIRI();
    }


//    protected void addDiagram(ConcreteDiagram diagram) {
//        diagramsOnCanvas.addDiagram(diagram);
//    }
//
//    protected void removeDiagram(ConcreteDiagram diagram) {
//        diagramsOnCanvas.removeDiagram(diagram);
//    }
//
//    protected DiagramSet getDiagramsOnCanvas() {
//        return diagramsOnCanvas;
//    }






    // --------------------------------
    // constraint checking
    // --------------------------------


    public boolean canHaveLabel(ConcreteDiagramElement elmnt) {
        return elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE ||
                elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEARROW ||
                elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER;
    }

    public boolean canBeArrowSource(ConcreteDiagramElement elmnt) {
        return elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE;
    }

    public boolean canBeArrowTarget(ConcreteDiagramElement elmnt) {
        return elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE;
    }


    public boolean deletable(ConcreteDiagramElement elmnt) {
        return elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEARROW
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE
                || elmnt.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE;
    }


    public boolean validBoundaryRectangleStart(String ID, Point p) {
        return (boundaryRectangleAtPoint(ID, p) == null);
    }

    // can this be a valid corner (any of the four) to start draging out a curve
    public boolean validCurveStart(String ID, Point p) {
        // firstly is it in a boundary rectangle
        ConcreteBoundaryRectangle br = boundaryRectangleAtPoint(ID, p);
        if(br != null && !(br instanceof ConcreteStarRectangle)) {

            // can't lie on the rectangles lines
            if(br.onLines(p, 0)) {
                return false;
            }


            // now does it satisfy the small curves constraint
            for(ConcreteCurve c : br.curvesAtPoint(p)) {
                // p is inside c ... does it lie atleast radius distance from the edges
                if(p.getX() < (c.getX() + c.getCornerRadius()) ||
                        p.getY() < (c.getY() + c.getCornerRadius()) ||
                        p.getX() > (c.getX() + c.getWidth() - c.getCornerRadius()) ||
                        p.getY() > (c.getY() + c.getHeight() - c.getCornerRadius())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    // is this a valid spot to drop a spider
    public boolean validSpiderPoint(String ID, Point p) {
        ConcreteBoundaryRectangle br = boundaryRectangleAtPoint(ID, p);
        if(br != null && !(br instanceof ConcreteStarRectangle)) {
            // also want it to be clear what zone the spider is in, so it can't be on a line ... or hanging over the
            // edge of one (curve or boundary rectangle).

            // first off check the rectangle
            if(br.onLines(p, ConcreteDiagramElement.spiderRadius)) {
                return false;
            }

            // can't just use curvesAtPoint, cause the radius might hang over the intersection grid
            // could collect together the curves at p, plus the N, E, S and W points of the radius.
            for(ConcreteCurve c : br.getCurves()) {
                if(c.onLines(p, ConcreteDiagramElement.spiderRadius)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }


    public boolean validCurve(String ID, Point topLeft, Point botRight) {
        ConcreteBoundaryRectangle br = boundaryRectangleAtPoint(ID, topLeft);
        if(br != null && br == boundaryRectangleAtPoint(ID, botRight) &&
                !br.onLines(topLeft, 0) && !br.onLines(botRight, 0)) {

            // it's in a single rectangle, is it big enough
            if(!ConcreteCurve.validCurveSize(topLeft, botRight)) {
                return false;
            }

            // are its intersections big enough

            // FIXME ... should it be just that all 4 corners are valid???

            // possible speed up here to use the BR to do the check using it's intersection grid ... that's what it's for
            for(ConcreteCurve c : br.getCurves()) {
                if(ConcreteRectangularElement.rectanglesIntersect(topLeft, botRight, c.topLeft(), c.bottomRight())) {

                    // need to check that the lines are far enough apart
                    if(Math.abs(topLeft.getX() - c.topLeft().getX()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(topLeft.getX() - c.bottomRight().getX()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(botRight.getX() - c.topLeft().getX()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(botRight.getX() - c.bottomRight().getX()) < ConcreteDiagramElement.curveCornerRadius ||
                            // Y
                            Math.abs(topLeft.getY() - c.topLeft().getY()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(topLeft.getY() - c.bottomRight().getY()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(botRight.getY() - c.topLeft().getY()) < ConcreteDiagramElement.curveCornerRadius ||
                            Math.abs(botRight.getY() - c.bottomRight().getY()) < ConcreteDiagramElement.curveCornerRadius) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }


    // valid if it doesn't intersect (or contain or be contained by) any existing boundary rectangles
    public boolean validBoundaryRectangle(String ID, Point topLeft, Point botRight) {
        // is it big enough to fit one curve?
        // FIXME : this should include some delta, so there is a little space around that one curve
        if(!ConcreteCurve.validCurveSize(topLeft, botRight)) {
            return false;
        }

        // does it intersect any existing rectangles
        for(ConcreteDiagram d : getDiagramSetByID(ID).getDiagrams()) {
            for(ConcreteBoundaryRectangle br : d.getRectangles()) {
                if(ConcreteRectangularElement.rectanglesIntersect(topLeft, botRight, br.topLeft(), br.bottomRight())) {
                    return false;
                }
            }
        }
        return true;
    }



    // --------------------------------
    // Adding (and removing) elements
    // --------------------------------


    public void addCurve(String ID, Point topLeft, Point botRight) {
        if(validCurve(ID, topLeft, botRight)) {
            getManagerByID(ID).executeCommand(new AddCurveCommand(topLeft, botRight, boundaryRectangleAtPoint(ID, topLeft)));
        }
    }


    public void addSpider(String ID, Point centre) {
        if(validSpiderPoint(ID, centre)) {
            getManagerByID(ID).executeCommand(new AddSpiderCommand(centre, boundaryRectangleAtPoint(ID, centre)));
        }
    }


    public void addBoundaryRectangle(String ID, Point topLeft, Point botRight) {
        if(validBoundaryRectangle(ID, topLeft, botRight)) {
            getManagerByID(ID).executeCommand(new AddBoundaryRectangleCommand(topLeft, botRight, getDiagramSetByID(ID)));
        }
    }


    public void addStarRectangle(String ID, Point topLeft, Point botRight) {
        if(validBoundaryRectangle(ID, topLeft, botRight)) {
            getManagerByID(ID).executeCommand(new AddStarRectangleCommand(topLeft, botRight, getDiagramSetByID(ID)));
        }
    }


    public void addArrow(String ID, Point sourcePoint, ConcreteDiagramElement source, Point targetPoint, ConcreteDiagramElement target) {
        if (canBeArrowSource(source) && canBeArrowTarget(target)
                && getDiagramSetByID(ID).containsElement(source)
                && getDiagramSetByID(ID).containsElement(target)) {
            getManagerByID(ID).executeCommand(new AddArrowCommand(sourcePoint, targetPoint, source, target));
        }
    }

    public void changeLabel(String ID, ConcreteDiagramElement elmnt, String newLabel) {
        Point pos;
        if(elmnt.hasLabel()) {
            pos = elmnt.getLabelPosition();
        } else {
            pos = new Point(elmnt.getX() + 5, elmnt.getY() - 10);
        }
        getManagerByID(ID).executeCommand(new ChangeLabelCommand(elmnt, newLabel, pos));
    }

    public void changeLabel(String ID, ConcreteDiagramElement elmnt, Point newPosition) {
        getManagerByID(ID).executeCommand(new ChangeLabelCommand(elmnt, elmnt.labelText(), newPosition));
    }

    public void changeConstraint(String ID, ConcreteArrow arrow, ConcreteArrow.CardinalityConstraint newConstraint, Integer newCardinality,
                                 boolean newDashed, boolean newInverse) {
        getManagerByID(ID).executeCommand(new ChangeArrowConstraintsCommand(arrow, newConstraint, newCardinality, newDashed, newInverse));
    }

    public void changeType(String ID, ConcreteDiagramElement elmnt, boolean isKnown, boolean isObject) {
        getManagerByID(ID).executeCommand(new ChangeElementTypeCommand(elmnt, isKnown, isObject));
    }

    public void flipZoneShading(String ID, ConcreteZone z) {
        if(getDiagramSetByID(ID).containsElement(z)) {
            getManagerByID(ID).executeCommand(new ChangeZoneShadingCommand(z));
        }
    }


    public void resizeElement(ConcreteRectangularElement elmnt, Point newTopLeft, Point newTopRight) {
        if(elmnt != null) {
            getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(
                    new ResizeCommand(elmnt, newTopLeft, newTopRight));
        }
    }


    public void moveElement(ConcreteDiagramElement elmnt, Point newTopLeft) {
        if(elmnt != null) {
            getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(
                    new MoveCommand(elmnt, newTopLeft));
        }
    }


    // There are some constraints on removal.
    // - can't remove something that is an arrow source or target
    // - can't remove a boundary rectangle if it's not empty
    public void removeElement(ConcreteDiagramElement elmnt) {
        if(deletable(elmnt)) {
            if(elmnt.getSourcedArrows().isEmpty() && elmnt.getTargetedArrows().isEmpty()) {
                switch(elmnt.getType()) {
                    case CONCRETECURVE:
                        getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(new RemoveCurveCommand((ConcreteCurve) elmnt));
                        break;
                    case CONCRETEARROW:
                        getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(new RemoveArrowCommand((ConcreteArrow) elmnt));
                        break;
                    case CONCRETESPIDER:
                        getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(new RemoveSpiderCommand((ConcreteSpider) elmnt));
                        break;
                    case CONCRETEBOUNDARYRECTANGE:
                        if(((ConcreteBoundaryRectangle) elmnt).isEmpty()) {
                            getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(new RemoveBoundaryRectangleCommand((ConcreteBoundaryRectangle) elmnt));
                            break;
                        }
                    case CONCRETESTARRECTANGLE:
                        if(((ConcreteBoundaryRectangle) elmnt).isEmpty()) {
                            getManagerByID(elmnt.getDiagramSet().getUniqueID()).executeCommand(new RemoveBoundaryRectangleCommand((ConcreteBoundaryRectangle) elmnt));
                            break;
                        }
                }
            }
        }
    }




    // --------------------------------
    // diagram state management
    // --------------------------------


    public boolean canUndo(String ID) {
        CommandManager manager = getManagerByID(ID);
        if(manager != null) {
            return manager.canUndo();
        }
        return false;
    }

    public boolean canRedo(String ID) {
        CommandManager manager = getManagerByID(ID);
        if(manager != null) {
            return getManagerByID(ID).canRedo();
        }
        return false;
    }

    // redo the last undone edit for the given diagram set
    public void redo(String ID) {
        if(canRedo(ID)) {
            getManagerByID(ID).redo();
        }
    }

    // undo the last undone edit for the given diagram set
    public void undo(String ID) {
        if(canUndo(ID)) {
            getManagerByID(ID).undo();
        }
    }




    // --------------------------------
    // events & drawing
    // --------------------------------

    protected void registerForDiagramEvents(EventBus eventBus) {

        eventBus.addHandler(AddCurveEvent.TYPE,
                new AddCurveEventHandler() {
                    public void onAddCurve(AddCurveEvent event) {
                        canvas.getPainter().drawCurve(event.getCurve());
                        canvas.setAsSelectedElement(event.getCurve());
                        canvas.drawSelectedRepresentation();
                    }
                });

        eventBus.addHandler(RemoveCurveEvent.TYPE,
                new RemoveCurveEventHandler() {
                    public void onRemoveCurve(RemoveCurveEvent event) {
                        canvas.getPainter().removeCurve(event.getCurve());
                    }
                });

        eventBus.addHandler(AddZoneEvent.TYPE,
                new AddZoneEventHandler() {
                    public void onAddZone(AddZoneEvent event) {
                        canvas.getPainter().drawZone(event.getAddedZone());
                    }
                });

        eventBus.addHandler(RemoveZoneEvent.TYPE,
                new RemoveZoneEventHandler() {
                    public void onRemoveZone(RemoveZoneEvent event) {
                        canvas.getPainter().removeZone(event.getRemovedZone());
                    }
                });

        eventBus.addHandler(AddSpiderEvent.TYPE,
                new AddSpiderEventHandler() {
                    public void onAddSpider(AddSpiderEvent event) {
                        canvas.getPainter().drawSpider(event.getSpider());
                        canvas.setAsSelectedElement(event.getSpider());
                        canvas.drawSelectedRepresentation();
                    }
                });

        eventBus.addHandler(RemoveSpiderEvent.TYPE,
                new RemoveSpiderEventHandler() {
                    public void onRemoveSpider(RemoveSpiderEvent event) {
                        canvas.getPainter().removeSpider(event.getSpider());
                    }
                });

        eventBus.addHandler(AddBoundaryRectangleEvent.TYPE,
                new AddBoundaryRectangleEventHandler() {
                    public void onAddBoundaryRectangle(AddBoundaryRectangleEvent event) {
                        canvas.getPainter().drawRectangle(event.getBoundaryRectangle());
                        canvas.setAsSelectedElement(event.getBoundaryRectangle());
                        canvas.drawSelectedRepresentation();
                    }
                });

        eventBus.addHandler(AddStarRectangleEvent.TYPE,
                new AddStarRectangleEventHandler() {
                    public void onAddStarRectangle(AddStarRectangleEvent event) {
                        canvas.getPainter().drawStarRectangle(event.getAddedRectangle());
                        canvas.setAsSelectedElement(event.getAddedRectangle());
                        canvas.drawSelectedRepresentation();
                    }
                });

        eventBus.addHandler(RemoveBoundaryRectangleEvent.TYPE,
                new RemoveBoundaryRectangleEventHandler() {
                    public void onRemoveBoundaryRectangle(RemoveBoundaryRectangleEvent event) {
                        drawRemoveBoundaryRectangle(event.getBoundaryRectangle());
                    }
                });

        eventBus.addHandler(ChangeZoneShadingEvent.TYPE,
                new ChangeZoneShadingEventHandler() {
                    public void onChangeZoneShading(ChangeZoneShadingEvent event) {
                        drawChangeShading(event.getZoneChanged());
                    }
                });

        eventBus.addHandler(AddArrowEvent.TYPE,
                new AddArrowEventHandler() {
                    public void onAddArrow(AddArrowEvent event) {
                        drawAddArrow(event.getAddedArrow());
                    }
                });

        eventBus.addHandler(RemoveArrowEvent.TYPE,
                new RemoveArrowEventHandler() {
                    public void onRemoveArrow(RemoveArrowEvent event) {
                        drawRemoveArrow(event.getRemovedArrow());
                    }
                });

        eventBus.addHandler(ResizeElementEvent.TYPE,
                new ResizeElementEventHandler() {
                    public void onResizeElement(ResizeElementEvent event) {
                        drawResizeElement(event.getResizedElement());
                    }
                });


        eventBus.addHandler(MoveElementEvent.TYPE,
                new MoveElementEventHandler() {
                    public void onMoveElement(MoveElementEvent event) {
                        drawMoveElement(event.getMovedElement());
                    }
                });



        eventBus.addHandler(ChangeLabelEvent.TYPE, new ChangeLabelEventHandler() {
            public void onChangeLabel(ChangeLabelEvent event) {
                drawChangeLabel(event.changedElement());
            }
        });
    }


    private void drawRemoveBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        canvas.getPainter().removeRectangle(rect);
    }

    private void drawChangeShading(ConcreteZone zone) {
        if (zone.shaded()) {
            canvas.getPainter().shadeZone(zone);
        } else {
            canvas.getPainter().unShadeZone(zone);
        }
    }

    private void drawAddArrow(ConcreteArrow arrow) {
        canvas.getPainter().drawArrow(arrow);
        canvas.setAsSelectedElement(arrow);
        canvas.drawSelectedRepresentation();
    }

    private void drawRemoveArrow(ConcreteArrow arrow) {
        canvas.getPainter().removeArrow(arrow);
    }

    private void drawResizeElement(ConcreteDiagramElement element) {
        canvas.getPainter().redraw(element);

        for(ConcreteArrow arrow : element.getAllAttachedArrows()) {
            canvas.getPainter().redraw(arrow);
        }
    }


    private void drawMoveElement(ConcreteDiagramElement element) {
        canvas.getPainter().redraw(element);

        for(ConcreteArrow arrow : element.getAllAttachedArrows()) {
            canvas.getPainter().redraw(arrow);
        }
    }

    private void drawChangeLabel(ConcreteDiagramElement element) {
        canvas.getPainter().changeLabel(element);
    }

}
