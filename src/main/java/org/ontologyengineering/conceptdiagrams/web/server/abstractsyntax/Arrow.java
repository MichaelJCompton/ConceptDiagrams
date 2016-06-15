package org.ontologyengineering.conceptdiagrams.web.server.abstractsyntax;

/**
 * Author: Michael Compton<br>
 * Date: September 2015<br>
 * See license information in base directory.
 */

import org.ontologyengineering.conceptdiagrams.web.shared.concretesyntax.ConcreteArrow;

import java.util.AbstractCollection;
import java.util.HashSet;

public abstract class Arrow extends DiagramElement<AbstractDiagram, ConcreteArrow> {

    // TODO : need to add the lambda functions once this code is linked up with WebProt

    private DiagramArrowSourceOrTarget source;
    private DiagramArrowSourceOrTarget target;

    public enum CardinalityConstraint {NONE, EQ, LEQ, GEQ}
    private CardinalityConstraint cardinalityConstraint;
    private Integer cardinality;

    public Arrow () {
        /// ???
    }

    Arrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target) {
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    Arrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
          String label) {
        super(label);
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    Arrow(DiagramArrowSourceOrTarget source, DiagramArrowSourceOrTarget target,
          String label, AbstractDiagram parent) {
        super(label, parent);
        this.source = source;
        this.target = target;
        cardinalityConstraint = CardinalityConstraint.NONE;
    }

    public void setCardinalityConstraint(CardinalityConstraint constraint, Integer cardinality) {
        cardinalityConstraint = constraint;
        this.cardinality = cardinality;
    }

    public void setCardinalityConstraintFromConcrete() {
        if(getConcreteRepresentation() == null) {
            return;
        }

        switch (getConcreteRepresentation().getCardinalityConstraint()) {
            case EQ:
                this.cardinalityConstraint = CardinalityConstraint.EQ;
                break;
            case GEQ:
                this.cardinalityConstraint = CardinalityConstraint.GEQ;
                break;
            case LEQ:
                this.cardinalityConstraint = CardinalityConstraint.LEQ;
                break;
            default:
                this.cardinalityConstraint = CardinalityConstraint.NONE;
                break;
        }
        this.cardinality = getConcreteRepresentation().getCardinality();
    }

    public Boolean hasCardinalityConstraint() {
        return cardinalityConstraint != CardinalityConstraint.NONE;
    }

    public CardinalityConstraint getCardinalityConstraint() {
        return cardinalityConstraint;
    }

    public Integer getCardinality() {
        return cardinality;
    }

    public boolean isInverse() {
        return false;
    }

    @Override
    public AbstractCollection<DiagramElement> children() {
        return new HashSet<DiagramElement>();
    }

    public DiagramArrowSourceOrTarget getTarget() {
        return target;
    }

    public DiagramArrowSourceOrTarget getSource() {
        return source;
    }


    public boolean targetIsCurve() {
        return getTarget().getClass() == Curve.class;
    }


    public Curve targetAsCurve() {
        if(targetIsCurve()) {
            return (Curve) getTarget();
        }
        return null;
    }

    public boolean targetIsBoundaryRectangle() {
        return getTarget().getClass() == BoundaryRectangle.class;
    }


}
