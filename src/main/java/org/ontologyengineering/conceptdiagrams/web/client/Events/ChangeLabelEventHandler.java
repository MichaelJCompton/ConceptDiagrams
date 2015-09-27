package org.ontologyengineering.conceptdiagrams.web.client.events;


/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


import com.google.gwt.event.shared.EventHandler;


public interface ChangeLabelEventHandler extends EventHandler {
    void onChangeLabel(ChangeLabelEvent event);
}
