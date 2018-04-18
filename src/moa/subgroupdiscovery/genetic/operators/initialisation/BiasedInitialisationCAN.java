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
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 * Class that represents a Biased initialisation operator for the canonical representation.
 * 
 * The idea is to initialise a % of the total number of individuals with, at most, a % of the number of variables initialised.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class BiasedInitialisationCAN extends InitialisationOperator<IndCAN> {

    /**
     * Maximum percentage of variables that could be initialised
     */
    private double varPct;

    /**
     * Percentage of individuals that should be initialised by the biased
     * initialisation The remaining are generated randomly
     */
    private double indPct;

    public BiasedInitialisationCAN(IndCAN base, double pctVariables, double pctIndividuals) {
        this.baseElement = base;
        this.varPct = pctVariables;
        this.indPct = pctIndividuals;
    }

    @Override
    public IndCAN doInitialisation() {
        Double maxVars = Math.floor(baseElement.getSize() * varPct);
        int variablesToInitialise = Randomize.RandintClosed(1, maxVars.intValue());
        boolean[] variablesInitialised = new boolean[baseElement.getSize()];

        IndCAN result = (IndCAN) baseElement.clone();
        for (int i = 0; i < variablesToInitialise; i++) {
            int variable = Randomize.Randint(0, baseElement.getSize());
            while (variablesInitialised[variable]) {
                variable = Randomize.Randint(0, baseElement.getSize());
            }

            // A non-initialised variable has been selected, initialise it randomly
            if (StreamMOEAEFEP.instancia.attribute(variable).isNominal()) {
                result.setCromElem(variable, Randomize.Randint(0, StreamMOEAEFEP.instancia.attribute(variable).numValues()));
            } else {
                result.setCromElem(variable, Randomize.Randint(0, StreamMOEAEFEP.nLabel));
            }
            variablesInitialised[variable] = true;
        }

        // Now, initialise the remaining variables as non-participants
        for (int i = 0; i < variablesInitialised.length; i++) {
            if (!variablesInitialised[i]) {
                if (StreamMOEAEFEP.instancia.attribute(i).isNominal()) {
                    result.setCromElem(i, StreamMOEAEFEP.instancia.attribute(i).numValues());
                } else {
                    result.setCromElem(i, StreamMOEAEFEP.nLabel);
                }
            }
        }
        
        return result;
    }

    @Override
    public ArrayList<IndCAN> doInitialisation(int longPopulation) {
        Double biasedIndividuals = Math.floor(longPopulation * indPct);
        ArrayList<IndCAN> result = new ArrayList<>();

        // biased initialisation
        for (int i = 0; i < biasedIndividuals.intValue(); i++) {
            result.add(doInitialisation());
        }

        //Random initialisation of the remaining individuals
        int remain = longPopulation - biasedIndividuals.intValue();
        RandomInitialisationCAN randInit = new RandomInitialisationCAN(baseElement);
        result.addAll(randInit.doInitialisation(remain));

        return result;
    }

}
