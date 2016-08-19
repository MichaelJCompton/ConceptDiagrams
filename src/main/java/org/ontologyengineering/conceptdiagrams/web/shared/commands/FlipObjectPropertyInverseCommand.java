package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.FlipObjectPropertyInverseEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;



/**
 * Author: Michael Compton<br>
 * Date: December 2015<br>
 * See license information in base directory.
 */



public class FlipObjectPropertyInverseCommand extends Command {

    private static String myType = "FlipObjectPropertyInverseCommand";

    private ConcreteArrow arrow;

    // just for serialization
    private FlipObjectPropertyInverseCommand() {
        super(myType);
    }

    public FlipObjectPropertyInverseCommand(ConcreteArrow arrow) {
        super(myType);
        this.arrow = arrow;
    }

    @Override
    public void execute() {
        arrow.swapInverse();
    }

    @Override
    public void unExecute() {
        arrow.swapInverse();
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new FlipObjectPropertyInverseEvent(arrow));
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

//    @Override
//    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
//        return null;
//    }



}
