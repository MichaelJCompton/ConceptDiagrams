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
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import org.ontologyengineering.conceptdiagrams.web.client.events.*;
import org.ontologyengineering.conceptdiagrams.web.client.ui.shapes.LienzoDiagramShape;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.AddArrowCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.ChangeLabelCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.ChangeZoneShadingCommand;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.CommandManager;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.*;

/**
 * Created by Michael on 4/09/2015.
 */
public class LienzoDiagramCanvas extends DiagramCanvas {


    private Rectangle rubberbandRectangle;
    private Circle rubberbandSpider;
    private Line rubberbandArrow;
    private ConcreteDiagramElement arrowSource;

    private LienzoPanel panel;
    private Layer boundaryRectangleLayer;
    private Layer curveLayer;
    private Layer zoneLayer;
    private VerticalPanel mainPanel;
    private HorizontalPanel buttonPanel;

    private Button deleteButton;
    private Button boundaryRectangleButton;
    private Button starRectangleButton;
    private Button shadeButton;
    private Button drawArrowButton;
    private Button drawSpiderButton;
    private Button drawCurveButton;
    private Button panButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button redoButton;
    private Button undoButton;
    private Button selectModeButton;

    private TextBox textInputBox;
    private Label inputLabel = new Label("  Label :  ");

    // quick debugging output
    private Label textOutLabel;
    private DateTimeFormat dateFormat;

    private Point click;
    private Point2D clickT; // The onscreen clicked point transformed into  coordinates on the panel (which may be scalled and transformed)
    private Point lastValidPoint;
    private Point2D lastValidPointT;
    private Point mouseAt;
    private Point2D mouseAtT;
    private ConcreteBoundaryRectangle clickedInRectangle;

    private AbstractMap<ConcreteDiagramElement, LienzoDiagramShape> elementsMap;
    private AbstractMap<ConcreteCurve, AbstractSet<ConcreteZone>> curveToZoneMap;

    private LienzoDiagramPainter painter;


    private final int widthReduction = 0;
    private final int heightReduction = 100;

    public LienzoDiagramCanvas(int width, int height, Panel addToThis) {
        super(width, height, addToThis);
        painter = new LienzoDiagramPainter(this);
        elementsMap = new HashMap<ConcreteDiagramElement, LienzoDiagramShape>();
        curveToZoneMap = new HashMap<ConcreteCurve, AbstractSet<ConcreteZone>>();
        clickedInRectangle = null;

        clickT = new Point2D();
        lastValidPointT = new Point2D();
        mouseAtT = new Point2D();

        createCanvas();
    }


    public void createCanvas() {

        mainPanel = new VerticalPanel();
        buttonPanel = new HorizontalPanel();
        //textOutLabel = new Label();

        panel = new LienzoPanel(); //getWidth()-widthReduction, getHeight()-heightReduction);

        // FIXME : remove this
        //dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        //textOutLabel.setText("Nothing to say yet. " + dateFormat.format(new Date()));

        addToolMenu(buttonPanel);

        curveLayer = new Layer();
        addLayerHandlers();
        panel.add(curveLayer);

        zoneLayer = new Layer();
        panel.add(zoneLayer);
        boundaryRectangleLayer = new Layer();
        panel.add(boundaryRectangleLayer);

        zoneLayer.moveToTop();
        curveLayer.moveToTop();

        curveLayer.setListening(true);
        zoneLayer.setListening(true);
        boundaryRectangleLayer.setListening(true);

        // Not a command, so it can't be undone ... but can delete the initial boundary rectangle
//        ConcreteBoundaryRectangle initalBoundaryRectangle = new ConcreteBoundaryRectangle(
//                new Point(initBoundaryRectangleXoffset, initBoundaryRectangleYoffset),
//                new Point(initBoundaryRectangleXoffset + initBoundaryRectangleWidth, initBoundaryRectangleYoffset + initBoundaryRectangleHeight));
        ConcreteBoundaryRectangle initalBoundaryRectangle = new ConcreteBoundaryRectangle(
                new Point(initBoundaryRectangleXoffset, initBoundaryRectangleYoffset),
                new Point((panel.getViewport().getWidth() / 2),
                        initBoundaryRectangleYoffset + (panel.getViewport().getHeight() - 150)));

        ConcreteDiagram initialDiagram = new ConcreteDiagram(initalBoundaryRectangle);
        addDiagram(initalBoundaryRectangle.getParentDiagram());
        painter.drawRectangle(initalBoundaryRectangle);
        painter.drawZone(initalBoundaryRectangle.getMainZone());

        mainPanel.add(buttonPanel);
        mainPanel.add(panel);
        panel.setTransform(new Transform());
        //mainPanel.add(textOutLabel);
        getParentPanel().add(mainPanel);

        panel.getDragLayer().setTransformable(false);

        boundaryRectangleLayer.batch();
        curveLayer.batch();
        zoneLayer.batch();

        registerForDiagramEvents();

        selectModeButton.click();

        // FIXME : doesn't seem to work in chrome??????????
        panel.getMediators().push(new MouseWheelZoomMediator(EventFilter.CONTROL));
        panel.getMediators().push(new MousePanMediator(EventFilter.SHIFT));

    }


