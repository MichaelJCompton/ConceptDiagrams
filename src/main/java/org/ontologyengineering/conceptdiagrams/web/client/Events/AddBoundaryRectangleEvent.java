package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;

public class AddBoundaryRectangleEvent extends Event<AddBoundaryRectangleEventHandler> {

    private ConcreteBoundaryRectangle theRectangle;

    public AddBoundaryRectangleEvent(ConcreteBoundaryRectangle addedRectangle) {
        theRectangle = addedRectangle;
    }

    public static Type<AddBoundaryRectangleEventHandler> TYPE = new Type<AddBoundaryRectangleEventHandler>();

    public Type<AddBoundaryRectangleEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddBoundaryRectangleEventHandler handler) {
        handler.onAddBoundaryRectangle(this);
    }

    public ConcreteBoundaryRectangle getBoundaryRectangle() {
        return theRectangle;
    }
}
