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
public final class MaxEvaluationsStoppingCriteria extends StoppingCriteria{

    
    private long maxEvals;
    
    public MaxEvaluationsStoppingCriteria(long max){
        maxEvals = max;
    }
    
    @Override
    public boolean checkStopCondition(GeneticAlgorithm GA) {
        return GA.getTrials() >=  getMaxEvals();
    }

    /**
     * @return the maxEvals
     */
    public long getMaxEvals() {
        return maxEvals;
    }

    /**
     * @param maxEvals the maxEvals to set
     */
    public void setMaxEvals(long maxEvals) {
        this.maxEvals = maxEvals;
    }
 

}
