package org.ontologyengineering.conceptdiagrams.web.client.ui;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.ontologyengineering.conceptdiagrams.web.client.ui.shapes.LienzoDiagramShape;
import org.ontologyengineering.conceptdiagrams.web.client.ui.shapes.LienzoDragBoundsGroup;
import org.ontologyengineering.conceptdiagrams.web.shared.OntologyFormat;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.presenter.DiagramCanvas;

import java.util.*;
import java.util.HashMap;
import java.util.HashSet;


public class LienzoDiagramCanvas extends DiagramCanvas {

    private LienzoDiagramPainter painter;


    // panels


    // I'm trying to push anything GWT as far out as possible so the bulk of the code is generic and for the bulk of
    // the workings we could swap anything in we want as the front end.
    private DockLayoutPanel mainPanel;
    private FlowPanel headerPanel;
    private FlowPanel toolPanel;
    private TabLayoutPanel drawingTabs;

    private FlowPanel filePanel;
    //private FlowPanel contextPanel;
    private FlowPanel tabPanel;
    private FlowPanel currentContextPanel;

    //private DockLayoutPanel canvasPanel;
    //private ScrollableTabLayoutPanel drawingTabs;




    //private Panel filePropertiesPanel;
    //private Panel tabPropertiesPanel;
    private FlowPanel curvePropertiesPanel;
    private FlowPanel arrowPropertiesPanel;
    private FlowPanel spiderPropertiesPanel;
    private FlowPanel rectanglePropertiesPanel;


    private Button newFileButton;
    private Button loadFileButton;
    private Button saveButton;
    private Button compileToOWLButton;


    // button panel
    private PushButton selectButton;
    private PushButton panButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button redoButton;
    private Button undoButton;
    private Button deleteButton;
    // -------------------------------
    // -------------------------------
    private PushButton boundaryRectangleButton;
    private PushButton starRectangleButton;
    private PushButton drawCurveButton;
    private PushButton shadeButton;
    private PushButton drawArrowButton;
    private PushButton drawSpiderButton;
    // -------------------------------


    // file properties widgets
    private TextBox ontologyIRIinputbox;
    //private TextBox fileLocationInputBox;
    private ListBox ontologySaveType;

    // tab properties widgets
    private TextBox tabNameInputBox;
    private ListBox tabType;
    private static int noTYPESELECTED = 0;
    private static int conceptTYPESELECTED = 1;
    private static int propertyTYPESELECTED = 2;



    // curve properties widgets
    private TextBox curveLabelInputBox;

    // - maybe make this also a search box, so you can have things that are already there brought in again - especially when working with a bigger ontology or included ontologies
    // - also could add a drag in feature .. ie. if there is a class tree portlet then can drag things from the tree onto the canvas - but that's dangerous cause what if we dragged them to the wrong/inconsisten spot (maybe just who cares)
    // - what about a function that searches for an existing concept and tries to place it in the selected boundary rectangle in the right way???
    //      actually that would be neat the OWL -> diagrams idea, not generating the whole diagram, just select some bits of the ontology and try to place on the canvas or in the selected boundary rectange -> way hard!!!



    // arrow properties widgets

    private TextBox arrowLabelInputBox;
    private ListBox constraintSelector;
    private TextBox arrowConstraint;
    private CheckBox functionalSelector;
    private CheckBox inverseSelector;
    private CheckBox dashedArrow;

    // spider properties widgets
    private TextBox spiderLabelInputBox;

    // rectangle properties widgets

    private ListBox rectangleType;
    private static int datatypeTYPESELECTED = 2;  // borrow the rest from above

    // global drawing properties

    private static double defaultZoomIn = 1.1;
    private static double defaultZoomOut = 0.9;




    // lienzo canvases

    private HashMap<String, LienzoPanel> canvases = new HashMap<String, LienzoPanel>();
    private HashMap<String, Layer> rectangleLayers = new HashMap<String, Layer>();
    private HashMap<String, Layer> curveLayers = new HashMap<String, Layer>();
    private HashMap<String, Layer> zoneLayers = new HashMap<String, Layer>();

    private HashMap<LienzoPanel, String> canvas2id = new HashMap<LienzoPanel, String>(); // how else to know what diagram set was selected?

    private HashMap<String, HTML> tabText = new HashMap<String, HTML>(); // easy way to change the text if the user is editing

    // state


    private boolean shiftDown = false;
    private LienzoDragBoundsGroup selectionGroup = new LienzoDragBoundsGroup(this);

    private Point click;  // on-screen point
    private Point2D clickT = new Point2D(); // The onscreen clicked point transformed into  coordinates on the diagramCanvas (which may be scalled and transformed)
    private Point lastValidPoint;
    private Point2D lastValidPointT = new Point2D();
    private Point mouseAt;
    private Point2D mouseAtT = new Point2D();
    private ConcreteBoundaryRectangle clickedInRectangle = null;

    private ConcreteDiagramElement arrowSource;


    // doesn't matter about the diagrams, get them from the elements
    private HashMap<ConcreteDiagramElement, LienzoDiagramShape> elementsMap = new HashMap<ConcreteDiagramElement, LienzoDiagramShape>();
    private HashMap<ConcreteCurve, AbstractSet<ConcreteZone>> curveToZoneMap = new HashMap<ConcreteCurve, AbstractSet<ConcreteZone>>();


    public LienzoDiagramCanvas() {
        super();

        painter = new LienzoDiagramPainter(this);

        doScreenLayout();
    }


