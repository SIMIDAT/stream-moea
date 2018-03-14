/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.initialisation;

import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class BiasedInitialisationDNF extends InitialisationOperator<IndDNF>{

    /**
     * Maximum percentage of variables that could be initialised
     */
    private double varPct;
    
    /**
     * Percentage of individuals that should be initialised by the biased initialisation
     * The remaining are generated randomly
     */
    private double indPct;

    public BiasedInitialisationDNF(IndDNF base, double pctVariables, double pctIndividuals){
        this.baseElement = base;
        this.varPct = pctVariables;
        this.indPct = pctIndividuals;
    }
    

    @Override
    public IndDNF doInitialisation() {
        Double maxVars = Math.floor(baseElement.getTamano() * varPct);
        int variablesToInitialise = Randomize.RandintClosed(1, maxVars.intValue());
        boolean[] variablesInitialised = new boolean[baseElement.getTamano()];
        
        IndDNF result = (IndDNF) baseElement.clone();
        for(int i = 0; i < variablesToInitialise; i++){
            int variable = Randomize.Randint(0, baseElement.getTamano());
            while(variablesInitialised[variable]){
                variable = Randomize.Randint(0, baseElement.getTamano());
            }
            
            // A non-initialised variable has been selected, initialise it randomly
            for(int j = 0; j < result.getCromElem(variable).getGeneLenght(); j++){
                result.setCromGeneElem(variable, j, Randomize.RandintClosed(0, 1) == 1);
            }
            variablesInitialised[variable] = true;
        }
        
        
        // Now, initialise the remaining variables as non-participants
        for(int i = 0; i < variablesInitialised.length; i++){
            if(!variablesInitialised[i]){
                result.getCromElem(i).NoTakeInitGene();
            }
        }
        
        
        return result;
    }

    @Override
    public ArrayList<IndDNF> doInitialisation(int longPopulation) {
        Double biasedIndividuals = Math.floor(longPopulation * indPct);
        ArrayList<IndDNF> result = new ArrayList<>();
        
        // biased initialisation
        for(int i = 0; i < biasedIndividuals.intValue(); i++){
            result.add(doInitialisation());
        }
        
        //Random initialisation of the remaining individuals
        int remain = longPopulation - biasedIndividuals.intValue();
        RandomInitialisationDNF randInit = new RandomInitialisationDNF(baseElement);
        result.addAll(randInit.doInitialisation(remain));
        
        return result;
    }
    
}
