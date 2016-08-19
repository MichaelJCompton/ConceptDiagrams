package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddSpiderEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveSpiderEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;

import java.util.Collection;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

public class RemoveSpiderCommand extends Command {

    private static String myType = "RemoveSpiderCommand";

    private ConcreteSpider theSpider;


    // just for serialization
    private RemoveSpiderCommand() {
        super(myType);
    }


    public RemoveSpiderCommand(ConcreteSpider theSpider) {
        super(myType);

        this.theSpider = theSpider;
    }


    @Override
    public void execute() {
        theSpider.getBoundaryRectangle().removeSpider(theSpider);

        // eventually also need to
        // remove from = and -
    }

    @Override
    public void unExecute() {
        theSpider.getBoundaryRectangle().addSpider(theSpider);

        // add back to = and -
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveSpiderEvent(theSpider));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddSpiderEvent(theSpider));
        return result;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return theSpider.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }
}
