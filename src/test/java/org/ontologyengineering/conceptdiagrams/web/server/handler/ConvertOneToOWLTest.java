package org.ontologyengineering.conceptdiagrams.web.server.handler;


import org.junit.*;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.ConvertDiagramsToOWL;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLAPIutils;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;


public class ConvertOneToOWLTest {

    private static String testDir = "/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void Gem_EX_1_test() {

        JacksonClassSerializer jacksonSerialiser = new JacksonClassSerializer();
        ArrayList<Command> history = jacksonSerialiser.readSerializedCommandHistory("/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories/workspace/test.cd.hist");

        OWLOntology ontology = OWLAPIutils.makeFreshOntology("http://www.test.test");

        ConvertDiagramsToOWL converter = new ConvertDiagramsToOWL();
        converter.convertToOWL(history, ontology);

        OWLAPIutils.writeOntology(ontology, "/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories/workspace/output.owl");


        assert (true);


    }


}