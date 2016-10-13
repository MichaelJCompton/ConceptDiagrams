package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Author: Michael Compton<br>
 * Date: Aug 2016<br>
 * See license information in base directory.
 */
public class ChangeTypeEvent extends Event<ChangeTypeEventHandler> {

    private ConcreteDiagramElement changedElement;

    public ChangeTypeEvent(ConcreteDiagramElement changedElement) {
        this.changedElement = changedElement;
    }

    public static Type<ChangeTypeEventHandler> TYPE = new Type<ChangeTypeEventHandler>();

    public Type<ChangeTypeEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeTypeEventHandler handler) {
        handler.onChangeType(this);
    }

    public ConcreteDiagramElement getChangedElement() {
        return changedElement;
    }
}