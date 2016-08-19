package org.ontologyengineering.conceptdiagrams.web.shared.commands;


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.SetElementAsDataEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.SetElementAsObjectEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.SetElementAsTypeAsknownEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

import java.util.Collection;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

public class SetElementAsDataCommand extends Command {

    private static String myType = "SetElementAsDataCommand";

    ConcreteDiagramElement element;
    Boolean typeWasKnown;
    Boolean wasObject;


    private SetElementAsDataCommand() {
        super(myType);
    }


    public SetElementAsDataCommand(ConcreteDiagramElement element) {
        super(myType);

        this.element = element;
        typeWasKnown = element.typeIsKnown();
        if(typeWasKnown) {
            wasObject = element.isObject();
        }
    }


    @Override
    public void execute() {
        element.setAsData();
    }

    @Override
    public void unExecute() {
        if(typeWasKnown && wasObject) {
            element.setAsObject();
        } else if (!typeWasKnown) {
            element.setTypeUnKnown();
        }
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new SetElementAsDataEvent(element));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        if(typeWasKnown && wasObject) {
            result.add(new SetElementAsObjectEvent(element));
        } else if (!typeWasKnown) {
            result.add(new SetElementAsTypeAsknownEvent(element));
        }
        return result;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return element.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }
}