    // lays out the basic parts of the UI, ready to be filled with diagrams later on
    public void doScreenLayout() {

        // basic structure

        mainPanel = new DockLayoutPanel(Unit.EM);

        toolPanel = buildToolPanel();

        headerPanel = createHeaderPanel();


        //canvasPanel = new DockLayoutPanel(Unit.EM);
        //contextPanel = new SimpleLayoutPanel();
        //contextPanel.getElement().getStyle().setBorderWidth(0.5, Unit.EM);

        drawingTabs = new TabLayoutPanel(2.5, Unit.EM);
        drawingTabs.add(new Label(),
                makeDecoratedTab("", new Image(InterfaceGlobals.INSTANCE.getIconImages().add()),
                        (new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                addNewCanvas(presenter.newDiagramSet("New Diagrams"), "New  Canvas");
                                ensureAddTabNotSelected();
                            }
                        })));
        drawingTabs.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> selectionEvent) {
                if(selectionEvent.getSelectedItem() != drawingTabs.getWidgetCount()-1) {
                    ensureState(ModeTypes.NONE);
                }
            }
        });

        //canvasPanel.addNorth(contextPanel, 5);
        //canvasPanel.add(drawingTabs);


        mainPanel.addNorth(headerPanel, 3.9);
        mainPanel.addWest(toolPanel, 8);
        mainPanel.add(drawingTabs);


        // set up other panels, ready to switch to

        //filePropertiesPanel = createFilePropertiesPanel();
        //tabPropertiesPanel = createTabPropertiesPanel();
    }

    public DockLayoutPanel asDockLayoutPanel() {
        return mainPanel;
    }


    public int getWidth() {
        return asDockLayoutPanel().getOffsetWidth();
    }

    public int getHeight() {
        return asDockLayoutPanel().getOffsetHeight();
    }

    protected void addNewCanvas(String ID, String name) {

        LienzoPanel panel = new LienzoPanel();

        Layer rectangleLayer = new Layer();
        Layer curveLayer = new Layer();
        Layer zoneLayer = new Layer();

        canvases.put(ID, panel);
        rectangleLayers.put(ID, rectangleLayer);
        curveLayers.put(ID, curveLayer);
        zoneLayers.put(ID, zoneLayer);

        panel.add(rectangleLayer);
        panel.add(curveLayer);
        panel.add(zoneLayer);

        zoneLayer.moveToTop();
        curveLayer.moveToTop();

        curveLayer.setListening(true);
        zoneLayer.setListening(true);
        rectangleLayer.setListening(true);

        panel.getDragLayer().setTransformable(false);

        rectangleLayer.batch();
        curveLayer.batch();
        zoneLayer.batch();

        panel.getViewport().pushMediator(new MouseWheelZoomMediator(EventFilter.SHIFT));
        panel.getViewport().pushMediator(new MousePanMediator(EventFilter.ALT));

        panel.setTransform(new Transform());

        canvas2id.put(panel, ID);

        addLayerHandlers(curveLayer);



        final Widget tabPanel = wrapLienzoPanel(panel);

        Widget closeTab = makeDecoratedTab(ID, new Image(InterfaceGlobals.INSTANCE.getIconImages().close()),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        if(Window.confirm("Closing this tab will delete the diagrams on it.  " +
                                "This action cannot be undone and the diagrams will not be recoverable.  " +
                                "Do you really want to do this?")) {
                            removeTab(tabPanel);
                        }
                    }
                });

        drawingTabs.insert(tabPanel, closeTab, drawingTabs.getWidgetCount()-1);

        ensureAddTabNotSelected();
    }


    private void removeTab(Widget panel) {
        // I think this should be enough to clean up all the memory for the panel and the diagram set

        String ID = canvas2id.get(getPanelForContainer(panel));

        presenter.deleteDiagramSet(ID);

        canvases.remove(ID);
        rectangleLayers.remove(ID);
        curveLayers.remove(ID);
        zoneLayers.remove(ID);

        canvas2id.remove(getPanelForContainer(panel));

        drawingTabs.remove(panel);
    }

    private void ensureAddTabNotSelected() {
        // used in selection events on + ... but if we just run it then, the event to select the tab can be run afterward
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                int numTabs = drawingTabs.getWidgetCount();
                int selectedTab = drawingTabs.getSelectedIndex();
                if(numTabs > 1 && selectedTab == numTabs - 1) {
                    drawingTabs.selectTab(numTabs - 2);
                }
            }
        });
    }



    private Widget makeDecoratedTab(String ID, Image image, ClickHandler handler) {
        final HTMLPanel hPanel = new HTMLPanel("");

        if(ID.length() > 0) {
            HTML html = new HTML(SafeHtmlUtils.fromString(presenter.getDiagramSetByID(ID).getLabel()));
            html.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
            html.getElement().getStyle().setFloat(Style.Float.LEFT);
            hPanel.add(html);
            html.getElement().getStyle().setMarginRight(0.5, Style.Unit.EM);

            tabText.put(ID, html);
        }

        image.setSize("13px", "13px");
        image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        image.getElement().getStyle().setFloat(Style.Float.RIGHT);

        image.addClickHandler(handler);


        hPanel.add(image);
        image.getElement().getStyle().setMarginTop(0.2, Style.Unit.EM);

        return hPanel;
    }

    protected void setAsFreshCanvas() {

        super.setAsFreshCanvas();

        for (String ID : canvases.keySet()) {
            presenter.deleteDiagramSet(ID);
        }

        while(drawingTabs.getWidgetCount() > 1) {
            drawingTabs.remove(0);
        }

        canvases.clear();
        rectangleLayers.clear();
        curveLayers.clear();
        zoneLayers.clear();

        canvas2id.clear();


        String ID = presenter.newDiagramSet("Concept Diagrams");
        presenter.setDiagramType(ID, DiagramSet.Diagram_TYPE.CONCEPT);
        addNewCanvas(ID, "Concept Diagrams");

        ID = presenter.newDiagramSet("Property Diagrams");
        presenter.setDiagramType(ID, DiagramSet.Diagram_TYPE.PROPERTY);
        addNewCanvas(ID, "Property Diagrams");

        displayFilePropertiesPanel();

        drawingTabs.selectTab(0);

        setMode(ModeTypes.NONE);
    }

    private void batchAll(String ID) {
        if(canvases.containsKey(ID)) {
            canvases.get(ID).batch();
        }
    }

    public LienzoDiagramPainter getPainter() {
        return painter;
    }

    protected Layer getCurveLayer(String ID) {
        return curveLayers.get(ID);
    }

    protected Layer getCurveLayer() {
        return getCurveLayer(getCurrentID());
    }

    protected Layer getBoundaryRectangleLayer(String ID) {
        return rectangleLayers.get(ID);
    }

    protected Layer getBoundaryRectangleLayer() {
        return getBoundaryRectangleLayer(getCurrentID());
    }

    protected Layer getZoneLayer(String ID) {
        return zoneLayers.get(ID);
    }

    protected Layer getZoneLayer() {
        return getZoneLayer(getCurrentID());
    }


    private Set<ConcreteDiagramElement> getElements() {
        return elementsMap.keySet();
    }
    private Collection<LienzoDiagramShape> getLienzoShapes() {
        return elementsMap.values();
    }

    protected boolean isOnCanvas(ConcreteDiagramElement element) {
        return getElementsMap().containsKey(element);
    }

    protected AbstractMap<ConcreteDiagramElement, LienzoDiagramShape> getElementsMap() {
        return elementsMap;
    }

    protected LienzoDiagramShape getRepresentation(ConcreteDiagramElement element) {
        return getElementsMap().get(element);
    }

    protected void resetMapForElement(ConcreteDiagramElement element, LienzoDiagramShape shape) {
        addToElementsOnCanvas(element, shape);
    }

    private void addToElementsOnCanvas(ConcreteDiagramElement element, LienzoDiagramShape shape) {
        getElementsMap().put(element, shape);
    }

    private AbstractMap<ConcreteCurve, AbstractSet<ConcreteZone>> getCurveToZoneMap() {
        return curveToZoneMap;
    }

    protected void addToCurveToZoneMap(ConcreteCurve curve, ConcreteZone zone) {
        if (!getCurveToZoneMap().containsKey(curve)) {
            getCurveToZoneMap().put(curve, new HashSet<ConcreteZone>());
        }
        getCurveToZoneMap().get(curve).add(zone);
    }

    protected void addToCurveToZoneMap(ConcreteCurve curve) {
        addToCurveToZoneMap(curve, curve.getMainZone());
        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            addToCurveToZoneMap(curve, zone);
            for (ConcreteCurve c : zone.getCurves()) {
                if (c != curve) {
                    addToCurveToZoneMap(curve, zone);
                }
            }
        }
    }

    protected void removeFromCurveToZoneMap(ConcreteCurve curve, ConcreteZone zone) {
        for (ConcreteCurve c : zone.getCurves()) {
            getCurveToZoneMap().get(c).remove(zone);
        }
    }

    protected void removeFromCurveToZoneMap(ConcreteCurve curve) {
        removeFromCurveToZoneMap(curve, curve.getMainZone());
        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            removeFromCurveToZoneMap(curve, zone);
        }
    }



    protected DiagramSet getCurrentDiagramSet() {
        return presenter.getDiagramSetByID(getCurrentID());
    }

    protected String getCurrentID() {
        return canvas2id.get(getCurrentPanel());
    }

    protected LienzoPanel getCurrentPanel() {
        int index = drawingTabs.getSelectedIndex();
        Widget selectedPanel = drawingTabs.getWidget(index);
        return getPanelForContainer(selectedPanel);
    }


    // ------------------ Panel Wrapping ----------

    // meant to get and wrap the LienzoPanel for whatever I'm currently choosing to put them in in the tab panel
    // this should be the only point to change if the container changes
    private LienzoPanel getPanelForContainer(Widget container) {
        if(container instanceof DockLayoutPanel) { // should always be true
            DockLayoutPanel panel = (DockLayoutPanel) container;
            return (LienzoPanel) panel.getWidget(0);
        }
        return null;
    }

    private Widget wrapLienzoPanel(LienzoPanel panel) {
        DockLayoutPanel tabPanel = new DockLayoutPanel(Unit.EM);
        tabPanel.add(panel);
        return tabPanel;
    }

    @Override
    public void setAsFocusDiagram(String ID) {
        if(canvases.containsKey(ID)) {
            for(Widget w : drawingTabs) {
                if(canvas2id.get(getPanelForContainer(w)).equals(ID)) {
                    drawingTabs.selectTab(w);
                }
            }
        }
        ensureState(ModeTypes.NONE);
    }

    // ------------------ Panel Wrapping ----------

















    protected void drawOnCanvas(ConcreteDiagramElement element, LienzoDiagramShape shape, Layer layer) {
        addToElementsOnCanvas(element, shape);
        addToLayer(shape, layer);
    }

    protected void removeFromCanvas(ConcreteDiagramElement element) {
        if (isOnCanvas(element)) {
            LienzoDiagramShape shape = getRepresentation(element);
            shape.undrawAll();
            removeFromElementsOnCanvas(element);
        }


    }

    private void addToLayer(LienzoDiagramShape shape, Layer layer) {
        shape.draw(layer);
        layer.batch();
    }

    protected void removeFromElementsOnCanvas(ConcreteDiagramElement element) {
        getElementsMap().remove(element);
    }




