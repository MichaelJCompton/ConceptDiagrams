package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import java.util.LinkedList;

/**
 * A singleton manager for Commands.
 *
 * Manages an undo and redo list of commands.
 */
public class CommandManager {

    private static CommandManager theInstance;

    // hhmmm should be a bit more separated and not have gwt stuff buried in here.
    // but need some sort of eventing infrastructure built in, or should the commands and the events be a bit more
    // separated, extract out the even controller to the GWT level?
    private EventBus eventBus;


    private LinkedList<Command> undoList;
    private LinkedList<Command> redoList;


    private CommandManager() {
        undoList = new LinkedList<Command>();
        redoList = new LinkedList<Command>();

        eventBus = new SimpleEventBus();
    }

    public static CommandManager get() {
        if(theInstance == null) {
            theInstance = new CommandManager();
        }
        return theInstance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void executeCommand(Command command) {
        // chop off any bits of the history that we can no longer redo after this
        redoList.clear();

        // add this command to history
        undoList.push(command);

        // execute the command
        command.execute();

        // fire off an events
        for(Event e : command.getEvents()) {
            eventBus.fireEvent(e);
        }
    }

    public void undo() {
        if(canUndo()) {
            Command undoMe = undoList.pop();
            redoList.push(undoMe);
            undoMe.unExecute();

            for(Event e : undoMe.getUnExecuteEvents()) {
                eventBus.fireEvent(e);
            }
        }
    }

    public void redo() {
        if(canRedo()) {
            Command redoMe = redoList.pop();
            undoList.push(redoMe);
            redoMe.execute();

            for(Event e : redoMe.getEvents()) {
                eventBus.fireEvent(e);
            }
        }
    }

    public boolean canUndo() {
        return undoList.size() > 0;
    }

    public boolean canRedo() {
        return redoList.size() > 0;
    }

}
