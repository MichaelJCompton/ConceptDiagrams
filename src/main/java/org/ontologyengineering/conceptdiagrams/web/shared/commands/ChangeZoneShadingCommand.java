package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeZoneShadingEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.AbstractCollection;
import java.util.HashSet;


public class ChangeZoneShadingCommand extends Command {

    private ConcreteZone zoneChanged;

    public ChangeZoneShadingCommand(ConcreteZone zoneToChange) {
        zoneChanged = zoneToChange;
    }

    @Override
    public void execute() {
        zoneChanged.swapShading();
    }

    @Override
    public void unExecute() {
        zoneChanged.swapShading();
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeZoneShadingEvent(zoneChanged));
        return result;
    }


    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeZoneShadingEvent(zoneChanged));
        return result;
    }
}