//    protected void addToCurveLayer(LienzoDiagramShape shape) {
//        shape.draw(curveLayer);
//        curveLayer.batch();
//    }
//
//    protected void addToBoundaryRectangleLayer(LienzoDiagramShape shape) {
//        shape.draw(boundaryRectangleLayer);
//        boundaryRectangleLayer.batch();
//    }
//
//    protected void addToZoneLayer(LienzoDiagramShape shape) {
//        shape.draw(zoneLayer);
//        zoneLayer.batch();
//    }

//    protected void removeShape(LienzoDiagramShape shape) {
//        Layer layer = shape.getLayer();
//        shape.undrawAll();
//        layer.batch();
//    }

//    protected void removeFromBoundaryRectangleLayer(IPrimitive node) {
//        boundaryRectangleLayer.remove(node);
//        boundaryRectangleLayer.batch();
//    }
//
//    protected void removeFromCurveLayer(IPrimitive node) {
//        curveLayer.remove(node);
//        curveLayer.batch();
//    }
//
//    protected void removeFromZoneLayer(IPrimitive node) {
//        zoneLayer.remove(node);
//        zoneLayer.batch();
//    }


    // --------------------------------
    // Canvas Management
    // --------------------------------

    protected LienzoPanel currentPanel() {
        return canvases.get(getCurrentID());
    }




    // --------------------------------
    // selection handling
    // --------------------------------


    public void addSelectedElement(ConcreteDiagramElement elmnt) {
        super.addSelectedElement(elmnt);
        selectionGroup.addElement(getRepresentation(elmnt));
    }

    protected void clearSelection() {
        selectionGroup.clearAndUndraw();
        super.clearSelection();
    }

    protected void drawSelectedRepresentation() {
        painter.draw(selectionGroup, getCurveLayer(getCurrentID()));
        /// now make sure the right context is displayed
        setContextPanel();
    }


//    protected void setNotSelected(ConcreteDiagramElement elmnt) {
//        //getRepresentation(elmnt).unDrawDragRepresentation();
//        getRepresentation(elmnt).setAsUnSelected();
//    }
//
//    protected void setSelected(ConcreteDiagramElement elmnt) {
//        //getRepresentation(elmnt).drawDragRepresentation();
//        getRepresentation(elmnt).setAsSelected();
//    }

//    public void setAsSelectedElement(ConcreteDiagramElement elmnt) {
//        super.setAsSelectedElement(elmnt);
//        //addSelectedElement(elmnt);
//    }


//
//    public void drawForSingleSelection(ConcreteDiagramElement elmnt) {
//       // super.setAsSelectedElement(elmnt);
//
//        if(getRepresentation(elmnt) != null && canHaveLabel(elmnt)) {
//            if (getRepresentation(elmnt).hasLabel()) {
//                textInputBox.setText(getRepresentation(elmnt).getLabelText());
//            } else {
//                textInputBox.setText("");
//            }
//            textInputBox.setFocus(true);
//        } else {
//            textInputBox.setText("");
//            textInputBox.setFocus(false);
//        }
//        //drawSelectedRepresentation();
//    }

