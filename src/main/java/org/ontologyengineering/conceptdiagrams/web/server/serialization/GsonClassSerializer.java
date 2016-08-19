package org.ontologyengineering.conceptdiagrams.web.server.serialization;

//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.graph.GraphAdapterBuilder;
//import com.google.gson.reflect.TypeToken;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import com.google.gson.graph.GraphAdapterBuilder;


/**
 * Author: Michael Compton<br>
 * Date: May 2016<br>
 * See license information in base directory.
 */


// First cut

public class GsonClassSerializer {

    final Logger logger = LoggerFactory.getLogger(GsonClassSerializer.class);

    // Just a hacky way.  Could be an option in the UI at the front end
    private final String fileprefix = "/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories";
    private final String filename = "Gem_EX_1.cd.hist";


//    private final String fileprefix = "/tmp";
//    private final String filename = "Gem_EX_4_pg1_4.cd.hist";
    private final String filePathandName = fileprefix + "/" + filename;


//    private Gson gson;


    public GsonClassSerializer() {
//        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void serializeCommandHistory(ArrayList<Command> history) {
        serializeCommandHistory(history, filePathandName);
    }

    public void serializeCommandHistory(ArrayList<Command> history, String file) {

//        try {
//            Writer writer = new FileWriter(file);
//
//            GsonBuilder gsonBuilder = makeGsonBuilder();
//            Gson gson = gsonBuilder.create();
//
//            gson.toJson(history, writer);
//            writer.close();
//
//        } catch (IOException e) {
//            logger.error("ouch something baaaadddd", e);
//        }
    }


    public ArrayList<Command> readSerializedCommandHistory(String file) {
        ArrayList<Command> result = null;
//
//        GsonBuilder gsonBuilder = makeGsonBuilder();
//        Gson gson = gsonBuilder.create();
//
//        try {
//            JsonReader reader = new JsonReader(new FileReader(file));
//
//            Type listType = new TypeToken<ArrayList<Command>>() {}.getType();
//            result = gson.fromJson(reader, listType);
//
//        } catch (IOException e) {
//            logger.error("ouch something badd", e);
//        }
//
        return result;
    }

//    private GsonBuilder makeGsonBuilder() {
//
//        RuntimeTypeAdapterFactory<Command> runtimeTypeAdapterFactory_command = RuntimeTypeAdapterFactory
//                .of(Command.class, "type")
//                .registerSubtype(AddArrowCommand.class, "AddArrowCommand")
//                .registerSubtype(AddBoundaryRectangleCommand.class, "AddBoundaryRectangleCommand")
//                .registerSubtype(AddCurveCommand.class, "AddCurveCommand")
//                .registerSubtype(AddSpiderCommand.class, "AddSpiderCommand")
//                .registerSubtype(AddStarRectangleCommand.class, "AddStarRectangleCommand")
//                .registerSubtype(ChangeLabelCommand.class, "ChangeLabelCommand")
//                .registerSubtype(ChangeZoneShadingCommand.class, "ChangeZoneShadingCommand")
//                .registerSubtype(FlipObjectPropertyInverseCommand.class, "FlipObjectPropertyInverseCommand")
//                .registerSubtype(MoveCommand.class, "MoveCommand")
//                .registerSubtype(RemoveCurveCommand.class, "RemoveCurveCommand")
//                .registerSubtype(ResizeCommand.class, "ResizeCommand");
//
//        RuntimeTypeAdapterFactory<ConcreteDiagramElement> runtimeTypeAdapterFactory_concreteElement = RuntimeTypeAdapterFactory
//                .of(ConcreteDiagramElement.class, "type")
//                .registerSubtype(ConcreteRectangularElement.class, "ConcreteRectangularElement")
//                .registerSubtype(ConcreteBoundaryRectangle.class, "ConcreteBoundaryRectangle")
//                .registerSubtype(ConcreteZone.class, "ConcreteZone")
//                .registerSubtype(ConcreteIntersectionZone.class, "ConcreteIntersectionZone")
//                .registerSubtype(ConcreteCurve.class, "ConcreteCurve")
//                .registerSubtype(ConcreteArrow.class, "ConcreteArrow")
//                .registerSubtype(ConcreteDiagram.class, "ConcreteDiagram")
//                .registerSubtype(ConcreteSpider.class, "ConcreteSpider")
//                .registerSubtype(ConcreteStarRectangle.class, "ConcreteStarRectangle");
//
//
//        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting()
//                .registerTypeAdapterFactory(runtimeTypeAdapterFactory_command)
//                .registerTypeAdapterFactory(runtimeTypeAdapterFactory_concreteElement);
//
//        new GraphAdapterBuilder()
//                .addType(ConcreteDiagramElement.class)
//                .addType(ConcreteRectangularElement.class)
//                .addType(ConcreteBoundaryRectangle.class)
//                .addType(ConcreteZone.class)
//                .addType(ConcreteIntersectionZone.class)
//                .addType(ConcreteCurve.class)
//                .addType(ConcreteArrow.class)
//                .addType(ConcreteDiagram.class)
//                .addType(ConcreteSpider.class)
//                .addType(ConcreteStarRectangle.class)
//                .addType(Command.class)
//                .addType(AddArrowCommand.class)
//                .addType(AddBoundaryRectangleCommand.class)
//                .addType(AddCurveCommand.class)
//                .addType(AddSpiderCommand.class)
//                .addType(AddStarRectangleCommand.class)
//                .addType(ChangeLabelCommand.class)
//                .addType(ChangeZoneShadingCommand.class)
//                .addType(FlipObjectPropertyInverseCommand.class)
//                .addType(MoveCommand.class)
//                .addType(RemoveCurveCommand.class)
//                .addType(ResizeCommand.class)
//                .registerOn(gsonBuilder);
//
//        return gsonBuilder;
//    }

}
