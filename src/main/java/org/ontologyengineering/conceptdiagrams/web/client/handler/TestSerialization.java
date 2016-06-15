package org.ontologyengineering.conceptdiagrams.web.client.handler;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;

/**
 * Created by Michael on 9/03/2016.
 */
@RemoteServiceRelativePath("TestSerialization")
public interface TestSerialization extends RemoteService {

    void testSerialization(Curve rect);

}
