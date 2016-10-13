package org.ontologyengineering.conceptdiagrams.web.client.events;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


public class RemoveCurveEvent extends Event<RemoveCurveEventHandler> {

    private ConcreteCurve curve;

    public RemoveCurveEvent(ConcreteCurve removedCurve) {
        curve = removedCurve;
    }

    public static Type<RemoveCurveEventHandler> TYPE = new Type<RemoveCurveEventHandler>();

    public Type<RemoveCurveEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveCurveEventHandler handler) {
        handler.onRemoveCurve(this);
    }


    public ConcreteCurve getCurve() {
        return curve;
    }
}
