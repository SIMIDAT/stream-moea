/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.criteria;

/**
 * Re-initialisation based on the non-evolution of a population. A population is defined as stagnant 
 * if the popoulation does not cover new examples for, at least, a % of the total of evaluations/generations
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class NonEvolutionReInitCriteria extends ReinitialisationCriteria{

    @Override
    public boolean checkReinitialisationCondition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
