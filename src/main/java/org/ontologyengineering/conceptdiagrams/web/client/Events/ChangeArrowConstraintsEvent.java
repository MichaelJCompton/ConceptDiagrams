package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class ChangeArrowConstraintsEvent extends Event<ChangeArrowConstraintsEventHandler> {

    private ConcreteArrow theArrow;

    public ChangeArrowConstraintsEvent(ConcreteArrow changedArrow) {
        theArrow = changedArrow;
    }

    public static Type<ChangeArrowConstraintsEventHandler> TYPE = new Type<ChangeArrowConstraintsEventHandler>();

    public Type<ChangeArrowConstraintsEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeArrowConstraintsEventHandler handler) {
        handler.onChangeCardinality(this);
    }

    public ConcreteArrow getChangedArrow() {
        return theArrow;
    }
}
