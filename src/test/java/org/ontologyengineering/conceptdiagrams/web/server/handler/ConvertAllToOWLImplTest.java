package org.ontologyengineering.conceptdiagrams.web.server.handler;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.ConvertDiagramsToOWL;
import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLAPIutils;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.GsonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.server.serialization.JacksonClassSerializer;
import org.ontologyengineering.conceptdiagrams.web.shared.commands.Command;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;


public class ConvertAllToOWLImplTest {

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

//        JacksonClassSerializer jacksonSerialiser = new JacksonClassSerializer();
//        ArrayList<Command> history = jacksonSerialiser.readSerializedCommandHistory("/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories/workspace/test.cd.hist");
//
//        OWLOntology ontology = OWLAPIutils.makeFreshOntology("http://www.test.test");
//
//        ConvertDiagramsToOWL converter = new ConvertDiagramsToOWL();
//        converter.convertToOWL(history, ontology);
//
//        OWLAPIutils.writeOntology(ontology, "/Users/Michael/thm_prv/code/ConceptDiagrams/src/test/resources/serializedHistories/workspace/output.owl");
//
//        // ...now we need to run our tests
//        assert (true);


        Boolean allPassed = true;


        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(testDir));
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".cd.hist")) {
                    allPassed &= testHistory(path);
                }
            }
        } catch (IOException ex) {

        }


//        try {
//            for(Path path : Files.list(new File(testDir).toPath()).filter(p -> p.getFileName().toString().endsWith(".cd.hist"))) {
//                allPassed &= testHistory(path);
//            }
////            Files.list(new File(testDir).toPath()).filter(p -> p.getFileName().toString().endsWith(".cd.hist"))
////                    .forEach(path -> testHistory(path));
//        } catch (IOException e) {
//
//        }

        Assert.assertTrue(allPassed);
    }


    private boolean testHistory(Path path) {
        boolean result = true;

        String fullname = path.getFileName().toString();

        String exname = fullname.substring(0, fullname.indexOf(".cd.hist"));

        // run the tool over this file
        JacksonClassSerializer jacksonSerialiser = new JacksonClassSerializer();
        ArrayList<Command> history = jacksonSerialiser.readSerializedCommandHistory(path.toString());

        OWLOntology ontology = OWLAPIutils.makeFreshOntology("http://www.test.test");

        ConvertDiagramsToOWL converter = new ConvertDiagramsToOWL();
        converter.convertToOWL(history, ontology);

        // save with .owl extension


        Path savedOWL = Paths.get(path.getParent().toString(), exname + ".owl");
        Path potentialTest = Paths.get(path.getParent().toString(), exname + ".test.owl");

        OWLAPIutils.writeOntology(ontology, savedOWL.toString());


        // if there is a test file for this then try the test

        // load the two files using Jean

        // run the reasoner

        // check that every YYY_TEST class is Nothing


        // All this is copied directly from the OWL api examples
        // https://github.com/owlcs/owlapi/blob/version4/contract/src/test/java/org/semanticweb/owlapi/examples/Examples.java

        System.out.println(exname + ".owl");
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontBase = manager.loadOntologyFromOntologyDocument(new FileDocumentSource(savedOWL.toFile()));

            OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();

            OWLReasoner reasonerBase = reasonerFactory.createReasoner(ontBase);
            reasonerBase.precomputeInferences();

            if (reasonerBase.isConsistent()) {
                System.out.println("consistent");
            } else {
                System.out.println("inconsistent");
                result = false;
            }

        } catch (OWLOntologyCreationException e) {
            result = false;
        } catch (InconsistentOntologyException e) {
            System.out.println("inconsistent");
            result = false;
        }

        if (result == true && potentialTest.toFile().isFile()) {

            try {
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                OWLOntology ontBase = manager.loadOntologyFromOntologyDocument(new FileDocumentSource(savedOWL.toFile()));
                OWLOntology ont = manager.loadOntologyFromOntologyDocument(new FileDocumentSource(potentialTest.toFile()));

                OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
                OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
                reasoner.precomputeInferences();

                Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
                Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();

                Set<OWLClass> allClasses = ont.getClassesInSignature(false);
                for (OWLClass c : allClasses) {
                    if (c.getIRI().toString().endsWith("_TEST")) {
                        if (unsatisfiable.contains(c)) {
                            System.out.println("Correct : " + c.getIRI().toString());
                        } else {
                            System.out.println("Incorrect : " + c.getIRI().toString());
                            result = false;
                        }
                    }
                }

            } catch (OWLOntologyCreationException e) {
                result = false;
            }
        }
        return result;
    }


}