//    private void drawForMultiSelection() {
//        textInputBox.setFocus(false);
//    }
//







    // --------------------------------
    // canvas interaction handlers
    // --------------------------------


    private void addLayerHandlers(Layer layer) {

        layer.addNodeMouseDownHandler(new NodeMouseDownHandler() {
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                if (event.isButtonLeft()) {

                    setClick(new Point(event.getX(), event.getY()));
                    clickedInRectangle = boundaryRectangleAtPoint(getCurrentID(), getClickAsCanvasCoordP());
                    setLastValidPoint(getClickAsScreenCoordP());

                    switch (getMode()) {
                        case SELECTION:
                            painter.startRubberBandRectangle(getClickAsScreenCoordP());
                            ensureState(ModeTypes.DRAGSELECT);
                            break;
                        case PAN:
                            ensureState(ModeTypes.PANNING);
                            break;
                        case DRAWCURVE:
                            if (presenter.validCurveStart(getCurrentID(), getClickAsCanvasCoordP())) {
                                painter.startRubberBandRectangle(getClickAsScreenCoordP());
                                ensureState(ModeTypes.DRAWINGCURVE);
                            }
                            break;
                        case DRAWSPIDER:
                            if (presenter.validSpiderPoint(getCurrentID(), getClickAsCanvasCoordP())) {
                                painter.startRubberBandSpider(getClickAsScreenCoordP());
                                ensureState(ModeTypes.DRAWINGSPIDER);
                            }
                            break;
                        case DRAWBOUNDARYRECTANGLE:
                            if (presenter.validBoundaryRectangleStart(getCurrentID(), getClickAsCanvasCoordP())) {
                                painter.startRubberBandRectangle(getClickAsScreenCoordP());
                                ensureState(ModeTypes.DRAWINGBOUNDARYRECTANGLE);
                            }
                            break;
                        case DRAWSTARRECTANGLE:
                            if (presenter.validBoundaryRectangleStart(getCurrentID(), getClickAsCanvasCoordP())) {
                                painter.startRubberBandRectangle(getClickAsScreenCoordP());
                                ensureState(ModeTypes.DRAWINGSTARRECTANGLE);
                            }
                            break;
                        case DRAWARROW:
                            if (underMouse() != null && presenter.canBeArrowSource(underMouse())) {
                                arrowSource = underMouse();
                                painter.startRubberBandArrow(getClickAsScreenCoordP());
                                ensureState(ModeTypes.DRAWINGARROW);
                            }
                            break;
                        case SHADE:
                            // nothing to do till mouse up
                            break;
                    }
                }
            }
        });

        layer.addNodeMouseMoveHandler(new NodeMouseMoveHandler() {
            public void onNodeMouseMove(NodeMouseMoveEvent event) {

                if(getMode() == ModeTypes.NONE) {
                    return;
                }


                boolean left, right, ctrl = false;
                if (event.isButtonLeft()) {
                    left = true;
                }
                if (event.isButtonRight()) {
                    right = true;
                }
                if (event.isControlKeyDown()) {
                    ctrl = true;
                }

                if (event.isButtonLeft()) {

                    setMouseAt(new Point(event.getX(), event.getY()));

                    switch (getMode()) {
                        case DRAGSELECT:
                            painter.dragRubberBandRectangle(getMouseAtAsScreenCoord());
                            setLastValidPoint(getMouseAtAsScreenCoord());
                            break;
                        case PANNING:
                            // ??? what about panel.getViewport().pushMediator(new MousePanMediator());
                            // sould be on the click???
                            break;
                        case DRAWINGCURVE:
                            if (presenter.validCurve(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()))) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                                painter.dragRubberBandRectangle(getLastValidAsScreenCoord());
                            }
                            break;
                        case DRAWINGSPIDER:
                            if (presenter.validSpiderPoint(getCurrentID(), getMouseAtAsCanvasCoordP())) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                                painter.dragRubberBandSpider(getLastValidAsScreenCoord());
                            }
                            break;
                        case DRAWINGBOUNDARYRECTANGLE:
                            if (presenter.validBoundaryRectangle(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()))) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                                painter.dragRubberBandRectangle(getLastValidAsScreenCoord());
                            }
                            break;
                        case DRAWINGSTARRECTANGLE:
                            if (presenter.validBoundaryRectangle(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getMouseAtAsCanvasCoordP()))) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                                painter.dragRubberBandRectangle(getLastValidAsScreenCoord());
                            }
                            break;
                        case DRAWINGARROW:
                            painter.dragRubberBandArrow(getMouseAtAsScreenCoord());
                            setLastValidPoint(getMouseAtAsScreenCoord());
                            break;
                        case SHADE:
                            // nothing to do till mouse up
                            break;
                    }
                }
            }
        });

        layer.addNodeMouseUpHandler(new NodeMouseUpHandler() {
            public void onNodeMouseUp(NodeMouseUpEvent event) {

                // setMouseAt(new Point(event.getX(), event.getY()));  //...should be the same as the last move, right??

                if (event.isButtonLeft()) {
                    switch (getMode()) {
                        case DRAGSELECT:
                            painter.removeRubberBandRectangle();

                            if (!event.isShiftKeyDown()) {
                                clearSelection();
                            }

                            // might have been a drag select or a regular click, check which
                            if (getMouseAtAsScreenCoord().getX() == getClickAsScreenCoordP().getX() && getMouseAtAsScreenCoord().getY() == getClickAsScreenCoordP().getY()) {
                                // click select
                                if (underMouse() != null) {
                                    if (selectable(underMouse())) {
                                        addSelectedElement(underMouse());
                                    }
                                }
                            } else {
                                //drag select

                                // as canvas coords
                                Point rubberbandTopLeft = Point.topLeft(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP());
                                Point rubberbandBotRight = Point.botRight(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP());

                                for (ConcreteDiagramElement d : (getCurrentDiagramSet().elementsInBoundingBox(rubberbandTopLeft,rubberbandBotRight))) {
                                    if (selectable(d)) {
                                        addSelectedElement(d);
                                    }
                                }
                            }
                            drawSelectedRepresentation();
                            ensureState(ModeTypes.SELECTION);
                            break;
                        case DRAWINGCURVE:
                            painter.removeRubberBandRectangle();

                            // the presenter will make sure it's valid and ignore it if not

                            presenter.addCurve(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()));
                            ensureState(ModeTypes.DRAWCURVE);
                            break;
                        case DRAWINGSPIDER:
                            painter.removeRubberBandSpider();
                            presenter.addSpider(getCurrentID(), getLastValidAsCanvasCoordP());
                            ensureState(ModeTypes.DRAWSPIDER);
                            break;
                        case DRAWINGBOUNDARYRECTANGLE:
                            painter.removeRubberBandRectangle();
                            presenter.addBoundaryRectangle(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()));
                            ensureState(ModeTypes.DRAWBOUNDARYRECTANGLE);
                            break;
                        case DRAWINGSTARRECTANGLE:
                            painter.removeRubberBandRectangle();
                            presenter.addStarRectangle(getCurrentID(),
                                    Point.topLeft(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()),
                                    Point.botRight(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP()));
                            ensureState(ModeTypes.DRAWSTARRECTANGLE);
                            break;
                        case SHADE:
                            if (underMouse() != null) {
                                if (underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEZONE ||
                                        underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEINTERSECTIONZONE) {
                                    presenter.flipZoneShading(getCurrentID(), (ConcreteZone) underMouse());
                                }
                            }
                            break;
                        case DRAWINGARROW:
                            painter.removeRubberBandArrow();

                            if (underMouse() != null) {
                                presenter.addArrow(getCurrentID(), getClickAsCanvasCoordP(), arrowSource, getLastValidAsCanvasCoordP(), underMouse());
                            }
                            ensureState(ModeTypes.DRAWARROW);
                            break;
                        case DELETE:
                            if(presenter.deletable(underMouse())) {
                                presenter.removeElement(underMouse());
                            }
                        break;

                    }
                }

                clickedInRectangle = null;
//                getCurrentPanel().getDragLayer().batch();
//                layer.batch();
            }
        });

    }



    private boolean selectable(ConcreteDiagramElement elmnt) {
        return presenter.deletable(elmnt);     // if it's deleteable it's selectable
    }


    // check that a requested zoom doesn't exceed the zoom bounds.
    // If it does, set it to the bound.
    private double ensureInZoomBounds(double zoomRatio) {
        return zoomRatio;

        // this'll have to be some sort of calculation on transforming a minimal curve to screen coords and checking against
        // the screen size and similarly for zoom out - or go the other way around and calculat from those.
    }

    public void zoom(double zoomRatio) {
        currentPanel().getViewport().getTransform().scale(ensureInZoomBounds(zoomRatio));
    }

    public void zoomIn() {
        zoom(defaultZoomIn);
    }


    public void zoomOut() {
        zoom(defaultZoomOut);
    }

    // --------------------------------
    // click and transform handling
    // --------------------------------


    private void setClick(Point clickedPoint) {
        click = clickedPoint;
        getCurrentPanel().getViewport().getTransform().getInverse().transform(click.asLienzoPoint2D(), clickT);
    }

    private Point getClickAsScreenCoordP() {
        return click;
    }

    private Point2D getClickAsCanvasCoord() {
        return clickT;
    }

    private Point getClickAsCanvasCoordP() {
        return new Point(getClickAsCanvasCoord().getX(), getClickAsCanvasCoord().getY());
    }

    private void setLastValidPoint(Point point) {
        lastValidPoint = point;
        getCurrentPanel().getViewport().getTransform().getInverse().transform(lastValidPoint.asLienzoPoint2D(), lastValidPointT);
    }

    private Point getLastValidAsScreenCoord() {
        return lastValidPoint;
    }

    private Point2D getLastValidAsCanvasCoord() {
        return lastValidPointT;
    }

    private Point getLastValidAsCanvasCoordP() {
        return new Point(getLastValidAsCanvasCoord().getX(), getLastValidAsCanvasCoord().getY());
    }


    private void setMouseAt(Point mouseAt) {
        this.mouseAt = mouseAt;
        getCurrentPanel().getViewport().getTransform().getInverse().transform(mouseAt.asLienzoPoint2D(), mouseAtT);
    }

    public Point getMouseAtAsScreenCoord() {
        return mouseAt;
    }

    public Point2D getMouseAtAsCanvasCoord() {
        return mouseAtT;
    }

    public Point getMouseAtAsCanvasCoordP() {
        return new Point(mouseAtT);
    }

















    // --------------------------------
    // state transitions
    // --------------------------------


    protected void setEnabledButtons() {
//        if (CommandManager.get().canUndo()) {
//            undoButton.setEnabled(true);
//        } else {
//            undoButton.setEnabled(false);
//        }
//
//        if (CommandManager.get().canRedo()) {
//            redoButton.setEnabled(true);
//        } else {
//            redoButton.setEnabled(false);
//        }
//
//        if(getSelectedElements().size() == 1) {
//            textInputBox.setEnabled(true);
//        } else {
//            //textInputBox.setEnabled(false);
//        }

        // FIXME can only zoom in and out if the maxes aren't reached
    }


