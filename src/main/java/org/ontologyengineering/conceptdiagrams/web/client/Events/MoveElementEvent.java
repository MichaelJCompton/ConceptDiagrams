package org.ontologyengineering.conceptdiagrams.web.client.events;



/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class MoveElementEvent extends Event<MoveElementEventHandler> {

    private ConcreteDiagramElement element;

    public MoveElementEvent(ConcreteDiagramElement movedElement) {
        element = movedElement;
    }

    public static Type<MoveElementEventHandler> TYPE = new Type<MoveElementEventHandler>();

    public Type<MoveElementEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(MoveElementEventHandler handler) {
        handler.onMoveElement(this);
    }

    public ConcreteDiagramElement getMovedElement() {
        return element;
    }

}
