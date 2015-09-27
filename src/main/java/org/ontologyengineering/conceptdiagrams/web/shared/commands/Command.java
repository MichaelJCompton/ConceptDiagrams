package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;

import java.util.AbstractCollection;

/**
 * Commands that get executed on the concrete/abstract syntax - these may change the onscreen representation.
 *
 *
 */

public abstract class Command {

    public abstract void execute();
    public abstract void unExecute();

    public abstract AbstractCollection<Event> getEvents();
    public abstract AbstractCollection<Event> getUnExecuteEvents();

}
