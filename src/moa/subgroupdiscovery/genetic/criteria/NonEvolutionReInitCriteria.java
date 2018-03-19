/* 
 * The MIT License
 *
 * Copyright 2018 Ángel Miguel García Vico.
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
package moa.subgroupdiscovery.genetic.criteria;

import java.util.BitSet;
import java.util.Iterator;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Class that implements the non-evolution criteria to trigger the
 * re-initialisation procedure This operators triggers when the population does
 * not evolve for % of the total of evaluations in the genetic algorithm. It is
 * defined that a population does not evolve when it does not cover new
 * examples.
 *
 * @author Ángel Miguel García Vico <agvico at ujaen.es>
 * @since JDK 8.0
 *
 * Created on: 16-mar-2018
 */
public class NonEvolutionReInitCriteria extends ReinitialisationCriteria {

    /**
     * The evaluation of the population with the last change.
     */
    private long[] lastChange;

    /**
     * The maximum number of evaluation or generations
     */
    private final long maxValue;

    /**
     * The percentage of the maximum number of evals or generations to consider
     * the population as stagnant.
     */
    private final double pct;

    /**
     * The value of maximum difference to trigger the reinitialisation
     */
    private final long max;

    /**
     * The coverage population on the previous call.
     */
    private BitSet[] previousCovered;
    

    /**
     * Default Constructor
     * @param pct The percentage of maximum evaluations to consider the reinitialisation
     * @param max The maximum evaluation/generations in the genetic algorithm
     * @param numClass  The number of class of the problem.
     */
    public NonEvolutionReInitCriteria(double pct, long max, int numClass) {
        // TODO: It is necessary to add the previous population for all the classes.
        this.maxValue = max;
        this.pct = pct;
        this.max = ((Double) (maxValue * pct)).longValue();
        this.lastChange = new long[numClass];
        this.previousCovered = new BitSet[numClass];
    }

    @Override
    public boolean checkReinitialisationCondition(GeneticAlgorithm ga) {
        BitSet oldCovered;
        if (previousCovered[ga.getCurrentClass()] == null) {
            oldCovered = new BitSet(ga.getBaseElement().getCubre().size());
        } else {
            oldCovered = (BitSet) previousCovered[ga.getCurrentClass()].clone();
        }

        // First, get the BitSet of the covered examples for the whole population in this iteration
        previousCovered[ga.getCurrentClass()] = new BitSet(ga.getBaseElement().getCubre().size());
        for (Iterator it = ga.getPoblacOfCurrentClass().iterator(); it.hasNext();) {
            Individual ind = (Individual) it.next();
            if (ind.getRank() == 0) {
                previousCovered[ga.getCurrentClass()].or(ind.getCubre());
            }
        }

        // Now, compare if there are new covered examples.
        BitSet aux = new BitSet(oldCovered.size());
        aux.clear(0,oldCovered.size());
        aux.or(oldCovered);
        aux.xor(previousCovered[ga.getCurrentClass()]);
        oldCovered.flip(0,oldCovered.size());
        aux.and(oldCovered);
        
        if (aux.cardinality() > 0) {
            lastChange[ga.getCurrentClass()] = ga.getTrials();
            return false;
        }

        return ga.getTrials() - lastChange[ga.getCurrentClass()] >= max;
    }

    @Override
    public void resetCriterion(GeneticAlgorithm ga) {
        lastChange[ga.getCurrentClass()] = ga.getTrials();
        previousCovered[ga.getCurrentClass()] = new BitSet(ga.getBaseElement().getCubre().size());
    }

}
