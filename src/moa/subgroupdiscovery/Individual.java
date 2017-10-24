/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package moa.subgroupdiscovery;

import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.BitSet;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

public abstract class Individual {
        
      /**
       * Sets the size of the individual
       */
      public int tamano;

      /**
       * Sets whether the individual is evaluated or not
       */
      public boolean evaluado;
      
      /**
       * Sets the individuals covered in this chunk of data
       */
      public BitSet cubre; 
      
      /**
       * The ranking of the individual
       */
      public int rank;
      
      /**
      * The crowding distance of the individual
      */
      public double crowdingDistance;

      /**
       * The evaluation of the population obtained 
       */
      public int n_eval;             
      
      /**
       * The quality measures of the individuals
       */
      public ArrayList<QualityMeasure> medidas;
      
      /**
       * It store the objective measures of the individual.
       */
      public ArrayList<QualityMeasure> objs;
      
      /**
       * The confidence of the individual.
       */
      public Confidence conf;
      
      /**
      * The class of the individual
      */
      public int clas;

    public Individual() {

    }
    
    /**
     * Random initialisation of an individual. 
     * It fills the individual array completely at random.
     * 
     * @param inst
     * @param neje
     * @param nFile 
     */
    public abstract void RndInitInd(Instance inst, int neje, String nFile);

    
    /**
     * It performs a biased initialisation of an individual.
     * The idea is the fill at most the percentage of variables specifiedat random
     * in order to improve generality
     * @param inst
     * @param porcVar
     * @param neje
     * @param nFile 
     */
    public abstract void BsdInitInd(Instance inst, float porcVar, int neje, String nFile);

    /**
     * Initialisation based on coverture, the individual is initialized with the idea
     * of covering an example of the dataset not covered up to the moment with a 
     * percentage of variables initialised in order to improve generality.
     * @param pop
     * @param Variables
     * @param Examples
     * @param porcCob
     * @param nobj
     * @param nFile 
     */
    public abstract void CobInitInd(Population pop, ArrayList<Instance> Examples, float porcCob, int nobj, int clas, String nFile);

    /**
     * <p>
     * Returns the position i of the array cubre
     * </p>
     * @param pos               Position of example
     * @return                  Value of the example
     */
    public boolean getIndivCovered (int pos) {
        return cubre.get(pos);
    }
    
    /**
     * <p>
     * Returns if the individual has been evaluated
     * </p>
     * @return                  Value of the example
     */
    public boolean getIndivEvaluated () {
        return evaluado;
    }

    /**
     * <p>
     * Sets that the individual has been evaluated
     * </p>
     * @param val               Value of the state of the individual
     */
    public void setIndivEvaluated (boolean val) {
        evaluado = val;
    }

    /**
     * <p>
     * Returns the crowdingDistance of the individual
     * </p>
     * @return                  Crowding distance of the individual
     */
    public double getCrowdingDistance () {
        return crowdingDistance;
    }
    
    /**
     * <p>
     * Sets the crowdingDistance of the individual
     * </p>
     * @param cd                Crowding distance for the individual
     */
    public void setCrowdingDistance (double cd) {
        crowdingDistance = cd;
    }

    /**
     * <p>
     * Returns the rank of the individual
     * </p>
     * @return              Ranking of the individual
     */
    public int getRank (){
        return rank;
    }
    
    /**
     * <p>
     * Sets the rank of the individual
     * </p>
     * @param arank         Ranking of the individual
     */
    public void setRank (int arank){
        rank = arank;
    }
    
    /**
     * <p>
     * Returns the number of evaluation when the individual was created
     * </p>
     * @return                  Number of evalution when the individual was created
     */
    public int getNEval (){
        return n_eval;
    }
    
    /**
     * <p>
     * Sets the number of evaluation when the individual was created
     * </p>
     * @param eval              Number of evaluation when the individual was created
     */
    public void setNEval (int eval){
        n_eval = eval;
    }

    /**
     * <p>
     * Return the quality measure of the individual
     * </p>
     * @return                  Quality measures of the individual
     */
    public ArrayList<QualityMeasure> getMeasures(){
        return medidas;
    }

    /**
     * <p>
     * Gets the value of the quality measure in the position pos
     * </p>
     * @param pos               Position of the quality measure
     * @return                  Value of the quality measure
     */
    public double getMeasureValue(int pos){
        return medidas.get(pos).getValue();
    }

    /**
     * <p>
     * Sets the value of the quality measure in the position pos
     * </p>
     * @param pos               Position of the quality measure
     * @param value             Value of the quality measure
     */
//    public void setMeasureValue(int pos, double value){
//        medidas.setObjectiveValue(pos, value);
//    }

    /**
     * <p>
     * Sets the value of confidence of the individual
     * </p>
     * @param value             Value of confidence of the individual
     */
//    public void setCnfValue(double value){
//        medidas.setCnf(value);
//    }

    /**
     * <p>
     * Gets the value of confidence of the individual
     * </p>
     * @return                  Value of confidence of the individual
     */
    public double getCnfValue(){
        return conf.value;
    }
    
    /**
     * Gets the chromosome element at the specified position
     * @param pos
     * @return 
     */
    public abstract int getCromElem(int pos);
    
    /**
     * Sets the value of the chromosome at the specified position
     * @param pos
     * @param val 
     */
    public abstract void setCromElem (int pos, int val);
    
    
    /**
     * Gets the gene {@code elem} at the specified position. Use in DNF 
     * @param pos
     * @param elem
     * @return 
     */
    public abstract boolean getCromGeneElem(int pos, int elem);
    
    /**
     * Sets the value of an element of the gene at the specified position for a DNF variable
     * @param pos
     * @param elem
     * @param val 
     */
    public abstract void setCromGeneElem(int pos, int elem, boolean val);

    //public abstract CromCAN getIndivCromCAN();
    
    /**
     * Gets the chromosome
     * @return 
     */
    public abstract CromDNF getIndivCromDNF();
    
    /**
     * Copy an individual 
     * @param indi
     * @param nobj
     * @param neje 
     */
    public abstract void copyIndiv (Individual indi, int nobj, int neje);
    
    /**
     * Evaluates the individual with respect to the examples of this data chunk
     * @param AG
     * @param Variables
     * @param Examples 
     */
    public abstract void evalInd (Genetic AG, ArrayList<Instance> Examples);
    
    
    /**
     * Gets the linguistic label that this value belongs to
     * @param valor
     * @param num_var
     * @param Variables
     * @return 
     */
    public abstract int NumInterv (double valor, int num_var, Instance inst);
    
    /**
     * Print the individual
     * @param nFile 
     */
    public abstract void Print(String nFile);

    
}