    // FIXME ... why not in diagram canvas
    protected void registerForDiagramEvents() {

        CommandManager.get().getEventBus().addHandler(AddCurveEvent.TYPE,
                new AddCurveEventHandler() {
                    public void onAddCurve(AddCurveEvent event) {
                        painter.drawCurve(event.getCurve());
                        setAsSelectedElement(event.getCurve());
                    }
                });

        CommandManager.get().getEventBus().addHandler(RemoveCurveEvent.TYPE,
                new RemoveCurveEventHandler() {
                    public void onRemoveCurve(RemoveCurveEvent event) {
                        painter.removeCurve(event.getCurve());
                    }
                });

        CommandManager.get().getEventBus().addHandler(AddZoneEvent.TYPE,
                new AddZoneEventHandler() {
                    public void onAddZone(AddZoneEvent event) {
                        painter.drawZone(event.getAddedZone());
                    }
                });

        CommandManager.get().getEventBus().addHandler(RemoveZoneEvent.TYPE,
                new RemoveZoneEventHandler() {
                    public void onRemoveZone(RemoveZoneEvent event) {
                        painter.removeZone(event.getRemovedZone());
                    }
                });

        CommandManager.get().getEventBus().addHandler(AddSpiderEvent.TYPE,
                new AddSpiderEventHandler() {
                    public void onAddSpider(AddSpiderEvent event) {
                        painter.drawSpider(event.getSpider());
                        setAsSelectedElement(event.getSpider());
                    }
                });

        CommandManager.get().getEventBus().addHandler(RemoveSpiderEvent.TYPE,
                new RemoveSpiderEventHandler() {
                    public void onRemoveSpider(RemoveSpiderEvent event) {
                        painter.removeSpider(event.getSpider());
                    }
                });

        CommandManager.get().getEventBus().addHandler(AddBoundaryRectangleEvent.TYPE,
                new AddBoundaryRectangleEventHandler() {
                    public void onAddBoundaryRectangle(AddBoundaryRectangleEvent event) {
                        addDiagram(event.getBoundaryRectangle().getParentDiagram());
                        painter.drawRectangle(event.getBoundaryRectangle());
                    }
                });

        CommandManager.get().getEventBus().addHandler(AddStarRectangleEvent.TYPE,
                new AddStarRectangleEventHandler() {
                    public void onAddStarRectangle(AddStarRectangleEvent event) {
                        addDiagram(event.getAddedRectangle().getParentDiagram());
                        painter.drawStarRectangle(event.getAddedRectangle());
                    }
                });

        CommandManager.get().getEventBus().addHandler(RemoveBoundaryRectangleEvent.TYPE,
                new RemoveBoundaryRectangleEventHandler() {
                    public void onRemoveBoundaryRectangle(RemoveBoundaryRectangleEvent event) {
                        painter.removeRectangle(event.getBoundaryRectangle());
                    }
                });

        CommandManager.get().getEventBus().addHandler(ChangeZoneShadingEvent.TYPE,
                new ChangeZoneShadingEventHandler() {
                    public void onChangeZoneShading(ChangeZoneShadingEvent event) {
                        if (event.getZoneChanged().shaded()) {
                            painter.shadeZone(event.getZoneChanged());
                        } else {
                            painter.unShadeZone(event.getZoneChanged());
                        }
                    }
                });

        CommandManager.get().getEventBus().addHandler(AddArrowEvent.TYPE,
                new AddArrowEventHandler() {
                    public void onAddArrow(AddArrowEvent event) {
                        painter.drawArrow(event.getAddedArrow());
                        setAsSelectedElement(event.getAddedArrow());
                    }
                });


        CommandManager.get().getEventBus().addHandler(ResizeElementEvent.TYPE, new ResizeElementEventHandler() {
            public void onResizeElement(ResizeElementEvent event) {
//                if (getCurveToZoneMap().containsKey(event.getResizedElement())) {
//                    // undraw all the curves in the map
//                    for (ConcreteZone z : getCurveToZoneMap().get(event.getResizedElement())) {
//                        painter.removeZone(z);
//                    }
//                    removeFromCurveToZoneMap((ConcreteCurve) event.getResizedElement());
//
//                    // now draw the new ones
//                    painter.drawAllZones((ConcreteCurve) event.getResizedElement());
//                }

                painter.redraw(event.getResizedElement());
            }
        });

        CommandManager.get().getEventBus().addHandler(ChangeLabelEvent.TYPE, new ChangeLabelEventHandler() {
            public void onChangeLabel(ChangeLabelEvent event) {
                painter.addLabel(event.changedElement());
            }
        });
    }


