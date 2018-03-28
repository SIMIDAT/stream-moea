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
    private int numClasses;

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
     *
     * @param popLength the population length
     * @param crossPct the crossover probability [0,1]
     * @param mutationPct the mutation probability [0,1]
     * @param elitism Is elitism available?
     * @param baseElement the base element of the population
     * @param numClasses The number of classes in the problem
     */
    public GeneticAlgorithm(int popLength, float crossPct, float mutationPct, boolean elitism, T baseElement, int numClasses) {
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
        this.numClasses = numClasses;
        
        for (int i = 0; i < numClasses; i++) {
            this.poblac.add(new ArrayList<>());
            this.offspring.add(new ArrayList<>());
            this.union.add(new ArrayList<>());
            this.elite.add(new ArrayList<>());
        }
    }

    @Override
    public void run() {

        Gen = 0;
        Trials = 0;
        
        // initialise poblac
        // Initialise only the necessary individuals to reach long_poblacion.
        // Other individuals were added from the previous timestamp.
        for (int i = 0; i < StreamMOEAEFEP.instancia.numClasses(); i++) {
            int remaining = long_poblacion - poblac.get(i).size();
            ArrayList<T> aux = initialisation.doInitialisation(remaining);
            final int clas = i;
            aux.forEach(ind -> ind.setClas(clas)); // Set the class of all individuals generated
            poblac.get(i).addAll(aux);
        }

        // First evaluation of the whole population;
        /*poblac.forEach(pop -> {
            evaluator.doEvaluation(pop, true, this);
        });*/

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
                //evaluator.doEvaluation(offspring.get(i), true, this);

                // NOW, ADDITIONAL STUFF, SUCH AS DOMINANCE RANKING, ETC.
                // Dominance ranking performance
                if (ranking != null) {
                    union.get(i).addAll(poblac.get(i));
                    union.get(i).addAll(offspring.get(i));
               
                    // Evaluates the whole union
                    evaluator.doEvaluation(union.get(i), true, this);
                    
                    
                    // Do the dominance ranking and get population for next generation
                    ranking.doDominanceRanking(union.get(i));
                    poblac.get(i).clear();
                    poblac.get(i).addAll(ranking.returnNextPopulation(getLong_poblacion()));
                } else {
                    // If there is no dominance ranking, it works as a generational mono-objective GA.
                    // Do the replacement of poblac (now, it works as a generational GA, i.e., the whole offspring replace the whole population
                    evaluator.doEvaluation(offspring.get(i), true, this);
                    poblac.get(currentClass).clear();
                    poblac.get(currentClass).addAll(offspring.get(currentClass));
                }

                // Elitism
                if (elitism) {
                    // Do something (store the best elements)
                }

                // Re-initialisation criteria
                if (reinitCriteria != null) {
                    if (reinitCriteria.checkReinitialisationCondition(this)) {
                        ArrayList<T> newpop = reinitialisation.doInitialisation(long_poblacion);
                        reinitCriteria.resetCriterion(this);
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

        System.out.println("Generations: " + Gen + "   Evaluations: " + Trials);
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
     * It gets the current class it is executed in the Genetic Algorithm
     * @return the currentClass
     */
    public int getCurrentClass() {
        return currentClass;
    }

    /**
     * It returns the population of the class that is now executing
     * @return 
     */
    public ArrayList<T> getPoblacOfCurrentClass() {
        return poblac.get(currentClass);
    }

    /**
     * It sums one to the number of trials
     */
    public void TrialsPlusPlus() {
        this.Trials++;
    }

    /**
     * It sums {@code value} to the number of trials
     * @param value 
     */
    public void TrialsPlusEqual(int value) {
        this.Trials += value;
    }
    
    /**
     * It sets in the population of the Genetic algorithm the population given. 
     * It clears the previous population (if exists).
     * 
     * NOTE: This population could contain a mix of individuals belonging to different classes. In this case
     * 
     * @param population The population
     */
    public void setPopulation(ArrayList<T> population){
        for(ArrayList<T> pop : poblac){
            pop.clear();
        }
        
        for(T ind : population){
            
            // reset the test measures array, the measurements stored are not valid anymore.
            ind.medidas.clear();
            ind.setEvaluated(false); // individual needs to be evaluated again in the genetic algorithm
            
            poblac.get(ind.getClas()).add(ind);
        }
    }
}
