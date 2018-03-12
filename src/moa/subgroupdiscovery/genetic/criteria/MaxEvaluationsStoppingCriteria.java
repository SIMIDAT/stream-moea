/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moa.subgroupdiscovery.genetic.criteria;

import moa.subgroupdiscovery.genetic.GeneticAlgorithm;

/**
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class MaxEvaluationsStoppingCriteria extends StoppingCriteria{

    
    private int maxEvals;
    
    public MaxEvaluationsStoppingCriteria(int max){
        maxEvals = max;
    }
    
    @Override
    public boolean checkStopCondition(GeneticAlgorithm GA) {
        return GA.getTrials() >=  maxEvals;
    }
 

}
