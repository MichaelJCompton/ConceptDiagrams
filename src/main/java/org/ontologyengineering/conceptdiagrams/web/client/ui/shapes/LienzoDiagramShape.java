package org.ontologyengineering.conceptdiagrams.web.client.ui.shapes;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Root abstract class for all the shapes used in the ui.
 * <p/>
 * Also allows for all the decorations and labels etc to be drawn.
 */
public abstract class LienzoDiagramShape<T extends ConcreteDiagramElement, M extends Node> {
// FIXME ... thought I could make good use of the generic typing here, not so sure now, look again


    protected static final Color visibleWhite = new Color(255, 255, 255, 1);     // 1 = visible
    protected static final Color invisibleWhite = new Color(255, 255, 255, 0);     // 0 = invisible


    // Should also be an option to read these from config files

    protected static final ColorName curveBorderColourName = ColorName.BLACK;
    protected static final Color curveBorderColour = curveBorderColourName.getColor();
    protected static final Color curveFillColour = invisibleWhite;
    protected static final Color curveMouseOverFillColour = invisibleWhite;
    protected static final Color curveSelectedFillColour = invisibleWhite;
    protected static final ColorName curveBorderSelectedColorName = ColorName.BLUE;
    protected static final Color curveBorderSelectedColor = curveBorderSelectedColorName.getColor();
    protected static final ColorName curveBorderMouseOverColorName = ColorName.RED;
    protected static final Color curveBorderMouseOverColor = curveBorderMouseOverColorName.getColor();


    protected static final ColorName spiderColourName = ColorName.BLACK;
    protected static final Color spiderColour = spiderColourName.getColor();
    protected static final ColorName spiderSelectedColorName = ColorName.RED;
    protected static final Color spiderSelectedColor = spiderSelectedColorName.getColor();
    protected static final ColorName spiderMouseOverColorName = ColorName.RED;
    protected static final Color spiderMouseOverColor = spiderMouseOverColorName.getColor();


    protected static final ColorName boundaryRectangleColourName = ColorName.BLACK;
    protected static final Color boundaryRectangleColour = boundaryRectangleColourName.getColor();
    protected static final Color boundaryRectangleFillColour = invisibleWhite;
    protected static final Color boundaryRectangleMouseOverFillColour = invisibleWhite;
    protected static final Color boundaryRectangleSelectedFillColour = invisibleWhite;
    protected static final ColorName boundaryRectangleSelectedColourName = ColorName.BLUE;
    protected static final Color boundaryRectangleSelectedColour = boundaryRectangleSelectedColourName.getColor();
    protected static final ColorName boundaryRectangleMouseOverColourName = ColorName.RED;
    protected static final Color boundaryRectangleMouseOverColour = boundaryRectangleMouseOverColourName.getColor();

    protected static final ColorName starColourName = ColorName.BLACK;
    protected static final Color starColour = starColourName.getColor();

    protected static final Color zoneStandardColour = visibleWhite;
    protected static final ColorName zoneMouseOverColorName = ColorName.LIGHTGRAY;
    protected static final Color zoneMouseOverColor = zoneMouseOverColorName.getColor().setA(1);
    protected static final ColorName zoneSelectedColorName = ColorName.LIGHTGRAY;
    protected static final Color zoneSelectedColor = zoneSelectedColorName.getColor().setA(1);
    protected static final ColorName zoneShadedColorName = ColorName.DARKGRAY;
    protected static final Color zoneShadedColor = zoneShadedColorName.getColor().setA(1);


    protected static final ColorName arrowColourName = ColorName.BLACK;
    protected static final Color arrowColour = arrowColourName.getColor();
    protected static final ColorName arrowSelectedColourName = ColorName.RED;
    protected static final Color arrowSelectedColour = arrowSelectedColourName.getColor();
    protected static final ColorName arrowMouseOverColourName = ColorName.RED;
    protected static final Color arrowMouseOverColour = arrowMouseOverColourName.getColor();


