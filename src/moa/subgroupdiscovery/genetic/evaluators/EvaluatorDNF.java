/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 * Evaluator for DNF representation based on fuzzy logic.
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class EvaluatorDNF extends Evaluator<IndDNF> {

    public EvaluatorDNF(ArrayList<Instance> data) {
        super.data = data;
    }

    @Override
    public void doEvaluation(IndDNF sample, boolean isTrain) {
        float disparoFuzzy;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);
        int numVarNoInterv = 0;  // Number of variables not taking part in the individual

        // If the individual is non-empty, evaluate it against the data
        if (!sample.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                disparoFuzzy = 1;

                // For each variable of the rule
                for (int j = 0; j < sample.getTamano() && disparoFuzzy > 0; j++) {
                    if (!sample.getCromElem(j).isNonParticipant()) {
                        // The variable participates
                        if (StreamMOEAEFEP.instancia.attribute(j).isNominal()) {
                            // Nominal Variable
                            Double value = data.get(i).valueInputAttribute(j);
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
                                        pert = StreamMOEAEFEP.Fuzzy(j, k, data.get(i).valueInputAttribute(j));
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
                    if (((Double) data.get(i).classValue()).intValue() == sample.getClas()) {
                        confMatrix.setTp(confMatrix.getTp() + 1);
                    } else {
                        confMatrix.setFp(confMatrix.getFp() + 1);
                    }
                } else {
                    sample.getCubre().clear(i);
                    if (((Double) data.get(i).classValue()).intValue() == sample.getClas()) {
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

}
