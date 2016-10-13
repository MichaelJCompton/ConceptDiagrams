package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteStarRectangle;


public class AddStarRectangleEvent extends Event<AddStarRectangleEventHandler> {

    private ConcreteStarRectangle rectangle;

    public AddStarRectangleEvent(ConcreteStarRectangle addedRectangle) {
        rectangle = addedRectangle;
    }

    public static Type<AddStarRectangleEventHandler> TYPE = new Type<AddStarRectangleEventHandler>();

    public Type<AddStarRectangleEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddStarRectangleEventHandler handler) {
        handler.onAddStarRectangle(this);
    }

    public ConcreteStarRectangle getAddedRectangle() {
        return rectangle;
    }
}
