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
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.AddEmptyClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.AddEmptyDatatypeDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.HashSet;

public class AddBoundaryRectangleCommand extends Command  {

    private Point topLeft, bottomRight;
    private ConcreteBoundaryRectangle boundaryRectangle;
    private ConcreteDiagram newDiagram;

    public AddBoundaryRectangleCommand(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;

        boundaryRectangle = new ConcreteBoundaryRectangle(topLeft, bottomRight);
        newDiagram = new ConcreteDiagram(boundaryRectangle);
    }

    @Override
    public void execute() {

        //boundaryRectangle = new ConcreteBoundaryRectangle(topLeft, bottomRight);
        // put it somewhere


    }

    @Override
    public void unExecute() {
        // can't just unexecute if there is anything in the boundary rectangle???
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddBoundaryRectangleEvent(boundaryRectangle));
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

    @Override
    public ConcreteDiagram getDiagram() {
        return boundaryRectangle.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return boundaryRectangle.isValid();
    }

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
        if(boundaryRectangle.isObject()) {
            return new AddEmptyClassAndObjectPropertyDiagram(boundaryRectangle);
        } else {
            return new AddEmptyDatatypeDiagram(boundaryRectangle);
        }
    }

}
