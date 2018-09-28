/*
 * The MIT License
 *
 * Copyright 2018 agvico.
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

package moa.subgroupdiscovery.genetic.filters;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;

/**
 *  Class that represents a filter that will be applied at the end of the evolutionary
 * process over the whole population.
 * 
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public abstract class Filter<T extends Individual> {

    /**
     * The threshold of the filter
     */
    protected double threshold;
    
    /**
     * Default constructor
     */
    public Filter(){
        
    }
    
    /**
     * Constructor that defines a threshold. Useful for threshold filters.
     * @param threshold 
     */
    public Filter(double threshold){
        this.threshold = threshold;
    }
    
    /**
     * It performs the filter of the given population
     * @param population The population to be filtered
     * @param ga The genetic algorithm the populations belongs to
     * @return 
     */
    public abstract ArrayList<T> doFilter(ArrayList<T> population, GeneticAlgorithm<T> ga);
    
}
