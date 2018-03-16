/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic;

import java.io.Serializable;
import java.util.ArrayList;
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
 * Class that implements a Multi-objective genetic algorithm
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 * @param <T> The individuals that forms the population
 */
public class GeneticAlgorithm<T extends Individual> implements Serializable, Runnable {

    private final ArrayList<ArrayList<T>> poblac;     // Main Population
    private ArrayList<ArrayList<T>> elite;       // Elite population
    private final ArrayList<ArrayList<T>> offspring;  // Offspring population
    private final ArrayList<ArrayList<T>> union;      // Main+Offspring populations
    private ArrayList<T> result;      // Result population which will be returned to the user.

    protected final int long_poblacion;   // Number of individuals of the population
    protected final float prob_crossover;     // Cross probability
    protected final float prob_mutation;  // Mutation probability
    private long Gen;		  // Number of generations performed by the GA
    private long Trials;		  // Number of evaluated chromosomes

    protected boolean elitism;        // Use the elite population or not

    private QualityMeasure diversity; // The diversity quality measures to use
    private final T baseElement;
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
     * Default constructor
     * @param popLength the population length
     * @param crossPct the crossover probability [0,1]
     * @param mutationPct the mutation probability [0,1]
     * @param elitism Is elitism available?
     * @param baseElement the base element of the population
     */
    public GeneticAlgorithm(int popLength, float crossPct, float mutationPct, boolean elitism, T baseElement) {
        this.long_poblacion = popLength;
        this.prob_crossover = crossPct;
        this.prob_mutation = mutationPct;
        this.elitism = elitism;
        this.baseElement = baseElement;
        this.Gen = 0;
        this.Trials = 0;

        // initialisation 
        this.poblac = new ArrayList<>();
        this.offspring = new ArrayList<>();
        this.union = new ArrayList<>();
        this.elite = new ArrayList<>();
    }

