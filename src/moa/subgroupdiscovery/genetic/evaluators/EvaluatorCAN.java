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
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 * Evaluator for canonical representation based on fuzzy logic
 *
 * @author agvico
 */
public class EvaluatorCAN extends Evaluator<IndCAN> {

    public EvaluatorCAN(ArrayList<Instance> data) {
        super(data);
    }

    @Override
    public void doEvaluation(IndCAN sample, boolean isTrain) {
        double disparoFuzzy;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);

        if (!sample.isEmpty()) {
            for (int i = 0; i < getData().size(); i++) {
                disparoFuzzy = 1;
                for (int j = 0; j < sample.getSize() && disparoFuzzy > 0; j++) {
                    if (getData().get(i).attribute(j).isNominal()) {
                        // Nominal Variable
                        Double val = getData().get(i).valueInputAttribute(j);
                        if (sample.getCromElem(j) < StreamMOEAEFEP.instancia.attribute(j).numValues()) {
                            boolean missing = getData().get(i).isMissing(j);
                            if (val.intValue() != sample.getCromElem(j) && !missing) {
                                // Variable does not cover the example
                                disparoFuzzy = 0;
                            }
                        }
                    } else {
                        // Numeric variable, do fuzzy computation
                        if (!data.get(i).isMissing(j) && sample.getCromElem(j) < StreamMOEAEFEP.nLabel) {
                            //System.out.println(j + " --- "+ sample.getCromElem(j));
                            try {
                                float pertenencia = StreamMOEAEFEP.Fuzzy(j, sample.getCromElem(j), getData().get(i).valueInputAttribute(j));
                                disparoFuzzy = Math.min(pertenencia, disparoFuzzy);
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                System.err.println("ERROR: " + j + "  -----  " + sample.getCromElem(j) + "\nChromosome: " + sample.getChromosome().toString());
                                System.exit(1);
                            }
                        }
                    }
                }

                if (disparoFuzzy > 0) {
                    sample.getCubre().set(i); // Cambiar dos líneas más abajo si el token competition se va a hacer por clase.
                    if (((Double) getData().get(i).classValue()).intValue() == sample.getClas()) {
                        confMatrix.setTp(confMatrix.getTp() + 1);
                    } else {
                        confMatrix.setFp(confMatrix.getFp() + 1);
                    }
                } else {
                    sample.getCubre().clear(i);
                    if (((Double) getData().get(i).classValue()).intValue() == sample.getClas()) {
                        confMatrix.setFn(confMatrix.getFn() + 1);
                    } else {
                        confMatrix.setTn(confMatrix.getTn() + 1);
                    }
                }
            }
        }
        // Calculate the measures and set as evaluated
        super.calculateMeasures(sample, confMatrix, isTrain);
        sample.setEvaluated(true);

    }

    @Override
    public void doEvaluation(ArrayList<IndCAN> sample, boolean isTrain, GeneticAlgorithm<IndCAN> GA) {
        for (IndCAN ind : sample) {
            if (!ind.isEvaluated()) {
                doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            }
        }
    }

}
