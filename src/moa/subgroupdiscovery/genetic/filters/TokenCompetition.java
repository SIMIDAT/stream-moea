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

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * This class performs the token competition procedure. First, all individuals
 * are sorted according to its diversity measure. After that, each instance is a
 * token. One by one, each individual in the population takes does tokens that
 * it covers but are not covered by any other previous individual.
 *
 * If the individual does not take any token, it is removed.
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class TokenCompetition extends Filter<Individual> {

    @Override
    public ArrayList<Individual> doFilter(ArrayList<Individual> population, GeneticAlgorithm<Individual> ga) {

        // Sorts population by the diversity measure
        population.sort(Comparator.comparing( (Individual ind) -> ind.getDiversityMeasure().getValue()).reversed());
        

        ArrayList<Individual> rules = new ArrayList<>();
        BitSet tokens = new BitSet(ga.getEvaluator().getData().size());

        int counter = 0;
        boolean allCovered = false;

        do {
            BitSet ruleCoverture = population.get(counter).getCubre();
            boolean newCover = false;
            int clas = population.get(counter).getClas();

            for (int i = 0; i < ruleCoverture.size(); i++) {
                // get all possible tokens of its class
                if (!tokens.get(i) && ruleCoverture.get(i) && ((Instance) ga.getEvaluator().getData().get(i)).classValue() == clas) {
                    tokens.set(i);
                    newCover = true;
                }
            }

            if (newCover) {
                rules.add(population.get(counter));
            }
            if (tokens.cardinality() == tokens.size()) {
                allCovered = true;
            }
            counter++;

        } while (counter < population.size() && !allCovered);

        return rules;
    }
}
