/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.initialisation;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import moa.recommender.rc.utils.Hash;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 * Class that represents the coverage-based initialisation for the DNF representation.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public final class CoverageBasedInitialisationDNF extends InitialisationOperator<IndDNF> {

    /**
     * The data to cover
     */
    private ArrayList<Instance> data;
    
    /**
     * The percentage of variables that participates
     */
    private double pctVar;
    
    
    /**
     * The genetic algorithm. It is necessary to access the population
     */
    private GeneticAlgorithm<IndDNF> geneticAlgorithm;
    
    
    public CoverageBasedInitialisationDNF(IndDNF base, double pctVar, ArrayList<Instance> data, GeneticAlgorithm<IndDNF> ga){
        this.baseElement = base;
        this.data = data;
        this.pctVar = pctVar;
        this.geneticAlgorithm = ga;
    }
    
    @Override
    public IndDNF doInitialisation() {
        // Get the BitSet of the covered examples of the whole population
        BitSet covExamples = new BitSet(data.size());
        for(IndDNF ind : geneticAlgorithm.getPoblacOfCurrentClass()){
            covExamples.or(ind.getCubre());
        }
        
        IndDNF result = (IndDNF) baseElement.clone();
        Double maxVariables = Math.floor(pctVar * baseElement.getTamano());
        boolean[] variablesInitialised = new boolean[baseElement.getTamano()];
        int variablesToInitialise = Randomize.RandintClosed(1, maxVariables.intValue());
        
        // Select an example not covered 
        boolean selected = false;
        int selectedExample = 0;
        for(int i = 0; i < data.size() && !selected; i++){
            selectedExample = Randomize.Randint(0, data.size());
            if(!covExamples.get(selectedExample)) {
                Double clas = data.get(selectedExample).classValue();
                if (clas.intValue() == geneticAlgorithm.getCurrentClass()) {
                    selected = true;
                }
             }
        }
        
        
        Instance inst = data.get(selectedExample);
        // Now, select the variables to initialise. These must cover the selected example
        for(int i = 0; i < variablesToInitialise; i++){
            int variable = Randomize.Randint(0, baseElement.getTamano());
            while(variablesInitialised[variable]){
                variable = Randomize.Randint(0, baseElement.getTamano());
            }
            
            if(inst.attribute(variable).isNominal()){
                // Discrete Variable
                result.getCromElem(variable).NoTakeInitGene();
                Double value = inst.valueInputAttribute(variable);
                result.setCromGeneElem(variable, value.intValue(), true);
            } else {
                // Numeric Variable
                int position = result.NumInterv(inst.valueInputAttribute(variable), variable, inst);
                result.getCromElem(variable).NoTakeInitGene();
                result.setCromGeneElem(variable, position, true);
            }
            
            // Set the variable as initialisated
            variablesInitialised[variable] = true;
        }
        
        // Now, initialise the rest of variables as non-participant
        for(int i = 0; i < variablesInitialised.length; i++){
            if(!variablesInitialised[i]){
                result.getCromElem(i).NoTakeInitGene();
            }
        }
        
        result.setClas(geneticAlgorithm.getCurrentClass());
        return result;
    }

    @Override
    public ArrayList<IndDNF> doInitialisation(int longPopulation) {
        // First, keep in the population only non-repeated individuals of the pareto front
        ArrayList<IndDNF> paretoFront = geneticAlgorithm.getRanking().getParetoFront();
        Set<IndDNF> set = new HashSet<>();
        set.addAll(paretoFront);
        paretoFront.clear();
        paretoFront.addAll(set);
        
        // now initialise the remaining of individuals by coverage
        int remaining = longPopulation - paretoFront.size();
        for(int i = 0; i < remaining; i++){
            paretoFront.add(doInitialisation());
        }
        
        return paretoFront;
    }
    
}
