/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.initialisation;

import java.util.ArrayList;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class RandomInitialisationDNF extends InitialisationOperator<IndDNF> {

    public RandomInitialisationDNF(IndDNF base){
        this.baseElement = base;
    }
    
    @Override
    public IndDNF doInitialisation() {
        IndDNF toReturn = (IndDNF) baseElement.clone();
        for (int i = 0; i < baseElement.getTamano(); i++) {
            int number = baseElement.getCromElem(i).getGeneLenght();
            for (int j = 0; j < number; j++) {
                toReturn.getCromElem(i).setGeneElem(j, Randomize.RandintClosed(0, 1) == 1);
            }
        }
        return toReturn;
    }

    @Override
    public ArrayList<IndDNF> doInitialisation(int longPopulation) {
        ArrayList<IndDNF> result = new ArrayList<>();
        for(int i = 0; i < longPopulation; i++){
            result.add(doInitialisation());
        }
        
        return result;
    }

}
