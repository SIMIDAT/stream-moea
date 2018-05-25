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
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Evaluator which only considers wheter the individual appears or not in
 * previous timestamps.
 *
 * The evaluation of the individuals is the following: let X be the value of any
 * objective measure computed by the base evaluator {@code T} in the current
 * timestamp t. Then, for each value from t-1 to t-{@code maxTime} it is
 * calculated:
 *
 * {@code decayFactor = 1 - sumatory(2^-i) for each i whose value is false; X *= decayfactor}
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @param <T> The base evaluator
 * @since JDK 8.0
 */
public class EvaluatorWithDecayBasedOnPresence<T extends Evaluator> extends EvaluatorWithDecay<T, Boolean> {

    public EvaluatorWithDecayBasedOnPresence(ArrayList<Instance> data, T evaluator, int maximumAppearance) {
        super(data, evaluator, maximumAppearance);
    }

    @Override
    public void updateAppearance(ArrayList<Individual> population, GeneticAlgorithm GA) {
        // Remove duplicated to avoid add values twice or more.
        HashSet<Individual> a = new HashSet<>();
        a.addAll(population);
        ArrayList<Individual> toProcess = new ArrayList<>();
        toProcess.addAll(a);

        // All individuals in population are present in the previous population. The remaining, not.
        for (Individual ind : toProcess) {
            ArrayDeque<Boolean> values = appearance.get(ind);
            if (values != null) {
                // the indivual was previously added. Update the structure
                values.addFirst(Boolean.TRUE);
                if (values.size() > maxTime) {
                    values.removeLast();
                }
            } else {
                // new Individual. Add it to the hashmap
                ArrayDeque<Boolean> toAdd = new ArrayDeque<>(maxTime);
                toAdd.add(Boolean.TRUE);
                appearance.put(ind, toAdd);
            }
        }

        // Now, the remaining individuals in the hashmap does not appear in this timestamp. Update it.
        Set<Individual> notPresent = appearance.keySet();
        //notPresent.removeAll(population);

        for (Individual ind : notPresent) {
            if (!population.contains(ind)) {
                ArrayDeque<Boolean> values = appearance.get(ind);
                values.addFirst(Boolean.FALSE);
                if (values.size() > maxTime) {
                    values.removeLast();
                }
            }
        }
    }

    @Override
    public void doEvaluation(Individual sample, boolean isTrain) {
        // Do the evaluation of the base evaluator. This is used only for test purposes
        mainEvaluator.doEvaluation(sample, isTrain);
    }

    @Override
    public void doEvaluation(ArrayList<Individual> sample, boolean isTrain, GeneticAlgorithm<Individual> GA) {
        // First, it evaluates all individuals
        for (Individual ind : sample) {
            if (!ind.isEvaluated()) {
                mainEvaluator.doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            }
        }

        if (isTrain) {
            // Now, apply the decay factor to all individuals in the population
            for (Individual ind : sample) {
                if (!ind.isEmpty()) {
                    ArrayDeque<Boolean> aux = appearance.get(ind);
                    if (aux != null) {
                        Iterator<Boolean> iterator = aux.iterator();

                        int exponent = -1;
                        double decayFactor = 1.0;

                        // Calculate the decay factor of this individual
                        while (iterator.hasNext()) {
                            if (!iterator.next()) {
                                // The element was present, add to the decay factor
                                decayFactor -= Math.pow(2, exponent);
                            }
                            exponent--;
                        }

                        // Now, get indivual's objectives and apply the dacay factor to all of them
                        for (QualityMeasure obj : (ArrayList<QualityMeasure>) ind.getObjs()) {
                            if (obj.getValue() >= 0) {
                                obj.setValue(obj.getValue() * decayFactor);
                            } else {
                                // if the value is negative, to make it worst we need to divide
                                obj.setValue(obj.getValue() / decayFactor);
                            }
                        }
                    }
                }
            }
            // Once applied the decay factor, update the time structure, adding the new individuals.
            //updateAppearance(sample, GA.getCurrentClass());
        }
    }

    @Override
    public void setData(ArrayList<Instance> data) {
        super.data = data;
        mainEvaluator.setData(data);
    }

}
