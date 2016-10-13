package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class SetElementAsObjectEvent extends GwtEvent<SetElementAsObjectEventHandler> {

    private ConcreteDiagramElement element;

    public SetElementAsObjectEvent(ConcreteDiagramElement changedElement) {
        element = changedElement;
    }

    public static Type<SetElementAsObjectEventHandler> TYPE = new Type<SetElementAsObjectEventHandler>();

    public Type<SetElementAsObjectEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SetElementAsObjectEventHandler handler) {
        handler.onSetElementAsObject(this);
    }

    public ConcreteDiagramElement getResizedElement() {
        return element;
    }
}
