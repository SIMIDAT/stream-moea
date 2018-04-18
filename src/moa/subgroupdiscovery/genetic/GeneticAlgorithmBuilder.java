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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.criteria.ReinitialisationCriteria;
import moa.subgroupdiscovery.genetic.criteria.StoppingCriteria;
import moa.subgroupdiscovery.genetic.dominancecomparators.DominanceComparator;
import moa.subgroupdiscovery.genetic.evaluators.Evaluator;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import moa.subgroupdiscovery.genetic.operators.MutationOperator;
import moa.subgroupdiscovery.genetic.operators.SelectionOperator;
import moa.subgroupdiscovery.genetic.operators.crossover.TwoPointCrossoverCAN;
import moa.subgroupdiscovery.genetic.operators.crossover.TwoPointCrossoverDNF;
import moa.subgroupdiscovery.genetic.operators.initialisation.BiasedInitialisationCAN;
import moa.subgroupdiscovery.genetic.operators.initialisation.RandomInitialisationCAN;
import moa.subgroupdiscovery.genetic.operators.initialisation.RandomInitialisationDNF;
import moa.subgroupdiscovery.genetic.operators.mutation.BiasedMutationCAN;
import moa.subgroupdiscovery.genetic.operators.mutation.BiasedMutationDNF;
import moa.subgroupdiscovery.genetic.operators.selection.BinaryTournamentSelection;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import moa.subgroupdiscovery.qualitymeasures.WRAccNorm;

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
    public GeneticAlgorithmBuilder setPopulationLength(int long_poblacion) {
        this.long_poblacion = long_poblacion;
        return this;
    }

    /**
     * @param prob_crossover the prob_crossover to set
     */
    public GeneticAlgorithmBuilder setCrossoverProbability(float prob_crossover) {
        this.prob_crossover = prob_crossover;
        return this;
    }

    /**
     * @param prob_mutation the prob_mutation to set
     */
    public GeneticAlgorithmBuilder setMutationProbability(float prob_mutation) {
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
    public GeneticAlgorithmBuilder setDominanceComparator(DominanceComparator ranking) {
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
        
        String WARN = "WARNING: ";
        String ERR = "ERROR: ";
        
        if(this.long_poblacion < 3){
            System.err.println(WARN + "Population length < 3. Default population lenght has been set value: 51");
            this.long_poblacion = 51;
        }
        
        if(this.prob_crossover < 0 || this.prob_crossover > 1){
            System.err.println(WARN + "Crossover probability greater than 1 or lower than 0. Default crossover probability has been set: 0.6");
            this.prob_crossover = 0.6f;
        }
        
        if(this.prob_mutation <0 || this.prob_mutation > 1){
            System.err.println(WARN + "Mutation probability greater than 1 or lower than 0. Default mutation probability has been set: 0.1");
            this.prob_mutation = 0.1f;
        }
        
       if(this.baseElement == null){
           // If base element is null, we must stop.
           System.err.println(ERR + "Base element not set. Please, set one.");
           System.exit(2);
       }
        
        GeneticAlgorithm ga = new GeneticAlgorithm(this.long_poblacion, this.prob_crossover, this.prob_mutation, this.elitism, this.baseElement, StreamMOEAEFEP.header.numClasses());
        
        if (crossover != null) {
            ga.setCrossover(crossover);
        } else {
            if(baseElement instanceof IndCAN){
                ga.setCrossover(new TwoPointCrossoverCAN());
            } else {
                ga.setCrossover(new TwoPointCrossoverDNF());
            }
            System.err.println(WARN + "Crossover not set. Setting two point crossover by default.");
        }

        if (diversity != null) {
            ga.setDiversity(diversity);
        } else {
            ga.setDiversity(new WRAccNorm());
            System.err.println(WARN + "Diversity measure not set. Setting the default: WRACC_Norm");
        }

        if (evaluator != null) {
            ga.setEvaluator(evaluator);
        } else {
            System.err.println(ERR + "Evaluator has not been set. Please, set one.");
            System.exit(2);
        }

        if (initialisation != null) {
            ga.setInitialisation(initialisation);
        } else {
           if(baseElement instanceof IndCAN){
               ga.setInitialisation(new RandomInitialisationCAN((IndCAN) baseElement));
           } else {
               ga.setInitialisation(new RandomInitialisationDNF((IndDNF) baseElement));
           }
            System.err.println(WARN + "Initialisation has not been set. Default random initialisation is set.");
        }

        if (mutation != null) {
            ga.setMutation(mutation);
        } else {
            if(baseElement instanceof IndCAN){
                ga.setMutation(new BiasedMutationCAN());
            } else {
                ga.setMutation(new BiasedMutationDNF());
            }
            System.err.println(WARN + "Mutation operator has not been set. Default biased mutation is set.");
        }
        
        ga.setRanking(ranking);
        ga.setReinitCriteria(reinitCriteria);
        ga.setReinitialisation(reinitialisation);

        if (selection != null) {
            ga.setSelection(selection);
        } else {
            ga.setSelection(new BinaryTournamentSelection());
            System.err.println(WARN + "Selection not set. Binary tournament selection has been set by default.");
            System.exit(2);
        }

        if (stopCriteria != null) {
            ga.setStopCriteria(stopCriteria);
        } else {
            System.err.println(ERR + "Stopping criterion not set. You must set one.");
            System.exit(2);
        }
        
        return ga;
    }
    
    

}
