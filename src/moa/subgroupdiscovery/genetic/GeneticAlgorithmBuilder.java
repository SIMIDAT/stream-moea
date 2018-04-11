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
package moa.subgroupdiscovery.genetic;

import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.criteria.ReinitialisationCriteria;
import moa.subgroupdiscovery.genetic.criteria.StoppingCriteria;
import moa.subgroupdiscovery.genetic.dominancecomparators.DominanceComparator;
import moa.subgroupdiscovery.genetic.evaluators.Evaluator;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import moa.subgroupdiscovery.genetic.operators.MutationOperator;
import moa.subgroupdiscovery.genetic.operators.SelectionOperator;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Builder class of genetic algorithm.
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class GeneticAlgorithmBuilder<T extends Individual> {

    private int long_poblacion;   // Number of individuals of the population
    private float prob_crossover;     // Cross probability
    private float prob_mutation;  // Mutation probability

    private boolean elitism;        // Use the elite population or not

    private QualityMeasure diversity; // The diversity quality measures to use
    private T baseElement;
    private int currentClass;

    /**
     * ELEMENTS OF THE GENETIC ALGORITHM
     */
    private Evaluator evaluator;
    private DominanceComparator ranking;
    private CrossoverOperator crossover;
    private MutationOperator mutation;
    private SelectionOperator selection;
    private InitialisationOperator initialisation;
    private InitialisationOperator reinitialisation;
    private StoppingCriteria stopCriteria;
    private ReinitialisationCriteria reinitCriteria;

    /**
     * @param long_poblacion the long_poblacion to set
     */
    public GeneticAlgorithmBuilder setLong_poblacion(int long_poblacion) {
        this.long_poblacion = long_poblacion;
        return this;
    }

    /**
     * @param prob_crossover the prob_crossover to set
     */
    public GeneticAlgorithmBuilder setProb_crossover(float prob_crossover) {
        this.prob_crossover = prob_crossover;
        return this;
    }

    /**
     * @param prob_mutation the prob_mutation to set
     */
    public GeneticAlgorithmBuilder setProb_mutation(float prob_mutation) {
        this.prob_mutation = prob_mutation;
        return this;
    }

    /**
     * @param elitism the elitism to set
     */
    public GeneticAlgorithmBuilder setElitism(boolean elitism) {
        this.elitism = elitism;
        return this;
    }

    /**
     * @param diversity the diversity to set
     */
    public GeneticAlgorithmBuilder setDiversity(QualityMeasure diversity) {
        this.diversity = diversity;
        return this;
    }

    /**
     * @param baseElement the baseElement to set
     */
    public GeneticAlgorithmBuilder setBaseElement(T baseElement) {
        this.baseElement = baseElement;
        return this;
    }

    /**
     * @param currentClass the currentClass to set
     */
    public GeneticAlgorithmBuilder setCurrentClass(int currentClass) {
        this.currentClass = currentClass;
        return this;
    }

    /**
     * @param evaluator the evaluator to set
     */
    public GeneticAlgorithmBuilder setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    /**
     * @param ranking the ranking to set
     */
    public GeneticAlgorithmBuilder setRanking(DominanceComparator ranking) {
        this.ranking = ranking;
        return this;
    }

    /**
     * @param crossover the crossover to set
     */
    public GeneticAlgorithmBuilder setCrossover(CrossoverOperator crossover) {
        this.crossover = crossover;
        return this;
    }

    /**
     * @param mutation the mutation to set
     */
    public GeneticAlgorithmBuilder setMutation(MutationOperator mutation) {
        this.mutation = mutation;
        return this;
    }

    /**
     * @param selection the selection to set
     */
    public GeneticAlgorithmBuilder setSelection(SelectionOperator selection) {
        this.selection = selection;
        return this;
    }

    /**
     * @param initialisation the initialisation to set
     */
    public GeneticAlgorithmBuilder setInitialisation(InitialisationOperator initialisation) {
        this.initialisation = initialisation;
        return this;
    }

    /**
     * @param reinitialisation the reinitialisation to set
     */
    public GeneticAlgorithmBuilder setReinitialisation(InitialisationOperator reinitialisation) {
        this.reinitialisation = reinitialisation;
        return this;
    }

    /**
     * @param stopCriteria the stopCriteria to set
     */
    public GeneticAlgorithmBuilder setStopCriteria(StoppingCriteria stopCriteria) {
        this.stopCriteria = stopCriteria;
        return this;
    }

    /**
     * @param reinitCriteria the reinitCriteria to set
     */
    public GeneticAlgorithmBuilder setReinitCriteria(ReinitialisationCriteria reinitCriteria) {
        this.reinitCriteria = reinitCriteria;
        return this;
    }

    /**
     * It builds the genetic algorithm set in this builder.
     *
     * @return
     */
    public GeneticAlgorithm build() {

        GeneticAlgorithm ga = new GeneticAlgorithm(this.long_poblacion, this.prob_crossover, this.prob_mutation, this.elitism, this.baseElement, StreamMOEAEFEP.header.numClasses());
        
        if (crossover != null) {
            ga.setCrossover(crossover);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Crossover is null.");
            System.exit(2);
        }

        if (diversity != null) {
            ga.setDiversity(diversity);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Diversity measure is null.");
            System.exit(2);
        }

        if (evaluator != null) {
            ga.setEvaluator(evaluator);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Evaluator is null.");
            System.exit(2);
        }

        if (initialisation != null) {
            ga.setInitialisation(initialisation);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Initialisation is null.");
            System.exit(2);
        }

        if (mutation != null) {
            ga.setMutation(mutation);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Mutation is null.");
            System.exit(2);
        }

        ga.setRanking(ranking);
        ga.setReinitCriteria(reinitCriteria);
        ga.setReinitialisation(reinitialisation);

        if (selection != null) {
            ga.setSelection(selection);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Selection operator is null.");
            System.exit(2);
        }

        if (stopCriteria != null) {
            ga.setStopCriteria(stopCriteria);
        } else {
            System.err.println("ERROR on building Genetic Algorithm: Stop Criteria is null.");
            System.exit(2);
        }
        
        return ga;
    }

}
