package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddStarRectangleEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveBoundaryRectangleEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteStarRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;
import java.util.HashSet;


public class AddStarRectangleCommand extends Command {

    private Point topLeft, bottomRight;
    private ConcreteStarRectangle boundaryRectangle;
    private ConcreteDiagram newDiagram;

    public AddStarRectangleCommand(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;

        boundaryRectangle = new ConcreteStarRectangle(topLeft, bottomRight);
        newDiagram = new ConcreteDiagram(boundaryRectangle);
    }

    @Override
    public void execute() {

        //boundaryRectangle = new ConcreteBoundaryRectangle(topLeft, bottomRight);
        // put it somewhere ... on a new diagram, but where does that go?
        // maybe command manager should keep it?

    }

    @Override
    public void unExecute() {
        // can't just unexecute if there is anything in the boundary rectangle???
        // but there shouldn't be!
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddStarRectangleEvent(boundaryRectangle));
        result.add(new AddZoneEvent(boundaryRectangle.getMainZone()));
        return result;
    }


    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveBoundaryRectangleEvent(boundaryRectangle));
        result.add(new RemoveZoneEvent(boundaryRectangle.getMainZone()));
        return result;
    }

}
