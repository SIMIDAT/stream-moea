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

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import moa.recommender.rc.utils.Hash;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.filters.TokenCompetition;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import org.core.Randomize;

/**
 * Class that represents the coverage-based initialisation for the DNF representation.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public final class CoverageBasedInitialisationDNFWithTC extends InitialisationOperator<IndDNF> {

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
    
    
    public CoverageBasedInitialisationDNFWithTC(IndDNF base, double pctVar, ArrayList<Instance> data, GeneticAlgorithm<IndDNF> ga){
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
        Double maxVariables = Math.floor(pctVar * baseElement.getSize());
        boolean[] variablesInitialised = new boolean[baseElement.getSize()];
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
            int variable = Randomize.Randint(0, baseElement.getSize());
            while(variablesInitialised[variable]){
                variable = Randomize.Randint(0, baseElement.getSize());
            }
            
            if(inst.inputAttribute(variable).isNominal()){
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
        TokenCompetition<IndDNF> tc = new TokenCompetition<>();
        ArrayList<IndDNF> pop = tc.doFilter(geneticAlgorithm.getPoblacOfCurrentClass(), geneticAlgorithm);
        
        // now initialise the remaining of individuals by coverage
        int remaining = longPopulation - pop.size();
        for(int i = 0; i < remaining; i++){
            pop.add(doInitialisation());
        }
        
        return pop;
    }
    
}