    private Set<ConcreteDiagramElement> getElements() {
        return elementsMap.keySet();
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
        getCurveToZoneMap().get(curve).remove(zone);
        for (ConcreteCurve c : zone.getCurves()) {
            if (c != curve) {
                removeFromCurveToZoneMap(c, zone);
            }
        }
    }

    protected void removeFromCurveToZoneMap(ConcreteCurve curve) {
        removeFromCurveToZoneMap(curve, curve.getMainZone());
        for (ConcreteIntersectionZone zone : curve.getEnclosedZones()) {
            removeFromCurveToZoneMap(curve, zone);
        }
    }

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

    public Layer getCurveLayer() {
        return curveLayer;
    }

    protected Layer getBoundaryRectangleLayer() {
        return boundaryRectangleLayer;
    }

    protected Layer getZoneLayer() {
        return zoneLayer;
    }

    private void addToLayer(LienzoDiagramShape shape, Layer layer) {
        shape.draw(layer);
        layer.batch();
    }

    protected void removeFromElementsOnCanvas(ConcreteDiagramElement element) {
        getElementsMap().remove(element);
    }

    protected void addToCurveLayer(LienzoDiagramShape shape) {
        shape.draw(curveLayer);
        curveLayer.batch();
    }

    protected void addToBoundaryRectangleLayer(LienzoDiagramShape shape) {
        shape.draw(boundaryRectangleLayer);
        boundaryRectangleLayer.batch();
    }

    protected void addToZoneLayer(LienzoDiagramShape shape) {
        shape.draw(zoneLayer);
        zoneLayer.batch();
    }

    protected void removeShape(LienzoDiagramShape shape) {
        Layer layer = shape.getLayer();
        shape.undrawAll();
        layer.batch();
    }

    protected void removeFromBoundaryRectangleLayer(IPrimitive node) {
        boundaryRectangleLayer.remove(node);
        boundaryRectangleLayer.batch();
    }

    protected void removeFromCurveLayer(IPrimitive node) {
        curveLayer.remove(node);
        curveLayer.batch();
    }

    protected void removeFromZoneLayer(IPrimitive node) {
        zoneLayer.remove(node);
        zoneLayer.batch();
    }


