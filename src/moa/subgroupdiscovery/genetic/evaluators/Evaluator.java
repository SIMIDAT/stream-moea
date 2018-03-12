/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.IndDNF;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.exceptions.InvalidRangeInMeasureException;

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
    public abstract void doEvaluation(T sample, boolean isTrain);
    
    
    
    /**
     * It calculates the quality measures given a contingency table
     *
     * @param confMatrix
     * @param objs
     * @param isTrain
     */
    public void calculateMeasures(T sample, ContingencyTable confMatrix, boolean isTrain) {
        ArrayList<QualityMeasure> objs = sample.getObjs();
        if (isTrain) {
            // Compute the objective quality measures
            if (sample.getObjs().isEmpty()) {
                objs.forEach((q) -> {
                    // If it is empty, then the measures are not created, copy the default objects
                    // from the objectives array
                    sample.getObjs().add(q.clone());
                });
            }
            
            sample.getObjs().stream().filter((QualityMeasure q) -> (!(q instanceof NULL))).forEachOrdered((QualityMeasure q) -> {
                // Calculate if it is not the null measure.
                q.calculateValue(confMatrix);
                try {
                    // Check for errors in the measures, exit if they are detected
                    q.validate();
                } catch (InvalidRangeInMeasureException ex) {
                    // If this exception occurred, then exit the program
                    ex.showAndExit(this);
                }
            });

            // Compute the confidence
            sample.getConf().calculateValue(confMatrix);

            // Compute the diversity function
            sample.getDiversityMeasure().calculateValue(confMatrix);

            // check confidence and diversity functions
            try {
                sample.getConf().validate();
                sample.getDiversityMeasure().validate();
            } catch (InvalidRangeInMeasureException ex) {
                System.err.println("In training:");
                ex.showAndExit(this);
            }
        } else {

            // Test the individual.
            try {
                // Get all the quality measures available in the package qualitymeasures
                ArrayList<QualityMeasure> measures = org.core.ClassLoader.getClasses();

                // Calculates the value of each measure
                measures.forEach(q -> {
                    try {
                        q.calculateValue(confMatrix);
                        q.validate();
                        sample.getMedidas().add(q);
                    } catch (InvalidRangeInMeasureException ex) {
                        System.err.println("In test: ");
                        ex.showAndExit(this);
                    }
                });
                
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Classes not found in package quality measures");
            }
            
        }
        
    }
    
}
