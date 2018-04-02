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
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.Pair;

/**
 * Evaluator which consider, in addition to the appearance, the previous values
 * of the objectives measurements of the individual.
 *
 * Let X be the value of an objective measure in the current timestamp, then the
 * new X for this timestamp is:
 *
 * {@code X = X + X_t-i * 2^-i} if and only if {@code i} is equal to true.
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @param <T> The base evaluator (CAN or DNF)
 * @since JDK 8.0
 */
public class EvaluatorWithTimeByMeasure<T extends Evaluator> extends EvaluatorWithTime<T, Pair<Boolean, ArrayList<QualityMeasure>>> {

    public EvaluatorWithTimeByMeasure(ArrayList<Instance> data, T evaluator, int maximumAppearance) {
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
            ArrayDeque<Pair<Boolean, ArrayList<QualityMeasure>>> values = appearance.get(ind);
            
            // This function is always called after the test phase. We stored the objective test values as
            // the values of the QMs of the previous timestamps. This is because the objs array is modified
            // due to the evaluation process that takes into account the previous timestamps.
            ArrayList<QualityMeasure> objectiveValues = new ArrayList<>();
            for(QualityMeasure q : (ArrayList<QualityMeasure>) ind.getMeasures()){
                for(QualityMeasure p : StreamMOEAEFEP.getObjectivesArray()){
                    if(q.getClass().equals(p.getClass())){
                        objectiveValues.add(q.clone());
                    }
                }
            }
            
            if (values != null) {
                Pair<Boolean, ArrayList<QualityMeasure>> pair = new Pair<>(Boolean.TRUE, objectiveValues);
                // the indivual was previously added. Update the structure
                values.addFirst(pair);
                if (values.size() > maxTime) {
                    values.removeLast();
                }
            } else {
                // new Individual. Add it to the hashmap
                ArrayDeque<Pair<Boolean, ArrayList<QualityMeasure>>> toAdd = new ArrayDeque<>(maxTime);
                toAdd.add(new Pair<>(Boolean.TRUE, objectiveValues));
                appearance.put(ind, toAdd);
            }
        }

        // Now, the remaining individuals in the hashmap does not appear in this timestamp. Update it.
        Set<Individual> notPresent = appearance.keySet();
        //notPresent.removeAll(population);

        for (Individual ind : notPresent) {
            if (!population.contains(ind)) {
                ArrayDeque<Pair<Boolean, ArrayList<QualityMeasure>>> values = appearance.get(ind);
                values.addFirst(new Pair<>(Boolean.FALSE, null));
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
            //if (!ind.isEvaluated()) { // TODO: Check whether you can avoid the evaluation of the whole population
                mainEvaluator.doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            //}
        }

        if (isTrain) {
            // Now, apply the decay factor to all individuals in the population
            for (Individual ind : sample) {
                ArrayDeque<Pair<Boolean, ArrayList<QualityMeasure>>> aux = appearance.get(ind);
                if (aux != null) {
                    Iterator<Pair<Boolean, ArrayList<QualityMeasure>>> iterator = aux.iterator();

                    // Creates the new objectives array for the individual.
                    ArrayList<QualityMeasure> newObjs = new ArrayList<>();
                    for (QualityMeasure q : (ArrayList<QualityMeasure>) ind.getObjs()) {
                        newObjs.add(q.clone());
                    }

                    int exponent = -1;
                    // now, for each element in the deque, if individual appeared on previous timestamps, 
                    // The new objective value is the value of the objectives in the current timestamp plus the previous objectives values with their decai factor
                    while (iterator.hasNext()) {
                        Pair<Boolean, ArrayList<QualityMeasure>> element = iterator.next();
                        if (element.getKey()) {
                            ArrayList<QualityMeasure> prevObjs = element.getValue();
                            // The element was present, add to the decay factor
                            for (int i = 0; i < prevObjs.size(); i++) {
                                newObjs.get(i).setValue(newObjs.get(i).getValue() + prevObjs.get(i).getValue() * Math.pow(2, exponent));
                            }
                        }
                        exponent--;
                    }

                    // Now, the new objective values for this individual are stored in newObjs
                    ind.setObjs(newObjs);

                }
            }

            // Once applied the decay factor, update the time structure, adding the new individuals.
            //updateAppearance(sample, GA.getCurrentClass());
        }
    }

}
