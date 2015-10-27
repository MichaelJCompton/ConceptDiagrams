package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import java.util.AbstractCollection;


// TODO : overall things that might need to be added to the code base
//
// - Lambda functions to map to literals, etc in OWL
// - various constraints and checks from the syntax/semantics to keep everything consistent
//   * constraints on diagrams (build and test these as diagrams are created)
// - checking of OWL compatibility of labels
// - handling in - K sets



/**
 *
 */
public abstract class DiagramElement<DiagramType extends AbstractDiagram> {


    private DiagramType myDiagram;

    private String label;

    private String uniqueID;

    private static final String idPrefix = "element";
    private static final IDGenerator id_Gen = new IDGenerator();

    DiagramElement() {
        initialise();
    }

    DiagramElement(String label) {
        initialise();
        setLabel(label);
    }

    DiagramElement(DiagramType diagram) {
        initialise();
        setDiagram(diagram);
    }

    DiagramElement(String label, DiagramType diagram) {
        initialise();
        setDiagram(diagram);
        setLabel(label);
    }

    private void initialise() {
        uniqueID = id_Gen.getID(idPrefix);
        label = "";
    }

    public DiagramType diagram() {
        return myDiagram;
    }


    public void setDiagram(DiagramType newDiagram) {
        myDiagram = newDiagram;
    }



    abstract public AbstractCollection<DiagramElement> children ();



    // Most DiagramElements need a name.
    // In the syntax/semantics document this is provided by labelling functions.
    // For the implementation, this seems more naturally implemented by giving each component a label (optionally
    // with labelling functions provided by the diagrams to mimic the document syntax).

    public void setLabel(String newLabel) {
        label = newLabel;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isUnLabelled() {
        return label.equals("");
    }

    public Boolean isLabelled() {
        return ! isUnLabelled();
    }


    // Also a unique id
    // For zones, unlabelled curves and spiders, diagrams, anything before it gets a label, etc.

    public String id() {
        return uniqueID;
    }
}
