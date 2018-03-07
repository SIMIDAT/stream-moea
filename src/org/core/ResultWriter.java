/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.core;

import com.yahoo.labs.samoa.instances.Instance;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.Population;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Class to store the results and rules of a given population of individuals
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8
 * @version 1.0
 */
public class ResultWriter {

    /**
     * The path where the objectives values for each individual is stored
     */
    private String pathTra;

    /**
     * The path where the test quality measures are stored (detailed file)
     */
    private String pathTst;

    /**
     * The path where the test quality measures are stored (summary file)
     */
    private String pathTstSummary;

    /**
     * The path where the rules are stored
     */
    private String pathRules;

    /**
     * The population to get the results
     */
    private ArrayList<Individual> population;

    /**
     * The instance where the variables are obtained
     */
    private Instance inst;

    /**
     * The formatter of the numbers
     */
    private DecimalFormat sixDecimals;

    /**
     * The symbols to use in the formatter
     */
    private DecimalFormatSymbols symbols;

    
    
    
    
    
    
    /**
     * Default constructor, it sets the path where the files are stored.
     *
     *
     * @param tra
     * @param tst
     * @param rules
     */
    public ResultWriter(String tra, String tst, String tstSummary, String rules, ArrayList<Individual> population, Instance inst) {
        this.pathRules = rules;
        this.pathTra = tra;
        this.pathTst = tst;
        this.pathTstSummary = tstSummary;
        this.population = population;
        this.inst = inst;
        symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator('.');
        symbols.setNaN("NaN");
        symbols.setInfinity("INFINITY");
        sixDecimals = new DecimalFormat("0.000000", symbols);
    }

    
    
    
    
    
    
    
    /**
     * Default contructor which uses the Population Structure.
     *
     * @param tra
     * @param tst
     * @param tstSummary
     * @param rules
     * @param population
     */
    public ResultWriter(String tra, String tst, String tstSummary, String rules, Population population, Instance inst) {
        this.pathRules = rules;
        this.pathTra = tra;
        this.pathTst = tst;
        this.pathTstSummary = tstSummary;
        this.population = new ArrayList<>();
        this.inst = inst;

        for (int i = 0; i < population.getNumIndiv(); i++) {
            this.population.add(population.getIndiv(i));
        }
        symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator('.');
        symbols.setNaN("NaN");
        symbols.setInfinity("INFINITY");
        sixDecimals = new DecimalFormat("0.000000", symbols);
    }

    
    
    
    
    /**
     * It only writes the results of the rules
     */
    public void writeRules() {
        String content = "*************************************************\n";
        content += "Timestamp " + StreamMOEAEFEP.getTimestamp() + ":\n";
        for (int i = 0; i < population.size(); i++) {
            content += "Rule " + i + ":\n";
            content += population.get(i).toString(inst);
        }
        content += "*************************************************\n";
        Files.addToFile(pathRules, content);
    }

    
    
    
    
    
    
    /**
     * It only writes the results of the objectives
     */
    public void writeTrainingMeasures() {
        String content = "*************************************************\n";
        content += "Timestamp " + StreamMOEAEFEP.getTimestamp() + ":\n";

        // Write the header (the consequent first, and next, the objective quality measures, finaly, the diversity measure)
        content += "Rule\tConsequent";
        for (QualityMeasure q : population.get(0).getObjs()) {
            content += "\t" + q.getShortName();
        }
        content += "\t" + population.get(0).getDiversityMeasure().getShortName() + "(Diversity)";
        content += "\n";

        // Now, for each individual, writes the training measures
        for (int i = 0; i < population.size(); i++) {
            content += i + "\t" + inst.outputAttribute(0).value(population.get(i).getClas()) + "\t";
            for (QualityMeasure q : population.get(i).getObjs()) {
                content += sixDecimals.format(q.getValue()) + "\t";
            }
            content += sixDecimals.format(population.get(i).getDiversityMeasure().getValue()) + "\n";
        }
        content += "*************************************************\n";
        Files.addToFile(pathTra, content);
    }

    
    
    
    
    
    /**
     * It writes the full version of the results test quality measures, i.e.,
     * the whole set of measures for each individual on each timestamp, in
     * addition to the summary
     */
    public void writeTestFullResults() {
        // this array stores the sum of the quality measures for the average
        ArrayList<Double> averages = new ArrayList<>();
        double numVars = 0.0;
        for (QualityMeasure q : population.get(0).getMedidas()) {
            averages.add(0.0);
        }

        // First, write the headers
        String content = "Timestamp\tRule\tClass\tNumRules\tNumVars";

        // now, append each test quality measure
        for (int j = 0; j < population.get(0).getMedidas().size(); j++) {
            content += "\t" + population.get(0).getMedidas().get(j).getShortName();
        }
        content += "\n";

        // now write the test results for each individual
        for (int i = 0; i < population.size(); i++) {
            content += sixDecimals.format(StreamMOEAEFEP.getTimestamp()) + "\t"
                    + i + "\t"
                    + inst.outputAttribute(0).value(population.get(i).getClas()) + "\t"
                    + "------\t"
                    + sixDecimals.format(population.get(i).getNumVars()) + "\t";
            numVars += population.get(i).getNumVars();

            for (int j = 0; j < population.get(i).getMedidas().size(); j++) {
                content += sixDecimals.format(population.get(i).getMedidas().get(j).getValue()) + "\t";
                averages.set(j, averages.get(j) + population.get(i).getMedidas().get(j).getValue());
            }
            content += "\n";
        }

        numVars /= (double) population.size();
        // finally, write the average results
        content += "------\t------\t------\t" + sixDecimals.format(population.size()) + "\t" + sixDecimals.format(numVars) + "\t";
        for (Double d : averages) {
            content += sixDecimals.format(d / (double) population.size()) + "\t";
        }
        content += "\n";
        Files.addToFile(pathTst, content);
    }

    
    
    
    
    
    
    /**
     * It writes the summary results of the test quality measures, i.e., it only
     * writes the line with the average results.
     */
    public void writeTestSummaryResults() {
        // this array stores the sum of the quality measures for the average
        ArrayList<Double> averages = new ArrayList<>();
        double numVars = 0.0;
        for (QualityMeasure q : population.get(0).getMedidas()) {
            averages.add(0.0);
        }

        // First, write the headers
        String content = "Timestamp\tRule\tClass\tNumRules\tNumVars";

        // now, append each test quality measure
        for (int j = 0; j < population.get(0).getMedidas().size(); j++) {
            content += "\t" + population.get(0).getMedidas().get(j).getShortName();
        }
        content += "\n";

        // Now, average the results of the test measures
        for (int i = 0; i < population.size(); i++) {
            numVars += population.get(i).getNumVars();
            for (int j = 0; j < population.get(i).getMedidas().size(); j++) {
                averages.set(j, averages.get(j) + population.get(i).getMedidas().get(j).getValue());
            }
        }

        numVars /= (double) population.size();
        // finally, write the average results
        content += sixDecimals.format(StreamMOEAEFEP.getTimestamp()) + "\t------\t------\t" + sixDecimals.format(population.size()) + "\t" + sixDecimals.format(numVars) + "\t";
        for (Double d : averages) {
            content += sixDecimals.format(d / (double) population.size()) + "\t";
        }
        content += "\n";
        Files.addToFile(pathTstSummary, content);

    }

    
    
    
    
    
    /**
     * It writes the results of the individuals in the files
     */
    public void writeResults() {
        writeRules();
        writeTrainingMeasures();
        writeTestFullResults();
        writeTestSummaryResults();
    }

}
