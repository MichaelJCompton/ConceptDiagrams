package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;

/**
 * Created by Michael on 9/03/2016.
 */
public interface TestSerializationAsync {

    void testSerialization(Curve rect, AsyncCallback<Void> callback);

}