//    public void ensureState() {
//        switch (getMode()) {
//            case DRAGSELECT:
//                setMode(ModeTypes.SELECTION);
//                break;
//            case PANNING:
//                setMode(ModeTypes.PAN);
//                break;
//            case ZOOMING:
//                setMode(ModeTypes.ZOOM);
//                break;
//            case DRAWINGCURVE:
//                setMode(ModeTypes.DRAWCURVE);
//                break;
//            case DRAWINGSPIDER:
//                setMode(ModeTypes.DRAWSPIDER);
//                break;
//            case DRAWINGARROW:
//                setMode(ModeTypes.DRAWARROW);
//                break;
//            case DRAWINGBOUNDARYRECTANGLE:
//                setMode(ModeTypes.DRAWBOUNDARYRECTANGLE);
//                break;
//            case DRAWINGSTARRECTANGLE:
//                setMode(ModeTypes.DRAWSTARRECTANGLE);
//                break;
//        }
//    }

    // make sure we have the right context panel displayed
    private void setContextPanel() {

        switch (getMode()) {
            case NONE:
                //displayTabPropertiesPanel();
                break;
            /// hmmm I think otherwise it's just display what's selected ??
            // so not mode dependent
        }

        if(getSelectedElements().size() == 1) {
            ConcreteDiagramElement elmnt = getSelectedElement();
            if(elmnt instanceof ConcreteBoundaryRectangle && !(elmnt instanceof ConcreteStarRectangle)) {
                setRectangleContextPanel();
            } else if(elmnt instanceof ConcreteCurve) {
                setCurveContextPanel();
            } else if(elmnt instanceof ConcreteArrow) {
                setArrowContextPanel();
            } else if(elmnt instanceof ConcreteSpider) {
                setSpiderContextPanel();
            }

        } else if (getSelectedElements().size() > 1) {
            // displayTabPropertiesPanel();
            // maybe eventually something else to do here with groups or something??
        } else {
            //displayTabPropertiesPanel();
        }
    }

    public void ensureState() {
        ensureState(getMode());
    }

    public void ensureState(ModeTypes newMode) {
        setMode(newMode);

        displayTabPropertiesPanel();

        DiagramSet currentDiagramSet = getCurrentDiagramSet();
        String ID = getCurrentID();

        /// now do the bits for change of context panel etc ..

        // maybe a fn to set a particular context panel
        // mode NONE -> tab context panel

        // set what buttons are enabled

        // when mode type is none ... nothing should be able to happen on the drawing panel, the buttons should
        // be in a default mode and the mini map should be scrollable







        if(presenter.canUndo(ID)) {
            undoButton.setEnabled(true);
        } else {
            undoButton.setEnabled(false);
        }

        if(presenter.canRedo(ID)) {
            redoButton.setEnabled(true);
        } else {
            redoButton.setEnabled(false);
        }

        if(currentDiagramSet.isEmpty() || currentDiagramSet.containsOnlyStar()) {
            drawCurveButton.setEnabled(false);
            shadeButton.setEnabled(false);
            drawArrowButton.setEnabled(false);
            drawSpiderButton.setEnabled(false);
        } else {
            drawCurveButton.setEnabled(true);
            shadeButton.setEnabled(true);
            drawArrowButton.setEnabled(true);
            drawSpiderButton.setEnabled(true);
        }

        if(currentDiagramSet.isPropertyDiagramSet()) {
            drawSpiderButton.setEnabled(false);
        } else if(!currentDiagramSet.isEmpty() && !currentDiagramSet.containsOnlyStar()) {
            drawSpiderButton.setEnabled(true);
        }

        if(currentDiagramSet.isConceptDiagramSet()) {
            starRectangleButton.setEnabled(false);
        } else {
            starRectangleButton.setEnabled(true);
        }
    }


    public void setMode(ModeTypes newMode) {
        if(getMode() != newMode) {
            ModeTypes oldMode = getMode();
            super.setMode(newMode);

            // ... mode change cleanup
            if(newMode == ModeTypes.DELETE) {
                // check delteable and delete them all
            }

            if(newMode == ModeTypes.NONE) {
                clearSelection();
            }

            if(oldMode == ModeTypes.DRAGSELECT) {
                painter.removeRubberBandRectangle();
            }

            if(oldMode == ModeTypes.DRAWINGSPIDER) {
                painter.removeRubberBandSpider();
            }

            if(oldMode != ModeTypes.DRAGINGARROW && oldMode != ModeTypes.DRAWINGARROW && oldMode != ModeTypes.DRAWINGBOUNDARYRECTANGLE &&
                    oldMode != ModeTypes.DRAWINGCURVE && oldMode != ModeTypes.DRAWINGSPIDER && oldMode != ModeTypes.DRAWINGSTARRECTANGLE &&
                    (newMode == ModeTypes.DELETE || newMode == ModeTypes.DRAWCURVE ||
                    newMode == ModeTypes.DRAWARROW || newMode == ModeTypes.DRAWBOUNDARYRECTANGLE || newMode == ModeTypes.SHADE ||
                    newMode == ModeTypes.DRAWSPIDER || newMode == ModeTypes.DRAWSTARRECTANGLE)) {
                clearSelection();
            }


        }
    }



    // I'm having a strange error where mouse events sometimes crop up that aren't mouse events - seems a bit like
    // sometimes clicks in the GWT window, but not in the Lienzo window fire when the mouse moves back into the Lienzo
    // window:  e.g. sometimes when I click a button then return to the Lienzo window, I get a mouse drag event with
    // left button recorded as down.
    //
    // Also I need a way when say a spider is draged or the drag boxes of a group are used to stop the rubberband of
    // a drag select coming up as well.
    //
    // So this routine just returns us to select mode and removes the drag select rubber band.
    public void turnOffDragSelect() {
        if(getMode() == ModeTypes.DRAGSELECT) {
            setMode(ModeTypes.SELECTION);
        }
    }

    public void turnOffDrawingSpider() {
        if(getMode() == ModeTypes.DRAWINGSPIDER) {
            setMode(ModeTypes.DRAWSPIDER);
        }
    }


    // redo the last undone edit for the currently selected tab
    public void redo() {
        clearSelection();

        presenter.redo(getCurrentID());

        ensureState();
    }

    // undo the last undone edit for the currently selected tab
    public void undo() {
        clearSelection();

        presenter.undo(getCurrentID());

        ensureState();
    }


    public void compileToOWL() {
        presenter.compileToOWL();
    }

    // --------------------------------
    // Layout Setup
    // --------------------------------


    private void handerForInputBox(final TextBox inputBox) {
        inputBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                presenter.changeLabel(getCurrentID(), getSelectedElement(), inputBox.getText());
            }
        });
    }

    private void setCurrentContextPanel(Panel p) {
        currentContextPanel.clear();
        currentContextPanel.add(p);
    }

    private void createCurveContextPanel() {
        //curvePropertiesPanel = new FlowPanel();
        Label text = new Label("Curve Label : ");
        curveLabelInputBox = new TextBox();
        handerForInputBox(curveLabelInputBox);

        //curvePropertiesPanel.add(text);
        //curvePropertiesPanel.add(curveLabelInputBox);
        curvePropertiesPanel = new FlowPanel();
        curvePropertiesPanel.add(wrapSimple(layoutHorizontally(text, curveLabelInputBox)));
        curvePropertiesPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        curvePropertiesPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
    }

    private void setCurveContextPanel() {
        setCurrentContextPanel(curvePropertiesPanel);

        ConcreteDiagramElement elmnt = getSelectedElement();
        if(elmnt != null && elmnt instanceof ConcreteCurve) {
            //ConcreteCurve c = (ConcreteCurve) elmnt;
            curveLabelInputBox.setText(elmnt.labelText());
        }
        curveLabelInputBox.setFocus(true);
    }

    private void createSpiderContextPanel() {
        Label text = new Label("Spider Label : ");
        spiderLabelInputBox = new TextBox();
        handerForInputBox(spiderLabelInputBox);

        //spiderPropertiesPanel.add(text);
        //spiderPropertiesPanel.add(spiderLabelInputBox);
        spiderPropertiesPanel = new FlowPanel();
        spiderPropertiesPanel.add(wrapSimple(layoutHorizontally(text, spiderLabelInputBox)));
        spiderPropertiesPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        spiderPropertiesPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
    }

    private void setSpiderContextPanel() {
        setCurrentContextPanel(spiderPropertiesPanel);

        ConcreteDiagramElement elmnt = getSelectedElement();
        if(elmnt != null && elmnt instanceof ConcreteSpider) {
            spiderLabelInputBox.setText(elmnt.labelText());
        }
        spiderLabelInputBox.setFocus(true);
    }





    private void createArrowContextPanel() {
        arrowPropertiesPanel = new FlowPanel();
        Label text = new Label("Arrow Label : ");

        arrowLabelInputBox = new TextBox();
        handerForInputBox(arrowLabelInputBox);

        arrowConstraint = new TextBox();
        arrowConstraint.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                changeArrowConstraints();
            }
        });

        functionalSelector = new CheckBox("Functional");

        inverseSelector = new CheckBox("Inverse");
        inverseSelector.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                changeArrowConstraints();
            }
        });

        dashedArrow = new CheckBox("Dashed");
        dashedArrow.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                changeArrowConstraints();
            }
        });

        //arrowPropertiesPanel.add(functionalSelector);
        //convience for = 1

        constraintSelector = new ListBox();
        constraintSelector.addItem("No Constraint", String.valueOf(ConcreteArrow.CardinalityConstraint.NONE));
        constraintSelector.addItem("=", String.valueOf(ConcreteArrow.CardinalityConstraint.EQ));
        constraintSelector.addItem("<=", String.valueOf(ConcreteArrow.CardinalityConstraint.LEQ));
        constraintSelector.addItem(">=", String.valueOf(ConcreteArrow.CardinalityConstraint.GEQ));

        constraintSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                changeArrowConstraints();
            }
        });


