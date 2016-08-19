package org.ontologyengineering.conceptdiagrams.web.client.ui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

public interface MenuIcons extends ClientBundle {

    // -------------------------------
    // TOOL BAR
    // -------------------------------

    ImageResource newFile();

    ImageResource loadFile();

    ImageResource save();

    ImageResource properties();

    // -------------------------------

    ImageResource selectArrow();

    ImageResource panArrows();

    ImageResource zoomIn();

    ImageResource zoomOut();

    ImageResource undoArrow();

    ImageResource redoArrow();

    ImageResource delete();

    // -------------------------------

    ImageResource toOWL();

    // -------------------------------

    ImageResource boundaryRectangle();

    ImageResource asterisk();

    ImageResource curve();

    ImageResource shade();

    ImageResource arrow();

    ImageResource spider();



    // -------------------------------
    // TAB BAR
    // -------------------------------

    ImageResource add();

    ImageResource close();

    ImageResource leftArrow();

    ImageResource rightArrow();

    ImageResource more();
}
