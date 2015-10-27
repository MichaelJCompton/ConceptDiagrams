package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.ResizeElementEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteRectangularElement;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.HashSet;


public class ResizeCommand extends Command {

    ConcreteRectangularElement element;
    Point newTopLeft, previousTopLeft;
    Point newBotRight, previousBotRight;

    AbstractCollection<ConcreteZone> removedZones;  // peeking inside the curves we need to know what zones are affected


    public ResizeCommand(ConcreteRectangularElement resizedElement, Point newTopLeft, Point newBotRight) {
        element = resizedElement;
        this.newTopLeft = newTopLeft;
        this.newBotRight = newBotRight;
        previousTopLeft = element.topLeft();
        previousBotRight = element.bottomRight();
        
        removedZones = new HashSet<ConcreteZone>();
    }

    private void setRemovedZones() {
        removedZones.clear();
        removedZones.addAll(element.getAllZones());
    }

    @Override
    public void execute() {
        setRemovedZones();
        element.resize(newTopLeft, newBotRight);
    }

    @Override
    public void unExecute() {
        setRemovedZones();
        element.resize(previousTopLeft, previousBotRight);
    }

    private AbstractCollection<Event> events() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ResizeElementEvent(element));

        for(ConcreteZone z : removedZones) {
            result.add(new RemoveZoneEvent(z));
        }

        AbstractCollection<ConcreteZone> bogus = element.getAllZones();
        for(ConcreteZone z : bogus) {
            result.add(new AddZoneEvent(z));
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

    @Override
    public ConcreteDiagram getDiagram() {
        return element.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
        return null;
    }
}
