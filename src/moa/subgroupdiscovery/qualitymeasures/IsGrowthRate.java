/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;
import org.core.exceptions.InvalidRangeInMeasureException;

/**
 *
 * Class to calculate the percentage of rules that are EPs. It returns 1 if the
 * GrowthRate is greater than 1 or 0 otherwise.
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class IsGrowthRate extends QualityMeasure {

    public IsGrowthRate() {
        this.name = "Is Growth Rate";
        this.short_name = "GR_Pct";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        GrowthRate gr = new GrowthRate();

        gr.calculateValue(t);
        try {
            gr.validate();
            if (gr.getValue() > 1.0) {
                value = 1.0;
            } else {
                value = 0.0;
            }
        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (value > 1.0 || value < 0.0) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        IsGrowthRate a = new IsGrowthRate();
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
            if (!(o instanceof IsGrowthRate)) {
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
        return 0;    }

}
