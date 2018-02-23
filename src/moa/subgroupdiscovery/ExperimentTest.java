package moa.subgroupdiscovery;

import java.io.IOException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import moa.classifiers.Classifier;

import moa.core.InstanceExample;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import moa.subgroupdiscovery.qualitymeasures.InvalidContingencyTableException;
import moa.subgroupdiscovery.qualitymeasures.InvalidRangeInMeasureException;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

public class ExperimentTest {
    
    public ExperimentTest() {
    }
    
    public void run(int numInstances, boolean isTesting) {
        
        Classifier learner = new StreamMOEAEFEP();
        
        RandomRBFGenerator stream = new RandomRBFGenerator();
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
        }
        double accuracy = 100.0 * (double) numberSamplesCorrect / (double) numberSamples;
        double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
        System.out.println(numberSamples + " instances processed with " + accuracy + "% accuracy in " + time + " seconds.");
    }
    
    public static void main(String[] args) throws IOException {
        ExperimentTest exp = new ExperimentTest();
        exp.run(1000000, true);
    }
}
