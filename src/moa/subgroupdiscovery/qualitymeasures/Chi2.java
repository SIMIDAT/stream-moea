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
public class Chi2 extends QualityMeasure {

    public Chi2() {
        this.name = "Chi-Squared";
        this.short_name = "Chi2";
        this.value = 0.0;
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        ContingencyTable expected = new ContingencyTable(0, 0, 0, 0);
        int Real_P = t.getTp() + t.getFn();
        int Real_N = t.getFp() + t.getTn();
        int Pred_P = t.getTp() + t.getFp();
        int Pred_N = t.getFn() + t.getTn();
        double T = t.getFn() + t.getFp() + t.getTn() + t.getTp();

        double[][] observed = {{t.getTp(), t.getFn()}, {t.getFp(), t.getTn()}};
        double[][] expt = {{(Real_P * Pred_P) / T, (Real_P * Pred_N) / T}, {(Real_N * Pred_P) / T, (Real_N * Pred_N) / T}};

        double chiSquared = 0.0;

        for (int i = 0; i < observed.length; i++) {
            for (int j = 0; j < observed[i].length; j++) {
                double num = Math.pow(observed[i][j] - expt[i][j], 2);
                chiSquared += num / expt[i][j];
            }
        }

        this.value = chiSquared;
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        Chi2 a = new Chi2();
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
