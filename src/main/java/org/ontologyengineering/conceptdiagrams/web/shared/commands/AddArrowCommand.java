package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddArrowEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;
import java.util.HashSet;

public class AddArrowCommand extends Command {

    Point startPoint, endPoint;
    ConcreteDiagramElement source, target;
    private ConcreteArrow theArrow;

    public AddArrowCommand(Point startPoint, Point endPoint, ConcreteDiagramElement source, ConcreteDiagramElement target) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.source = source;
        this.target = target;

        theArrow = new ConcreteArrow(startPoint, endPoint);
    }

    @Override
    public void execute() {
        theArrow.setSource(source);
        theArrow.setTarget(target);
    }

    @Override
    public void unExecute() {
        theArrow.setSource(null);
        theArrow.setTarget(null);
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddArrowEvent(theArrow));
        return result;
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        // FIXME
        return null;
    }

}
