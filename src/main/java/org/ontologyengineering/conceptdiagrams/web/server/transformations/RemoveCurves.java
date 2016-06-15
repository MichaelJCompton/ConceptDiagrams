package org.ontologyengineering.conceptdiagrams.web.server.transformations;

/**
 * Author: Michael Compton<br> Date: October 2015<br> See license information in base directory.
 */


import org.ontologyengineering.conceptdiagrams.web.server.owlOutput.OWLOutputBuilder;
import org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax.*;
import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteCurve;


import java.util.AbstractSet;

/**
 * Transformation 10
 * <p/>
 * Differently to the document, this class implements both Transformation 9 and 10 in.  Seems like it will be faster to
 * do the (pretend) curve removal as one operation rather than curve by curve.
 * <p/>
 * Seems that it's zones that's important, so just concentrate on pretending to remove those.  Rather than making a new
 * diagram on this removal, we'll just keep the removed curves and for any checked zones just ignore them in the
 * testing.  This seems equivalent to the actual removal.  As far as I can see, I'm never making up new zones to test
 * against this modified diagram, I'm just testing things about the existing zone.  Removing zones may make two previous
 * zones into the same zone, but in having the removed zone set means we will just treat the two the same.
 */
public class RemoveCurves<T extends LabelledDiagram> extends LabelledDiagramTransformation <T> {

    private AbstractSet<ConcreteCurve> removedCurves;
    private FastCurveSet fastRemovedCurves;

    public RemoveCurves(AbstractSet<ConcreteCurve> removedCurves) {
//        this.removedCurves = removedCurves;
//        fastRemovedCurves = new FastCurveSet();
//        for(ConcreteCurve c : removedCurves) {
//            fastRemovedCurves.set(c.getAbstractSyntaxRepresentation().getCurveID());
//        }
    }

    @Override
    public void executeTransformation(T transformedDiagram) {
        super.executeTransformation(transformedDiagram);

        // should never be called
    }


    public void translate(LabelledMultiDiagram diagram, OWLOutputBuilder outputter) {
    }

    @Override
    public void translate(T transformedDiagram, OWLOutputBuilder outputter) {
        // should never be called
    }

    /*
    Is the given zone (ignorning any removed curves in that) in the zones of the resultant diagram, again ingoring
    any removed zones.  But this is really just equivalent to testing if it was in the diagram to start with ...
    the zone {A,B,...}\{...} can't have become part of the missing zones, so if it was there, it's still there.
     */
    public boolean inZ(Zone z) {
        return diagram() == z.diagram();
    }


    public boolean inZstar(Zone z) {

        if(!inZ(z)) {
            return false;
        }

        // make z - fastRemovedCurves
        FastCurveSet zMinus = new FastCurveSet();



        for(Zone z1 : diagram().getShadedZones()) {
            // make z1 - fastRemovedCurves
            // are the two ==??
        }

        return false;
    }
}
