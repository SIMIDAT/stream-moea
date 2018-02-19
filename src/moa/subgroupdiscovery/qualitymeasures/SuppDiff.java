/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

/**
 *
 * @author agvico
 */
public class SuppDiff extends AbstractOptionHandler implements QualityMeasure{
    
    public String name = "Support Difference";
    public double value;
    
    @Override
    protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public double getValue(ContingencyTable t) {
        TPR tpr = new TPR();
        FPR fpr = new FPR();
        
        value = tpr.getValue(t) - fpr.getValue(t);
        return value;
    }

    @Override
    public boolean validate(double value) {
        return value >= -1.0 && value <= 1.0;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QualityMeasure clone() {
       try {
            super.clone();
            SuppDiff a = new SuppDiff();
            a.name = this.name;
            a.value = this.value;
            
            return a;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(AUC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error ocurred when cloning the quality measure: " + name);
        }
        return null;
    }
    
    
    
}
