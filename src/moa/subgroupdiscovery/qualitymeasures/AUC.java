/*
 * The MIT License
 *
 * Copyright 2017 angel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package moa.subgroupdiscovery.qualitymeasures;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

/**
 *
 * @author angel
 */
public class AUC extends AbstractOptionHandler implements QualityMeasure{

    public double value;
    public String name = "Area Under the Curve (AUC)";
    
    @Override
    public double getValue(ContingencyTable t) {
        TPR tpr = new TPR();
        FPR fpr = new FPR();
        value = (1.0 + tpr.getValue(t) - fpr.getValue(t)) / 2.0;
        return value;
    }

    @Override
    public boolean validate(double value) {
        return value >= 0.0 && value <= 1.0;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
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
    public QualityMeasure clone()  {
        try {
            super.clone();
            AUC a = new AUC();
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
