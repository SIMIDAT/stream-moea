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

import org.core.Fuzzy;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.FloatOption;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.MultiClassClassifier;
import moa.core.AutoExpandVector;
import moa.core.Measurement;
import moa.core.SizeOf;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import moa.options.ClassOption;
import moa.subgroupdiscovery.genetic.GeneticAlgorithm;
import moa.subgroupdiscovery.genetic.GeneticAlgorithmBuilder;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.individual.*;
import moa.subgroupdiscovery.genetic.criteria.MaxEvaluationsStoppingCriteria;
import moa.subgroupdiscovery.genetic.criteria.MaxGenerationsStoppingCriteria;
import moa.subgroupdiscovery.genetic.criteria.NonEvolutionReInitCriteria;
import moa.subgroupdiscovery.genetic.criteria.ReinitialisationCriteria;
import moa.subgroupdiscovery.genetic.criteria.StoppingCriteria;
import moa.subgroupdiscovery.genetic.dominancecomparators.DominanceComparator;
import moa.subgroupdiscovery.genetic.dominancecomparators.FastNonDominatedSorting;
import moa.subgroupdiscovery.genetic.evaluators.Evaluator;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorCAN;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorCANImproved;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorDNF;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorWithDecay;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorWithDecayBasedOnDiversity;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorWithDecayBasedOnPresence;
import moa.subgroupdiscovery.genetic.evaluators.EvaluatorWithDecayBasedOnPreviousObjectives;
import moa.subgroupdiscovery.genetic.filters.Filter;
import moa.subgroupdiscovery.genetic.filters.MeasureFilter;
import moa.subgroupdiscovery.genetic.filters.TokenCompetition;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import moa.subgroupdiscovery.genetic.operators.MutationOperator;
import moa.subgroupdiscovery.genetic.operators.SelectionOperator;
import moa.subgroupdiscovery.genetic.operators.crossover.TwoPointCrossoverCAN;
import moa.subgroupdiscovery.genetic.operators.crossover.TwoPointCrossoverDNF;
import moa.subgroupdiscovery.genetic.operators.initialisation.BiasedInitialisationCAN;
import moa.subgroupdiscovery.genetic.operators.initialisation.BiasedInitialisationDNF;
import moa.subgroupdiscovery.genetic.operators.initialisation.CoverageBasedInitialisationCAN;
import moa.subgroupdiscovery.genetic.operators.initialisation.CoverageBasedInitialisationDNF;
import moa.subgroupdiscovery.genetic.operators.initialisation.RandomInitialisationCAN;
import moa.subgroupdiscovery.genetic.operators.initialisation.RandomInitialisationDNF;
import moa.subgroupdiscovery.genetic.operators.mutation.BiasedMutationCAN;
import moa.subgroupdiscovery.genetic.operators.mutation.BiasedMutationDNF;
import moa.subgroupdiscovery.genetic.operators.selection.BinaryTournamentSelection;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import org.core.File;
import org.core.Randomize;
import org.core.ResultWriter;

