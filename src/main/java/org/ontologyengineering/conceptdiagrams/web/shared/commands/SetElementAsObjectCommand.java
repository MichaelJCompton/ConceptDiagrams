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
public class SetElementAsObjectCommand extends Command {

    private static String myType = "SetElementAsObjectCommand";

    ConcreteDiagramElement element;
    Boolean typeWasKnown;
    Boolean wasData;


    private SetElementAsObjectCommand() {
        super(myType);
    }


    public SetElementAsObjectCommand(ConcreteDiagramElement element) {
        super(myType);

        this.element = element;
        typeWasKnown = element.typeIsKnown();
        if(typeWasKnown) {
            wasData = element.isObject();
        }
    }


    @Override
    public void execute() {
        element.setAsObject();
    }

    @Override
    public void unExecute() {
        if(typeWasKnown && wasData) {
            element.setAsData();
        } else if (!typeWasKnown) {
            element.setTypeUnKnown();
        }
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new SetElementAsObjectEvent(element));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        if(typeWasKnown && wasData) {
            result.add(new SetElementAsDataEvent(element));
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
