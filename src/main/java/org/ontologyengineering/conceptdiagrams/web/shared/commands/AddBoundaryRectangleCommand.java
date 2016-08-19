package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddBoundaryRectangleEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveBoundaryRectangleEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.Collection;
import java.util.HashSet;

public class AddBoundaryRectangleCommand extends Command  {

    private static String myType = "AddBoundaryRectangleCommand";

    //private Point topLeft, bottomRight;
    private ConcreteBoundaryRectangle boundaryRectangle;
    //private ConcreteDiagram newDiagram;
    //private DiagramSet diagrams;

    // just for serialization
    private AddBoundaryRectangleCommand() {
        super(myType);
    }


    public AddBoundaryRectangleCommand(Point topLeft, Point bottomRight, DiagramSet diagrams) {
        super(myType);

        //this.topLeft = topLeft;
        //this.bottomRight = bottomRight;
        //this.diagrams = diagrams;

        boundaryRectangle = new ConcreteBoundaryRectangle(topLeft, bottomRight);
        //newDiagram = new ConcreteDiagram(boundaryRectangle, diagrams);
        new ConcreteDiagram(boundaryRectangle, diagrams);
    }


    protected AddBoundaryRectangleCommand(ConcreteBoundaryRectangle boundaryRectangle) {
        super(myType);
        this.boundaryRectangle = boundaryRectangle;
        //newDiagram = boundaryRectangle.getDiagram();
        //diagrams = boundaryRectangle.getDiagramSet();
    }

    // this works also for delete / undelete because the only thing that could remain in the diagram is shading of the
    // boundary rectangle, which is kept here.

    @Override
    public void execute() {
        getBoundaryRectangle().getDiagramSet().addDiagram(getDiagram());
    }

    @Override
    public void unExecute() {
        // the diagram will only delete it if there isn't anything in it ... should already have been checked by now
        getBoundaryRectangle().getDiagramSet().removeDiagram(getDiagram());
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddBoundaryRectangleEvent(getBoundaryRectangle()));
        result.add(new AddZoneEvent(getBoundaryRectangle().getMainZone()));
        return result;
    }


    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveBoundaryRectangleEvent(getBoundaryRectangle()));
        result.add(new RemoveZoneEvent(getBoundaryRectangle().getMainZone()));
        return result;
    }

    public ConcreteBoundaryRectangle getBoundaryRectangle() {
        return boundaryRectangle;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getBoundaryRectangle().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return getBoundaryRectangle().isValid();
    }

//    @Override
//    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
//        if(boundaryRectangle.isObject()) {
//            return new AddEmptyClassAndObjectPropertyDiagram(boundaryRectangle);
//        } else {
//            return new AddEmptyDatatypeDiagram(boundaryRectangle);
//        }
//    }

}
