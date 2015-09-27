package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


/**
 *  * may need functions for accessing curves, spiders etc - are these children + separate?
 *
 *
 * Some constraints
 *
 * - The parent of a BoundaryRectangle can really only be a LabelledDiagram (or subclass).
 */
public class BoundaryRectangle extends DiagramArrowSourceOrTarget {

    public BoundaryRectangle() {
        super();
    }

    public BoundaryRectangle(LabelledDiagram parent) {
        super(parent);
    }


    // Are its children every thing in the labelled diagram?  The semantics document attaches everything to the
    // diagram: i.e. the boundary rectangle, the curves, etc are all parts of the tuple defining a diagram,
    // rather than them being part of the boundary rectangle.  So that's how it's implemented here; however,
    // it's easy enough to get, for example, the curves in a boundary rectangle through its parent diagram.

}
