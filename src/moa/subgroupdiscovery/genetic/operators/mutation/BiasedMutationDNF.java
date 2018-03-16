/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.mutation;

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
            int var = Randomize.Randint(0, mutated.getTamano());
            mutated.getCromElem(var).NoTakeInitGene();

        } else {
            // Random change on the variable 
            int var = Randomize.Randint(0, mutated.getTamano());
            for(int i = 0; i < mutated.getCromElem(var).getGeneLenght(); i++){
                mutated.setCromGeneElem(var, i, Randomize.RandintClosed(0, 1) == 1);
            }
        }
        
        mutated.setEvaluado(false);
        
        return mutated;
    }

}
