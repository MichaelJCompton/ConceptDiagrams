package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;


/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class ChangeArrowSourceTargetEvent extends Event<ChangeArrowSourceTargetEventHandler> {

    private ConcreteArrow theArrow;

    public ChangeArrowSourceTargetEvent(ConcreteArrow changedArrow) {
        theArrow = changedArrow;
    }

    public static Type<ChangeArrowSourceTargetEventHandler> TYPE = new Type<ChangeArrowSourceTargetEventHandler>();

    public Type<ChangeArrowSourceTargetEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeArrowSourceTargetEventHandler handler) {
        handler.onChangeArrowSourceTarget(this);
    }

    public ConcreteArrow getChangedArrow() {
        return theArrow;
    }
}
