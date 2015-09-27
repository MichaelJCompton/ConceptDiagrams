package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.gwt.event.shared.EventHandler;

/**
 * Created by Michael on 10/09/2015.
 */
public interface AddArrowEventHandler extends EventHandler {
    void onAddArrow(AddArrowEvent event);
}
