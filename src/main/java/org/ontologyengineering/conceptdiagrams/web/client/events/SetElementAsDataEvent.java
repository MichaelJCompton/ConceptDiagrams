package org.ontologyengineering.conceptdiagrams.web.client.events;


import com.google.gwt.event.shared.GwtEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class SetElementAsDataEvent extends GwtEvent<SetElementAsDataEventHandler> {

    private ConcreteDiagramElement element;

    public SetElementAsDataEvent(ConcreteDiagramElement changedElement) {
        element = changedElement;
    }

    public static Type<SetElementAsDataEventHandler> TYPE = new Type<SetElementAsDataEventHandler>();

    public Type<SetElementAsDataEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SetElementAsDataEventHandler handler) {
        handler.onSetElementAsData(this);
    }

    public ConcreteDiagramElement getResizedElement() {
        return element;
    }
}
