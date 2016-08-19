package org.ontologyengineering.conceptdiagrams.web.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public interface ChangeTypeEventHandler extends EventHandler {
    void onChangeType(ChangeTypeEvent event);
}
