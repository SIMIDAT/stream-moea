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
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.exceptions.InvalidRangeInMeasureException;

/**
 * Class that represents the evaluation function of the individuals of a genetic algorithm
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public abstract class Evaluator<T extends Individual> {


    
    /**
     * The data used
     */
    protected ArrayList<Instance> data;

    /**
     * It performs the evaluation of the sample. It modifies the sample, so it
     * must be necessary that {@code T} contains elements to save the evaluation
     * results.
     *
     * @param sample
     */
    public abstract void doEvaluation(T sample, boolean isTrain);
    
    /**
     * It performs the evaluation of a whole population.
     * 
     * @param sample
     * @param isTrain 
     */
    public abstract void doEvaluation(ArrayList<T> sample, boolean isTrain, GeneticAlgorithm<T> GA);

    
        
    public Evaluator(ArrayList<Instance> data){
        this.data = data;
    }
    
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
            
             try {
                sample.getMeasures().clear();
                 
                // Get all the quality measures available in the package qualitymeasures
                ArrayList<QualityMeasure> measures = org.core.ClassLoader.getClasses();

                // Calculates the value of each measure
                measures.forEach(q -> {
                    try {
                        q.calculateValue(confMatrix);
                        q.validate();
                        sample.getMeasures().add(q);
                        
                        for(QualityMeasure p : (ArrayList<QualityMeasure>) sample.getObjs()){
                            if(q.getClass().equals(p.getClass())){
                                p.setValue(q.getValue());
                            }
                        }
                        
                    } catch (InvalidRangeInMeasureException ex) {
                        System.err.println("In test: ");
                        ex.showAndExit(this);
                    }
                });

            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Classes not found in package quality measures");
            }
             
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
            sample.getMeasures().clear();
            
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
                Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Classes not found in package quality measures");
            }

        }

    }

    /**
     * @return the data
     */
    public ArrayList<Instance> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList<Instance> data) {
        this.data = data;
    }

}
