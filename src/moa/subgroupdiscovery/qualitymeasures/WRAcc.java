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

import org.core.exceptions.InvalidRangeInMeasureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;

/**
 *
 * @author agvico
 */
public final class WRAcc extends QualityMeasure {

    public WRAcc() {
        super.name = "Weighted Relative Accuracy";
        super.short_name = "WRAcc";
        super.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        try {
            // Calculate the coverage
            double cov = 0.0; // Change with Coverage class when it is available
            if (t.getTotalExamples() != 0) {
                cov = (double) (t.getTp() + t.getFp()) / (double) t.getTotalExamples();
            }

            // Calculate the confidence
            Confidence conf = new Confidence();
            conf.calculateValue(t);
            conf.validate();

            // Calculate the class percentage with respect to the total examples
            double class_pct = 0.0;
            if (t.getTotalExamples() != 0) {
                class_pct = (double) (t.getTp() + t.getFn()) / (double) t.getTotalExamples();
            }

            // Calculate the value
            value = cov * (conf.value - class_pct);
        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public QualityMeasure clone() {
        WRAcc a = new WRAcc();
        a.name = this.name;
        a.value = this.value;

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof WRAcc)) {
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
        return 0;
    }

}
