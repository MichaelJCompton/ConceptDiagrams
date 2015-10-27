package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;

import java.util.AbstractCollection;
import java.util.AbstractList;

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

    // Note this may change.  It's a reference to the diagram that this command acted on, but as the diagram changes
    // it may be for example that the diagram and another get joined by an arrow and so become one diagram.
    public abstract ConcreteDiagram getDiagram();

    public abstract boolean leadsToValid();  // does this command eventually lead to a valid state
    // myPlace should be a valid index and the index of 'this'.
    // Need to be able to check through the rest of the command list to make sure that this command is valid and if it needs to
    // be made into a transformation (i.e. if there are many re-names, just take the last)
    public abstract LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace);

}
