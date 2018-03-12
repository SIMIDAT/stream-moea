/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package moa.subgroupdiscovery.genetic;

import com.yahoo.labs.samoa.instances.Instance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.options.ClassOption;
import moa.subgroupdiscovery.CromCAN;
import moa.subgroupdiscovery.CromDNF;
import moa.subgroupdiscovery.IndDNF;
import moa.subgroupdiscovery.Population;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import org.core.exceptions.InvalidRangeInMeasureException;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import weka.classifiers.evaluation.ConfusionMatrix;


/**
 * Class that represents an individual of the population.
 * 
 * It implements the clase comparable. Although it is for multi-objective purpose.
 * The main idea of the comparison is for other kind of sorting such us the one performed in the token competition procedure.
 * 
 * @author agvico
 * @param <T> The elements of the chromosome that conforms the individual
 */
public abstract class Individual<T> implements Serializable, Cloneable, Comparable<Individual<T>>{
    
    /**
     * The chromosome of the individual.
     */
    protected ArrayList<T> chromosome;

    /**
     * Sets the size of the individual
     */
    protected int tamano;

    /**
     * Sets whether the individual is evaluated or not
     */
    protected boolean evaluado;

    /**
     * Sets the individuals covered in this chunk of data
     */
    protected BitSet cubre;

    /**
     * The ranking of the individual
     */
    protected int rank;

    /**
     * The crowding distance of the individual
     */
    protected double crowdingDistance;

    /**
     * The evaluation of the population obtained
     */
    protected int n_eval;

    /**
     * The quality measures of the individuals
     */
    protected ArrayList<QualityMeasure> medidas;

    /**
     * It store the objective measures of the individual.
     */
    protected ArrayList<QualityMeasure> objs;

    /**
     * It stores the diversity function to be used on the token competition
     */
    protected QualityMeasure diversityMeasure;

    /**
     * The confidence of the individual.
     */
    protected Confidence conf;

    /**
     * The class of the individual
     */
    protected int clas;
    
    public Individual() {
        
    }

    /**
     * Random initialisation of an individual. It fills the individual array
     * completely at random.
     *
     * @param inst
     * @param neje
     * @param nFile
     * @param clas
     */
    //public abstract void RndInitInd(Instance inst, int neje, String nFile, int clas);

    /**
     * It performs a biased initialisation of an individual. The idea is the
     * fill at most the percentage of variables specifiedat random in order to
     * improve generality
     *
     * @param inst
     * @param porcVar
     * @param neje
     * @param nFile
     * @param clas
     */
    //public abstract void BsdInitInd(Instance inst, float porcVar, int neje, String nFile, int clas);

    /**
     * Initialisation based on coverture, the individual is initialized with the
     * idea of covering an example of the dataset not covered up to the moment
     * with a percentage of variables initialised in order to improve
     * generality.
     *
     * @param pop
     * @param Variables
     * @param Examples
     * @param porcCob
     * @param nobj
     * @param nFile
     */
    //public abstract void CobInitInd(Population pop, ArrayList<Instance> Examples, float porcCob, int nobj, int clas, String nFile);

    
    /**
     * Gets the chromosome element at the specified position
     *
     * @param pos
     * @return
     */
    public abstract T getCromElem(int pos);

    /**
     * Sets the value of the chromosome at the specified position
     *
     * @param pos
     * @param val
     */
    public abstract void setCromElem(int pos, T val);

    /**
     * Gets the gene {@code elem} at the specified position. Use in DNF
     *
     * @param pos
     * @param elem
     * @return
     */
    public abstract boolean getCromGeneElem(int pos, int elem);

    /**
     * Sets the value of an element of the gene at the specified position for a
     * DNF variable
     *
     * @param pos
     * @param elem
     * @param val
     */
    public abstract void setCromGeneElem(int pos, int elem, boolean val);
    
    //public abstract CromCAN getIndivCromCAN();

    /**
     * Gets the chromosome
     *
     * @return
     */
    //public abstract CromDNF getIndivCromDNF();

    /**
     * Copy an individual
     *
     * @param indi
     * @param nobj
     * @param neje
     */
    public abstract void copyIndiv(Individual indi, int nobj, int neje);

    /**
     * Evaluates the individual with respect to the examples of this data chunk
     *
     * @param AG
     * @param Examples
     * @param objs
     */
    //public abstract void evalInd(ArrayList<Instance> Examples, ArrayList<QualityMeasure> objs, boolean isTrain);

    /**
     * Gets the linguistic label that this value belongs to
     *
     * @param valor
     * @param num_var
     * @param Variables
     * @return
     */
    public abstract int NumInterv(double valor, int num_var, Instance inst);

    /**
     * It gets the string representation of the rules in a human-readable way
     * @param inst
     * @return 
     */
    public abstract String toString(Instance inst);
    
    /**
     * It applies the mutation operator over all genes of an individual,
     * according to the mutation probability
     *
     * @return A copy of the mutated individual.
     */
    //public abstract void mutate(Instance inst, float mutProb);

    /**
     * It performs the mutation operator over a gene of the individual, with a
     * 100% of probability of application
     *
     * @return A copy of the mutated individual.
     */
    //public abstract void mutate(Instance inst, int pos);

    
    /**
     * Gets the number of variables that participate in the rule
     * @return 
     */
    public abstract int getNumVars();
    /**
     * Print the individual
     *
     * @param nFile
     */
    //public abstract void Print(String nFile);
    
    /**
     * Checks whether it is an empty rule, i.e., no variables participate in the rule
     * @return 
     */
    public abstract boolean isEmpty();

