/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Class that represents a selection operator
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public abstract class SelectionOperator<T extends Individual> {
    
    /**
     * The number of participant elements in the selection
     */
    protected int numParticipants;
    
    
    /**
     * It performs the selection operator over the given list of elements, returning one of them
     * according to some criteria.
     * 
     * @param elements
     * @return 
     */
    public abstract T doSelection(ArrayList<T> elements);
    
}
