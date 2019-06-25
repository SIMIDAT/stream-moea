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
package moa.subgroupdiscovery.genetic.operators.mutation;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.MutationOperator;
import org.core.Randomize;

/**
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class BiasedMutationDNF extends MutationOperator<IndDNF> {

    @Override
    public IndDNF doMutation(IndDNF source) {
        IndDNF mutated = (IndDNF) source.clone();

        double option = Randomize.RanddoubleClosed(0.0, 1.0);
        if (option <= 0.5) {
            // Erase variable
            int var = Randomize.Randint(0, mutated.getSize());
            mutated.getCromElem(var).NoTakeInitGene();

        } else {
            // Random change on the variable 
            int var = Randomize.Randint(0, mutated.getSize());
            for (int i = 0; i < mutated.getCromElem(var).getGeneLenght(); i++) {
                mutated.setCromGeneElem(var, i, Randomize.RandintClosed(0, 1) == 1);
            }
        }

        mutated.setEvaluated(false);

        return mutated;
    }

    @Override
    public ArrayList<IndDNF> doMutation(ArrayList<IndDNF> source, GeneticAlgorithm<IndDNF> ga) {
        for (int i = 0; i < source.size(); i++) {
            if (Randomize.RanddoubleClosed(0.0, 1.0) <= ga.getProb_mutation()) {
                source.set(i, doMutation((IndDNF) source.get(i).clone()));
            }
        }
        return source;
    }

}
