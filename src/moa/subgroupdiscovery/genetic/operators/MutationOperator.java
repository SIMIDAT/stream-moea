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
package moa.subgroupdiscovery.genetic.operators;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;

/**
 * Class that represents a mutation operator
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public abstract class MutationOperator<T extends Individual> {
    
    /**
     * It performs a mutation of an element. It returns the result of the mutation.
     * 
     * @param source
     * @return 
     */
    public abstract T doMutation(T source);
    
    
    /**
     * It applies performs the mutation phase of the genetic algorithm. Given a {@code source} population, e.g., the offspring
     * it returns a new population with the mutation operators applied according to some probability.
     * 
     * @param source the population to be mutated.
     * @param ga the genetic algorithm where this operator is applied.
     * @return 
     */
    public abstract ArrayList<T> doMutation(ArrayList<T> source, GeneticAlgorithm<T> ga);
}
