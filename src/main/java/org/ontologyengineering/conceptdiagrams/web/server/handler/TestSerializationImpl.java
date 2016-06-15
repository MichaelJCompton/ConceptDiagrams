package org.ontologyengineering.conceptdiagrams.web.server.handler;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.ontologyengineering.conceptdiagrams.web.client.handler.TestSerialization;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;

/**
 * Created by Michael on 9/03/2016.
 */
public class TestSerializationImpl  extends RemoteServiceServlet implements TestSerialization {

    public TestSerializationImpl() {

    }

    public void testSerialization(Curve rect) {
        System.out.println("***** here we are");
    }


}