    protected void setNotSelected(ConcreteDiagramElement elmnt) {
        getRepresentation(elmnt).unDrawDragRepresentation();
        getRepresentation(elmnt).setAsUnSelected();
    }

    protected void setSelected(ConcreteDiagramElement elmnt) {
        getRepresentation(elmnt).drawDragRepresentation();
        getRepresentation(elmnt).setAsSelected();
    }

    public void setAsSelectedElement(ConcreteDiagramElement elmnt) {
        super.setAsSelectedElement(elmnt);

        if(getRepresentation(elmnt) != null && getRepresentation(elmnt).hasLabel()) {
            textInputBox.setText(getRepresentation(elmnt).getLabelText());
        } else {
            textInputBox.setText("");
        }
        textInputBox.setFocus(true);
    }

    private void addLayerHandlers() {

        curveLayer.addNodeMouseDownHandler(new NodeMouseDownHandler() {
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                if (event.isButtonLeft()) {

                    setClick(new Point(event.getX(), event.getY()));
                    clickedInRectangle = boundaryRectangleAtPoint(getClickAsCanvasCoordP());

                    switch (getMode()) {
                        case SELECTION:
                            // FIXME : if the thing under the mouse is the/a selected thing, we start it's drag and it
                            // handles it from there.
                            setMode(ModeTypes.DRAGSELECT);
                            startRubberBandRectangle();
                            break;
                        case PAN:
                            setMode(ModeTypes.PANNING);
                            break;
                        case DRAWCURVE:
                            if (clickedInRectangle != null) {
                                setMode(ModeTypes.DRAWINGCRVE);
                                startRubberBandRectangle();
                            }
                            break;
                        case DRAWSPIDER:
                            if (clickedInRectangle != null) {
                                setMode(ModeTypes.DRAWINGSPIDER);
                                startRubberBandSpider();
                            }
                            break;
                        case DRAWBOUNDARYRECTANGLE:
                            if (clickedInRectangle == null) {
                                setMode(ModeTypes.DRAWINGBOUNDARYRECTANGLE);
                                startRubberBandRectangle();
                            }
                            break;
                        case DRAWSTARRECTANGLE:
                            if (clickedInRectangle == null) {
                                setMode(ModeTypes.DRAWINGSTARRECTANGLE);
                                startRubberBandRectangle();
                            }
                            break;
                        case DRAWARROW:
                            if (underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                                    || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                                    || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE
                                    || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE) {
                                setMode(ModeTypes.DRAWINGARROW);
                                startRubberBandArrow();
                                arrowSource = underMouse();
                            }
                            break;
                    }
                }
                curveLayer.batch();
            }
        });

