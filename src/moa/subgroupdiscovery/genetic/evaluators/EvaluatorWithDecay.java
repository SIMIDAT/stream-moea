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
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Evaluator of individuals which considers the appearance of the individuals on
 * previous timestamps
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @param <T> The base evaluator without considering this time constraint
 * @param <E> Additional class which consider a relevant property of the individuals thorought time. 
 *             For example, it could be {@code Boolean} to represent whether the individual appears or not
 *             in previous timestamps.
 * @since JDK 8.0
 */
public abstract class EvaluatorWithDecay<T extends Evaluator, E> extends Evaluator<Individual> {

    /**
     * The main structure of this class, it stores the appearance of all
     * individuals throught the execution of the method.
     */
    protected HashMap<Individual, ArrayDeque<E>> appearance;

    /**
     * The base evaluator
     */
    protected T mainEvaluator;

    /**
     * The maximum time
     */
    protected int maxTime;

    /**
     * Default constructor
     *
     * @param data The data to be used in the evaluation
     * @param evaluator The base evaluator to calculate the measures without the
     * time constraint
     * @param maximumAppearance the number of appearances to be stores in the
     * queues of individuals
     */
    public EvaluatorWithDecay(ArrayList<Instance> data, T evaluator, int maximumAppearance) {
        super(data);
        this.mainEvaluator = evaluator;
        appearance = new HashMap<>();
        maxTime = maximumAppearance;
    }

    /**
     * It updates the structure that store tha appearance of individuals throughout time
     * 
     * @param population
     * @param GA 
     */
    public abstract void updateAppearance(ArrayList<Individual> population, GeneticAlgorithm GA); 

    @Override
    public abstract void doEvaluation(Individual sample, boolean isTrain); 

    @Override
    public abstract void doEvaluation(ArrayList<Individual> sample, boolean isTrain, GeneticAlgorithm<Individual> GA); 

}
