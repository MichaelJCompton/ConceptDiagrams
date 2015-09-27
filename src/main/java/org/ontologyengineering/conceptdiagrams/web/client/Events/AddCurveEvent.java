package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


public class AddCurveEvent extends Event<AddCurveEventHandler> {

    private ConcreteCurve theCurve;

    public AddCurveEvent(ConcreteCurve addedCurve) {
        theCurve = addedCurve;
    }

    public static Type<AddCurveEventHandler> TYPE = new Type<AddCurveEventHandler>();

    public Type<AddCurveEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddCurveEventHandler handler) {
        handler.onAddCurve(this);
    }

    public ConcreteCurve getCurve() {
        return theCurve;
    }
}
