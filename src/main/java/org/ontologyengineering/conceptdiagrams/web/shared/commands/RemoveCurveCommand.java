package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveCurveEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.RemoveZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.AbstractCollection;
import java.util.HashSet;


public class RemoveCurveCommand extends Command {

    ConcreteCurve curve;
    AddCurveCommand myUndo;

    public RemoveCurveCommand(ConcreteCurve curveToRemove) {
        curve = curveToRemove;

        myUndo = new AddCurveCommand(this);
    }

    public RemoveCurveCommand(AddCurveCommand opposite) {
        curve = opposite.getCurve();

        myUndo = opposite;
    }

    @Override
    public void execute() {
        curve.getBoundaryRectangle().removeCurve(curve);
    }

    @Override
    public void unExecute() {
        myUndo.execute();
        //curve.getBoundaryRectangle().addCurve(curve);
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new RemoveCurveEvent(curve));

        result.add(new RemoveZoneEvent(curve.getMainZone()));
        for(ConcreteZone z : curve.getEnclosedZones()) {
            result.add(new RemoveZoneEvent(z));
        }

        return result;
    }


    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        return myUndo.getEvents();
//        HashSet<Event> result = new HashSet<Event>();
//        result.add(new AddCurveEvent(curve));
//        return result;
    }

    protected ConcreteCurve getCurve() {
        return curve;
    }
}
