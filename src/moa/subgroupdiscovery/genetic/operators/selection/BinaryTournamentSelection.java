/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        
        int winner = 0;
        if (e2.getRank() < e1.getRank()) {
            winner = 1;
        } else if (e1.getRank() > e2.getRank()) {
            winner = 0;
        } else {
            if (e1.getCrowdingDistance() >= e2.getCrowdingDistance()) {
                winner = 0;
            } else {
                winner = 1;
            }
        }
        
        return elements.get(winner);
    }

}
