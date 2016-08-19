package org.ontologyengineering.conceptdiagrams.web.shared.commands;


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.Collection;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class RemoveArrowCommand extends Command {

    private static String myType = "RemoveArrowCommand";

    private AddArrowCommand myOpposite;

    // just for serialization
    private RemoveArrowCommand() {
        super(myType);
    }


    public RemoveArrowCommand(ConcreteArrow theArrow) {
        super(myType);
        myOpposite = new AddArrowCommand(theArrow);
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
