package org.ontologyengineering.conceptdiagrams.web.shared.commands;

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeArrowSourceTargetEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.Collection;
import java.util.HashSet;


/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

public class ChangeArrowSourceTargetCommand extends Command {


    private static String myType = "ChangeArrowSourceTargetCommand";

    private ConcreteArrow theArrow;
    private Point newStartPoint, newEndPoint;
    private Point oldStartPoint, oldEndPoint;
    private ConcreteDiagramElement newSource, newTarget;
    private ConcreteDiagramElement oldSource, oldTarget;


    // for serialization
    private ChangeArrowSourceTargetCommand() {
        super(myType);
    }


    public ChangeArrowSourceTargetCommand(ConcreteArrow arrow, Point startPoint, Point endPoint, ConcreteDiagramElement source, ConcreteDiagramElement target) {
        super(myType);

        this.theArrow = arrow;
        newStartPoint = startPoint;
        newEndPoint = endPoint;
        newSource = source;
        newTarget = target;

        oldStartPoint = arrow.getStartPoint();
        oldEndPoint = arrow.getEndPoint();
        oldSource = arrow.getSource();
        oldTarget = arrow.getTarget();
    }


    @Override
    public void execute() {
        getArrow().setNewSource(newSource, newStartPoint);
        getArrow().setNewTarget(newTarget, newEndPoint);
    }

    @Override
    public void unExecute() {
        getArrow().setNewSource(oldSource, oldStartPoint);
        getArrow().setNewTarget(oldTarget, oldEndPoint);
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeArrowSourceTargetEvent(getArrow()));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return getEvents();
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getArrow().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

    public ConcreteArrow getArrow () {
        return theArrow;
    }
}
