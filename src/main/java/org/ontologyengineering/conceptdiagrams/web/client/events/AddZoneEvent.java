package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


public class AddZoneEvent extends Event<AddZoneEventHandler> {

    private ConcreteZone theZone;

    public AddZoneEvent(ConcreteZone addedZone) {
        theZone = addedZone;
    }

    public static Type<AddZoneEventHandler> TYPE = new Type<AddZoneEventHandler>();

    public Type<AddZoneEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddZoneEventHandler handler) {
        handler.onAddZone(this);
    }

    public ConcreteZone getAddedZone() {
        return theZone;
    }
}