    /**
     * @return the clas
     */
    public int getClas() {
        return clas;
    }

    /**
     * @param clas the clas to set
     */
    public void setClas(int clas) {
        this.clas = clas;
    }

    

    /**
     * @return the chromosome
     */
    public ArrayList<T> getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(ArrayList<T> chromosome) {
        this.chromosome = chromosome;
    }
    
    
    
    
    
        /**
     * @return the tamano
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * @param tamano the tamano to set
     */
    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    /**
     * @return the evaluado
     */
    public boolean isEvaluado() {
        return evaluado;
    }

    /**
     * @param evaluado the evaluado to set
     */
    public void setEvaluado(boolean evaluado) {
        this.evaluado = evaluado;
    }

    /**
     * @return the cubre
     */
    public BitSet getCubre() {
        return cubre;
    }

    /**
     * @param cubre the cubre to set
     */
    public void setCubre(BitSet cubre) {
        this.cubre = cubre;
    }

    /**
     * @return the n_eval
     */
    public int getN_eval() {
        return n_eval;
    }

    /**
     * @param n_eval the n_eval to set
     */
    public void setN_eval(int n_eval) {
        this.n_eval = n_eval;
    }

    /**
     * @return the medidas
     */
    public ArrayList<QualityMeasure> getMedidas() {
        return medidas;
    }

    /**
     * @param medidas the medidas to set
     */
    public void setMedidas(ArrayList<QualityMeasure> medidas) {
        this.medidas = medidas;
    }

    /**
     * @return the objs
     */
    public ArrayList<QualityMeasure> getObjs() {
        return objs;
    }

    /**
     * @param objs the objs to set
     */
    public void setObjs(ArrayList<QualityMeasure> objs) {
        this.objs = objs;
    }

    /**
     * @return the diversityMeasure
     */
    public QualityMeasure getDiversityMeasure() {
        return diversityMeasure;
    }

    /**
     * @param diversityMeasure the diversityMeasure to set
     */
    public void setDiversityMeasure(QualityMeasure diversityMeasure) {
        this.diversityMeasure = diversityMeasure;
    }

    /**
     * @return the conf
     */
    public Confidence getConf() {
        return conf;
    }

    /**
     * @param conf the conf to set
     */
    public void setConf(Confidence conf) {
        this.conf = conf;
    }
    
    
    /**
     * <p>
     * Returns the position i of the array cubre
     * </p>
     *
     * @param pos Position of example
     * @return Value of the example
     */
    public boolean getIndivCovered(int pos) {
        return getCubre().get(pos);
    }

    /**
     * <p>
     * Returns if the individual has been evaluated
     * </p>
     *
     * @return Value of the example
     */
    public boolean getIndivEvaluated() {
        return isEvaluado();
    }

    /**
     * <p>
     * Sets that the individual has been evaluated
     * </p>
     *
     * @param val Value of the state of the individual
     */
    public void setIndivEvaluated(boolean val) {
        setEvaluado(val);
    }

    /**
     * <p>
     * Returns the crowdingDistance of the individual
     * </p>
     *
     * @return Crowding distance of the individual
     */
    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    /**
     * <p>
     * Sets the crowdingDistance of the individual
     * </p>
     *
     * @param cd Crowding distance for the individual
     */
    public void setCrowdingDistance(double cd) {
        crowdingDistance = cd;
    }

    /**
     * <p>
     * Returns the rank of the individual
     * </p>
     *
     * @return Ranking of the individual
     */
    public int getRank() {
        return rank;
    }

    /**
     * <p>
     * Sets the rank of the individual
     * </p>
     *
     * @param arank Ranking of the individual
     */
    public void setRank(int arank) {
        rank = arank;
    }

    /**
     * <p>
     * Returns the number of evaluation when the individual was created
     * </p>
     *
     * @return Number of evalution when the individual was created
     */
    public int getNEval() {
        return getN_eval();
    }

    /**
     * <p>
     * Sets the number of evaluation when the individual was created
     * </p>
     *
     * @param eval Number of evaluation when the individual was created
     */
    public void setNEval(int eval) {
        setN_eval(eval);
    }

    /**
     * <p>
     * Return the quality measure of the individual
     * </p>
     *
     * @return Quality measures of the individual
     */
    public ArrayList<QualityMeasure> getMeasures() {
        return getMedidas();
    }

    /**
     * <p>
     * Gets the value of the quality measure in the position pos
     * </p>
     *
     * @param pos Position of the quality measure
     * @return Value of the quality measure
     */
    public double getMeasureValue(int pos) {
        return getMedidas().get(pos).getValue();
    }

    /**
     * <p>
     * Sets the value of the quality measure in the position pos
     * </p>
     *
     * @param pos Position of the quality measure
     * @param value Value of the quality measure
     */
//    public void setMeasureValue(int pos, double value){
//        medidas.setObjectiveValue(pos, value);
//    }
    /**
     * <p>
     * Sets the value of confidence of the individual
     * </p>
     *
     * @param value Value of confidence of the individual
     */
//    public void setCnfValue(double value){
//        medidas.setCnf(value);
//    }
    /**
     * <p>
     * Gets the value of confidence of the individual
     * </p>
     *
     * @return Value of confidence of the individual
     */
    public double getCnfValue() {
        return getConf().getValue();
    }

    @Override
    public int compareTo(Individual<T> o) {
        return this.diversityMeasure.compareTo(o.diversityMeasure);
    }

    @Override
    public abstract Individual<T> clone();
    
    
    @Override
    public abstract int hashCode();

}
