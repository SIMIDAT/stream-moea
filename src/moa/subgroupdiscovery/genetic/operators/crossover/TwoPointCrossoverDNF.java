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
package moa.subgroupdiscovery.genetic.operators.crossover;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.individual.*;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import org.core.Randomize;

/**
 * Class that represents the two point crossover operator for the DNF representation
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class TwoPointCrossoverDNF extends CrossoverOperator<IndDNF> {

    public TwoPointCrossoverDNF() {
        super.numChildren = 2;
        super.numParents = 2;
    }

    @Override
    public ArrayList<IndDNF> doCrossover(ArrayList<IndDNF> parents) {
        if (parents.size() != getNumParents()) {
            throw new UnsupportedOperationException("Two point crossover: The number of parents is different than two.");
        }

        ArrayList<IndDNF> children = new ArrayList<>();
        children.add((IndDNF) parents.get(0).clone());
        children.add((IndDNF) parents.get(1).clone());

        // Get the two point of crossover
        int xpoint1 = Randomize.Randint(0, parents.get(0).getTamano() - 1);
        int xpoint2;
        if (xpoint1 != parents.get(0).getTamano() - 1) {
            xpoint2 = Randomize.Randint(xpoint1 + 1, parents.get(0).getTamano() - 1);
        } else {
            xpoint2 = parents.get(0).getTamano() - 1;
        }

        // Perform the crossover
        for (int i = xpoint1; i <= xpoint2; i++) {
           for(int j = 0; j < parents.get(0).getCromElem(i).getGeneLenght(); j++){
               children.get(0).setCromGeneElem(i, j, parents.get(1).getCromGeneElem(i, j));
               children.get(1).setCromGeneElem(i, j, parents.get(0).getCromGeneElem(i, j));
           }
        }

        // Set individuals as non-evaluated
        for(int i = 0; i < children.size(); i++){
            children.get(i).setEvaluado(false);
        }
        
        return children;
    }

}
