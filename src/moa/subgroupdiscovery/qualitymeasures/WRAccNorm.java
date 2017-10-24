/*
 * The MIT License
 *
 * Copyright 2017 agvico.
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

import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

/**
 *
 * @author agvico
 */
public class WRAccNorm  extends AbstractOptionHandler implements QualityMeasure{
    
    public String name = "Normalized WRAcc";
    public double value;
    
    @Override
    public double getValue(ContingencyTable t) {
        double classPct = (double) (t.getTp() + t.getFn()) / t.getTotalExamples();
        double minUnus = (1.0 - classPct) * (0.0 - classPct);
        double maxUnus = classPct * (1.0 - classPct);
        
        if(maxUnus - minUnus != 0){
            WRAcc unus = new WRAcc();
            value = (unus.getValue(t) - minUnus) / (maxUnus - minUnus);
        } else {
            value =  0.0;
        }
        
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
    
}
