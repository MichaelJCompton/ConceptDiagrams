package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddArrowEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveArrowEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.Collection;
import java.util.HashSet;

public class AddArrowCommand extends Command {

    private static String myType = "AddArrowCommand";


    private Point startPoint, endPoint;
    private ConcreteDiagramElement source, target;
    private ConcreteArrow theArrow;


    // just for serialization
    private AddArrowCommand() {
        super(myType);
    }

    // expecting source and target to be in the same diagram set
    public AddArrowCommand(Point startPoint, Point endPoint, ConcreteDiagramElement source, ConcreteDiagramElement target) {
        super(myType);

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.source = source;
        this.target = target;
    }

    // really for use as an inverse for remove arrow
    protected AddArrowCommand(ConcreteArrow arrow) {
        theArrow = arrow;
    }


    @Override
    public void execute() {
        if(theArrow == null) {
            theArrow = new ConcreteArrow(startPoint, endPoint, source, target);
            theArrow.setBoundaryRectangle(source.getBoundaryRectangle());
        } else {
            theArrow.linkSource(theArrow.getSource());
            theArrow.linkTarget(theArrow.getTarget());
        }
        theArrow.getBoundaryRectangle().addArrow(theArrow);
    }

    @Override
    public void unExecute() {
        // this does the unlinking of source and target
        theArrow.getBoundaryRectangle().removeArrow(theArrow);
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddArrowEvent(theArrow));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveArrowEvent(theArrow));
        return result;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return theArrow.getDiagram();
    }

    @Override
    public boolean leadsToValid() {   // needs to be a labelled arrow
        return getArrow().hasLabel();  // rest should have been handled at the whole diagram level
    }


    public ConcreteArrow getArrow () {
        return theArrow;
    }
}
