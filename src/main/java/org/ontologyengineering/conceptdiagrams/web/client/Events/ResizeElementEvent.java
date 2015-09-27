package org.ontologyengineering.conceptdiagrams.web.client.events;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.gwt.event.shared.GwtEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;


public class ResizeElementEvent extends GwtEvent<ResizeElementEventHandler> {

    private ConcreteDiagramElement element;

    public ResizeElementEvent(ConcreteDiagramElement resizedElement) {
        element = resizedElement;
    }

    public static Type<ResizeElementEventHandler> TYPE = new Type<ResizeElementEventHandler>();

    public Type<ResizeElementEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ResizeElementEventHandler handler) {
        handler.onResizeElement(this);
    }

    public ConcreteDiagramElement getResizedElement() {
        return element;
    }
}
