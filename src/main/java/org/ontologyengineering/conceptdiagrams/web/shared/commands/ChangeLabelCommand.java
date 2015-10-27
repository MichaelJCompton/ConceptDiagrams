package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeLabelEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.AddCurveLabel;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.TransformAClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.TransformADatatypeDiagram;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.HashSet;


public class ChangeLabelCommand extends Command {

    ConcreteDiagramElement element;
    String newLabel, oldLabel;


    public ChangeLabelCommand(ConcreteDiagramElement elementToLabel, String label) {
        element = elementToLabel;
        oldLabel = element.labelText();
        newLabel = label;
    }


    @Override
    public void execute() {
        element.setLabel(newLabel);
    }

    @Override
    public void unExecute() {
        element.setLabel(oldLabel);
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeLabelEvent(element));
        return result;
    }

    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        return getEvents();
    }

    protected ConcreteDiagramElement getElement() {
        return element;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getElement().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {
        // is this a curve and the first labelling occurance for the curve
        if(getElement().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE) {
            boolean firstLabelling = true;
            for(int i = 0; i < myPlace; i++) {
                Command c = commands.get(i);
                if(c.getClass() == this.getClass()) { // thought I couldn't do this in GWT??
                    if(((ChangeLabelCommand) c).getElement() == getElement()) {
                        firstLabelling = false;
                    }
                }
            }
            if(firstLabelling) {
                if(getElement().isObject()) {
                    return new TransformAClassAndObjectPropertyDiagram(new AddCurveLabel((ConcreteCurve) getElement()));
                } else {
                    return new TransformADatatypeDiagram(new AddCurveLabel((ConcreteCurve) getElement()));
                }
            }
        }
        return null;
    }
}
