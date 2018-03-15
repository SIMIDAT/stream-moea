/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.criteria;

/**
 * Class that representent the criteria to trigger the re-initialisation procedure.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public abstract class ReinitialisationCriteria {
    public abstract boolean checkReinitialisationCondition();
}
