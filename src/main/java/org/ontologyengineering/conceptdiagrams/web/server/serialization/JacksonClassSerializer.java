package org.ontologyengineering.conceptdiagrams.web.server.serialization;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: May 2016<br>
 * See license information in base directory.
 */

public class JacksonClassSerializer {

    final Logger logger = LoggerFactory.getLogger(JacksonClassSerializer.class);

    // Just a hacky way.  Could be an option in the UI at the front end
    private final String fileprefix = "/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories/workspace";
    private final String filename = "LatestSerialization.cd.hist";


    private final String filePathandName = fileprefix + "/" + filename;

    private ObjectMapper mapper;

    public JacksonClassSerializer() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void serializeCommandHistory(ArrayList<Command> history) {
        serializeCommandHistory(history, filePathandName);
    }

    public void serializeCommandHistory(ArrayList<Command> history, String file) {
        File outfile = new File(file);
        try {
            mapper.writer().withDefaultPrettyPrinter().writeValue(outfile, history);
            //mapper.writerWithType(new TypeReference<ArrayList<Command>>() {}).writeValue(outfile, history);
        } catch(IOException e) {
            logger.error("Exception serializing history", e);
        }
    }


    public void serializeCommandHistory(HashSet<ArrayList<Command>> histories, String file) {
        File outfile = new File(file);
        try {
            mapper.writer().withDefaultPrettyPrinter().writeValue(outfile, histories);
            //mapper.writerWithType(new TypeReference<ArrayList<Command>>() {}).writeValue(outfile, history);
        } catch(IOException e) {
            logger.error("Exception serializing history", e);
        }
    }



    public ArrayList<Command> readSerializedCommandHistory(String file) {
        ArrayList<Command> result = null;

        File outfile = new File(file);
        try {
            result = mapper.readValue(outfile, new TypeReference<ArrayList<Command>>() {});
        } catch(IOException e) {
            logger.info("Exception reading serialized", e);
        }

        return result;
    }

    public HashSet<ArrayList<Command>> readSerializedCommandHistoryToHashSet(String file) {
        HashSet<ArrayList<Command>> result = null;

        File outfile = new File(file);
        try {
            result = mapper.readValue(outfile, new TypeReference<HashSet<ArrayList<Command>>>() {});
        } catch(IOException e) {
            logger.info("Exception reading serialized", e);
        }

        return result;
    }
}