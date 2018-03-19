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
