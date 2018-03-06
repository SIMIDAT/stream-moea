/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class Coverage extends QualityMeasure {

    public Coverage(){
        this.name = "Coverage";
        this.short_name = "Cov";
        this.value = 0.0;
    }
    
    @Override
    public double calculateValue(ContingencyTable t) {

        if (t.getTotalExamples() == 0) {
            value = 0.0;
        } else {
            value = (double) (t.getTp() + t.getFp()) / t.getTotalExamples();
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
        Coverage a = new Coverage();
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

}
