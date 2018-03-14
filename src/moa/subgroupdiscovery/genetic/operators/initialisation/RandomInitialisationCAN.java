/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.initialisation;

import java.util.ArrayList;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class RandomInitialisationCAN extends InitialisationOperator<IndCAN> {

    
    public RandomInitialisationCAN(IndCAN base){
        super.baseElement = base;
    }
    
    @Override
    public IndCAN doInitialisation() {
        IndCAN toReturn = (IndCAN) baseElement.clone();
        for (int i = 0; i < baseElement.getTamano(); i++) {
            if(StreamMOEAEFEP.instancia.attribute(i).isNominal()){
                toReturn.setCromElem(i, Randomize.RandintClosed(0, StreamMOEAEFEP.instancia.attribute(i).numValues()));
            } else {
                toReturn.setCromElem(i, Randomize.RandintClosed(0, StreamMOEAEFEP.nLabel));      
            }
        }
        return toReturn;
    }

    @Override
    public ArrayList<IndCAN> doInitialisation(int longPopulation) {
        ArrayList<IndCAN> result = new ArrayList<>();
        for(int i = 0; i < longPopulation; i++){
            result.add(doInitialisation());
        }
        
        return result;
    }

}