    protected static final ColorName rubberbandRetangleColorName = ColorName.LIGHTSLATEGREY;
    protected static final Color rubberbandRetangleColor = rubberbandRetangleColorName.getColor();
    protected static final ColorName dragBoxColourName = ColorName.LIGHTSLATEGREY;
    protected static final Color dragBoxColour = dragBoxColourName.getColor();

    protected static final ColorName labelColourName = ColorName.BLACK;
    protected static final Color labelColour = labelColourName.getColor();


    protected static final double arrowLineWidth = 2;
    protected static final int pointsInArrowLine = 4;
    protected static final double rubberbandLineWidth = 1;  // not absoloute, I'll adjust inline to get a screen width
    protected static final ColorName rubberBandColourName = ColorName.DARKSLATEGRAY;
    protected static final Color rubberBandColour = rubberBandColourName.getColor().setA(0.5);

    protected static final double dragBoxSize = 6;


    private T element;
    protected M representation;

    private LienzoDragBoundsGroup dragRepresentation;
    private LienzoDragRubberBand dragRubberBand;

    private LienzoLabel label;

    protected LienzoDiagramCanvas canvas;

    protected Color lineColour, mouseOverLineColour, selectedLineColour;
    protected Color fillColour, mouseOverFillColour, selectedFillColour;
    private Color shadedColour;


    // for some drawables the element doesn't make sense ... such as a group, which matches to a Lienzo group
    // but no similar random grouping in the syntax
    public LienzoDiagramShape(LienzoDiagramCanvas canvas) {
        element = null;
        this.canvas = canvas;
    }

    public LienzoDiagramShape(T elementToRepresent, LienzoDiagramCanvas canvas) {
        element = elementToRepresent;
        this.canvas = canvas;
    }

    // each shape will also store some sort of Lienzo shape to draw.
    public M getRepresentation() {
        return representation;
    }

    public T getDiagramElement() {
        return element;
    }

    public LienzoDiagramCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(LienzoDiagramCanvas canvas) {
        this.canvas = canvas;
    }

    public abstract BoundingBox getBoundingBox();

    // FIXME : why doesn't this just rely on the internal drag bounds boxes and not pass this in?
    public abstract void dragBoundsMoved(BoundingBox newBoundingBox);

    public void addLabel() {
        boolean hadLabel = false;
        double x = 0;
        double y = 0;

        if(hasLabel()) {
            hadLabel = true;
            x = getLabel().getRepresentation().getX();
            y = getLabel().getRepresentation().getY();
            getLabel().undraw();
        }

        if(getDiagramElement() != null && getDiagramElement().labelText() != null) {
            label = new LienzoLabel(getDiagramElement(), getCanvas(), getDiagramElement().labelText());

            if(hadLabel) {
                label.getRepresentation().setX(x);
                label.getRepresentation().setY(y);
            }
            label.draw(getLayer());
            redraw();
        }
    }

    public LienzoLabel getLabel() {
        return label;
    }

    public String getLabelText() { return getLabel().getLabelText(); }

    public boolean hasLabel() {
        return (getLabel() != null);
    }


    // ---------------------------------------------------------------------------------------
    //                          Drawing
    // ---------------------------------------------------------------------------------------


    /**
     * Draw the representation onto layer.
     * <p/>
     * This version assumes the representation has already been set up, e.g. in constructor.  If not override.
     *
     * @param layer
     */
    public void draw(Layer layer) {
        layer.add(getRepresentation().asPrimitive());
        layer.batch();
    }


    /**
     * After some change to the underlying element, say a resize or move, the element on screen needs to be redrawn.
     */
    public abstract void redraw();

    public void drawRubberBand() {
        makedragRubberBand();
        getDragRubberBand().draw(null);
    }

    public void undrawRubberBand() {
        getDragRubberBand().undraw();
    }


    /**
     * draw the boundary rubber band
     * <p/>
     * Requires that has already been drawn and getRepresentation().getLayer() != null
     */
//    public void drawRubberBandRepresentation() {
//        if (getRepresentation() != null && getLayer() != null) {
//            setDragRepresentation(new Lie(getCanvas(), this));
//            getDragRepresentation().draw(getLayer());
//        }
//    }
//
//    public void unDrawDragRepresentation() {
//        if (getDragRepresentation() != null) {
//            getDragRepresentation().undraw();
//            setDragRepresentation(null);
//        }
//    }

