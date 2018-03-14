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
public class BiasedInitialisationCAN extends InitialisationOperator<IndCAN> {

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
        Double maxVars = Math.floor(baseElement.getTamano() * varPct);
        int variablesToInitialise = Randomize.RandintClosed(1, maxVars.intValue());
        boolean[] variablesInitialised = new boolean[baseElement.getTamano()];

        IndCAN result = (IndCAN) baseElement.clone();
        for (int i = 0; i < variablesToInitialise; i++) {
            int variable = Randomize.Randint(0, baseElement.getTamano());
            while (variablesInitialised[variable]) {
                variable = Randomize.Randint(0, baseElement.getTamano());
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
