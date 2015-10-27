package org.ontologyengineering.conceptdiagrams.web.shared.abstractsyntax;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */


public class Curve extends DiagramArrowSourceOrTarget<LabelledDiagram> {

    private int curveID; // unique ordering id in this boundary rectangle

    private AbstractSet<Zone> unShadedZones;
    private AbstractSet<Zone> shadedZones;

    public Curve() {
        super();
        unShadedZones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
    }

    public Curve(LabelledDiagram parent) {
        super(parent);
        unShadedZones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        setID(parent.nextCurveID());
    }

    public Curve(String label) {
        super(label);
        unShadedZones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
    }

    public Curve(String label, LabelledDiagram parent) {
        super(label, parent);
        unShadedZones = new HashSet<Zone>();
        shadedZones = new HashSet<Zone>();
        setID(parent.nextCurveID());
    }

    // this should be more protected, just only set it once and keep the ids dense
    protected void setID(int newID) {
        curveID = newID;
    }

    public int getCurveID() {
        return curveID;
    }

    public void addZone(Zone z) {
        unShadedZones.add(z);
    }

    public void shadeZone(Zone z) {
        unShadedZones.remove(z);
        shadedZones.add(z);
    }

    public AbstractSet<Zone> zones() {
        AbstractSet<Zone> result = new HashSet<Zone>();
        result.addAll(unShadedZones);
        result.addAll(shadedZones);
        return result;
    }

    public AbstractSet<Zone> unShadedZones() {
        return unShadedZones;
    }

    public AbstractSet<Zone> shadedZones() {
        return shadedZones;
    }
}
