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
    
    /**
     * It performs a sorting of the elements by a dominance ranking. This means that elements should 
     * contain more than one object
     * 
     * @param elements
     * @return 
     */
    public abstract ArrayList<T> doDominanceRanking(ArrayList<T> elements);
    
}
