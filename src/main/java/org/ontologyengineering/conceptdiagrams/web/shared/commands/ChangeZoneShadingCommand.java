package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.client.events.ChangeZoneShadingEvent;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.AddShading;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.LabelledMultiDiagramTransformation;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.TransformAClassAndObjectPropertyDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.transformations.TransformADatatypeDiagram;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.HashSet;


public class ChangeZoneShadingCommand extends Command {

    private ConcreteZone zoneChanged;

    public ChangeZoneShadingCommand(ConcreteZone zoneToChange) {
        zoneChanged = zoneToChange;
    }

    @Override
    public void execute() {
        zoneChanged.swapShading();
    }

    @Override
    public void unExecute() {
        zoneChanged.swapShading();
    }

    @Override
    public AbstractCollection<Event> getEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeZoneShadingEvent(zoneChanged));
        return result;
    }


    @Override
    public AbstractCollection<Event> getUnExecuteEvents() {
        HashSet<Event> result = new HashSet<Event>();
        result.add(new ChangeZoneShadingEvent(zoneChanged));
        return result;
    }

    protected ConcreteZone getZone() {
        return zoneChanged;
    }

    @Override
    public ConcreteDiagram getDiagram() {
        return getZone().getDiagram();
    }

    @Override
    public boolean leadsToValid() {
        return true;
    }

    @Override
    public LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace) {

        // FIXME : maybe this command should take a set of zones (through the multiselect in the interface)
        // this is tough though because zones can be shaded and unshaded many times ... and maybe in different sets
        // each time ... might be best to just bundle up all the shaded zones at the end and do them all in one batch.

        // the below implements a bit as if it's like this - find the last shading event for this boundary rectangle
        // do all the shading in one.

        // need to make sure we don't issue multiple transformations for the one zone ... so just take the last state
        boolean lastShading = true;
        for(int i = myPlace + 1; i < commands.size(); i++) {
            Command c = commands.get(i);
            if(c.getClass() == this.getClass()) { // thought I couldn't do this in GWT??
                if(((ChangeZoneShadingCommand) c).getZone().getBoundaryRectangle() == getZone().getBoundaryRectangle()) {
                    lastShading = false;
                }
            }
        }

        if(lastShading) {
            AbstractSet<ConcreteZone> shadedZones = new HashSet<ConcreteZone>();
            shadedZones.addAll(getZone().getBoundaryRectangle().getShadedZones());

            if (getZone().getBoundaryRectangle().isObject()) {
                return new TransformAClassAndObjectPropertyDiagram(new AddShading(shadedZones));
            } else {
                return new TransformADatatypeDiagram(new AddShading(shadedZones));
            }
        }

        return null;
    }
}
