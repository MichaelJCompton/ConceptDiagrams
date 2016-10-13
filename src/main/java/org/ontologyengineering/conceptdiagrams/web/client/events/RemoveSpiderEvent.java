package org.ontologyengineering.conceptdiagrams.web.client.events;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;


public class RemoveSpiderEvent extends Event<RemoveSpiderEventHandler> {

    private ConcreteSpider theSpider;

    public RemoveSpiderEvent(ConcreteSpider spiderToRemove) {
        theSpider = spiderToRemove;
    }

    public static Type<RemoveSpiderEventHandler> TYPE = new Type<RemoveSpiderEventHandler>();

    public Type<RemoveSpiderEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveSpiderEventHandler handler) {
        handler.onRemoveSpider(this);
    }

    public ConcreteSpider getSpider() {
        return theSpider;
    }
}
