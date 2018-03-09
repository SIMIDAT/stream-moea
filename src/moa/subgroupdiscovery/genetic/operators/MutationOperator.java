/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators;

import moa.subgroupdiscovery.genetic.Individual;

/**
 *
 * @author agvico
 */
public abstract class MutationOperator<T extends Individual> {
    
    /**
     * It performs a mutation of an element. It returns the result of the mutation.
     * 
     * @param source
     * @return 
     */
    public abstract T doMutation(T source);
}