        curveLayer.addNodeMouseMoveHandler(new NodeMouseMoveHandler() {
            public void onNodeMouseMove(NodeMouseMoveEvent event) {
                setMouseAt(new Point(event.getX(), event.getY()));

                switch (getMode()) {
                    case DRAGSELECT:
                        dragRubberBandRectangle(getMouseAtAsScreenCoord());
                        break;
                    case DRAWINGCRVE:
                        if (clickedInRectangle != null) {
                            if (clickedInRectangle.containsPoint(getMouseAtAsCanvasCoordP())) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                            }
                        }
                        dragRubberBandRectangle(getLastValidAsScreenCoord());
                        break;
                    case DRAWINGSPIDER:
                        if (clickedInRectangle != null) {
                            if (clickedInRectangle.containsPoint(getMouseAtAsCanvasCoordP())) {
                                setLastValidPoint(getMouseAtAsScreenCoord());
                            }
                        }
                        dragRubberBandSpider(getLastValidAsScreenCoord());
                        break;
                    case DRAWINGBOUNDARYRECTANGLE:
                        if (boundaryRectangleAtPoint(getMouseAtAsCanvasCoordP()) == null) {
                            setLastValidPoint(getMouseAtAsScreenCoord());
                        }
                        dragRubberBandRectangle(getLastValidAsScreenCoord());
                        break;
                    case DRAWINGSTARRECTANGLE:
                        if (boundaryRectangleAtPoint(getMouseAtAsCanvasCoordP()) == null) {
                            setLastValidPoint(getMouseAtAsScreenCoord());
                        }
                        dragRubberBandRectangle(getLastValidAsScreenCoord());
                        break;
                    case DRAWINGARROW:
                        dragRubberBandArrow(getMouseAtAsScreenCoord());
                        break;
                }
                curveLayer.batch();
            }

        });

        curveLayer.addNodeMouseUpHandler(new NodeMouseUpHandler() {
            public void onNodeMouseUp(NodeMouseUpEvent event) {
                setMouseAt(new Point(event.getX(), event.getY()));

                boolean sameRectangle = false;

                if (clickedInRectangle != null) {
                    if (clickedInRectangle.containsPoint(getMouseAtAsCanvasCoordP())) {
                        setLastValidPoint(getMouseAtAsScreenCoord());
                        sameRectangle = true;
                    }
                }

                if (event.isButtonLeft()) {

                    switch (getMode()) {
                        case DRAGSELECT:
                            setMode(ModeTypes.SELECTION);

                            // FIXME : need to check for shift key down for add to selection
                            // how do I know if it's shift or not???

                            //clearSelection();
                            // might have been a drag select or a regular click, check which
                            if (getMouseAtAsScreenCoord().getX() == getClickAsScreenCoord().getX() && getMouseAtAsScreenCoord().getY() == getClickAsScreenCoord().getY()) {
                                // click select


                                if (underMouse() != null) {
                                    if (underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                                            || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                                            || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEARROW
                                            || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE
                                            || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESTARRECTANGLE) {
                                        setAsSelectedElement(underMouse());
                                    }
                                }


                                // FIXME : need to check here if we are adding to selection or new selection
//                                if(ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETEARROW) {
//                                    setMode(ModeTypes.DRAGINGARROW);
//                                    addSelectedElement(ConcreteSyntaxElement.getElementUnderMouse());
//                                    (ConcreteSyntaxElement.getElementUnderMouse()).setAsSelected();
//                                }

                                // find what's under the mouse
                                // could do this in reverse and have objects register that they are under the mouse in the mouse over routines
                                //ConcreteSyntaxElement.getElementUnderMouse().swapShading();
                            } else {
                                //drag select
                            }

                            removeRubberBandRectangle();
                            break;
                        case DRAWINGCRVE:
                            setMode(ModeTypes.DRAWCURVE);
                            panel.getDragLayer().remove(rubberbandRectangle);

                            if (sameRectangle) {
                                // no small curves constraint
                                if (Math.abs(getLastValidAsCanvasCoord().getX() - getClickAsCanvasCoord().getX()) > ConcreteDiagramElement.curveMinWidth &&
                                        Math.abs(getLastValidAsCanvasCoord().getY() - getClickAsCanvasCoord().getY()) > ConcreteDiagramElement.curveMinHeight) {

                                    addCurve(new Point((getLastValidAsCanvasCoord().getX() >= getClickAsCanvasCoord().getX()) ? getClickAsCanvasCoord().getX() : getLastValidAsCanvasCoord().getX(), (getLastValidAsCanvasCoord().getY() >= getClickAsCanvasCoord().getY()) ? getClickAsCanvasCoord().getY() : getLastValidAsCanvasCoord().getY()), // topleft
                                            new Point((getLastValidAsCanvasCoord().getX() >= getClickAsCanvasCoord().getX()) ? getLastValidAsCanvasCoord().getX() : getClickAsCanvasCoord().getX(), (getLastValidAsCanvasCoord().getY() >= getClickAsCanvasCoord().getY()) ? getLastValidAsCanvasCoord().getY() : getClickAsCanvasCoord().getY()),
                                            clickedInRectangle); // botright
                                }


//                                if (Math.abs(lastValidPoint.getX() - click.getX()) > ConcreteDiagramElement.curveMinWidth &&
//                                        Math.abs(lastValidPoint.getY() - click.getY()) > ConcreteDiagramElement.curveMinHeight) {
//
//                                    addCurve(new Point((lastValidPoint.getX() >= click.getX()) ? click.getX() : lastValidPoint.getX(), (lastValidPoint.getY() >= click.getY()) ? click.getY() : lastValidPoint.getY()), // topleft
//                                            new Point((lastValidPoint.getX() >= click.getX()) ? lastValidPoint.getX() : click.getX(), (lastValidPoint.getY() >= click.getY()) ? lastValidPoint.getY() : click.getY()),
//                                            clickedInRectangle); // botright
//                                }
                            }

                            break;
                        case DRAWINGSPIDER:
                            setMode(ModeTypes.DRAWSPIDER);
                            panel.getDragLayer().remove(rubberbandSpider);

                            if (sameRectangle) {
                                addSpider(new Point(getLastValidAsCanvasCoord().getX(), getLastValidAsCanvasCoord().getY()), clickedInRectangle);
                            }

                            break;
                        case DRAWINGBOUNDARYRECTANGLE:
                            setMode(ModeTypes.DRAWBOUNDARYRECTANGLE);
                            addBoundaryRectangle(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP());
                            panel.getDragLayer().remove(rubberbandRectangle);
                            break;
                        case DRAWINGSTARRECTANGLE:
                            setMode(ModeTypes.DRAWSTARRECTANGLE);
                            addStarRectangle(getClickAsCanvasCoordP(), getLastValidAsCanvasCoordP());
                            panel.getDragLayer().remove(rubberbandRectangle);
                            break;
                        case SHADE:
                            if (underMouse() != null) {
                                if (underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEZONE ||
                                        underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEINTERSECTIONZONE) {
                                    CommandManager.get().executeCommand(new ChangeZoneShadingCommand((ConcreteZone) underMouse()));
                                }
                            } else {
                                ConcreteBoundaryRectangle brUnderMouse = boundaryRectangleAtPoint(getMouseAtAsCanvasCoordP());
                                if (brUnderMouse != null) {
                                    // FIXME this should be in the comands
                                    getRepresentation(brUnderMouse).shade();
                                }
                            }
                            break;
                        case DELETE:
//                            if(ConcreteSyntaxElement.getElementUnderMouse().getType() != ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETEZONE) {
//                                ConcreteSyntaxElement.getElementUnderMouse().deleteMe();
//                                // incase it's a curve
//                                for(int i = 0; i < intersectionGrid.size(); i++) {
//                                    for(int j = 0; j < intersectionGrid.get(i).size(); j++) {
//                                        intersectionGrid.get(i).get(j).remove(ConcreteSyntaxElement.getElementUnderMouse());
//                                    }
//                                }
//                            }
                            break;
                        case DRAWINGARROW:
                            setMode(ModeTypes.DRAWARROW);
                            if (underMouse() != null) {
                                if (underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE
                                        || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETESPIDER
                                        || underMouse().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE) {

                                    CommandManager.get().executeCommand(new AddArrowCommand(getClickAsCanvasCoordP(), new Point(mouseAtT.getX(), mouseAtT.getY()), arrowSource, underMouse()));
                                }
                            }
                            setMode(ModeTypes.DRAWARROW);
                            panel.getDragLayer().remove(rubberbandArrow);
                            break;
                    }
                }

                clickedInRectangle = null;
                panel.getDragLayer().batch();
                curveLayer.batch();
            }
        });

    }

    private void setClick(Point clickedPoint) {
        click = clickedPoint;
        panel.getViewport().getTransform().getInverse().transform(click.asLienzoPoint2D(), clickT);
    }

    private Point getClickAsScreenCoord() {
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
        panel.getViewport().getTransform().getInverse().transform(lastValidPoint.asLienzoPoint2D(), lastValidPointT);
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
        panel.getViewport().getTransform().getInverse().transform(mouseAt.asLienzoPoint2D(), mouseAtT);
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


    // assumes that a click.getX() and click.getY() have been set
    private void startRubberBandRectangle() {
        rubberbandRectangle = new Rectangle(0, 0);
        rubberbandRectangle.setX(getClickAsScreenCoord().getX()).setY(getClickAsScreenCoord().getY());
        panel.getDragLayer().add(rubberbandRectangle);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandRectangle(Point newPoint) {

        //Point2D newPointT = new Point2D();
        //panel.getViewport().getTransform().getInverse().transform(new Point2D(newPoint.getX(), newPoint.getY()), newPointT);

//        double newWidth = (newPoint.getX() >= click.getX()) ? (newPoint.getX() - click.getX()) : (click.getX() - newPoint.getX());
//        double newHeight = (newPoint.getY() >= click.getY()) ? (newPoint.getY() - click.getY()) : (click.getY() - newPoint.getY());
//        rubberbandRectangle.setHeight(newHeight);
//        rubberbandRectangle.setWidth(newWidth);
//
//        rubberbandRectangle.setX((newPoint.getX() >= click.getX()) ? click.getX() : newPoint.getX());
//        rubberbandRectangle.setY((newPoint.getY() >= click.getY()) ? click.getY() : newPoint.getY());

        double newWidth = (newPoint.getX() >= getClickAsScreenCoord().getX()) ? (newPoint.getX() - getClickAsScreenCoord().getX()) : (getClickAsScreenCoord().getX() - newPoint.getX());
        double newHeight = (newPoint.getY() >= getClickAsScreenCoord().getY()) ? (newPoint.getY() - getClickAsScreenCoord().getY()) : (getClickAsScreenCoord().getY() - newPoint.getY());
        rubberbandRectangle.setHeight(newHeight);
        rubberbandRectangle.setWidth(newWidth);

        rubberbandRectangle.setX((newPoint.getX() >= getClickAsScreenCoord().getX()) ? getClickAsScreenCoord().getX() : newPoint.getX());
        rubberbandRectangle.setY((newPoint.getY() >= getClickAsScreenCoord().getY()) ? getClickAsScreenCoord().getY() : newPoint.getY());

        panel.getDragLayer().batch();
    }

    public void removeRubberBandRectangle() {
        panel.getDragLayer().remove(rubberbandRectangle);
        panel.getDragLayer().batch();
    }

    private void startRubberBandSpider() {
        // FIXME : how about a little rubber banding spider with legs for fun?
        rubberbandSpider = new Circle(ConcreteDiagramElement.spiderRadius);
        rubberbandSpider.setX(getClickAsScreenCoord().getX()).setY(getClickAsScreenCoord().getY());
        panel.getDragLayer().add(rubberbandSpider);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandSpider(Point newPoint) {
        rubberbandSpider.setX(newPoint.getX()).setY(newPoint.getY());
        panel.getDragLayer().batch();
    }

    private void startRubberBandArrow() {
        rubberbandArrow = new Line(new Point2D(click.getX(), click.getY()), new Point2D(click.getX(), click.getY()));
        panel.getDragLayer().add(rubberbandArrow);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandArrow(Point newPoint) {
        rubberbandArrow.setPoints(new Point2DArray(new Point2D(click.getX(), click.getY()), new Point2D(newPoint.getX(), newPoint.getY())));
        panel.getDragLayer().batch();
    }


    private void addToolMenu(Panel toolPanel) {

        selectModeButton = new Button("Select");
        toolPanel.add(selectModeButton);
        selectModeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.SELECTION);
            }
        });

        panButton = new Button("Pan");
        toolPanel.add(panButton);
        panButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.PAN);
                // FIXME : think everything on the canvas should be unclickable at this point
                curveLayer.setListening(true);
            }
        });

        zoomInButton = new Button("Zoom In");
        toolPanel.add(zoomInButton);
        zoomInButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                panel.getViewport().getTransform().scale(1.1);
                // FIXME can't get any sort of transform changed event to register witht the handler, even when I fire it myself
                // panel.getScene().getViewport().fireEvent(new ViewportTransformChangedEvent(panel.getViewport()));
                panel.batch();
            }
        });


        zoomOutButton = new Button("Zoom Out");
        toolPanel.add(zoomOutButton);
        zoomOutButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                panel.getViewport().getTransform().scale(.90);
                // not sure if this is necessary -- works without
                // is the panel smaller than the visible viewport at this zoom?
