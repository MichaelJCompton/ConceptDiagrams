package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddCurveEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveCurveEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;


public class RemoveCurveCommand extends Command {

    private static String myType = "RemoveCurveCommand";

    ConcreteCurve curve;

    private RemoveCurveCommand() {
        super(myType);
    }

    public RemoveCurveCommand(ConcreteCurve curveToRemove) {
        super(myType);

        curve = curveToRemove;
    }

    // Can do this way around as an opposite of add, but doesn't work the otherway around.
    // Removing a curve means also removing its shaded zones and undoing the removal of a curve means reinstating
    // those shaded zones, so just an add curve isn't enough as the undo of delete.
    //
    // Instead remove will keep the curve and all its zones, just disassociated from the rest of the diagram.
    // If the remove happens to be undone, we can just put those zones back where they belong.
    public RemoveCurveCommand(AddCurveCommand opposite) {
        curve = opposite.getCurve();
    }

    @Override
    public void execute() {
        curve.getBoundaryRectangle().removeCurve(curve);
    }

    @Override
    public void unExecute() {
        curve.getBoundaryRectangle().unremoveCurve(curve);
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveCurveEvent(curve));

        result.add(new RemoveZoneEvent(curve.getMainZone()));
        for(ConcreteZone z : curve.getEnclosedZones()) {
            result.add(new RemoveZoneEvent(z));
        }

        return result;
    }


    @Override
    public Collection<Event> getUnExecuteEvents() {
        return AddCurveCommand.addCurveEvents(curve);
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return curve.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

//    @Override
//    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
//        return null;
//    }

    protected ConcreteCurve getCurve() {
        return curve;
    }
}
