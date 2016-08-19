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
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteStarRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.Collection;
import java.util.HashSet;


public class AddStarRectangleCommand extends Command {

    private static String myType = "AddStarRectangleCommand";

    private ConcreteStarRectangle boundaryRectangle;

    // just an interface over the normal add.  Not a sublclass cause the would require mucking around with the
    // constructor interface for the no arg etc.
    //private AddBoundaryRectangleCommand myAddCommand;

    // just for serialization
    private AddStarRectangleCommand() {
        super(myType);
    }

    public AddStarRectangleCommand(Point topLeft, Point bottomRight, DiagramSet diagrams) {
        super(myType);

        boundaryRectangle = new ConcreteStarRectangle(topLeft, bottomRight);
        ConcreteDiagram newDiagram = new ConcreteDiagram(boundaryRectangle, diagrams);

        //myAddCommand = new AddBoundaryRectangleCommand(boundaryRectangle);
    }


    public ConcreteStarRectangle getBoundaryRectangle() {
        //return (ConcreteStarRectangle) myAddCommand.getBoundaryRectangle();
        return boundaryRectangle;
    }

    @Override
    public void execute() {
        getBoundaryRectangle().getDiagramSet().addDiagram(getDiagram());
    }

    @Override
    public void unExecute() {
        getBoundaryRectangle().getDiagramSet().removeDiagram(getDiagram());
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddStarRectangleEvent(getBoundaryRectangle()));
        return result;
    }


    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveBoundaryRectangleEvent(getBoundaryRectangle()));
        return result;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getBoundaryRectangle().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;  // nothing to test, just valid on its own
    }

//    @Override
//    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
//        return new AddEmptyClassAndObjectPropertyDiagram(boundaryRectangle);
//    }

}
