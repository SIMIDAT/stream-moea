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
public class Jaccard extends QualityMeasure {

    public Jaccard() {
        this.name = "Jaccard Index";
        this.short_name = "Jacc";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        if (t.getTp() + t.getFn() + t.getFp() == 0) {
            value = 0;
        } else {
            value = (double) t.getTp() / (double) (t.getTp() + t.getFn() + t.getFp());
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
        Jaccard a = new Jaccard();
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
