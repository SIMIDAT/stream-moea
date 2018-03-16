/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;
import org.core.exceptions.InvalidRangeInMeasureException;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public final class Support extends QualityMeasure {

    public Support() {
        this.name = "Support";
        this.short_name = "Supp";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        if (t.getTotalExamples() == 0) {
            value = 0;
        } else {
            value = (double) t.getTp() / t.getTotalExamples();
        }

        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (value > 1.0 || value < 0.0 || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        Support a = new Support();
        a.name = this.name;
        a.value = this.value;

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public void getDescription(StringBuilder sb, int i) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof Support)) {
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
