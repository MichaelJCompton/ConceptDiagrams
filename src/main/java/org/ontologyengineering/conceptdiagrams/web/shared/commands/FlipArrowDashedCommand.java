package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.FlipArrowDashedEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.Collection;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class FlipArrowDashedCommand extends Command {

    private static String myType = "FlipArrowDashedCommand";

    private ConcreteArrow arrow;

    // just for serialization
    private FlipArrowDashedCommand() {
        super(myType);
    }

    public FlipArrowDashedCommand(ConcreteArrow arrow) {
        super(myType);
        this.arrow = arrow;
    }

    @Override
    public void execute() {
        arrow.flipDashing();
    }

    @Override
    public void unExecute() {
        arrow.flipDashing();
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new FlipArrowDashedEvent(arrow));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return getEvents();
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return arrow.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        // should just be valid as long as the types are right, but that's elsewhere
        return true;
    }

}
