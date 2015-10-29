package org.ontologyengineering.conceptdiagrams.web.shared.transformations;

/**
 * Author: Michael Compton<br>
 * Date: October 2015<br>
 * See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.Arrow;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.LabelledMultiDiagram;
import org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax.ZonalRegion;
import org.ontologyengineering.conceptdiagrams.web.shared.owlOutput.OWLOutputter;

import java.util.AbstractSet;


public abstract class LabelledMultiDiagramTransformation extends Transformation <LabelledMultiDiagram> {

    public LabelledMultiDiagramTransformation() {
    }


    // Definition 26
    public void T_SC(Arrow arrow, OWLOutputter outputter) {
        if(arrow.diagram() != diagram()) {
            return;
        }
        // should also check if it's the right kind of arrow (i.e. in A_o) but for now we'll trust the calling code

        AbstractSet<ZonalRegion> SC = diagram().SC(arrow);

        if(SC == null) {
            // ??? shouldn't happen
        } else if (SC.size() == 0) {
            // OWL:Nothing
        } else if (SC.size() == 1) {
            // T(<IN, OUT, v>)
        }

    }

}
