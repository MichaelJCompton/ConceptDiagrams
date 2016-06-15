package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import java.io.Serializable;

/**
 * A class to help generate a unique id for each diagram element as it is created.
 *
 * Want to keep it simple and readable, so things like UID are no good.
 *
 * This one just gives each a number as a string.  Abstracted from Diagram element in case I want to make it different
 * some day.
 */
public class IDGenerator implements Serializable {

    private Integer currentID;

    public IDGenerator() {
        currentID = 0;
    }

    // no need for synchronized cause it's GWT
    public String getID() {
        String result = currentID.toString();
        currentID++;
        return result;
    }

    public Integer getIDasNum() {
        return currentID++;
    }

    public String getID(String prefix) {
        return prefix + getID();
    }
}
