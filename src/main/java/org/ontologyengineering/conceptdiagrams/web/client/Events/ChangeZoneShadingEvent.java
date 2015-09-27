package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;


public class ChangeZoneShadingEvent extends Event<ChangeZoneShadingEventHandler> {

    private ConcreteZone theZone;

    public ChangeZoneShadingEvent(ConcreteZone zoneChanged) {
        theZone = zoneChanged;
    }

    public static Type<ChangeZoneShadingEventHandler> TYPE = new Type<ChangeZoneShadingEventHandler>();

    public Type<ChangeZoneShadingEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeZoneShadingEventHandler handler) {
        handler.onChangeZoneShading(this);
    }

    public ConcreteZone getZoneChanged() {
        return theZone;
    }
}
