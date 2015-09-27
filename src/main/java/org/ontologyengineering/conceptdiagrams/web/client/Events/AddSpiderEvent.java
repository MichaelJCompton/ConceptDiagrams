package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteSpider;


public class AddSpiderEvent extends Event<AddSpiderEventHandler> {

    private ConcreteSpider theSpider;

    public AddSpiderEvent(ConcreteSpider spiderToAdd) {
        theSpider = spiderToAdd;
    }

    public static Type<AddSpiderEventHandler> TYPE = new Type<AddSpiderEventHandler>();

    public Type<AddSpiderEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddSpiderEventHandler handler) {
        handler.onAddSpider(this);
    }

    public ConcreteSpider getSpider() {
        return theSpider;
    }
}
