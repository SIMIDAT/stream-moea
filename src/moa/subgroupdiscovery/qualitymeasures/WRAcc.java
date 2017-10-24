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

import com.github.javacliparser.Options;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.OptionHandler;
import moa.tasks.TaskMonitor;



/**
 *
 * @author agvico
 */
public class WRAcc extends AbstractOptionHandler implements QualityMeasure{

    @Override
    public double getValue(ContingencyTable t) {
        double cov = (double) (t.getTp() + t.getFp()) / t.getTotalExamples();
        double conf = (double) t.getTp() / (double) (t.getTp() + t.getFp());
        double class_pct = (double)(t.getTp() + t.getFn()) / t.getTotalExamples();
        
        return cov * (conf - class_pct);
    }

    @Override
    public boolean validate(double value) {
       return true;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }
    
    
}
