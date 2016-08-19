package org.ontologyengineering.conceptdiagrams.web.server.transformations;

import org.ontologyengineering.conceptdiagrams.web.shared.commands.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteDiagramElement;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteZone;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.List;

/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */
public class CommandTransformer {

    //  Couldn't work out how to do this a nicer way.  Because the Commands get transfered from client to server
    // they can't output their own transformations (because those have access to the Outputter, which runs
    // server side only code).  So seemed that the options were to either have each action on the client call the
    // server and make and store the Commands only on the server side, or do this.  The other option is a good one -
    // might make undo etc on the client a little slower though.

    public CommandTransformer() {

    }

    // FIXME :: will need to catch nulls here and make sure handled ok in caller
    public LabelledMultiDiagramTransformation makeTransformationFromCommand(Command command, List<Command> commands, int myPlace) {

        if(command instanceof AddArrowCommand) {
            return transformAddArrowCommand((AddArrowCommand) command);
        } else if (command instanceof AddBoundaryRectangleCommand) {
            return transformAddBoundaryRectangleCommand((AddBoundaryRectangleCommand) command);
        } else if(command instanceof AddCurveCommand) {
            return transformAddCurveCommand((AddCurveCommand) command);
        } else if (command instanceof AddSpiderCommand) {
            return transformAddSpiderCommand((AddSpiderCommand) command);
        } else if (command instanceof AddStarRectangleCommand) {
            return transformAddStarRectangleCommand((AddStarRectangleCommand) command);
        } else if (command instanceof ChangeLabelCommand) {
            return transformChangeLabelCommand((ChangeLabelCommand) command, commands, myPlace);
        } else if (command instanceof ChangeZoneShadingCommand) {
            return transformChangeZoneShadingCommand((ChangeZoneShadingCommand) command, commands, myPlace);
        } else if (command instanceof FlipObjectPropertyInverseCommand) {
            return transformFlipObjectPropertyInverse((FlipObjectPropertyInverseCommand) command);
        } else if (command instanceof MoveCommand) {
            return transformMoveCommand((MoveCommand) command);
        } else if (command instanceof RemoveCurveCommand) {
            return transformRemoveCurveCommand((RemoveCurveCommand) command);
        } else if (command instanceof ResizeCommand) {
            return transformResizeCommand((ResizeCommand) command);
        }

        return null;  // unreachable!
    }


    private LabelledMultiDiagramTransformation transformAddArrowCommand(AddArrowCommand command) {
        if(command.getArrow().isObjectProperty()) {
            if(command.getArrow().singleRectangle()) {
                return new TransformAClassAndObjectPropertyDiagram(new AddLabelledArrow(command.getArrow()));
            } else {
                return new AddAnObjectPropertyLabelledArrow(command.getArrow());
            }
        } else {
            if(command.getArrow().singleRectangle()) {
                return new TransformADatatypeDiagram(new AddLabelledArrow(command.getArrow()));
            } else {
                return new AddADataPropertyLabelledArrow(command.getArrow());
            }
        }
    }

    private LabelledMultiDiagramTransformation transformAddBoundaryRectangleCommand(AddBoundaryRectangleCommand command) {
        if(command.getBoundaryRectangle().isObject()) {
            return new AddEmptyClassAndObjectPropertyDiagram(command.getBoundaryRectangle());
        } else {
            return new AddEmptyDatatypeDiagram(command.getBoundaryRectangle());
        }
    }

    private LabelledMultiDiagramTransformation transformAddCurveCommand(AddCurveCommand command) {
        if(command.getBoundaryRectangle().isObject()) {  // types should have been inferred by now
            return new TransformAClassAndObjectPropertyDiagram(new AddUnlabelledCurve(command.getCurve()));
        } else {
            return new TransformADatatypeDiagram(new AddUnlabelledCurve(command.getCurve()));
        }
    }

    private LabelledMultiDiagramTransformation transformAddSpiderCommand(AddSpiderCommand command) {
        if(command.getSpider().isObject()) {
            return new TransformAClassAndObjectPropertyDiagram(new AddLabelledSpider(command.getSpider()));
        } else {
            return new TransformADatatypeDiagram(new AddLabelledSpider(command.getSpider()));
        }
    }

    private LabelledMultiDiagramTransformation transformAddStarRectangleCommand(AddStarRectangleCommand command) {
        return new AddEmptyClassAndObjectPropertyDiagram(command.getBoundaryRectangle());
    }

    private LabelledMultiDiagramTransformation transformChangeLabelCommand(ChangeLabelCommand command, List<Command> commands, int myPlace) {
        // is this a curve and the first labelling occurance for the curve
        if(command.getElement().getType() == ConcreteDiagramElement.ConcreteDiagramElement_TYPES.CONCRETECURVE) {
            boolean firstLabelling = true;
            for(int i = 0; i < myPlace; i++) {
                Command c = commands.get(i);
                if(c.getClass() == command.getClass()) {
                    if(((ChangeLabelCommand) c).getElement() == command.getElement()) {
                        firstLabelling = false;
                    }
                }
            }
            if(firstLabelling) {
                if(command.getElement().isObject()) {
                    return new TransformAClassAndObjectPropertyDiagram(new AddCurveLabelToClassPropertyDiagram((ConcreteCurve) command.getElement()));
                } else {
                    return new TransformADatatypeDiagram(new AddCurveLabelToDataPropertyDiagram((ConcreteCurve) command.getElement()));
                }
            }
        }
        return null;
    }


    private LabelledMultiDiagramTransformation transformChangeZoneShadingCommand(ChangeZoneShadingCommand command, List<Command> commands, int myPlace) {

        // FIXME : maybe this command should take a set of zones (through the multiselect in the interface)
        // this is tough though because zones can be shaded and unshaded many times ... and maybe in different sets
        // each time ... might be best to just bundle up all the shaded zones at the end and do them all in one batch.

        // the below implements a bit as if it's like this - find the last shading event for this boundary rectangle
        // do all the shading in one.

        // need to make sure we don't issue multiple transformations for the one zone ... so just take the last state
        boolean lastShading = true;
        for(int i = myPlace + 1; i < commands.size(); i++) {
            Command c = commands.get(i);
            if(c.getClass() == command.getClass()) {
                if(((ChangeZoneShadingCommand) c).getZone().getBoundaryRectangle() == command.getZone().getBoundaryRectangle()) {
                    lastShading = false;
                }
            }
        }

        if(lastShading) {
            AbstractSet<ConcreteZone> shadedZones = new HashSet<ConcreteZone>();
            shadedZones.addAll(command.getZone().getBoundaryRectangle().getShadedZones());

            if (command.getZone().getBoundaryRectangle().isObject()) {
                return new TransformAClassAndObjectPropertyDiagram(new AddShadingToClassPropertyDiagram(shadedZones));
            } else {
                return new TransformADatatypeDiagram(new AddShadingToDatatypeDiagram(shadedZones));
            }
        }

        return null;
    }

    private LabelledMultiDiagramTransformation transformFlipObjectPropertyInverse(FlipObjectPropertyInverseCommand command) {
        return null;
    }

    private LabelledMultiDiagramTransformation transformMoveCommand(MoveCommand command) {
        return null;
    }

    private LabelledMultiDiagramTransformation transformRemoveCurveCommand(RemoveCurveCommand command) {
        return null;
    }

    private LabelledMultiDiagramTransformation transformResizeCommand(ResizeCommand command) {
        return null;
    }
}
