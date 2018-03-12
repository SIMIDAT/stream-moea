/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.criteria;

import moa.subgroupdiscovery.genetic.GeneticAlgorithm;

/**
 *
 * @author agvico
 */
public abstract class StoppingCriteria {
    
    
    public abstract boolean checkStopCondition(GeneticAlgorithm GA);
}
