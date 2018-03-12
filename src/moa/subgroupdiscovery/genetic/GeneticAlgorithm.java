/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import moa.subgroupdiscovery.Population;
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
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class GeneticAlgorithm<T extends Individual> implements Serializable, Runnable {

    private ArrayList<ArrayList<Individual<T>>> poblac;     // Main Population
    private ArrayList<ArrayList<Individual<T>>> best;       // Best population
    private ArrayList<ArrayList<Individual<T>>> offspring;  // Offspring population
    private ArrayList<ArrayList<Individual<T>>> union;      // Main+Offspring populations
    private ArrayList<ArrayList<Individual<T>>> result;      // Result population which will be returned to the user.

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
    private Individual<T> baseElement;
    
    
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

    @Override
    public void run() {
        
        // initialisation 
        poblac = new ArrayList<>();
        offspring = new ArrayList<>();
        union = new ArrayList<>();
        best = new ArrayList<>();
        
        // initialise poblac, one array for each class
        for(int i = 0; i < StreamMOEAEFEP.instancia.numClasses(); i++){
            ArrayList<Individual<T>> aux = new ArrayList<>();
            for(int j = 0; j < long_poblacion; j++){
                Individual a = initialisation.doInitialisation(baseElement);
                a.setClas(i);
                aux.add(a.clone());
            }
            poblac.add(aux);
        }
        
        
        Trials = poblac.stream().mapToInt(p -> p.size()).sum();  // The trials are the number of individuals (no individuals are evaluated yet)
        Gen = 1;
        
        // First evaluation of the whole population;
        poblac.forEach(pop ->{
            pop.forEach(ind -> {
               evaluator.doEvaluation(ind, true);
            });
        });

        // Genetic Algorithm evolutionary cycle
        do {
            // For each class
            for(int i = 0; i < StreamMOEAEFEP.instancia.numClasses(); i++){
                
                offspring.get(i).clear();
                // Selection
                ArrayList<Individual> selected = new ArrayList<>();
                for(int j = 0; j < long_poblacion; j++){
                    selected.add(selection.doSelection(poblac.get(i)));
                }
                
                // Crossover
                for(int j = 0; j < selected.size(); j += 2){
                    ArrayList<Individual> cross = new ArrayList<>();
                    cross.add(selected.get(j));
                    cross.add(selected.get(j + 1));
                    if(Randomize.RanddoubleClosed(0.0, 1.0) <= prob_crossover){
                        offspring.get(i).addAll(crossover.doCrossover(cross));
                    }
                }
            }
        } while (!stopCriteria.checkStopCondition(this));
    }

    /**
     * @return the n_eval
     */
    public int getN_eval() {
        return n_eval;
    }

    /**
     * @return the Gen
     */
    public int getGen() {
        return Gen;
    }

    /**
     * @return the Trials
     */
    public int getTrials() {
        return Trials;
    }

    /**
     * @return the StrictDominance
     */
    public boolean isStrictDominance() {
        return StrictDominance;
    }

    /**
     * @param StrictDominance the StrictDominance to set
     */
    public void setStrictDominance(boolean StrictDominance) {
        this.StrictDominance = StrictDominance;
    }

    /**
     * @return the diversity
     */
    public QualityMeasure getDiversity() {
        return diversity;
    }

    /**
     * @param diversity the diversity to set
     */
    public void setDiversity(QualityMeasure diversity) {
        this.diversity = diversity;
    }

    /**
     * @return the evaluator
     */
    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * @param evaluator the evaluator to set
     */
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * @return the ranking
     */
    public DominanceComparator getRanking() {
        return ranking;
    }

    /**
     * @param ranking the ranking to set
     */
    public void setRanking(DominanceComparator ranking) {
        this.ranking = ranking;
    }

    /**
     * @return the crossover
     */
    public CrossoverOperator getCrossover() {
        return crossover;
    }

    /**
     * @param crossover the crossover to set
     */
    public void setCrossover(CrossoverOperator crossover) {
        this.crossover = crossover;
    }

    /**
     * @return the mutation
     */
    public MutationOperator getMutation() {
        return mutation;
    }

    /**
     * @param mutation the mutation to set
     */
    public void setMutation(MutationOperator mutation) {
        this.mutation = mutation;
    }

    /**
     * @return the selection
     */
    public SelectionOperator getSelection() {
        return selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(SelectionOperator selection) {
        this.selection = selection;
    }

    /**
     * @return the initialisation
     */
    public InitialisationOperator getInitialisation() {
        return initialisation;
    }

    /**
     * @param initialisation the initialisation to set
     */
    public void setInitialisation(InitialisationOperator initialisation) {
        this.initialisation = initialisation;
    }

    /**
     * @return the reinitialisation
     */
    public InitialisationOperator getReinitialisation() {
        return reinitialisation;
    }

    /**
     * @param reinitialisation the reinitialisation to set
     */
    public void setReinitialisation(InitialisationOperator reinitialisation) {
        this.reinitialisation = reinitialisation;
    }

    /**
     * @return the stopCriteria
     */
    public StoppingCriteria getStopCriteria() {
        return stopCriteria;
    }

    /**
     * @param stopCriteria the stopCriteria to set
     */
    public void setStopCriteria(StoppingCriteria stopCriteria) {
        this.stopCriteria = stopCriteria;
    }

    /**
     * @return the reinitCriteria
     */
    public ReinitialisationCriteria getReinitCriteria() {
        return reinitCriteria;
    }

    /**
     * @param reinitCriteria the reinitCriteria to set
     */
    public void setReinitCriteria(ReinitialisationCriteria reinitCriteria) {
        this.reinitCriteria = reinitCriteria;
    }

    /**
     * @return the result
     */
    public ArrayList<ArrayList<Individual<T>>> getResult() {
        return result;
    }
    
    
    

    
    
    
}
