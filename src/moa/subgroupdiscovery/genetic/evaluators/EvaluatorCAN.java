/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.evaluators;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 * Evaluator for canonical representation based on fuzzy logic
 * 
 * @author agvico
 */
public final class EvaluatorCAN extends Evaluator<IndCAN> {

    public EvaluatorCAN(ArrayList<Instance> data) {
        super.data = data;
    }

    @Override
    public void doEvaluation(IndCAN sample, boolean isTrain) {
        double disparoFuzzy;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);

        if (!sample.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                disparoFuzzy = 1;
                for (int j = 0; j < sample.getTamano() && disparoFuzzy > 0; j++) {
                    if (data.get(i).attribute(j).isNominal()) {
                        // Nominal Variable
                        Double val = data.get(i).valueInputAttribute(j);
                        if (sample.getCromElem(j) < StreamMOEAEFEP.instancia.attribute(j).numValues()) {
                            boolean missing = data.get(i).isMissing(j);
                            if (val.intValue() != sample.getCromElem(j) && !missing) {
                                // Variable does not cover the example
                                disparoFuzzy = 0;
                            }
                        }
                    } else {
                        // Numeric variable, do fuzzy computation
                        if (!data.get(i).isMissing(j) && sample.getCromElem(j) < StreamMOEAEFEP.nLabel) {
                            float pertenencia = StreamMOEAEFEP.Fuzzy(j, sample.getCromElem(j), data.get(i).valueInputAttribute(j));
                            disparoFuzzy = Math.min(pertenencia, disparoFuzzy);
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
        // Calculate the measures and set as evaluated
        super.calculateMeasures(sample, confMatrix, isTrain);
        sample.setEvaluado(true);

    }

}
