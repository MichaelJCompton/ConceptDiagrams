package org.ontologyengineering.conceptdiagrams.web.client.handler;

import org.ontologyengineering.conceptdiagrams.web.shared.ClientContext;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.DiagramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */


public abstract class ConvertToOWLServiceManager {


    public ConvertToOWLServiceManager() {

    }

    public abstract void convertAllToOWL(HashSet<ArrayList<Command>> histories, HashMap<String, DiagramSet> diagrams); //, DiagramSet diagrams);

    public abstract ClientContext getContext();

}
