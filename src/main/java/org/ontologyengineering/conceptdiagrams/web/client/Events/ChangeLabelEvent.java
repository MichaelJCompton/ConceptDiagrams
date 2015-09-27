package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class ChangeLabelEvent extends Event<ChangeLabelEventHandler> {

    ConcreteDiagramElement element;

    public ChangeLabelEvent(ConcreteDiagramElement elementChanged) {
        element = elementChanged;
    }

    public static Type<ChangeLabelEventHandler> TYPE = new Type<ChangeLabelEventHandler>();

    public Type<ChangeLabelEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeLabelEventHandler handler) {
        handler.onChangeLabel(this);
    }

    public ConcreteDiagramElement changedElement() {
        return element;
    }
}