//        arrowPropertiesPanel.add(text);
//        arrowPropertiesPanel.add(arrowLabelInputBox);
//        arrowPropertiesPanel.add(inverseSelector);
//        arrowPropertiesPanel.add(dashedArrow);
//        arrowPropertiesPanel.add(constraintSelector);
        arrowPropertiesPanel.add(wrapSimple(layoutHorizontally(text, arrowLabelInputBox, inverseSelector, dashedArrow, constraintSelector, arrowConstraint))); //functionalSelector,
        arrowPropertiesPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        arrowPropertiesPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);

    }

    private void changeArrowConstraints() {
        ConcreteDiagramElement elmnt = getSelectedElement();
        if(elmnt != null && elmnt instanceof ConcreteArrow) {
            ConcreteArrow arrow = (ConcreteArrow) elmnt;

            boolean numberOK = true;
            int constraint = -1;
            if(arrowConstraint.getText() != null) {
                try {
                    constraint = Integer.parseInt(arrowConstraint.getText());
                } catch (NumberFormatException e) {
                    numberOK = false;
                }
            } else {
                numberOK = false;
            }

            if(numberOK && constraint >= 0) {
                if (constraintSelector.getSelectedValue().equals(String.valueOf(ConcreteArrow.CardinalityConstraint.NONE))) {
                    getPresenter().changeConstraint(getCurrentID(), arrow, ConcreteArrow.CardinalityConstraint.NONE, 0, dashedArrow.getValue(), inverseSelector.getValue());
                } else if (constraintSelector.getSelectedValue().equals(String.valueOf(ConcreteArrow.CardinalityConstraint.EQ))) {
                    getPresenter().changeConstraint(getCurrentID(), arrow, ConcreteArrow.CardinalityConstraint.EQ, constraint, dashedArrow.getValue(), inverseSelector.getValue());
                } else if (constraintSelector.getSelectedValue().equals(String.valueOf(ConcreteArrow.CardinalityConstraint.GEQ))) {
                    getPresenter().changeConstraint(getCurrentID(), arrow, ConcreteArrow.CardinalityConstraint.GEQ, constraint, dashedArrow.getValue(), inverseSelector.getValue());
                } else if (constraintSelector.getSelectedValue().equals(String.valueOf(ConcreteArrow.CardinalityConstraint.LEQ))) {
                    getPresenter().changeConstraint(getCurrentID(), arrow, ConcreteArrow.CardinalityConstraint.LEQ, constraint, dashedArrow.getValue(), inverseSelector.getValue());
                } else {
                    // nothing ??
                }
            } else {
                getPresenter().changeConstraint(getCurrentID(), arrow, ConcreteArrow.CardinalityConstraint.NONE, 0, dashedArrow.getValue(), inverseSelector.getValue());
            }
        }
    }

    private void setArrowContextPanel() {
        setCurrentContextPanel(arrowPropertiesPanel);

        ConcreteDiagramElement elmnt = getSelectedElement();
        if(elmnt != null && elmnt instanceof ConcreteArrow) {
            ConcreteArrow arrow = (ConcreteArrow) elmnt;
            arrowLabelInputBox.setText(elmnt.labelText());
            arrowLabelInputBox.setFocus(true);

            if(arrow.isDashed()) {
                dashedArrow.setValue(true);
            } else {
                dashedArrow.setValue(false);
            }

            if(arrow.isInverse()) {
                inverseSelector.setValue(true);
            } else {
                inverseSelector.setValue(false);
            }

            if(arrow.getCardinality() != null) {
                arrowConstraint.setText(arrow.getCardinality().toString());
            } else {
                arrowConstraint.setText("");
            }

            if(arrow.getCardinalityConstraint() == ConcreteArrow.CardinalityConstraint.NONE) {
                constraintSelector.setSelectedIndex(ConcreteArrow.CardinalityConstraint.NONE.ordinal());
            } else if(arrow.getCardinalityConstraint() == ConcreteArrow.CardinalityConstraint.EQ) {
                constraintSelector.setSelectedIndex(ConcreteArrow.CardinalityConstraint.EQ.ordinal());
            } else if(arrow.getCardinalityConstraint() == ConcreteArrow.CardinalityConstraint.GEQ) {
                constraintSelector.setSelectedIndex(ConcreteArrow.CardinalityConstraint.GEQ.ordinal());
            } else if(arrow.getCardinalityConstraint() == ConcreteArrow.CardinalityConstraint.LEQ) {
                constraintSelector.setSelectedIndex(ConcreteArrow.CardinalityConstraint.LEQ.ordinal());
            }
        }
    }

    private void createRectangleContextPanel() {
        rectanglePropertiesPanel = new FlowPanel();

        rectangleType = new ListBox();
        rectangleType.addItem("<Rectangle Type>", String.valueOf(noTYPESELECTED));
        rectangleType.addItem("Concepts", String.valueOf(conceptTYPESELECTED));
        rectangleType.addItem("Datatypes", String.valueOf(datatypeTYPESELECTED));
        rectangleType.setVisibleItemCount(1);
        rectangleType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                ConcreteDiagramElement elmnt = getSelectedElement();
                if(elmnt != null && elmnt instanceof ConcreteBoundaryRectangle && !(elmnt instanceof ConcreteStarRectangle)) {
                    ConcreteBoundaryRectangle br = (ConcreteBoundaryRectangle) elmnt;
                    // really need to check here if type is allowed
                    // really need to check here if type is allowed
                    if(rectangleType.getSelectedIndex() == noTYPESELECTED) {
                        getPresenter().changeType(getCurrentID(), br, false, false);
                    } else if(rectangleType.getSelectedIndex() == conceptTYPESELECTED) {
                        getPresenter().changeType(getCurrentID(), br, true, true);
                    } else {
                        getPresenter().changeType(getCurrentID(), br, true, false);
                    }
                }
            }
        });

        rectanglePropertiesPanel.add(rectangleType);
    }

    private void setRectangleContextPanel() {

        setCurrentContextPanel(rectanglePropertiesPanel);

        ConcreteDiagramElement elmnt = getSelectedElement();
        if(elmnt != null && elmnt instanceof ConcreteBoundaryRectangle && !(elmnt instanceof ConcreteStarRectangle)) {
            ConcreteBoundaryRectangle br = (ConcreteBoundaryRectangle) elmnt;
            if(!br.typeIsKnown()) {
                rectangleType.setSelectedIndex(noTYPESELECTED);
            } else if(br.isObject()) {
                rectangleType.setSelectedIndex(conceptTYPESELECTED);
            } else {
                rectangleType.setSelectedIndex(datatypeTYPESELECTED);
            }
        }
    }


    private FlowPanel createHeaderPanel() {
        FlowPanel result = new FlowPanel();

        currentContextPanel = new FlowPanel();
        tabPanel = createTabPropertiesPanel();
        filePanel = buildFilePanel();


        result.add(filePanel);
        //result.add(wrapSimple(tabPanel));

        //result.add(layoutHorizontally(tabPanel, currentContextPanel));



        createCurveContextPanel();
        createSpiderContextPanel();
        createArrowContextPanel();
        createRectangleContextPanel();

        return result;
    }

    private FlowPanel buildFilePanel () {
        FlowPanel result = new FlowPanel();

        newFileButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().newFile(), "New File");
        newFileButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(Window.confirm("Starting a new project will delete any unsaved changes in the current project.  "  +
                        "If you wish to save changes, click cancel, save and then open a new project.")) {
                    setAsFreshCanvas();
                }
            }
        });


        loadFileButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().loadFile(), "Load File");
        loadFileButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {


            }
        });

        saveButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().save(), "Save File");

        compileToOWLButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().toOWL(), "Compile to OWL");
        compileToOWLButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                compileToOWL();
            }
        });

        result.add(wrapSimple(layoutHorizontally(newFileButton, loadFileButton,saveButton,compileToOWLButton)));

        FlowPanel inputPanel = new FlowPanel();

        //Panel fileInput = ;

        //fileInput.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        //currentContextPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        inputPanel.add(wrapSimple(layoutHorizontally(createFilePropertiesPanel(), tabPanel)));
        inputPanel.add(wrapSimple(currentContextPanel));

        result.add(inputPanel);

        return result;
    }

    private void resetTabName(String ID) {
        if(tabText.containsKey(ID)) {
            tabText.get(ID).setHTML(SafeHtmlUtils.fromString(getCurrentDiagramSet().getLabel()));
        }
    }

    private FlowPanel createTabPropertiesPanel() {
        FlowPanel result = new FlowPanel();

        Label tabLabel = new Label("Tab Name : ");
        tabNameInputBox = new TextBox();
        tabNameInputBox.setVisibleLength(30);
        tabNameInputBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                presenter.setDiagramName(getCurrentID(), tabNameInputBox.getText());
                resetTabName(getCurrentID());
            }
        });

        tabType = new ListBox();
        tabType.addItem("<Diagram Type>", String.valueOf(noTYPESELECTED));
        tabType.addItem("Concept Diagram", String.valueOf(conceptTYPESELECTED));
        tabType.addItem("Property Diagram", String.valueOf(propertyTYPESELECTED));
        tabType.setVisibleItemCount(1);
        tabType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                setCurrentDiagramType(getTypeForSelection(tabType.getSelectedIndex()));
                ensureState(ModeTypes.NONE);
            }
        });

        //SimplePanel line1 = new SimplePanel();
        //line1.add(layoutHorizontally(tabLabel, tabNameInputBox, tabType));

        result.add(wrapSimple(layoutHorizontally(tabLabel, tabNameInputBox, tabType)));

        return result;
    }

    private DiagramSet.Diagram_TYPE getTypeForSelection(int selection) {
        if(selection == conceptTYPESELECTED) {
            return DiagramSet.Diagram_TYPE.CONCEPT;
        } else if(selection == propertyTYPESELECTED) {
            return DiagramSet.Diagram_TYPE.PROPERTY;
        } else {
            return DiagramSet.Diagram_TYPE.MIXED;
        }
    }

    private void setCurrentDiagramType(DiagramSet.Diagram_TYPE newType) {
        presenter.setDiagramType(getCurrentID(), newType);
    }

    private void displayTabPropertiesPanel() {
//        contextPanel.clear();
//        contextPanel.add(tabPropertiesPanel);
        setTabPropertiesPanel();
    }

    private void setTabPropertiesPanel() {
        DiagramSet current = getCurrentDiagramSet();

        if(current != null) {
            tabNameInputBox.setText(current.getLabel());
            if (current.isConceptDiagramSet()) {
                tabType.setSelectedIndex(conceptTYPESELECTED);
            } else if (current.isPropertyDiagramSet()) {
                tabType.setSelectedIndex(propertyTYPESELECTED);
            } else {
                tabType.setSelectedIndex(noTYPESELECTED);
            }
        }
    }

    private Panel createFilePropertiesPanel() {

        //FlowPanel result = new FlowPanel();

        // line 1

        Label ontologyLabel = new Label("Ontology IRI : ");
        ontologyIRIinputbox = new TextBox();
        ontologyIRIinputbox.setVisibleLength(60);
        ontologyIRIinputbox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                presenter.setIRI(ontologyIRIinputbox.getText());
            }
        });

        ontologySaveType = new ListBox();
        for(OntologyFormat format : OntologyFormat.values()) {
            ontologySaveType.addItem(format.name());
        }
        ontologySaveType.setVisibleItemCount(1);

        //SimplePanel line1 = new SimplePanel();
        //line1.add(layoutHorizontally(ontologyLabel, ontologyIRIinputbox, ontologySaveType));


        // line 2


