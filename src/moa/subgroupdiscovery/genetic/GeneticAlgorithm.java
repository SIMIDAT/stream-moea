/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic;

import java.util.ArrayList;
import moa.subgroupdiscovery.Population;
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
 *
 * @author agvico
 */
public class GeneticAlgorithm<T extends Individual> {

    private ArrayList<ArrayList<Individual<T>>> poblac;     // Main Population
    private ArrayList<ArrayList<Individual<T>>> best;       // Best population
    private ArrayList<ArrayList<Individual<T>>> offspring;  // Offspring population
    private ArrayList<ArrayList<Individual<T>>> union;      // Main+Offspring populations

    private int long_poblacion;   // Number of individuals of the population
    private int n_eval;           // Number of evaluations per ejecution
    private float prob_crossover;     // Cross probability
    private float prob_mutation;  // Mutation probability
    private int Gen;		  // Number of generations performed by the GA
    private int Trials;		  // Number of evaluated chromosomes

    private boolean StrictDominance; // Use strict dominance in the dominance comparison
    
    private float porcCob = 1;          // Biased initialization for individuals in ReInitCob
    private float minCnf = 0;
    private QualityMeasure diversity; // The diversity quality measures to use
    
    
    
    /**********
     * ELEMENTS OF THE GENETIC ALGORITHM
     */
    
    Evaluator evaluator;
    DominanceComparator ranking;
    CrossoverOperator crossover;
    MutationOperator mutation;
    SelectionOperator selection;
    InitialisationOperator initialisation;
    StoppingCriteria stopCriteria;
    ReinitialisationCriteria reinitCriteria;
    

}
