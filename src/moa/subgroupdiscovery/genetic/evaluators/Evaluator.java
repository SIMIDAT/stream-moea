/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import moa.subgroupdiscovery.genetic.Individual;

/**
 *
 * @author agvico
 */
public abstract class Evaluator<T extends Individual> {
    
    /**
     * The data used
     */
    protected ArrayList<Instance> data;
    
    /**
     * It performs the evaluation of the sample.
     * It modifies the sample, so it must be necessary that {@code T} contains elements to save
     * the evaluation results.
     * 
     * @param sample 
     */
    public abstract void doEvaluation(T sample);
    
}
