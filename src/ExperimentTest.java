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

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Range;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import moa.classifiers.Classifier;

import moa.core.InstanceExample;
import moa.core.TimingUtils;
import moa.streams.ArffFileStream;
import moa.streams.InstanceStream;
import moa.streams.generators.HyperplaneGenerator;
import moa.streams.generators.LEDGenerator;
import moa.streams.generators.RandomRBFGenerator;
import moa.streams.generators.RandomRBFGeneratorDrift;
import moa.streams.generators.RandomTreeGenerator;
import moa.streams.generators.SEAGenerator;
import moa.streams.generators.STAGGERGenerator;
import moa.streams.generators.cd.AbruptChangeGenerator;
import moa.streams.generators.cd.GradualChangeGenerator;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.GeneticAlgorithmBuilder;
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

    public void run(int numInstances, boolean isTesting, String paramFile, int classColumn) {
        Classifier learner = new StreamMOEAEFEP();
        String inputData = ((StreamMOEAEFEP) learner).setParametersFromFile(paramFile);
        ArffFileStream stream = new ArffFileStream(inputData, classColumn);
        stream.prepareForUse();

        System.out.println("Using stream dataset named: " + stream.getHeader().getRelationName());
        learner.setModelContext(stream.getHeader());
        learner.prepareForUse();

        int numberSamplesCorrect = 0;
        int numberSamples = 0;
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
        while (stream.hasMoreInstances() && numberSamples < numInstances) {

            InstanceExample trainInst = stream.nextInstance();
            learner.trainOnInstance(trainInst);
            numberSamples++;
        }
        double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
        System.out.println(numberSamples + " instances processed in " + time + " seconds.");

    }

    public static void main(String[] args) throws IOException {
        ExperimentTest exp = new ExperimentTest();
        exp.run(100000000, true, args[0], Integer.parseInt(args[1]));
    }

    /**
     * It dinamycally creates the generator class specified at name.
     *
     * Note that some generators could only generate numeric data and only use
     * the numAtts option.
     *
     * @param name The name of the class
     * @param numAtts The total number of attributes
     * @param numNumeric The number of numeric attributes
     * @param numNominal The number of nominal attributes.
     * @return The generator
     *
     */
    public Object getGenerator(String name, int numAtts, int numNumeric, int numNominal) {
        try {
            // Call to ANY generator available in the moa.streams.generators package
            // Instantiation of the generator
            Class clazz = Class.forName("moa.streams.generators.RandomRBFGenerator");

            Object generator = clazz.newInstance();
            Method prepareForUse = generator.getClass().getMethod("prepareForUse", (Class<?>[]) null);
            Method getHeader = generator.getClass().getMethod("getHeader", (Class<?>[]) null);
            Method hasMoreInstances = generator.getClass().getMethod("hasMoreInstances", (Class<?>[]) null);
            Method nextInstance = generator.getClass().getMethod("nextInstance", (Class<?>[]) null);
            Method atts;

            // Catch attributes to set the number of variables.
            try {
                Field numAttsOption = generator.getClass().getField("numAttsOption");
                IntOption a = (IntOption) numAttsOption.get(generator);
                a.setValue(15);
                System.out.println("");
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ExperimentTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            Field[] fields = generator.getClass().getFields();
            System.out.println("Holaa");
            return null;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ExperimentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
