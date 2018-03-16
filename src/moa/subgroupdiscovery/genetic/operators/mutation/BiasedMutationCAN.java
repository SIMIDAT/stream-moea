/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.mutation;

import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.genetic.operators.MutationOperator;
import org.core.Randomize;

/**
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class BiasedMutationCAN extends MutationOperator<IndCAN> {

    @Override
    public IndCAN doMutation(IndCAN source) {
        IndCAN mutated = (IndCAN) source.clone();

        double option = Randomize.RanddoubleClosed(0.0, 1.0);
        if (option <= 0.5) {
            // Erase variable
            int var = Randomize.Randint(0, mutated.getTamano());
            if (StreamMOEAEFEP.instancia.attribute(var).isNominal()) {
                mutated.setCromElem(var, StreamMOEAEFEP.instancia.attribute(var).numValues());
            } else {
                mutated.setCromElem(var, StreamMOEAEFEP.nLabel);
            }
        } else {
            // Random Change on the variable (erase value can participate)
            int var = Randomize.Randint(0, mutated.getTamano());
            if (StreamMOEAEFEP.instancia.attribute(var).isNominal()) {
                mutated.setCromElem(var, Randomize.RandintClosed(0, StreamMOEAEFEP.instancia.attribute(var).numValues()));
            } else {
                mutated.setCromElem(var, Randomize.RandintClosed(0, StreamMOEAEFEP.nLabel));
            }
        }
        
        mutated.setEvaluado(false);
        
        return mutated;
    }

}
