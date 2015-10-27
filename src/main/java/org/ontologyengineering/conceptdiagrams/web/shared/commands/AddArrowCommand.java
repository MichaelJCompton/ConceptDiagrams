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
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.*;

import java.util.AbstractCollection;
import java.util.AbstractList;
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
    }

    @Override
    public void execute() {
        if(theArrow == null) {
            theArrow = new ConcreteArrow(startPoint, endPoint, source, target);
        } else {
            theArrow.setSource(source);
            theArrow.setTarget(target);
        }
    }

    @Override
    public void unExecute() {
        if(theArrow != null) {
            theArrow.setSource(null);
            theArrow.setTarget(null);
        }
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddArrowEvent(theArrow));
        return result;
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveArrowEvent(theArrow));
        return result;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return source.getDiagram();
    }

    @Override
    public boolean leadsToValid() {   // needs to be a labelled arrow
        return getArrow().hasLabel();  // rest should have been handled at the whole diagram level
    }

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
        if(getArrow().isObjectProperty()) {
            if(getArrow().singleRectangle()) {
                return new TransformAClassAndObjectPropertyDiagram(new AddLabelledArrow(getArrow()));
            } else {
                return new AddAnObjectPropertyLabelledArrow(getArrow());
            }
        } else {
            if(getArrow().singleRectangle()) {
                return new TransformADatatypeDiagram(new AddLabelledArrow(getArrow()));
            } else {
                return new AddADataPropertyLabelledArrow(getArrow());
            }
        }
    }

    protected ConcreteArrow getArrow () {
        return theArrow;
    }
}
