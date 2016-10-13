package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A manager for Commands - one for each drawing canvas.
 *
 * Manages an undo and redo list of commands.
 */
public class CommandManager {

    private LinkedList<Command> undoList;
    private LinkedList<Command> redoList;

    private static final EventBus eventBus = new SimpleEventBus();

    public CommandManager() {
        undoList = new LinkedList<Command>();
        redoList = new LinkedList<Command>();
    }

    // make the command manager from a given set of commands
    public CommandManager(List<Command> commands) {
        undoList = new LinkedList<Command>(commands);
        redoList = new LinkedList<Command>();
    }


    public void clearAll() {
        undoList.clear();
        redoList.clear();
    }

    public static EventBus getEventBus() {
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
            getEventBus().fireEvent(e);
        }
    }

    public void undo() {
        if(canUndo()) {
            Command undoMe = undoList.pop();
            redoList.push(undoMe);
            undoMe.unExecute();

            for(Event e : undoMe.getUnExecuteEvents()) {
                getEventBus().fireEvent(e);
            }
        }
    }

    public void redo() {
        if(canRedo()) {
            Command redoMe = redoList.pop();
            undoList.push(redoMe);
            redoMe.execute();

            for(Event e : redoMe.getEvents()) {
                getEventBus().fireEvent(e);
            }
        }
    }

    public boolean canUndo() {
        return undoList.size() > 0;
    }


    public ArrayList<Command> getUndoList() {
        ArrayList<Command> result = new ArrayList<Command>();
        for(Command c : undoList) {
            result.add(c);
        }
        return result;
    }

    public boolean canRedo() {
        return redoList.size() > 0;
    }


    // re-fire all the events for commands so far (in order)
    public void reFireAll() {

        for(Command command : undoList) {

            for(Event e : command.getEvents()) {
                getEventBus().fireEvent(e);
            }

        }
    }
}
