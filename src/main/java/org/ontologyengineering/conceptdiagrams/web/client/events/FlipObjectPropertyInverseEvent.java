package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */


public class FlipObjectPropertyInverseEvent extends Event<FlipObjectPropertyInverseEventHandler> {


    private ConcreteArrow theArrow;

    public FlipObjectPropertyInverseEvent(ConcreteArrow arrowChanged) {
        theArrow = arrowChanged;
    }

    public static Type<FlipObjectPropertyInverseEventHandler> TYPE = new Type<FlipObjectPropertyInverseEventHandler>();


    public ConcreteArrow getArrowChanged() {
        return theArrow;
    }



    @Override
    public Type<FlipObjectPropertyInverseEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FlipObjectPropertyInverseEventHandler handler) {
        handler.onFlipObjectPropertyInverse(this);
    }
}
