package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeLabelEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.AbstractCollection;
import java.util.HashSet;


public class ChangeLabelCommand extends Command {

    ConcreteDiagramElement element;
    String newLabel, oldLabel;


    public ChangeLabelCommand(ConcreteDiagramElement elementToLabel, String label) {
        element = elementToLabel;
        oldLabel = element.labelText();
        newLabel = label;
    }


    @Override
    public void execute() {
        element.setLabel(newLabel);
    }

    @Override
    public void unExecute() {
        element.setLabel(oldLabel);
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeLabelEvent(element));
        return result;
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        return getEvents();
    }
}
