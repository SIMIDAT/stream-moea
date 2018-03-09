/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators;

import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.Individual;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class RandomInitialisationDNF extends InitialisationOperator<IndDNF> {

    @Override
    public IndDNF doInitialisation(IndDNF baseElement) {
        IndDNF toReturn = new IndDNF(0, 0, StreamMOEAEFEP.instancia, 0);
        toReturn.copyIndiv(baseElement, baseElement.getObjs().size(), baseElement.getCubre().size());

        for (int i = 0; i < baseElement.getTamano(); i++) {
            int number = baseElement.getCromElem(i).getGeneLenght();
            for (int j = 0; j < number; i++) {
                toReturn.getCromElem(i).setGeneElem(j, Randomize.RandintClosed(0, 1) == 1);
            }
        }
        return toReturn;
    }

}
