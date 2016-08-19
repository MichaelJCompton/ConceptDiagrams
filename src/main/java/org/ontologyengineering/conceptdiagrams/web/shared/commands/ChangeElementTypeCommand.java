package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeTypeEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import com.google.web.bindery.event.shared.Event;

import java.util.Collection;
import java.util.HashSet;


/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
public class ChangeElementTypeCommand  extends Command {

    private static String myType = "ChangeElementTypeCommand";

    ConcreteDiagramElement element;
    boolean oldIsKnown;
    boolean oldIsObject;
    boolean newIsKnown;
    boolean newIsObject;


    // just for serialization
    public ChangeElementTypeCommand() {
        super(myType);
    }

    public ChangeElementTypeCommand(ConcreteDiagramElement elementToChange, boolean isknown, boolean isObject) {
        super(myType);

        element = elementToChange;
        oldIsKnown = element.typeIsKnown();
        oldIsObject = element.isObject();

        newIsKnown = isknown;
        newIsObject = isObject;
    }


    @Override
    public void execute() {
        setObject(newIsKnown, newIsObject);
    }

    @Override
    public void unExecute() {
        setObject(oldIsKnown, oldIsObject);
    }

    private void setObject(boolean known, boolean object) {
        if(known) {
            if(object) {
                element.setAsObject();
            } else {
                element.setAsData();
            }
        } else {
            element.setTypeUnKnown();
        }
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeTypeEvent(element));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return getEvents();
    }

    public ConcreteDiagramElement getElement() {
        return element;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getElement().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

}