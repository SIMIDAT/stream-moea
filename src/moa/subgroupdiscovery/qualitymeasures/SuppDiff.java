/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import org.core.exceptions.InvalidRangeInMeasureException;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;

/**
 *
 * @author agvico
 */
public final class SuppDiff extends QualityMeasure {

    public SuppDiff() {
        super.name = "Support Difference";
        super.short_name = "SuppDiff";
        super.value = 0.0;
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        try {
            TPR tpr = new TPR();
            FPR fpr = new FPR();

            tpr.calculateValue(t);
            fpr.calculateValue(t);
            tpr.validate();
            fpr.validate();

            value = tpr.value - fpr.value;
        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (!(value >= -1.0 && value <= 1.0) || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        SuppDiff a = new SuppDiff();
        a.name = this.name;
        a.value = this.value;

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof SuppDiff)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            if (this.value < o.value) {
                return -1;
            } else if (this.value > o.value) {
                return 1;
            } else {
                return 0;
            }
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
