package org.ontologyengineering.conceptdiagrams.web.client;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import org.ontologyengineering.conceptdiagrams.web.client.handler.ConvertToOWLServiceManager;
import org.ontologyengineering.conceptdiagrams.web.client.handler.StandardDiagramsConvertToOWLServiceManager;
import org.ontologyengineering.conceptdiagrams.web.client.ui.DiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.client.ui.LienzoDiagramCanvas;
import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.StandardClientContext;

/**
 *
 */
public class ConceptDiagrams implements EntryPoint {



    // make the concrete elements parametric on the main type of thing they represent

    // TODO :
    // - 1) calcuate curve intersections  ... works, but not for multi curve intersections & some highlights don't quite work e.g. one curve contained in other, the inner should blank the highlight of the outer
    // - 2) calculate abstract zones
    // - 3) editable/moveable  curves (drag and resize dots)
    // - 4) attach labels
    // - 4) editable labels
    // - all inside a boundary rectangle .. maybe all the shapes in here should be relative to the shapes x,y , like a
    //      group??? so a place on boundry would adjust the x,y ... when pick up group them and move together??
    // - set drag bounds to keep things inside the boundary rectangle c.setDragBounds(new DragBounds(x1, y1, x2, y2));
    // - add new boundary rectangles - only on clean space (use min size)
    // - 5) arrows ... how to do snapping ... and then dragging when the curves/spiders are moved
    //          drawArcJoinedLines in Geometry might help
    // - 6) group select and move
    // - 7) zoom
    // - 7) pan
    // - smaller map representation (just of the curves I think)
    // - how to adjust so that everything is clickable even if another curve is drawn over
    //  i.e. at the moment draw inside curve, then outside curve and the inside isn't selectable.s
    //  they have move up and down functions
    // - enforce a minimum size for curves 2*radius plus a bit (no small curves constraint)
    // - enforce snapping so that intersections are at least 2*radius, i.e. a curcle at the corner (no small intersections constraint)
    // - refactor to have zones, boundary and curves, inheret from a common to cut down on some duplicate code; not essential, but they are rectangular things .. but then the intersection code could all be in one place


    // TODO : Open Questions
    //
    // - How to handle moving a curve that has a shaded zone in it?  If it's the curve that's easy enough.  If it's in
    //      an intersection and the whole intersection moves, that's fine too.  But how to handle moves otherwise.
    //      I would say remove it, but what if the move was just adjusting the curve a bit to make the zone biggier/smaller???
    //      First cut move a curve, just delete all the zones then add back at the new location
    // - How to resize the panel & have a view port that is only part of the panel?  Can't see methods to resize a
    //      panel at the moment ... maybe the idea is start with something say 2X the width and hight of the screen
    //      space and then if we need to resize it, then have to move everything to a new panel of the correct size
    //      and swap the panels?  So panel would be created, then us the view port to zoom in on part of it?
    //      When all this is going on, I'll have to convert viewport coordinates to layer coordinates
    // - When to find the zone a spider resides in?  If it's done on placement of the spider it needs to promoted
    //      if another curve is drawn and makes a zone covering that spider.  But do I need to know the zone of a spider
    //      in the concrete syntax?  Maybe only to ensure that two spiders with a line between them don't appear in
    //      the one zone, but that's probably not so important at the moment.








    private Panel mainPanel;
    private DiagramCanvas diagramCanvas;

    public void onModuleLoad() {
        mainPanel = new AbsolutePanel();
        RootPanel.get().add(mainPanel);

        mainPanel.setSize("1200px", "550px");


        StandardClientContext context = new StandardClientContext();
        context.setIri("http://this.is.a.test/");
        context.setFileName("TEST.owl");

        ConvertToOWLServiceManager converToOWLsrvc = new StandardDiagramsConvertToOWLServiceManager(context);

        diagramCanvas = new LienzoDiagramCanvas(mainPanel.getOffsetWidth(), mainPanel.getOffsetHeight(), mainPanel, converToOWLsrvc);


    }
}
