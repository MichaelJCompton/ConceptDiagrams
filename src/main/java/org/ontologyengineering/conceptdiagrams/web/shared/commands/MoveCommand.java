package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.MoveElementEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteRectangularElement;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;
import java.util.HashSet;


public class MoveCommand extends Command {

    Point topLeft, oldTopLeft;
    ConcreteDiagramElement element;

    // only if it's a rectangular element
    AbstractCollection<ConcreteZone> removedZones;  // peeking inside the curves we need to know what zones are affected

    public MoveCommand(ConcreteDiagramElement elementToMove, Point newTopLeft) {
        topLeft = newTopLeft;
        element = elementToMove;
        oldTopLeft = element.topLeft();

        removedZones = new HashSet<ConcreteZone>();
    }


    private void setRemovedZones() {
        removedZones.clear();
        if (element.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE ||
                element.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE) {
            removedZones.addAll(((ConcreteRectangularElement) element).getAllZones());
        }
    }


    @Override
    public void execute() {
        setRemovedZones();
        element.move(topLeft);
    }

    @Override
    public void unExecute() {
        setRemovedZones();
        element.move(oldTopLeft);
    }


    private AbstractCollection<Event> events() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new MoveElementEvent(element));

        for (ConcreteZone z : removedZones) {
            result.add(new RemoveZoneEvent(z));
        }

        if (element.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETEBOUNDARYRECTANGE ||
                element.getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE) {
            for (ConcreteZone z : ((ConcreteRectangularElement) element).getAllZones()) {
                result.add(new AddZoneEvent(z));
            }
        }

        return result;
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        return events();
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        return events();
    }
}
