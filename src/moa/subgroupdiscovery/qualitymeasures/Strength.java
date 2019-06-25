/*
 * The MIT License
 *
 * Copyright 2018 agvico.
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
import moa.tasks.TaskMonitor;
import org.core.exceptions.InvalidMeasureComparisonException;
import org.core.exceptions.InvalidRangeInMeasureException;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class Strength extends QualityMeasure {

    public Strength() {
        this.name = "Strength";
        this.short_name = "Str";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {

        // Strength = TPR ^ 2 / (tpr + fpr)
        GrowthRate gr = new GrowthRate();
        gr.calculateValue(t);

        TPR tpr = new TPR();
        FPR fpr = new FPR();
        tpr.calculateValue(t);
        fpr.calculateValue(t);
        if (gr.getValue() == Double.POSITIVE_INFINITY) {
            this.value = tpr.getValue();
        } else {
            this.value = Math.pow(tpr.getValue(), 2) / (tpr.getValue() + fpr.getValue());
        }
        return this.value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (value < 0.0 - THRESHOLD || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        Strength a = new Strength();
        a.name = this.name;
        a.setValue(this.value);

        return a;
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof Strength)) {
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

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public void getDescription(StringBuilder sb, int i) {
    }

}
