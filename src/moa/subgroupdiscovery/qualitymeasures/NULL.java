/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

/**
 *
 * @author agvico
 */
public class NULL extends AbstractOptionHandler implements QualityMeasure{

    public double value;
    public String name = "NULL";
    
    @Override
    protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public double getValue(ContingencyTable t) {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public boolean validate(double value) {
        return true;
    }
    
}
