/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import org.core.exceptions.InvalidRangeInMeasureException;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

/**
 *
 * @author agvico
 */
public class NULL extends QualityMeasure {

    public NULL() {
        super.name = "NULL";
        super.short_name = "NULL";
        super.value = Double.NaN;
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        return Double.NaN;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        throw new InvalidRangeInMeasureException(this);
    }

    @Override
    public QualityMeasure clone() {

        NULL a = new NULL();
        a.name = this.name;
        a.value = this.value;

        return a;

    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        return 0;
    }

}
