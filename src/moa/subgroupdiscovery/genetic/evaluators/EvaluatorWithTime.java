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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Evaluator of individuals which considers the appearance of the individuals on
 * previous timestamps
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @param <T> The base evaluator without considering this time constraint
 * @since JDK 8.0
 */
public class EvaluatorWithTime<T extends Evaluator> extends Evaluator<Individual> {

    /**
     * The main structure of this class, it stores the appearance of all
     * individuals throught the execution of the method.
     */
    protected HashMap<Individual, ArrayDeque<Boolean>> appearance;

    /**
     * The base evaluator
     */
    protected T mainEvaluator;

    /**
     * The maximum time
     */
    private int maxTime;

    /**
     * Default constructor
     *
     * @param data The data to be used in the evaluation
     * @param evaluator The base evaluator to calculate the measures without the
     * time constraint
     * @param maximumAppearance the number of appearances to be stores in the
     * queues of individuals
     */
    public EvaluatorWithTime(ArrayList<Instance> data, T evaluator, int maximumAppearance) {
        super(data);
        this.mainEvaluator = evaluator;
        appearance = new HashMap<>();
        maxTime = maximumAppearance;
    }

    private void updateAppearance(ArrayList<Individual> population, int clas) {
        // Remove duplicated to avoid add values twice or more.
        HashSet<Individual> a = new HashSet<>();
        a.addAll(population);
        ArrayList<Individual> toProcess = new ArrayList<>();
        toProcess.addAll(a);
        // All individuals in population are present in the previous population. The remaining, not.
        for (Individual ind : toProcess) {
            ArrayDeque<Boolean> values = appearance.get(ind);
            if (values != null) {
                System.out.println("He entrado! ");
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
            if ((!population.contains(ind)) && clas == ind.getClas()) {
                System.out.println("Polizon!!");
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
            if (!ind.isEvaluado()) {
                mainEvaluator.doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            }
        }

        if (isTrain) {
            // Now, apply the decay factor to all individuals in the population
            for (Individual ind : sample) {
                ArrayDeque<Boolean> aux = appearance.get(ind);
                if (aux != null) {
                    Iterator<Boolean> iterator = aux.iterator();

                    int exponent = -1;
                    double decayFactor = Math.pow(2, -1 * maxTime);

                    // Calculate the decay factor of this individual
                    while (iterator.hasNext()) {
                        if (iterator.next()) {
                            // The element was present, add to the decay factor
                            decayFactor += Math.pow(2, exponent);
                        }
                        exponent--;
                    }

                    // Now, get indivual's objectives and apply the dacay factor to all of them
                    for (QualityMeasure obj : (ArrayList<QualityMeasure>) ind.getObjs()) {
                        obj.setValue(obj.getValue() * decayFactor);
                    }
                }
            }

            // Once applied the decay factor, update the time structure, adding the new individuals.
            updateAppearance(sample, GA.getCurrentClass());
        }
    }

}