//                Point2D viewportBotRight = new Point2D(panel.getViewport().getWidth(), panel.getViewport().getWidth());
//                Point2D viewportBotRightT = new Point2D();
//                panel.getViewport().getTransform().getInverse().transform(viewportBotRight, viewportBotRightT);
//
//                if(viewportBotRightT.getX() > curveLayer.getWidth()) {
//                    curveLayer.getScene().setWidth((int) viewportBotRightT.getX());
//                }
//                if(viewportBotRightT.getY() > curveLayer.getHeight()) {
//                    curveLayer.getScene().setHeight((int) viewportBotRightT.getY());
//                }

                panel.batch();
            }
        });


        undoButton = new Button("Undo");
        toolPanel.add(undoButton);
        //undoButton.setEnabled(false);
        undoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (CommandManager.get().canUndo()) {
                    CommandManager.get().undo();
                }
                setEnabledButtons();
            }
        });


        redoButton = new Button("Redo");
        toolPanel.add(redoButton);
        //redoButton.setEnabled(false);
        redoButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (CommandManager.get().canRedo()) {
                    CommandManager.get().redo();
                }
                setEnabledButtons();
            }
        });


        drawCurveButton = new Button("Curve");
        toolPanel.add(drawCurveButton);
        drawCurveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DRAWCURVE);
                //for (ConcreteSyntaxElement e : elementsMap) {
                //  e.getConcreteRepresentation().setDraggable(false);
                //}
                curveLayer.setListening(true);
            }
        });


        drawSpiderButton = new Button("Spider");
        toolPanel.add(drawSpiderButton);
        drawSpiderButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DRAWSPIDER);
                curveLayer.setListening(true);
            }
        });


        drawArrowButton = new Button("Arrow");
        toolPanel.add(drawArrowButton);
        drawArrowButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DRAWARROW);
                curveLayer.setListening(true);
            }
        });


        shadeButton = new Button("Shade");
        toolPanel.add(shadeButton);
        shadeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.SHADE);
                curveLayer.setListening(true);
            }
        });

        boundaryRectangleButton = new Button("Rectangle");
        toolPanel.add(boundaryRectangleButton);
        boundaryRectangleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DRAWBOUNDARYRECTANGLE);
                curveLayer.setListening(true);
            }
        });

        starRectangleButton = new Button("*");
        toolPanel.add(starRectangleButton);
        starRectangleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DRAWSTARRECTANGLE);
                curveLayer.setListening(true);
            }
        });

        deleteButton = new Button("Delete");
        toolPanel.add(deleteButton);
        deleteButton.setEnabled(false);
        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setMode(ModeTypes.DELETE);
                curveLayer.setListening(true);
            }
        });


        toolPanel.add(inputLabel);
        textInputBox = new TextBox();
        toolPanel.add(textInputBox);
        //textInputBox.setEnabled(false);
        textInputBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent changeEvent) {
                if (getSelectedElement() != null) {
                    if (getRepresentation(getSelectedElement()) != null) {
                        CommandManager.get().executeCommand(new ChangeLabelCommand(
                                getSelectedElement(),
                                textInputBox.getText()));
                }
            }
        }
    });


        setEnabledButtons();
    }

    protected void setEnabledButtons() {
        if (CommandManager.get().canUndo()) {
            undoButton.setEnabled(true);
        } else {
            undoButton.setEnabled(false);
        }

        if (CommandManager.get().canRedo()) {
            redoButton.setEnabled(true);
        } else {
            redoButton.setEnabled(false);
        }

        if(getSelectedElements().size() == 1) {
            textInputBox.setEnabled(true);
        } else {
            //textInputBox.setEnabled(false);
        }

        // FIXME can only zoom in and out if the maxes aren't reached
    }
}