    @Override
    public void run() {

        // initialise poblac, one array for each class
        for (int i = 0; i < StreamMOEAEFEP.instancia.numClasses(); i++) {
            ArrayList<T> aux = initialisation.doInitialisation(long_poblacion);
            final int clas = i;
            aux.forEach(ind -> ind.setClas(clas)); // Set the class of all individuals generated
            poblac.add(aux);
            offspring.add(new ArrayList<>());
            union.add(new ArrayList<>());
            
            //DEBUG_ Show hash values
            for(T ind : aux){
                System.out.println(ind.hashCode());
            }
        }

        // First evaluation of the whole population;
        poblac.forEach(pop -> {
            pop.forEach(ind -> {
                evaluator.doEvaluation(ind, true);
                Trials++;
                ind.setNEval((int) Trials);
            });
        });

        // Genetic Algorithm evolutionary cycle
        do {
            Gen++;
            // For each class
            // APLICATION OF THE GENETIC OPERATORS
            for (int i = 0; i < StreamMOEAEFEP.instancia.numClasses(); i++) {
                offspring.get(i).clear();
                union.get(i).clear();
                currentClass = i;

                // Selection
                ArrayList<T> selected = new ArrayList<>();
                for (int j = 0; j < getLong_poblacion(); j++) {
                    selected.add((T) selection.doSelection(poblac.get(i)));
                }

                // Crossover
                // The crossover must be changed. It should use the whole population and return an offspring.
                // Now, the implementation of the GA depends on the type of crossover used.
                for (int j = 0; j < selected.size(); j += crossover.getNumParents()) {
                    // Add the required number of parents to an auxiliar array in order to perform the crossover
                    ArrayList<T> cross = new ArrayList<>();
                    for (int k = 0; k < crossover.getNumParents(); k++) {
                        cross.add(selected.get((j + k) % selected.size())); // The last matches with the first if necessary
                    }

                    // Do the crossover if necessary
                    if (Randomize.RanddoubleClosed(0.0, 1.0) <= getProb_crossover()) {
                        offspring.get(i).addAll(crossover.doCrossover(cross));
                    } else {
                        offspring.get(i).addAll(cross);
                    }
                }

                // Mutation 
                for (int j = 0; j < offspring.get(i).size(); j++) {
                    if (Randomize.RanddoubleClosed(0.0, 1.0) <= getProb_mutation()) {
                        Individual mutated = mutation.doMutation(offspring.get(i).get(j));
                        offspring.get(i).set(j, (T) mutated.clone());
                    }
                }

                // Evaluates the offspring
                for (Individual ind : offspring.get(i)) {
                    if (!ind.isEvaluado()) {
                        evaluator.doEvaluation(ind, true);
                        Trials++;
                        ind.setNEval((int) Trials);
                    }
                }

                // NOW, ADDITIONAL STUFF, SUCH AS DOMINANCE RANKING, ETC.
                // Dominance ranking performance
                if (ranking != null) {
                    union.get(i).addAll(poblac.get(i));
                    union.get(i).addAll(offspring.get(i));

                    // Do the dominance ranking and get population for next generation
                    ranking.doDominanceRanking(union.get(i));
                    poblac.get(i).clear();
                    poblac.get(i).addAll(ranking.returnNextPopulation(getLong_poblacion()));
                }

                // Elitism
                if (elitism) {
                    // Do something (store the best elements)
                }

                // Re-initialisation criteria
                if (reinitCriteria != null) {
                    if (reinitCriteria.checkReinitialisationCondition()) {
                        // Re-initialisation. Erase all elements in poblac
                        ArrayList<T> newpop = reinitialisation.doInitialisation(long_poblacion);
                        poblac.get(i).clear();
                        poblac.get(i).addAll(newpop);
                        // Set the class of all new created inidividuals
                        final int clas = i;
                        poblac.get(i).forEach(ind -> ind.setClas(clas));
                    }
                }
            }

        } while (!stopCriteria.checkStopCondition(this));

        result = new ArrayList<>();
        // At the end, return the final population, or the elite
        if (elitism) {
            for (int i = 0; i < elite.size(); i++) {
                result.addAll(elite.get(i));
            }
        } else {
            for (int i = 0; i < poblac.size(); i++) {
                result.addAll(poblac.get(i));
            }
        }

        //System.out.println("Generations: " + Gen + "   Evaluations: " + Trials);
    }

    /**
     * @return the Gen
     */
    public long getGen() {
        return Gen;
    }

    /**
     * @return the Trials
     */
    public long getTrials() {
        return Trials;
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
    public ArrayList<T> getResult() {
        return result;
    }

    /**
     * @return the elite
     */
    public ArrayList<ArrayList<T>> getElite() {
        return elite;
    }

    /**
     * @param elite the elite to set
     */
    public void setElite(ArrayList<ArrayList<T>> elite) {
        this.elite = elite;
    }

    /**
     * @return the elitism
     */
    public boolean isElitism() {
        return elitism;
    }

    /**
     * @param elitism the elitism to set
     */
    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    /**
     * @return the long_poblacion
     */
    public int getLong_poblacion() {
        return long_poblacion;
    }


    /**
     * @return the prob_crossover
     */
    public float getProb_crossover() {
        return prob_crossover;
    }


    /**
     * @return the prob_mutation
     */
    public float getProb_mutation() {
        return prob_mutation;
    }


    /**
     * @return the baseElement
     */
    public T getBaseElement() {
        return baseElement;
    }

    /**
     * @return the poblac
     */
    public ArrayList<ArrayList<T>> getPoblac() {
        return poblac;
    }

    /**
     * @return the offspring
     */
    public ArrayList<ArrayList<T>> getOffspring() {
        return offspring;
    }

    /**
     * @return the currentClass
     */
    public int getCurrentClass() {
        return currentClass;
    }

    public ArrayList<T> getPoblacOfCurrentClass() {
        return poblac.get(currentClass);
    }

}
