package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

public class AddArrowEvent extends Event<AddArrowEventHandler> {

    private ConcreteArrow theArrow;

    public AddArrowEvent(ConcreteArrow addedArrow) {
        theArrow = addedArrow;
    }

    public static Type<AddArrowEventHandler> TYPE = new Type<AddArrowEventHandler>();

    public Type<AddArrowEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddArrowEventHandler handler) {
        handler.onAddArrow(this);
    }

    public ConcreteArrow getAddedArrow() {
        return theArrow;
    }
}
