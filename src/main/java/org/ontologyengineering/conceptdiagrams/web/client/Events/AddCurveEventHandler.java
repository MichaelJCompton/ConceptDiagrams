package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.gwt.event.shared.EventHandler;

public interface AddCurveEventHandler extends EventHandler {
    void onAddCurve(AddCurveEvent event);
}