    public abstract void setAsSelected();

    public abstract void setAsUnSelected();

    protected LienzoDragBoundsGroup getDragRepresentation() {
        return dragRepresentation;
    }

    protected void setDragRepresentation(LienzoDragBoundsGroup dragRepresentation) {
        this.dragRepresentation = dragRepresentation;
    }

    protected LienzoDragRubberBand getDragRubberBand() {
        return dragRubberBand;
    }

    protected void makedragRubberBand() {
        if(dragRubberBand == null) {
            dragRubberBand = new LienzoDragRubberBand(getCanvas(), this);
        }
    }

    // subclasses with children will override
    public void drawAll(Layer layer) {
        draw(layer);
    }


    public void undraw() {
        if (getLayer() != null) {
            Layer layer = getRepresentation().getLayer();
            getRepresentation().getLayer().remove(getRepresentation().asPrimitive());
            batch();
        }
    }


    // subclasses with children will override
    public void undrawAll() {
        undraw();
    }

    // assume any shape is on only one layer - true for underlying graphics library
    public Layer getLayer() {
        if (getRepresentation() != null) {
            return getRepresentation().getLayer();
        }
        return null;
    }

    public void moveToTop() {
        if (getLayer() != null) {
            getRepresentation().getLayer().moveToTop(getRepresentation().asPrimitive());
        }
    }

    protected void batch() {
        if (getLayer() != null) {
            getLayer().batch();
        }
    }


    // ---------------------------------------------------------------------------------------
    //                          Handlers
    // ---------------------------------------------------------------------------------------


    // need to pass this in because the stored representation could be anything, eg groups which will reimplement
    protected void stdMouseEnterHandler(final Shape shape) {
        shape.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                getCanvas().setIsUnderMouse(getDiagramElement());
                shape.setStrokeColor(getMouseOverLineColour());
                shape.setFillColor(getMouseOverFillColour());
                shape.getLayer().batch();
            }
        });
    }


    protected void stdMouseExitHandler(final Shape shape) {
        shape.addNodeMouseExitHandler(new NodeMouseExitHandler() {
            public void onNodeMouseExit(NodeMouseExitEvent event) {
                shape.setStrokeColor(getLineColour());
                shape.setFillColor(getFillColour());
                shape.getLayer().batch();
            }
        });
    }

    // ---------------------------------------------------------------------------------------
    //                          Colours
    // ---------------------------------------------------------------------------------------

    // just implemented in the zones
    public void shade() {
    }

    public void unShade() {
    }


    public void setLineColour(Color lineColour) {
        this.lineColour = lineColour;
    }


    public Color getLineColour() {
        return lineColour;
    }


    public Color getSelectedLineColour() {
        return selectedLineColour;
    }


    public void setSelectedLineColour(Color selectedLineColour) {
        this.selectedLineColour = selectedLineColour;
    }


    public Color getMouseOverLineColour() {
        return mouseOverLineColour;
    }


    public void setMouseOverLineColour(Color mouseOverLineColour) {
        this.mouseOverLineColour = mouseOverLineColour;
    }


    public Color getFillColour() {
        return fillColour;
    }


    public void setFillColour(Color fillColour) {
        this.fillColour = fillColour;
    }


    public Color getMouseOverFillColour() {
        return mouseOverFillColour;
    }


    public void setMouseOverFillColour(Color mouseOverFillColour) {
        this.mouseOverFillColour = mouseOverFillColour;
    }


    public Color getSelectedFillColour() {
        return selectedFillColour;
    }


    public void setSelectedFillColour(Color selectedFillColour) {
        this.selectedFillColour = selectedFillColour;
    }


    public void setShadedColour(Color colour) {
        shadedColour = colour;
    }


    public Color getShadedColour() {
        return shadedColour;
    }
}
