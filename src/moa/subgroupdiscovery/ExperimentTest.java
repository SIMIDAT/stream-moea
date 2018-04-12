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
package moa.subgroupdiscovery;

import com.yahoo.labs.samoa.instances.Range;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import moa.classifiers.Classifier;

import moa.core.InstanceExample;
import moa.core.TimingUtils;
import moa.streams.generators.HyperplaneGenerator;
import moa.streams.generators.LEDGenerator;
import moa.streams.generators.RandomRBFGenerator;
import moa.streams.generators.RandomRBFGeneratorDrift;
import moa.streams.generators.RandomTreeGenerator;
import moa.streams.generators.SEAGenerator;
import moa.streams.generators.STAGGERGenerator;
import moa.streams.generators.cd.AbruptChangeGenerator;
import moa.streams.generators.cd.GradualChangeGenerator;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.criteria.MaxEvaluationsStoppingCriteria;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.qualitymeasures.AUC;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import org.core.exceptions.InvalidContingencyTableException;
import org.core.exceptions.InvalidRangeInMeasureException;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import moa.subgroupdiscovery.qualitymeasures.*;

public class ExperimentTest {
    
    public ExperimentTest() {
    }
    
    public void run(int numInstances, boolean isTesting) {
        
        Classifier learner = new StreamMOEAEFEP();
        
        //RandomRBFGenerator stream = new RandomRBFGenerator();
        //SEAGenerator stream = new SEAGenerator();
        RandomTreeGenerator stream = new RandomTreeGenerator();
        //LEDGenerator stream = new LEDGenerator();
        //STAGGERGenerator stream = new STAGGERGenerator();
        //HyperplaneGenerator stream = new HyperplaneGenerator();
        //stream.numAttsOption.setValue(15);
        stream.numNumericsOption.setValue(0);
        stream.numNominalsOption.setValue(15);
      
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/real/elecNormNew.arff", -1);
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/real/covtypeNorm.arff", -1);
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/real/poker-lsn.arff", -1);
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/artificial/sudden_drift_med.arff", -1);
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/artificial/incremental_slow_med.arff", -1);

        //ArffFileStream stream = new ArffFileStream("/home/sramirez/TEST_FUSINTER/datasets/spambase/spambase-10-1tra-weka.dat", -1);
        //ArffFileStream stream = new ArffFileStream("/home/sramirez/datasets/drift/artificial/gradual_drift_100k.arff", -1);
        //stream.numAttsOption.setValue(1000);
        stream.prepareForUse();
        
        learner.setModelContext(stream.getHeader());
        learner.prepareForUse();
        
        int numberSamplesCorrect = 0;
        int numberSamples = 0;
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
        while (stream.hasMoreInstances() && numberSamples < numInstances) {
            
            InstanceExample trainInst = stream.nextInstance();
            /*if (isTesting) {
                                if (learner.correctlyClassifies(trainInst.getData())){
                                        numberSamplesCorrect++;
                                }
                        }
                        numberSamples++;*/
            learner.trainOnInstance(trainInst);
            numberSamples++;
        }
        double accuracy = 100.0 * (double) numberSamplesCorrect / (double) numberSamples;
        double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
        System.out.println(numberSamples + " instances processed with " + accuracy + "% accuracy in " + time + " seconds.");
    }
    
    public static void main(String[] args) throws IOException {
        ExperimentTest exp = new ExperimentTest();
        exp.run(52*2500, true);
    }
}
