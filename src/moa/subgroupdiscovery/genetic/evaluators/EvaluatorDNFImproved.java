/*
 * The MIT License
 *
 * Copyright 2018 agvico.
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
import com.yahoo.labs.samoa.instances.InstancesHeader;
import java.util.ArrayList;
import java.util.BitSet;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.individual.IndCAN;
import moa.subgroupdiscovery.genetic.individual.IndDNF;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 * Improved version of the {@link EvaluatorDNF} class to calculate the quality
 * of a given individual or rule.
 *
 * This improved version make use of a BitSet for each attribute-value pair in
 * the population. These BitSets contains information about whether a given
 * examples is covered by the attribute value pair or not. Therefore, the
 * calculation the coverage of a rules is by means of bit operations among
 * several BitSets.
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8.0
 */
public class EvaluatorDNFImproved extends Evaluator<IndDNF> {

    /**
     * Coverage information for each attribute-pair on each example
     */
    private ArrayList<ArrayList<BitSet>> coverInformation;

    /**
     * The classes the samples belongs to
     */
    private ArrayList<BitSet> classes;
    
    public EvaluatorDNFImproved(ArrayList<Instance> data, InstancesHeader dataInfo, int nLabels) {
        super(data);
        coverInformation = new ArrayList<>();
        // fill coverInformation with nulls according to the number of LLs or nominal variables it owns
        for (int i = 0; i < dataInfo.numInputAttributes(); i++) {
            coverInformation.add(new ArrayList<>());
            if (dataInfo.inputAttribute(i).isNominal()) {
                for (int j = 0; j < dataInfo.inputAttribute(i).numValues(); j++) {
                    coverInformation.get(i).add(null);
                }
            } else {
                for (int j = 0; j < nLabels; j++) {
                    coverInformation.get(i).add(null);
                }
            }
        }

        // fill the classes array
        classes = new ArrayList<>();
        
    }
    
    @Override
    public void doEvaluation(IndDNF sample, boolean isTrain) {
        BitSet coverage = new BitSet(this.data.size());
        boolean first = true;
        if (!sample.isEmpty()) {
            for (int j = 0; j < sample.getSize(); j++) {
                if (!sample.getCromElem(j).isNonParticipant()) {
                    BitSet coverageWithinVariable = new BitSet(this.data.size());
                    // for each element in the gene
                    for (int k = 0; k < sample.getCromElem(j).getGeneLenght(); k++) {
                        if (sample.getCromGeneElem(j, k) && coverInformation.get(j).get(k) == null) {
                            BitSet aux = initialiseBitSet(sample, j, k);
                            coverInformation.get(j).set(k, aux);
                        }

                        // active genes within the same variable: OR operation.
                        if (sample.getCromGeneElem(j, k)) {
                            coverageWithinVariable.or(coverInformation.get(j).get(k));
                        }
                    }

                    // At this point, all variables in the rules are initialised. Do the bitset computation
                    // AND between different variables.
                    if (first) {
                        coverage.or(coverageWithinVariable);
                        first = false;
                    } else {
                        coverage.and(coverageWithinVariable);
                    }
                }
            }
            
            sample.setCubre(coverage);

            // now, all variables have been processed , perform computing of the confusion matrix.
            BitSet noClass = (BitSet) classes.get(sample.getClas()).clone();
            noClass.flip(0, this.data.size());
            BitSet noCoverage = (BitSet) coverage.clone();
            noCoverage.flip(0, this.data.size());
            
            BitSet tp = (BitSet) coverage.clone();
            tp.and(classes.get(sample.getClas()));
            
            BitSet tn = (BitSet) noCoverage.clone();
            tn.and(noClass);
            
            BitSet fp = (BitSet) coverage.clone();
            fp.and(noClass);
            
            BitSet fn = (BitSet) noCoverage.clone();
            fn.and(classes.get(sample.getClas()));
            
            ContingencyTable confMatrix = new ContingencyTable(tp.cardinality(), fp.cardinality(), tn.cardinality(), fn.cardinality());

            // Calculate the measures and set as evaluated
            super.calculateMeasures(sample, confMatrix, isTrain);
            sample.setEvaluated(true);
        }
        
    }
    
    @Override
    public void doEvaluation(ArrayList<IndDNF> sample, boolean isTrain, GeneticAlgorithm<IndDNF> GA) {
        for (IndDNF ind : sample) {
            if (!ind.isEvaluated()) {
                doEvaluation(ind, isTrain);
                GA.TrialsPlusPlus();
                ind.setNEval((int) GA.getTrials());
            }
        }
    }

    /**
     * Initialise an attribute-value pair. It returns a bitset that determines
     * whether a given sample is covered by the attribute-value pair or not.
     *
     * @param sample The individual to evaluate
     * @param position The variable
     * @param value The value within the variable
     * @return
     */
    private BitSet initialiseBitSet(IndDNF sample, int position, int value) {
        BitSet infoCovering = new BitSet(this.data.size());

        // If this value is null, it means that the att-value pair has not been initialised yet. Lets initialise
        for (int i = 0; i < this.data.size(); i++) {
            if (data.get(i).attribute(position).isNominal()) {
                // Nominal variable
                Double val = getData().get(i).valueInputAttribute(position);
                boolean missing = getData().get(i).isMissing(position);
                if (val.intValue() == value || missing) {
                    infoCovering.set(i);
                }
                
            } else {
                // Numeric variable
                //System.out.println(j + " --- "+ sample.getCromElem(j));
                try {
                    float pertenencia = StreamMOEAEFEP.Fuzzy(position, value, getData().get(i).valueInputAttribute(position));
                    if (pertenencia > 0 || data.get(i).isMissing(position)) {
                        infoCovering.set(i);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println("ERROR: " + position + "  -----  " + sample.getCromElem(position) + "\nChromosome: " + sample.getChromosome().toString());
                    System.exit(1);
                }
                
            }
        }
        
        return infoCovering;
        
    }
    
    @Override
    public void setData(ArrayList<Instance> data) {
        classes.clear();
        for (int i = 0; i < data.get(0).numClasses(); i++) {
            classes.add(new BitSet(data.size()));
        }
        for (int i = 0; i < data.size(); i++) {
            Instance inst = data.get(i);
            classes.get(((Double) inst.classValue()).intValue()).set(i);
        }
    }
    
}
