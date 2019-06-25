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
package moa.subgroupdiscovery.genetic.filters;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class MeasureFilter extends Filter<Individual> {

    /**
     * The quality measure that defines the filter
     */
    QualityMeasure qualityForFiltering;

    public MeasureFilter(QualityMeasure measure, double threshold) {
        super(threshold);
        qualityForFiltering = measure;
    }

    @Override
    public ArrayList<Individual> doFilter(ArrayList<Individual> population, GeneticAlgorithm<Individual> ga) {
        ArrayList<Individual> rules = new ArrayList<>();
        int index = -1;
        boolean found = false;
        for (int i = 0; i < population.get(0).getMeasures().size() && !found; i++) {
            QualityMeasure q = (QualityMeasure) population.get(0).getMeasures().get(i);
            if (q.getClass().equals(qualityForFiltering.getClass())) {
                index = i;
                found = true;
            }
        }

        for (Individual ind : population) {
            if (((QualityMeasure) ind.getMeasures().get(index)).getValue() >= super.threshold) {
                rules.add(ind);
            }
        }
        return null;
    }

}
