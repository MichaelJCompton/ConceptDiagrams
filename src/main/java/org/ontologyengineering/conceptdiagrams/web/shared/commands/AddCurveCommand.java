package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddCurveEvent;
import org.ontologyengineering.conceptdiagrams.web.client.events.AddZoneEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteBoundaryRectangle;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry.Point;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;


public class AddCurveCommand extends Command {

    private static String myType = "AddCurveCommand";

    private Point topLeft, bottomRight;

    private ConcreteBoundaryRectangle boundaryRectangle;
    private ConcreteCurve curve;
    private RemoveCurveCommand myUndo;

    // just for serialization
    private AddCurveCommand() {
        super(myType);
    }

    // The boundary rectangle implies the diagram set
    public AddCurveCommand(Point topLeft, Point bottomRight, ConcreteBoundaryRectangle boundaryRectangle) {
        super(myType);

        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.boundaryRectangle = boundaryRectangle;

        curve = new ConcreteCurve(topLeft, bottomRight);

        myUndo = new RemoveCurveCommand(this);
    }

    public ConcreteBoundaryRectangle getBoundaryRectangle() {
        return boundaryRectangle;
    }

    @Override
    public void execute() {
        curve.setBoundaryRectangle(boundaryRectangle);
    }

    @Override
    public void unExecute() {
        myUndo.execute();
    }

    public Collection<Event> getEvents() {
        return addCurveEvents(curve);
    }

    public static Collection<Event> addCurveEvents(ConcreteCurve curve) {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new AddCurveEvent(curve));

        // every zone in a new curve is new, so we have a bunch of add zone events too
        result.add(new AddZoneEvent(curve.getMainZone()));
        for(ConcreteZone z : curve.getEnclosedZones()) {
            result.add(new AddZoneEvent(z));
        }

        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return myUndo.getEvents();
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return boundaryRectangle.getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;  // not sure what could be invalid here?
    }


    public ConcreteCurve getCurve () {
        return curve;
    }
}
