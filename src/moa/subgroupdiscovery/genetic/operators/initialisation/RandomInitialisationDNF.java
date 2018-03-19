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
package moa.subgroupdiscovery.genetic.operators.initialisation;

import java.util.ArrayList;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class RandomInitialisationDNF extends InitialisationOperator<IndDNF> {

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
