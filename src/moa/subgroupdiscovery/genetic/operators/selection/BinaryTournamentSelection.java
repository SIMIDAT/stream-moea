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
package moa.subgroupdiscovery.genetic.operators.selection;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.operators.SelectionOperator;
import org.core.Randomize;

/**
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class BinaryTournamentSelection extends SelectionOperator<Individual> {

    public BinaryTournamentSelection() {
        super.numParticipants = 2;
    }

    @Override
    public Individual doSelection(ArrayList<Individual> elements) {
        if (elements.size() < numParticipants) {
            throw new UnsupportedOperationException("Binary Tournament Selection: The size of the tournaments is less than 2.");
        }

        int ind1 = Randomize.Randint(0, elements.size());
        int ind2;
        do {
            ind2 = Randomize.Randint(0, elements.size());
        } while(ind2 == ind1);
        
        // Selection by crowding distance 
        Individual e1 = elements.get(ind1);
        Individual e2 = elements.get(ind2);
        
        int winner = ind1;
        if (e2.getRank() < e1.getRank()) {
            winner = ind2;
        } else if (e1.getRank() > e2.getRank()) {
            winner = ind1;
        } else {
            if (e1.getCrowdingDistance() >= e2.getCrowdingDistance()) {
                winner = ind1;
            } else {
                winner = ind2;
            }
        }
        
        return elements.get(winner);
    }

}
