package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class FlipArrowDashedEvent  extends GwtEvent<FlipArrowDashedEventHandler> {

    private ConcreteArrow theArrow;

    public FlipArrowDashedEvent(ConcreteArrow changedArrow) {
        theArrow = changedArrow;
    }

    public static Type<FlipArrowDashedEventHandler> TYPE = new Type<FlipArrowDashedEventHandler>();

    public Type<FlipArrowDashedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(FlipArrowDashedEventHandler handler) {
        handler.onFlipArrowDashed(this);
    }

    public ConcreteArrow getArrow() {
        return theArrow;
    }
}