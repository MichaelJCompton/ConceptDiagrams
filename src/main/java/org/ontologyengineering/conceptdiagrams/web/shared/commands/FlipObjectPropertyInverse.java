package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.HashSet;



/**
 * Author: Michael Compton<br>
 * Date: December 2015<br>
 * See license information in base directory.
 */



public class FlipObjectPropertyInverse extends Command {

    private ConcreteArrow arrow;

    public FlipObjectPropertyInverse(ConcreteArrow arrow) {
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
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        // I think for the moment there aren't any events to catch for this
        return result;
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        return result;
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

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
        return null;
    }



}
