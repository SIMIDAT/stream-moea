/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.criteria;

import moa.subgroupdiscovery.genetic.GeneticAlgorithm;

/**
 * Stopping criterion based on the number of maximum generations of the genetic algorithm
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class MaxGenerationsStoppingCriteria extends StoppingCriteria{

    private long maxGen;
    
    public MaxGenerationsStoppingCriteria(long maxGenerations){
        this.maxGen = maxGenerations;
    }
    
    @Override
    public boolean checkStopCondition(GeneticAlgorithm GA) {
        return GA.getGen() >= getMaxGen();
    }

    /**
     * @return the maxGen
     */
    public long getMaxGen() {
        return maxGen;
    }

    /**
     * @param maxGen the maxGen to set
     */
    public void setMaxGen(long maxGen) {
        this.maxGen = maxGen;
    }
    
}
