package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;

;

public class RemoveBoundaryRectangleEvent extends Event<RemoveBoundaryRectangleEventHandler> {

    private ConcreteBoundaryRectangle theRectangle;

    public RemoveBoundaryRectangleEvent(ConcreteBoundaryRectangle removedRectangle) {
        theRectangle = removedRectangle;
    }

    public static Type<RemoveBoundaryRectangleEventHandler> TYPE = new Type<RemoveBoundaryRectangleEventHandler>();

    public Type<RemoveBoundaryRectangleEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveBoundaryRectangleEventHandler handler) {
        handler.onRemoveBoundaryRectangle(this);
    }

    public ConcreteBoundaryRectangle getBoundaryRectangle() {
        return theRectangle;
    }
}
