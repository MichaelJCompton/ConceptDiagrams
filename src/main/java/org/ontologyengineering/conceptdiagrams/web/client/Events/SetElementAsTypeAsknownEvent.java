package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class SetElementAsTypeAsknownEvent extends GwtEvent<SetElementAsTypeAsknownEventHandler> {

    private ConcreteDiagramElement element;

    public SetElementAsTypeAsknownEvent(ConcreteDiagramElement changedElement) {
        element = changedElement;
    }

    public static Type<SetElementAsTypeAsknownEventHandler> TYPE = new Type<SetElementAsTypeAsknownEventHandler>();

    public Type<SetElementAsTypeAsknownEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SetElementAsTypeAsknownEventHandler handler) {
        handler.onSetElementAsUnknown(this);
    }

    public ConcreteDiagramElement getResizedElement() {
        return element;
    }
}
