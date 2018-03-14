/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 *
 * @author agvico
 */
public abstract class InitialisationOperator<T extends Individual> {
    
    /**
     * A base element
     */
    protected T baseElement;

    /**
     * It initialises the baseElement and return it
     * @return 
     */
    public abstract T doInitialisation();
    
    
    /*
    * It initialises a whole population by the operator
    */
    public abstract ArrayList<T> doInitialisation(int longPopulation);
}
