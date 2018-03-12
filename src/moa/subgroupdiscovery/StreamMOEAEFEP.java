/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery;

import org.core.Fuzzy;
import moa.subgroupdiscovery.genetic.Genetic;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.FloatOption;
import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import moa.classifiers.AbstractClassifier;
import moa.core.AutoExpandVector;
import moa.core.Measurement;
import moa.evaluation.LearningCurve;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import moa.subgroupdiscovery.qualitymeasures.WRAccNorm;
import moa.options.ClassOption;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.operators.InitialisationOperator;
import moa.subgroupdiscovery.genetic.operators.initialisation.RandomInitialisationDNF;
import org.core.File;
import org.core.ResultWriter;

/**
 *
 * @author agvico
 */
public class StreamMOEAEFEP extends AbstractClassifier {

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
            "The number of individuals in the population of the genetic algorithm", 3);

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
    protected Population previousPopulation = null;

    /**
     * The class that writes the results to a file
     */
    private ResultWriter writer;

    public static Instance instancia;

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
        index = 0;
        dataChunk = new AutoExpandVector<>();
        EjClass = new ArrayList<>();
        nLabel = nLabels.getValue();
        objectives = new ArrayList<>();
        objectives.add((QualityMeasure) getPreparedClassOption(Obj1));
        objectives.add((QualityMeasure) getPreparedClassOption(Obj2));
        objectives.add((QualityMeasure) getPreparedClassOption(Obj3));
        diversityMeasure = (QualityMeasure) getPreparedClassOption(diversity);
        System.out.println("Executing the Stream-MOEA algorithm...");
    }

    public static ArrayList<QualityMeasure> getObjectivesArray(){
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

            // Following the interleaved test-then-train schema:
            // ---------------------------------------------
            //  TEST THE NEW DATA
            // ---------------------------------------------
            if (previousPopulation != null && baseDatos != null) {
                for (int i = 0; i < previousPopulation.getNumIndiv(); i++) {
                    // Evaluates the individuals agains the test data and show its measures
                    previousPopulation.getIndiv(i).evalInd(dataChunk, objectives, false);
                }
                
                // Writes the results in the quality measures files.
                writer = new ResultWriter("tra_qua.txt",            // Training qm file
                                          "tst_qua.txt",            // Full test qm file
                                          "tst_quaSumm.txt",        // test qm file with only averages
                                          "rules.txt",              // Rule extracted file
                                          previousPopulation,       // population of results
                                          inst);                    // object of class Instance to get variables information
                writer.writeResults();

            }

            // Now, perform the training 
            // ---------------------------------------------
            // TRAIN THE MODEL WITH THE DATA
            // ---------------------------------------------
            /// Initialize the genetic algorithm
            if (index == period.getValue()) {
                // initialize the fuzzy sets definitions (only once)
                baseDatos = new Fuzzy[inst.numInputAttributes()][nLabels.getValue()];
                InitSemantics(dataChunk, nLabels.getValue(), baseDatos);
            }
            
            // initialize the genetic algorithm and set its parameters
            System.out.println("Processing Time " + getTimestamp() + "...");
            Genetic GA = new Genetic();
            GA.setObjectives(objectives);
            GA.setLengthPopulation(populationSize.getValue());
            GA.setNEval(5000); // Hay que cambiar por generaciones y poner la opción aqui
            GA.setProbCross(((Double) crossPob.getValue()).floatValue());
            GA.setProbMutation(((Double) mutProb.getValue()).floatValue());
            GA.setRulesRep("DNF"); // Poner parametro y cambiar

            // Initialise the genetic algorithm with the population in t-1
            previousPopulation = GA.GeneticAlgorithm(dataChunk, "", previousPopulation);

            System.out.println("Rules generated: " + previousPopulation.getNumIndiv());

            //testChunk.clear();
            //testChunk.addAll(dataChunk);
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
        int num_vars = aux.numInputAttributes();
        for (v = 0; v < num_vars; v++) {
            if (aux.attribute(v).isNumeric()) {
                marca = (maxis[v] - minis[v]) / ((float) (nLabels - 1));
                p_corte = minis[v] + marca / 2;
                contents += "Fuzzy sets parameters for variable " + aux.attribute(v).name() + ":\n";
                for (etq = 0; etq < nLabels; etq++) {
                    valor = minis[v] + marca * (etq - 1);
                    auxX0 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
                    valor = minis[v] + marca * etq;
                    auxX1 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
                    valor = minis[v] + marca * (etq + 1);
                    auxX3 = Round(((Double) valor).floatValue(), ((Double) maxis[v]).floatValue());
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
}
