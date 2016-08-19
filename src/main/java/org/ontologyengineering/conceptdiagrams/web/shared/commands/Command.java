package org.ontologyengineering.conceptdiagrams.web.shared.commands;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.web.bindery.event.shared.Event;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagram;

import java.io.Serializable;
import java.util.Collection;

/**
 * Commands that get executed on the concrete/abstract syntax - these may change the onscreen representation.
 *
 *
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")  // Jackson serialization
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddArrowCommand.class, name = "AddArrowCommand"),
        @JsonSubTypes.Type(value = AddBoundaryRectangleCommand.class, name = "AddBoundaryRectangleCommand"),
        @JsonSubTypes.Type(value = AddCurveCommand.class, name = "AddCurveCommand"),
        @JsonSubTypes.Type(value = AddSpiderCommand.class, name = "AddSpiderCommand"),
        @JsonSubTypes.Type(value = AddStarRectangleCommand.class, name = "AddStarRectangleCommand"),
        @JsonSubTypes.Type(value = AddStarRectangleCommand.class, name = "ChangeArrowSourceTargetCommand"),
        @JsonSubTypes.Type(value = AddStarRectangleCommand.class, name = "ChangeArrowConstraintsCommand"),
        @JsonSubTypes.Type(value = AddStarRectangleCommand.class, name = "ChangeElementTypeCommand"),
        @JsonSubTypes.Type(value = ChangeLabelCommand.class, name = "ChangeLabelCommand"),
        @JsonSubTypes.Type(value = ChangeZoneShadingCommand.class, name = "ChangeZoneShadingCommand"),
        @JsonSubTypes.Type(value = ChangeZoneShadingCommand.class, name = "FlipArrowDashedCommand"),
        @JsonSubTypes.Type(value = FlipObjectPropertyInverseCommand.class, name = "FlipObjectPropertyInverseCommand"),
        @JsonSubTypes.Type(value = MoveCommand.class, name = "MoveCommand"),
        @JsonSubTypes.Type(value = MoveCommand.class, name = "RemoveArrowCommand"),
        @JsonSubTypes.Type(value = MoveCommand.class, name = "RemoveBoundaryRectangleCommand"),
        @JsonSubTypes.Type(value = RemoveCurveCommand.class, name = "RemoveCurveCommand"),
        @JsonSubTypes.Type(value = RemoveCurveCommand.class, name = "RemoveSpiderCommand"),
        @JsonSubTypes.Type(value = ResizeCommand.class, name = "ResizeCommand"),
        @JsonSubTypes.Type(value = ResizeCommand.class, name = "SetElementAsDataCommand"),
        @JsonSubTypes.Type(value = ResizeCommand.class, name = "SetElementAsObjectCommand")
})
public abstract class Command implements Serializable {

    // for gson serialization with RuntimeTypeAdaptor
    private String type;

    public Command() { }

    protected Command(String type) {
        this.type = type;
    }

    public abstract void execute();
    public abstract void unExecute();

    public abstract Collection<Event> getEvents();
    public abstract Collection<Event> getUnExecuteEvents();

    // Note this may change.  It's a reference to the diagram that this command acted on, but as the diagram changes
    // it may be for example that the diagram and another get joined by an arrow and so become one diagram.
    public abstract ConcreteDiagram getDiagram();

    public abstract boolean leadsToValid();  // does this command eventually lead to a valid state
    // myPlace should be a valid index and the index of 'this'.
    // Need to be able to check through the rest of the command list to make sure that this command is valid and if it needs to
    // be made into a transformation (i.e. if there are many re-names, just take the last)
    //public abstract LabelledMultiDiagramTransformation asMultiDiagramTransformation(AbstractList<Command> commands, int myPlace);

}
