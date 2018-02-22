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

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;


/**
 *
 * @author agvico
 */
public class WRAccNorm extends QualityMeasure {

    public WRAccNorm(){
        super.value = 0.0;
        super.name = "Weighter Relative Accuracy (Normalised)";
        super.short_name = "WRAcc_Norm";
    }
    
    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        double classPct = 0.0 ;
        if (t.getTotalExamples() != 0) {
            classPct = (double) (t.getTp() + t.getFn()) / (double) t.getTotalExamples();
        }
        
        double minUnus = (1.0 - classPct) * (0.0 - classPct);
        double maxUnus = classPct * (1.0 - classPct);

        if (maxUnus - minUnus != 0) {
            try {
                WRAcc unus = new WRAcc();
                unus.calculateValue(t);
                unus.validate();
                value = (unus.value - minUnus) / (maxUnus - minUnus);
            } catch (InvalidRangeInMeasureException ex) {
                ex.showAndExit(this);
            }
        } else {
            value = 0.0;
        }

        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (!(value >= 0.0 && value <= 1.0) || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public QualityMeasure clone() {
        WRAccNorm a = new WRAccNorm();
        a.name = this.name;
        a.value = this.value;

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

}
