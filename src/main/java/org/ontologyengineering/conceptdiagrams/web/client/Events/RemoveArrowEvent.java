package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

/**
 * Created by Michael on 4/10/2015.
 */
public class RemoveArrowEvent extends Event<RemoveArrowEventHandler> {

    private ConcreteArrow arrow;

    public RemoveArrowEvent(ConcreteArrow removedArrow) {
        arrow = removedArrow;
    }

    public static Type<RemoveArrowEventHandler> TYPE = new Type<RemoveArrowEventHandler>();

    public Type<RemoveArrowEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveArrowEventHandler handler) {
        handler.onRemoveArrow(this);
    }

    public ConcreteArrow getRemovedArrow() {
        return arrow;
    }
}
