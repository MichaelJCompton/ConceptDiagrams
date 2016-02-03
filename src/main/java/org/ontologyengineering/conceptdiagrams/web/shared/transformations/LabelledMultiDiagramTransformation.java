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


    // Definition 20
    public void T(AbstractSet<ZonalRegion> zonalRegions, OWLOutputter outputter) {

            // FIXME : these are in the outputters --- what should be here???

    }

    // Definition 21
    public void T_data(AbstractSet<ZonalRegion> zonalRegions, OWLOutputter outputter) {

    }



    // Definition 26
    public void T_SC(Arrow arrow, OWLOutputter outputter) {
        if(arrow.diagram() != diagram()) {
            return;
        }
        // should also check if it's the right kind of arrow (i.e. in A_o) but for now we'll trust the calling code

        AbstractSet<ZonalRegion> SC = diagram().SC(arrow);



    }

    // Definition 27
    public void T_SC_data(Arrow arrow, OWLOutputter outputter) {
    }



}
