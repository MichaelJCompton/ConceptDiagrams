package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.Collection;


public class RemoveBoundaryRectangleCommand extends Command {

    private static String myType = "RemoveBoundaryRectangleCommand";

    private AddBoundaryRectangleCommand myOpposite;

    // just for serialization
    private RemoveBoundaryRectangleCommand() {
        super(myType);
    }

    public RemoveBoundaryRectangleCommand(ConcreteBoundaryRectangle boundaryRectangle) {
        super(myType);

        myOpposite = new AddBoundaryRectangleCommand(boundaryRectangle);
    }
    @Override
    public void execute() {
        myOpposite.unExecute();
    }

    @Override
    public void unExecute() {
        myOpposite.execute();
    }

    @Override
    public Collection<Event> getEvents() {
        return myOpposite.getUnExecuteEvents();
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return myOpposite.getEvents();
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return myOpposite.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return myOpposite.leadsToValid();
    }
}