//        Label filenameLabel = new Label("Filename : ");
//        fileLocationInputBox = new TextBox();
//        fileLocationInputBox.setVisibleLength(60);
//        fileLocationInputBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent changeEvent) {
//                presenter.setFileLocation(fileLocationInputBox.getText());
//            }
//        });
//
//        SimplePanel line2 = new SimplePanel();
//        line2.add(layoutHorizontally(filenameLabel, fileLocationInputBox));
//
//        line1.getElement().getStyle().setBorderWidth(0.5, Unit.EM);
//        line2.getElement().getStyle().setBorderWidth(0.5, Unit.EM);
//        line1.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
//        line2.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
//
//
//        result.add(wrapSimple(layoutHorizontally(ontologyLabel, ontologyIRIinputbox, ontologySaveType)));
////        result.add(line2);
//
//        return result;

        return wrapSimple(layoutHorizontally(ontologyLabel, ontologyIRIinputbox, ontologySaveType));
    }


    private void displayFilePropertiesPanel() {
//        contextPanel.clear();
//        contextPanel.add(filePropertiesPanel);
        setFilePropertiesPanel();
    }

    private void setFilePropertiesPanel() {
        ontologyIRIinputbox.setText(presenter.getIRI());
//        if(presenter.isWebProtege()) {
//            ontologyIRIinputbox.setText(presenter.getIRI());
//            fileLocationInputBox.setText("No file location - Saved in WebProtege");
//            ontologyIRIinputbox.setEnabled(false);
//            fileLocationInputBox.setEnabled(false);
//        } else {
//            ontologyIRIinputbox.setText(presenter.getIRI());
//            fileLocationInputBox.setText(presenter.getFileName());
//            ontologyIRIinputbox.setEnabled(true);
//            fileLocationInputBox.setEnabled(true);
//        }
    }

    private Button imageAsButton(ImageResource img, String title) {
        Button result;

        Image icon = new Image(img);
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(icon.toString());
        result = new Button(builder.toSafeHtml());
        result.setTitle(title);

        return result;
    }


    private FlowPanel layoutHorizontally(Widget... widgets) {
        FlowPanel result = new FlowPanel();

        for(Widget w : widgets) {
            SimplePanel wrapper = new SimplePanel();
            wrapper.setWidget(w);
            wrapper.getElement().getStyle().setFloat(Style.Float.LEFT);
            wrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            result.add(wrapper);
        }

        return result;
    }


    private SimplePanel wrapSimple(Panel p) {
        SimplePanel wrapper = new SimplePanel();
        wrapper.setWidget(p);
        wrapper.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        //wrapper.getElement().getStyle().setProperty("border","red solid 1px");
//        wrapper.getElement().getStyle().setBorderColor("red");
//        wrapper.getElement().getStyle().setMarginTop(0, Style.Unit.EM);
//        wrapper.getElement().getStyle().setMarginBottom(0, Style.Unit.EM);
//        wrapper.getElement().getStyle().clearPaddingTop();
//        wrapper.getElement().getStyle().clearPaddingBottom();
//        wrapper.getElement().getStyle().clearMargin();
//        wrapper.getElement().getStyle().setPadding(0, Unit.PT);
        //wrapper.getElement().getStyle().setFloat(Fl);
        return wrapper;
    }

    private FlowPanel buildToolPanel() {

        FlowPanel result = new FlowPanel();
        //result.getElement().getStyle().setProperty("border","blue solid 1px");
        result.getElement().getStyle().setProperty("textAlign", "center");

//        FlowPanel filePanel = new FlowPanel();
//
//        newFileButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().newFile(), "New File");
//        loadFileButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().loadFile(), "Load File");
//        filePanel.add(wrapSimple(layoutHorizontally(newFileButton, loadFileButton)));
//
//        saveButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().save(), "Save File");
//
//        propertiesButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().properties(), "Edit Properties");
//        propertiesButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                ensureState(ModeTypes.NONE);
//                displayFilePropertiesPanel();
//            }
//        });
//
//        filePanel.add(wrapSimple(layoutHorizontally(saveButton, propertiesButton)));
//
//        SimplePanel fileWrapper = wrapSimple(filePanel);
//        fileWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
//        fileWrapper.getElement().getStyle().setProperty("TextAlign", "center");
//        result.add(fileWrapper);
//

//        // --------------------------------------------------
//        HTML html = new HTML("<hr  style=\"width:100%;\" />");
//        result.add(html);
//        // --------------------------------------------------


        FlowPanel editPanel = new FlowPanel();

        selectButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().selectArrow()));
        selectButton.setTitle("Select Mode");
        selectButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.SELECTION);
            }
        });

        panButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().panArrows()));
        panButton.setTitle("Pan Mode");
        panButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.PAN);
            }
        });

        editPanel.add(wrapSimple(layoutHorizontally(selectButton, panButton)));


        zoomInButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().zoomIn(), "Zoom In");
        zoomInButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                zoomIn();
            }
        });

        zoomOutButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().zoomOut(), "Zoom Out");
        zoomOutButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                zoomOut();
            }
        });

        editPanel.add(wrapSimple(layoutHorizontally(zoomInButton, zoomOutButton)));


        undoButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().undoArrow(), "Undo");
        undoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                undo();
            }
        });


        redoButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().redoArrow(), "Redo");
        redoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                redo();
            }
        });

        editPanel.add(wrapSimple(layoutHorizontally(undoButton, redoButton)));


        deleteButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().delete(), "Delete Selected");
        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DELETE);
            }
        });

        editPanel.add(wrapSimple(layoutHorizontally(deleteButton)));

        SimplePanel editWrapper = wrapSimple(editPanel);
        editWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        result.add(editWrapper);



