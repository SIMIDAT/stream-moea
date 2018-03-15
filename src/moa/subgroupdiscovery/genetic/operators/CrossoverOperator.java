/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Class that represents a crossover operator
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public abstract class CrossoverOperator<T extends Individual> {
    
    /**
     * The number of parents required in the crossover
     */
    protected int numParents;
    
    /**
     * The number of children generated for the operator
     */
    protected int numChildren;
    
    /**
     * It performs a crossover operation between the parents in order to generate the children
     * @return 
     */
    public abstract ArrayList<T> doCrossover(ArrayList<T> parents);

    /**
     * @return the numParents
     */
    public int getNumParents() {
        return numParents;
    }

    /**
     * @return the numChildren
     */
    public int getNumChildren() {
        return numChildren;
    }
}
