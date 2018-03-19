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
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 * Evaluator for DNF representation based on fuzzy logic.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public class EvaluatorDNF extends Evaluator<IndDNF> {

    public EvaluatorDNF(ArrayList<Instance> data) {
        super(data);
    }

    @Override
    public void doEvaluation(IndDNF sample, boolean isTrain) {
        float disparoFuzzy;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);
        int numVarNoInterv = 0;  // Number of variables not taking part in the individual

        // If the individual is non-empty, evaluate it against the data
        if (!sample.isEmpty()) {
            for (int i = 0; i < getData().size(); i++) {
                disparoFuzzy = 1;

                // For each variable of the rule
                for (int j = 0; j < sample.getTamano() && disparoFuzzy > 0; j++) {
                    if (!sample.getCromElem(j).isNonParticipant()) {
                        // The variable participates
                        if (StreamMOEAEFEP.instancia.attribute(j).isNominal()) {
                            // Nominal Variable
                            Double value = getData().get(i).valueInputAttribute(j);
                            if (sample.getCromGeneElem(j, value.intValue()) && !data.get(i).isMissing(j)) {
                                // The rule does not cover the example.
                                disparoFuzzy = 0;
                            }
                        } else {
                            // Numeric variable. It calculates the fuzzy computation
                            if (!data.get(i).isMissing(j)) {
                                float pertenencia = 0;
                                float pert;
                                for (int k = 0; k < StreamMOEAEFEP.nLabel; k++) {
                                    if (sample.getCromGeneElem(j, k)) {
                                        pert = StreamMOEAEFEP.Fuzzy(j, k, getData().get(i).valueInputAttribute(j));
                                    } else {
                                        pert = 0;
                                    }
                                    pertenencia = Math.max(pertenencia, pert);
                                }
                                disparoFuzzy = Math.min(pertenencia, disparoFuzzy);
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
        // Once finished the evaluation against the data, calculate the quality measures
        super.calculateMeasures(sample, confMatrix, isTrain);

        // Now, set individual as evaluated.
        sample.setEvaluado(true);

    }

    @Override
    public void doEvaluation(ArrayList<IndDNF> sample, boolean isTrain, GeneticAlgorithm<IndDNF> GA) {
        for(IndDNF ind : sample){
            if(!ind.isEvaluado()){
                doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            }
        }
    }

}
