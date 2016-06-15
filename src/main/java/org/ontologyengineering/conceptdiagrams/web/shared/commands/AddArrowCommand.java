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
import org.ontologyengineering.conceptdiagrams.web.shared.diagrams.DiagramSet;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;

public class AddArrowCommand extends Command {

    private static String myType = "AddArrowCommand";


    Point startPoint, endPoint;
    ConcreteDiagramElement source, target;
    private ConcreteArrow theArrow;
    DiagramSet diagrams;    // should this be embedded in the diagrams??


    // just for serialization
    private AddArrowCommand() {
        super(myType);
    }

    public AddArrowCommand(Point startPoint, Point endPoint, ConcreteDiagramElement source, ConcreteDiagramElement target, DiagramSet diagrams) {
        super(myType);

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.source = source;
        this.target = target;

        this.diagrams = diagrams;
    }

    @Override
    public void execute() {
        // FIXME also need to change the diagram set here??
        if(theArrow == null) {
            theArrow = new ConcreteArrow(startPoint, endPoint, source, target);
        } else {
            theArrow.setSource(source);
            theArrow.setTarget(target);
        }

        theArrow.setBoundaryRectangle(source.getBoundaryRectangle());
        theArrow.getBoundaryRectangle().addArrow(theArrow);

        // if diagrams knew their DiagramSet then this would all happen internally in the addArrow
        // for a boundary rectangle and just get bubbled up for ones that require a merge.
        if(source.getDiagram() != target.getDiagram()) {
            diagrams.addArrowBetween(theArrow);
        }
    }

    @Override
    public void unExecute() {
        // FIXME may need to break up diagrams because of arrows here!
        if(theArrow != null) {
            theArrow.setSource(null);
            theArrow.setTarget(null);
        }
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
        return source.getDiagram();
    }

    @Override
    public boolean leadsToValid() {   // needs to be a labelled arrow
        return getArrow().hasLabel();  // rest should have been handled at the whole diagram level
    }

//    @Override
//    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
//        if(getArrow().isObjectProperty()) {
//            if(getArrow().singleRectangle()) {
//                return new TransformAClassAndObjectPropertyDiagram(new AddLabelledArrow(getArrow()));
//            } else {
//                return new AddAnObjectPropertyLabelledArrow(getArrow());
//            }
//        } else {
//            if(getArrow().singleRectangle()) {
//                return new TransformADatatypeDiagram(new AddLabelledArrow(getArrow()));
//            } else {
//                return new AddADataPropertyLabelledArrow(getArrow());
//            }
//        }
//    }

    public ConcreteArrow getArrow () {
        return theArrow;
    }
}