//        // --------------------------------------------------
//        HTML html = new HTML("<hr  style=\"width:100%;\" />");
//        result.add(html);
//        // --------------------------------------------------


        //FlowPanel owlFlow = new FlowPanel();
        //owlFlow.getElement().getStyle().setProperty("textAlign", "center");

//        compileToOWLButton = imageAsButton(InterfaceGlobals.INSTANCE.getIconImages().toOWL(), "Compile to OWL");
//        compileToOWLButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent clickEvent) {
//                compileToOWL();
//            }
//        });

        //owlFlow.add(compileToOWLButton);

        SimplePanel owlWrapper = new SimplePanel();
        owlWrapper.setWidget(compileToOWLButton);
        owlWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        result.add(owlWrapper);
        //getElement().getStyle().setFloat(Float.CENTER);


//        // --------------------------------------------------
//        html = new HTML("<hr  style=\"width:100%;\" />");
//        result.add(html);
//        // --------------------------------------------------


        FlowPanel shapesPanel = new FlowPanel();

        boundaryRectangleButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().boundaryRectangle()));
        boundaryRectangleButton.setTitle("Draw Boundary Rectangle");
        boundaryRectangleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DRAWBOUNDARYRECTANGLE);
            }
        });

        starRectangleButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().asterisk()));
        starRectangleButton.setTitle("Draw Star Rectangle");
        starRectangleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DRAWSTARRECTANGLE);
            }
        });

        shapesPanel.add(wrapSimple(layoutHorizontally(boundaryRectangleButton, starRectangleButton)));


        drawCurveButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().curve()));
        drawCurveButton.setTitle("Draw Curves");
        drawCurveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DRAWCURVE);
            }
        });

        shadeButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().shade()));
        shadeButton.setTitle("Shade");
        shadeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.SHADE);
            }
        });

        shapesPanel.add(wrapSimple(layoutHorizontally(drawCurveButton, shadeButton)));


        drawArrowButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().arrow()));
        drawArrowButton.setTitle("Draw Arrows");
        drawArrowButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DRAWARROW);
            }
        });


        drawSpiderButton = new PushButton(new Image(InterfaceGlobals.INSTANCE.getIconImages().spider()));
        drawSpiderButton.setTitle("Draw Spiders");
        drawSpiderButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ensureState(ModeTypes.DRAWSPIDER);
            }
        });


        shapesPanel.add(wrapSimple(layoutHorizontally(drawArrowButton, drawSpiderButton)));


        SimplePanel shapesWrapper = wrapSimple(shapesPanel);
        shapesWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        result.add(shapesWrapper);


        // maybe a mini map button here too

        return result;
    }



}








//
//        toolPanel.add(inputLabel);
//        textInputBox = new TextBox();
//        toolPanel.add(textInputBox);
//        //textInputBox.setEnabled(false);
//        textInputBox.addChangeHandler(new ChangeHandler() {
//            public void onChange(ChangeEvent changeEvent) {
//                if (getSelectedElement() != null) {
//                    if (getRepresentation(getSelectedElement()) != null) {
//                        CommandManager.get().executeCommand(new ChangeLabelCommand(
//                                getSelectedElement(),
//                                textInputBox.getText()));
//                    }
//                }
//            }
//        });


//        // FIXME : should use commands (also do below)
//        // also needs an input for the constraint number
//        toolPanel.add(new Label("fn:"));
//        functionalSelector = new CheckBox();
//        toolPanel.add(functionalSelector);
//        functionalSelector.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                if(getSelectedElement() != null && getSelectedElement() instanceof ConcreteArrow) {
//                    ConcreteArrow selectedArrow = (ConcreteArrow) getSelectedElement();
//
//                    boolean checked = ((CheckBox) event.getSource()).getValue();
//                    if (checked) {
//                        selectedArrow.setCardinalityConstraint(ConcreteArrow.CardinalityConstraint.LEQ, 1);
//                    } else {
//                        selectedArrow.setCardinalityConstraint(ConcreteArrow.CardinalityConstraint.NONE, 0);
//                    }
//                }
//            }
//        });

//        // FIXME should also use to command ... fix when I do the above
//        // also only the last change to inv counts ... but that should be ok cause we just
//        // get it from the arrow.
//        toolPanel.add(new Label("inv:"));
//        inverseSelector = new CheckBox();
//        toolPanel.add(inverseSelector);
//        inverseSelector.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                if(getSelectedElement() != null && getSelectedElement() instanceof ConcreteArrow) {
//                    ConcreteArrow selectedArrow = (ConcreteArrow) getSelectedElement();
//
//                    boolean checked = ((CheckBox) event.getSource()).getValue();
//                    if (checked) {
//                        selectedArrow.setAsInverse();
//                    } else {
//                        selectedArrow.setAsNotInverse();
//                    }
//                }
//            }
//        });

//        constraintSelector = new ListBox();
//        constraintSelector.addItem("none");
//        constraintSelector.addItem("=");
//        constraintSelector.addItem("<=");
//        constraintSelector.addItem(">=");
//        constraintSelector.setVisibleItemCount(1);
//        constraintSelector.addChangeHandler(
//                new ChangeHandler() {
//                    @Override
//                    public void onChange(ChangeEvent changeEvent) {
//                        if(getSelectedElement() != null && getSelectedElement() instanceof ConcreteArrow) {
//                            ConcreteArrow selectedArrow = (ConcreteArrow) getSelectedElement();
//                            String selected = constraintSelector.getSelectedItemText();
//                            if (selected.equals("none")) {
//
//                            } else if (selected.equals("=")) {
//
//                            } else if (selected.equals("<=")) {
//
//                            } else if (selected.equals(">=")) {
//
//                            }
//                        }
//                    }
//                });



//        clearAllButton = new PushButton("Clear");
//        toolPanel.add(clearAllButton);
//        clearAllButton.setEnabled(true);
//        clearAllButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent clickEvent) {
//                clearAll();
//            }
//        });clearAll