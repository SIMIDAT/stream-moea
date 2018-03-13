/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.dominancecomparators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 *
 * @author agvico
 */
public abstract class DominanceComparator<T extends Individual> {
    
    /*
    * The different pareto fronts where the individuals are stored
    */
    protected ArrayList<ArrayList<T>> fronts;
    
    /**
     * It controls whether the comparison is by strict dominance or not.
     */
    protected boolean StrictDominance;
    
    /**
     * It performs a sorting of the elements by a dominance ranking. This means that elements should 
     * contain more than one object
     * 
     * @param elements
     * @return 
     */
    public abstract void doDominanceRanking(ArrayList<T> elements);
    
    
    /**
     * It returns a population from the fronts which could be used as the population of the
     * next generation.
     * 
     * @return 
     */
    public abstract ArrayList<T> returnNextPopulation(int tamPopulation);
    
    /**
     * It returns the Pareto front, i.e., the first front 
     * @return 
     */
    public ArrayList<T> getParetoFront(){
        return fronts.get(0);
    }
    
    /**
     * It returns the number of subfronts obtained.
     * @return 
     */
    public int getNumberOfSubFronts(){
        return fronts.size();
    }
    
    /**
     * It returns the given subfront
     * @param front
     * @return 
     */
    public ArrayList<T> getSubFront(int front){
        return fronts.get(front);
    }
    
}