/**
 * Main Class of the algorithm Stream-MOEA
 *
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public class StreamMOEAEFEP extends AbstractClassifier implements MultiClassClassifier {

    // OPTIONS FOR THE ALGORITHM
    /**
     * Sets the size of the environment, i.e., the size of the chunks of data.
     *
     */
    public IntOption period = new IntOption("period", 'c',
            "The size of the environment", 2500);

    /**
     * Set the number of fuzzy labels to use for numeric variables
     */
    public IntOption nLabels = new IntOption("nLabels", 'L',
            "The number of fuzzy labels to use for numeric variables", 3);

    /**
     * Set the population size of the genetic algorithm
     */
    public IntOption populationSize = new IntOption("popSize", 'p',
            "The number of individuals in the population of the genetic algorithm", 51);

    /**
     * Set the Maximum evaluations/generations to stop the evolutionary process
     */
    public IntOption maxGenerations = new IntOption("maxGenerations", 'G',
            "The number of individuals in the population of the genetic algorithm", 20);

    /**
     * Set the crossover probability of the genetic algorithm
     */
    public FloatOption crossPob = new FloatOption("crossProb", 'x',
            "The crossover probability", 0.6, 0.0, 1.0);

    /**
     * Set the mutation probability of the genetic algorithm
     */
    public FloatOption mutProb = new FloatOption("mutProb", 'm',
            "The mutation probability", 0.1, 0.0, 1.0);

    /**
     * Set the measure to use when the Token Competition is performed
     */
    public ClassOption diversity = new ClassOption("diversity", 'q',
            "The quality measure to use when Token Competition is performed", QualityMeasure.class, "WRAccNorm");
    /**
     * Set objective number 1 for the genetic algorithm
     */
    public ClassOption Obj1 = new ClassOption("Obj1", '1',
            "The objective to use for the genetic algorithm", QualityMeasure.class, "GMean");

    /**
     * Set objective number 2 for the genetic algorithm
     */
    public ClassOption Obj2 = new ClassOption("Obj2", '2',
            "The objective to use for the genetic algorithm", QualityMeasure.class, "SuppDiff");

    /**
     * Set objective number 3 for the genetic algorithm
     */
    public ClassOption Obj3 = new ClassOption("Obj3", '3',
            "The objective to use for the genetic algorithm", QualityMeasure.class, "NULL");

    /**
     * The vector that forms the chunk of data
     */
    protected AutoExpandVector<Instance> dataChunk;

    /**
     * The vector that forms the test data
     */
    protected AutoExpandVector<Instance> testChunk;

    /**
     * It counts the number of examples of each class in the chunk of data
     */
    protected static ArrayList<Integer> EjClass;

    /**
     * It gets the objects set as objecive measures from the user
     */
    protected static ArrayList<QualityMeasure> objectives;

    /**
     * The diversity function to sort the individuals in the token competition
     */
    protected static QualityMeasure diversityMeasure;

    /**
     * It stores the fuzzy linguistic labels definitions
     */
    public static Fuzzy[][] baseDatos;

    /**
     * The number of fuzzy labels used
     */
    public static int nLabel;

    /**
     * Control variable to trigger batch processing
     */
    protected long index = 0;

    /**
     * Stores the time, i.e., the number of microbatches processed.
     */
    private static long timestamp = 0;

    /**
     * The previous population obtained in T-1
     */
    protected ArrayList<Individual> previousPopulation = null;

    /**
     * The class that writes the results to a file
     */
    private ResultWriter writer;

    /**
     * The execution time on each run
     */
    private long execTime;

    public static Instance instancia;
    public static InstancesHeader header;
    private static String representation = "CAN";
    private static String evaluator = "byMeasure";
    private static int SLIDING_WINDOW_SIZE = 5;         // The size of the sliding window used in evaluator that uses a sliding window model.

    /**
     * GENETIC ALGORITHM ELEMENTS
     */
    private GeneticAlgorithmBuilder gaBuilder = new GeneticAlgorithmBuilder();
    private GeneticAlgorithm ga;
    private Evaluator eval;

    /**
     * Only for DEBUG purposes.
     */
    public static boolean DEBUG = false;

    @Override
    public double[] getVotesForInstance(Instance inst) {
        double[] a = new double[1];
        a[0] = 0;
        return a;

    }

    @Override
    public void resetLearningImpl() {

        double PCT_REINIT = 0.25;            // Percentage used for the re-initialisation criterion based on non-evolution of the population
        double PCT_VARS_BIASED_INIT = 0.25;  // Maximum percentage of variables initialised when individuals are biased initialised
        double PCT_INDS_BIASED_INIT = 0.75;  // On biased initialisation, the percentage of individuals that must be initialised following the biased initialisation

        index = 0;
        dataChunk = new AutoExpandVector<>();
        EjClass = new ArrayList<>();
        nLabel = nLabels.getValue();
        objectives = new ArrayList<>();
        objectives.add((QualityMeasure) getPreparedClassOption(Obj1));
        objectives.add((QualityMeasure) getPreparedClassOption(Obj2));
        objectives.add((QualityMeasure) getPreparedClassOption(Obj3));
        objectives.sort((x, y) -> {
            if (x instanceof NULL) {
                return 1;
            }
            if (y instanceof NULL) {
                return -1;
            }
            return x.getShortName().compareTo(y.getShortName());
        });
        diversityMeasure = (QualityMeasure) getPreparedClassOption(diversity);
        header = this.getModelContext();

        // Instantiate the result writer. Overwrite previous files???
        writer.setInstancesHeader(header);

        // Genetic algorithm elements
        gaBuilder.setSelection(new BinaryTournamentSelection())
                .setDominanceComparator(new FastNonDominatedSorting(true))
                .setStopCriteria(new MaxGenerationsStoppingCriteria(maxGenerations.getValue()))
                .setReinitCriteria(new NonEvolutionReInitCriteria(PCT_REINIT, maxGenerations.getValue() * populationSize.getValue(), this.getModelContext().numClasses()))
                .setPopulationLength(populationSize.getValue())
                .setCrossoverProbability(((Double) crossPob.getValue()).floatValue())
                .setMutationProbability(((Double) mutProb.getValue()).floatValue())
                .setElitism(false)
                .setDiversity(diversityMeasure);

        // *****************************************************************
        // Instantiation of the elements of the genetic algorithm
        if (representation.equalsIgnoreCase("DNF")) {
            IndDNF base = new IndDNF(this.getModelContext().numInputAttributes(), period.getValue(), header, 0);
            if (evaluator.equalsIgnoreCase("byobjectives")) {
                eval = new EvaluatorWithDecayBasedOnDiversity<EvaluatorDNF>(dataChunk, new EvaluatorDNF(dataChunk), SLIDING_WINDOW_SIZE);
            } else if (evaluator.equalsIgnoreCase("bydiversity")) {
                eval = new EvaluatorWithDecayBasedOnDiversity<EvaluatorDNF>(dataChunk, new EvaluatorDNF(dataChunk), SLIDING_WINDOW_SIZE);
            } else {
                eval = new EvaluatorWithDecayBasedOnPresence<EvaluatorDNF>(dataChunk, new EvaluatorDNF(dataChunk), SLIDING_WINDOW_SIZE);
            }

            gaBuilder.setInitialisation(new BiasedInitialisationDNF(base, PCT_VARS_BIASED_INIT, PCT_INDS_BIASED_INIT))
                    .setCrossover(new TwoPointCrossoverDNF())
                    .setMutation(new BiasedMutationDNF())
                    .setBaseElement(base)
                    .setEvaluator(eval);
        } else {
            IndCAN base = new IndCAN(this.getModelContext().numInputAttributes(), period.getValue(), 0);
            if (evaluator.equalsIgnoreCase("byobjectives")) {
                eval = new EvaluatorWithDecayBasedOnPreviousObjectives<EvaluatorCAN>(dataChunk, new EvaluatorCAN(dataChunk), SLIDING_WINDOW_SIZE);
            } else if (evaluator.equalsIgnoreCase("bydiversity")) {
                eval = new EvaluatorWithDecayBasedOnDiversity(dataChunk, new EvaluatorCAN(dataChunk), SLIDING_WINDOW_SIZE);
            } else {
                //eval = new EvaluatorWithDecayBasedOnPresence(dataChunk, new EvaluatorCAN(dataChunk), SLIDING_WINDOW_SIZE);
                eval = new EvaluatorCANImproved(dataChunk, header, nLabel);
                //eval = new EvaluatorCAN(dataChunk);
            }

            gaBuilder.setInitialisation(new BiasedInitialisationCAN(base, PCT_VARS_BIASED_INIT, PCT_INDS_BIASED_INIT))
                    .setCrossover(new TwoPointCrossoverCAN())
                    .setMutation(new BiasedMutationCAN())
                    .setBaseElement(base)
                    .setEvaluator(eval);
        }

        ga = gaBuilder.build();

        if (representation.equalsIgnoreCase("DNF")) { // The re-initialisation based on coverage needs an instance of the genetic algorithm
            ga.setReinitialisation(new CoverageBasedInitialisationDNF((IndDNF) ga.getBaseElement(), 0.25, dataChunk, ga));
        } else {
            ga.setReinitialisation(new CoverageBasedInitialisationCAN((IndCAN) ga.getBaseElement(), 0.25, dataChunk, ga));
        }

        // *****************************************************************
        // END instantiation of elements of genetic algorithm
        System.out.println("Executing the Stream-MOEA algorithm...");

    }

    public static ArrayList<QualityMeasure> getObjectivesArray() {
        return objectives;
    }

    @Override
    public void trainOnInstanceImpl(Instance inst) {
        instancia = inst;
        if (index % period.getValue() == 0) {
            // initialize the EjClass structure
            EjClass = new ArrayList<>();
            for (int i = 0; i < inst.numClasses(); i++) {
                EjClass.add(0);
            }
        }

        if (dataChunk.size() < period.getValue()) {
            //Accumulate the data in the array
            dataChunk.add(inst);
            Double cl = inst.valueOutputAttribute(0);
            EjClass.set(cl.intValue(), EjClass.get(cl.intValue()) + 1);
        } else {
            // Sets in the evaluator the new data chunk
            eval.setData(dataChunk);

            // Following the interleaved test-then-train schema:
            // ---------------------------------------------
            //  TEST THE NEW DATA
            // ---------------------------------------------
            if (previousPopulation != null && baseDatos != null) {
                for (Individual ind : previousPopulation) {
                    // Evaluates the individuals agains the test data and show its measures
                    eval.doEvaluation(ind, false);

                    // Sets the individual as NON-EVALUATED: Data change in the next timestamp and it is necessary a new evaluation
                    ind.setEvaluated(false);
                }

                // Writes the results in the quality measures files.
                writer.setPopulation(previousPopulation);
                writer.writeResults(execTime);

                // Sets the rules in the evaluator if it is evaluator with time for streaming data
                if (eval instanceof EvaluatorWithDecay) {
                    ((EvaluatorWithDecay) eval).updateAppearance(previousPopulation, ga);
                }

                // Finally, sets in the genetic algorithm this population
                ga.setPopulation(previousPopulation);

            }

            // Now, perform the training 
            // ---------------------------------------------
            // TRAIN THE MODEL WITH THE DATA
            // ---------------------------------------------
            if (index == period.getValue()) {
                // initialize the fuzzy sets definitions (only once)
                baseDatos = new Fuzzy[inst.numInputAttributes()][nLabels.getValue()];
                InitSemantics(dataChunk, nLabels.getValue(), baseDatos);
            }

            // initialize the genetic algorithm and set its parameters
            String str = "Processing Time " + getTimestamp() + "...";
            System.out.print(str + "\r");

            // Initialise the genetic algorithm with the population in t-1
            long t_ini = System.currentTimeMillis();
            ga.run();
            execTime = System.currentTimeMillis() - t_ini;
            /*System.out.println("");
            System.out.println("Evaluation time with evaluator " + eval.getClass().getName() + " = " + execTime + " ms.");
            System.out.println("Number of Trials: " + ga.getTrials());
            System.exit(0);*/
            
            // Shows information of the current run
            //System.out.println("  Time: " + execTime + " ms.");
            // Remove repeated rules
            previousPopulation = ga.getResult();
            Set<Individual> repes = new HashSet<Individual>();
            repes.addAll(previousPopulation);
            previousPopulation.clear();
            previousPopulation.addAll(repes);
            //System.out.println("  Rules generated: " + previousPopulation.size() + "\r");

            // Prepare next iteration
            dataChunk.clear();
            timestamp++;
        }

        index++;
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getModelDescription(StringBuilder arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRandomizable() {
        return true;
    }

    /**
     * <p>
     * Defined to manage de semantics of the linguistic variables Generates the
     * semantics of the linguistic variables using a partition consisting of
     * triangle simetrics fuzzy sets. The cut points al stored at 0.5 level of
     * the fuzzy sets to be considered in the computation of the gain of
     * information. Also writes the semantics of the linguistic variables in the
     * specified file
     * </p>
     *
     *
     */
    public void InitSemantics(ArrayList<Instance> instances, int nLabels, Fuzzy[][] BaseDatos) {
        int v, etq;
        double marca, valor, p_corte;
        float auxX0, auxX1, auxX3, auxY;
        String contents;
        Instance aux = instances.get(0);

        contents = "\n--------------------------------------------\n";
        contents += "|  Semantics for the continuous variables  |\n";
        contents += "--------------------------------------------\n";

        // Initialize the structures to calculate the maximum and minimum of the instance set
        double[] maxis = new double[aux.numInputAttributes()];
        double[] minis = new double[aux.numInputAttributes()];
        for (int i = 0; i < maxis.length; i++) {
            maxis[i] = Double.NEGATIVE_INFINITY;
            minis[i] = Double.POSITIVE_INFINITY;
        }

        // Calculate maximum and minimum for numeric variables in order to calculate the fuzzy labels
        instances.forEach((ins) -> {
            for (int i = 0; i < ins.numInputAttributes(); i++) {
                if (ins.attribute(i).isNumeric()) {
                    if (ins.valueInputAttribute(i) > maxis[i]) {
                        maxis[i] = ins.valueInputAttribute(i);
                    }

                    if (ins.valueInputAttribute(i) < minis[i]) {
                        minis[i] = ins.valueInputAttribute(i);
                    }
                }
            }
        });

        // Calculate the fuzzy intervals
        /**
         * The fuzzy intervals are generated by means of triangular fuzzy
         * partitions. the minimum value of each variable corresponds to a
         * belonging degree of 1 for the linguistic label number 0. The maximum
         * value of each variable corresponds to a belonging degree of 1 for the
         * linguistic label number l-1, where l is the number of labels used.
         *
         * Linguistic labels 0 and l-1 must cover the whole range of real
         * numbers. This means that triangular partitions of label 0 starts at
         * minus infinity (-1 * Float.MAX_VALUE) to the minimum value. The same
         * is applied for the last label.
         */
        int num_vars = aux.numInputAttributes();
        for (v = 0; v < num_vars; v++) {
            if (aux.attribute(v).isNumeric()) {
                marca = (maxis[v] - minis[v]) / ((float) (nLabels - 1));
                p_corte = minis[v] + marca / 2;
                contents += "Fuzzy sets parameters for variable " + aux.attribute(v).name() + ":\n";
                for (etq = 0; etq < nLabels; etq++) {
                    valor = minis[v] + marca * (etq - 1);
                    if (etq == 0) {
                        auxX0 = -1 * Float.MAX_VALUE;
                    } else {
                        auxX0 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
                    }
                    valor = minis[v] + marca * etq;
                    auxX1 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
                    valor = minis[v] + marca * (etq + 1);
                    if (etq == nLabels - 1) {
                        auxX3 = Float.MAX_VALUE;
                    } else {
                        auxX3 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
                    }
                    auxY = 1;
                    BaseDatos[v][etq] = new Fuzzy();
                    BaseDatos[v][etq].setVal(auxX0, auxX1, auxX3, auxY);
                    p_corte += marca;
                    contents += "\tLabel " + etq + ": " + BaseDatos[v][etq].getX0() + " " + BaseDatos[v][etq].getX1() + " " + BaseDatos[v][etq].getX3() + "\n";
                }
            }
        }
        //System.out.println(contents);
        File.writeFile("FuzzySemantics.txt", contents);
    }

    /**
     * <p>
     * Rounds the generated value for the semantics when necesary
     * </p>
     *
     * @param val The value to round
     * @param tope
     * @return
     */
    public float Round(float val, float tope) {
        if (val > -0.0001 && val < 0.0001) {
            return (0);
        }
        if (val > tope - 0.0001 && val < tope + 0.0001) {
            return (tope);
        }
        return (val);
    }

    public static float Fuzzy(int pos, int val, Double value) {
        return baseDatos[pos][val].Fuzzy(value.floatValue());
    }

    /**
     * @return the timestamp
     */
    public static long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the diversityMeasure
     */
    public static QualityMeasure getDiversityMeasure() {
        return diversityMeasure;
    }

    public static void setSeed(long seed) {
        Randomize.setSeed(seed);
    }

    public static void setRepresentation(String rep) {
        representation = rep;
    }

    /**
     * This function sets all the necessary parameters of the Stream-MOEA
     * algorithm by means of a parameters file. If a parameter is not set in the
     * file, it is set the default value.
     *
     * @param path the path to the parameter file
     */
    public String setParametersFromFile(String path) {
        // TODO: You need to finished this function.
        String pathInputData = "";
        try {

            BufferedReader bf = new BufferedReader(new FileReader(path));
            String line;

            while ((line = bf.readLine()) != null) {
                // Comments starts with a #
                if (!line.startsWith("#") && !line.isEmpty()) {
                    StringTokenizer tokens = new StringTokenizer(line, "[ \t\n\f\r]*=[ \t\n\f\r]*");
                    String tok = tokens.nextToken();
                    if (tok.equalsIgnoreCase("algorithm")) {
                        tok = tokens.nextToken();
                        if (!tok.equalsIgnoreCase("stream-moea")) {
                            System.err.println("ERROR: Parameters file: 'algorithm' is incorrect.");
                            System.exit(1);
                        }
                    } else if (tok.equalsIgnoreCase("inputdata")) {
                        pathInputData = tokens.nextToken();
                    } else if (tok.equalsIgnoreCase("outputdata")) {
                        // For output data, we need 4 fields, separated by whitespaces: training measures, test measures for all rules, summary of test measure and rules file
                        String tra = tokens.nextToken();
                        String tst = tokens.nextToken();
                        String tstSumm = tokens.nextToken();
                        String rules = tokens.nextToken();

                        this.writer = new ResultWriter(tra, tst, tstSumm, rules, null, null, true);
                    } else if (tok.equalsIgnoreCase("seed")) {   // Seed of the genetic algorithm
                        Randomize.setSeed(Long.parseLong(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("rulesrepresentation")) {  // The representation to use
                        String a = tokens.nextToken();
                        representation = a;
                    } else if (tok.equalsIgnoreCase("nlabels")) { // The number of fuzzy labels
                        int num = Integer.parseInt(tokens.nextToken());
                        nLabel = num;
                        nLabels.setValue(num);
                    } else if (tok.equalsIgnoreCase("ngen")) {
                        this.maxGenerations.setValue(Integer.parseInt(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("poplength")) {
                        this.populationSize.setValue(Integer.parseInt(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("crossprob")) {
                        this.crossPob.setValue(Double.parseDouble(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("mutprob")) {
                        this.mutProb.setValue(Double.parseDouble(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("period")) {
                        this.period.setValue(Integer.parseInt(tokens.nextToken()));
                    } else if (tok.equalsIgnoreCase("obj1")) {
                        String className = tokens.nextToken();
                        if (!className.equalsIgnoreCase("null")) {
                            String klass = QualityMeasure.class.getPackage().getName() + "." + className;
                            Class a = Class.forName(klass);
                            QualityMeasure q = (QualityMeasure) a.newInstance();
                            this.Obj1.setCurrentObject(q);
                        } else {
                            System.err.println("WARNING: NULL measure set as Obj1, using G-Mean as default.");
                        }
                    } else if (tok.equalsIgnoreCase("obj2")) {
                        String className = tokens.nextToken();
                        String klass = QualityMeasure.class.getPackage().getName() + "." + className;
                        Class a = Class.forName(klass);
                        QualityMeasure q = (QualityMeasure) a.newInstance();
                        this.Obj2.setCurrentObject(q);
                    } else if (tok.equalsIgnoreCase("obj3")) {
                        String className = tokens.nextToken();
                        String klass = QualityMeasure.class.getPackage().getName() + "." + className;
                        Class a = Class.forName(klass);
                        QualityMeasure q = (QualityMeasure) a.newInstance();
                        this.Obj3.setCurrentObject(q);
                    } else if (tok.equalsIgnoreCase("diversity")) {
                        String className = tokens.nextToken();
                        String klass = QualityMeasure.class.getPackage().getName() + "." + className;
                        Class a = Class.forName(klass);
                        QualityMeasure q = (QualityMeasure) a.newInstance();
                        this.diversity.setCurrentObject(q);
                    } else if (tok.equalsIgnoreCase("evaluator")) {
                        evaluator = tokens.nextToken();
                    } else if (tok.equalsIgnoreCase("slidingwindowsize")) {
                        SLIDING_WINDOW_SIZE = Integer.parseInt(tokens.nextToken());
                    } else if (tok.equalsIgnoreCase("filter")) {
                        String className = tokens.nextToken();
                        String klass = Filter.class.getPackage().getName() + "." + className;
                        Class a = Class.forName(klass);
                        Filter f = (Filter) a.newInstance();
                        gaBuilder.addFilter(f);
                    } else {
                        System.err.println("ERROR: '" + tok + "' is not a valid parameter");
                        System.exit(1);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneticAlgorithmBuilder.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(GeneticAlgorithmBuilder.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(StreamMOEAEFEP.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        return pathInputData;
    }

}
