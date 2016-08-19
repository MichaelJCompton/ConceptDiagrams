package org.ontologyengineering.conceptdiagrams.web.shared.commands;


/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

// Could be a number of commands, but wrapped up here so less classes

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeArrowConstraintsEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.util.Collection;
import java.util.HashSet;


public class ChangeArrowConstraintsCommand extends Command {

    private static String myType = "ChangeCardinalityCommand";

    private ConcreteArrow theArrow;
    private ConcreteArrow.CardinalityConstraint oldConstraint;
    private ConcreteArrow.CardinalityConstraint newConstraint;
    private Integer oldCardinality;
    private Integer newCardinality;
    private boolean oldDashed;
    private boolean newDashed;
    private boolean oldInverse;
    private boolean newInverse;


    // for serialization
    private ChangeArrowConstraintsCommand() {
        super(myType);
    }


    public ChangeArrowConstraintsCommand(ConcreteArrow arrow, ConcreteArrow.CardinalityConstraint newConstraint, Integer newCardinality,
                                         boolean dashed, boolean inverse) {
        super(myType);

        this.theArrow = arrow;
        oldConstraint = arrow.getCardinalityConstraint();
        oldCardinality = arrow.getCardinality();
        oldDashed = arrow.isDashed();
        oldInverse = arrow.isInverse();

        this.newConstraint = newConstraint;
        this.newCardinality = newCardinality;
        this.newDashed = dashed;
        this.newInverse = inverse;
    }


    @Override
    public void execute() {
        getArrow().setCardinalityConstraint(newConstraint, newCardinality);
        getArrow().setDashed(newDashed);
        getArrow().setInverse(newInverse);
    }


    @Override
    public void unExecute() {
        getArrow().setCardinalityConstraint(oldConstraint, oldCardinality);
        getArrow().setDashed(oldDashed);
        getArrow().setInverse(oldInverse);
    }

    @Override
    public Collection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeArrowConstraintsEvent(getArrow()));
        return result;
    }

    @Override
    public Collection<Event> getUnExecuteEvents() {
        return getEvents();
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getArrow().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }


    public ConcreteArrow getArrow() {
        return theArrow;
    }

}
