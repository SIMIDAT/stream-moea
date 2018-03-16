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
package moa.subgroupdiscovery.genetic.dominancecomparators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Class that represents a non-dominanted sorting algorithm
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
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
