package org.ontologyengineering.conceptdiagrams.web.client.events;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


public class RemoveZoneEvent extends Event<RemoveZoneEventHandler> {

    private ConcreteZone theZone;

    public RemoveZoneEvent(ConcreteZone removedZone) {
        theZone = removedZone;
    }

    public static Type<RemoveZoneEventHandler> TYPE = new Type<RemoveZoneEventHandler>();

    public Type<RemoveZoneEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveZoneEventHandler handler) {
        handler.onRemoveZone(this);
    }

    public ConcreteZone getRemovedZone() {
        return theZone;
    }
}
