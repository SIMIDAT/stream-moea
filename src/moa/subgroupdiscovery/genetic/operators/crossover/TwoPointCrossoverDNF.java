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
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.individual.*;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import org.core.Randomize;

/**
 * Class that represents the two point crossover operator for the DNF
 * representation
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class TwoPointCrossoverDNF extends CrossoverOperator<IndDNF> {

    public TwoPointCrossoverDNF() {
        super.numChildren = 2;
        super.numParents = 2;
    }

    @Override
    public ArrayList<IndDNF> doCrossover(ArrayList<IndDNF> parents, GeneticAlgorithm ga) {
        ArrayList<IndDNF> offspring = new ArrayList<>();

        for (int j = 0; j < parents.size(); j += numParents) {
            ArrayList<IndDNF> children = new ArrayList<>();
            for (int k = 0; k < numParents; k++) {
                children.add((IndDNF) parents.get((j + k) % parents.size()).clone());
            }

            if (Randomize.RanddoubleClosed(0.0, 1.0) <= ga.getProb_crossover()) {
                // Get the two point of crossover
                int xpoint1 = Randomize.Randint(0, parents.get(0).getSize() - 1);
                int xpoint2;
                if (xpoint1 != parents.get(0).getSize() - 1) {
                    xpoint2 = Randomize.Randint(xpoint1 + 1, parents.get(0).getSize() - 1);
                } else {
                    xpoint2 = parents.get(0).getSize() - 1;
                }

                // Perform the crossover
                for (int i = xpoint1; i <= xpoint2; i++) {
                    for (int k = 0; k < parents.get(0).getCromElem(i).getGeneLenght(); k++) {
                        children.get(0).setCromGeneElem(i, k, parents.get(j % parents.size()).getCromGeneElem(i, k));
                        children.get(1).setCromGeneElem(i, k, parents.get((j + 1) % parents.size()).getCromGeneElem(i, k));
                    }
                }

                // Set individuals as non-evaluated
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).setEvaluated(false);
                }
            }
            
            offspring.addAll(children);
        }

        return offspring;
    }

}
