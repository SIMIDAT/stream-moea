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
import java.util.Vector;

public class Population {

    /**
     * <p>
     * Population of candidate rules
     * </p>
     */
    private Individual indivi[];     // Population individuals
    private int num_indiv;           // Max number of individuals in the population
    public int num_used;             // Number or individuals really used
    public BitSet ej_cubiertos;   // Covered examples of the population
    public int ult_cambio_eval;      // Last change in the population

    /**
     * <p>
     * Constructor
     * </p>
     */
    public Population() {

    }

    /**
     * <p>
     * Creates a population of Individual
     * </p>
     *
     * @param numind Number of individuals
     * @param numgen Number of variables
     * @param nobj Number of objectives
     * @param neje Number of examples
     * @param RulRep Rules representation
     * @param Variables Variables structure
     */
    public Population(int numind, int numgen, int nobj, int neje, Instance inst) {

        indivi = new Individual[numind];
        num_indiv = numind;
        num_used = 0;
        for (int i = 0; i < numind; i++) {
            indivi[i] = new IndDNF(numgen, neje, nobj, inst,0);
        }
        ej_cubiertos = new BitSet(neje);
        ult_cambio_eval = 0;
        

    }

    /**
     * <p>
     * Biased random population initialization
     * </p>
     *
     * @param Variables Variables structure
     * @param porcVar Percentage of variables to form the rules
     * @param porcPob Percentage of population with biased initialisation
     * @param neje Number of examples
     * @param nFile File to write the population
     */
    public void BsdInitPob(Instance inst, float porcVar, float porcPob, int neje, String nFile) {
        String contents;
        float parteSesg = porcPob * num_indiv;
        int i, j;

        for (i = 0; i < parteSesg; i++) {
            contents = "Individuo(s) " + i + ": ";
            indivi[i].BsdInitInd(inst, porcVar, neje, nFile);
        }
        for (j = i; j < num_indiv; j++) {
            indivi[j].RndInitInd(inst, neje, nFile);
        }

        num_used = num_indiv;
        ej_cubiertos.clear(0, neje);
    }

    /**
     * <p>
     * Evaluates non-evaluated individuals
     * </p>
     *
     * @param AG Genetic algorithm
     * @param Variables Variables structure
     * @param Examples Examples structure
     * @return Number of evaluations performed
     */
    public int evalPop(Genetic AG, ArrayList<Instance> Examples) {
        
        int trials = 0;

        for (int i = 0; i < AG.getLengthPopulation(); i++) {
            if (!getIndivEvaluated(i)) {     // Not evaluated
                
                // Cambiar en la clase Genetic para que pueda almacenar los objetivos por defecto.
                indivi[i].evalInd(Examples, objs);
                setIndivEvaluated(i, true);   /* Now it is evaluated */

                indivi[i].setNEval(AG.getTrials());
                trials++;
            }
        }
        return trials;
    }

    /**
     * <p>
     * Returns the indicated individual of the population
     * </p>
     *
     * @param pos Position of the individual
     * @return Individual
     */
    public Individual getIndiv(int pos) {
        return indivi[pos];
    }

    /**
     * <p>
     * Return the number of individuals of the population
     * </p>
     *
     * @return Number of individuals of the population
     */
    public int getNumIndiv() {
        return num_indiv;
    }

    /**
     * <p>
     * Copy the individual in the Individual otro
     * </p>
     *
     * @param pos Position of the individual to copy
     * @param neje Number of examples
     * @param nobj Number of objectives
     * @param a Individual to copy
     */
    public void CopyIndiv(int pos, int neje, int nobj, Individual a) {
        indivi[pos].copyIndiv(a, neje, nobj);
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     *
     * @param num_indiv Position of the individual
     * @param pos Position of the variable
     * @param elem Position of the gene of the variable
     * @param RulesRep Rules representation
     * @return Gene of the chromosome
     */
    public int getCromElem(int num_indiv, int pos, int elem) {

        if (indivi[num_indiv].getCromGeneElem(pos, elem) == true) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     *
     * @param num_indiv Position of the individual
     * @param pos Position of the variable
     * @param elem Position of the gene of the variable
     * @param val Value for the gene
     * @param RulesRep Rules representation
     */
    public void setCromElem(int num_indiv, int pos, int elem, int val) {

        if (val == 0) {
            indivi[num_indiv].setCromGeneElem(pos, elem, false);
        } else {
            indivi[num_indiv].setCromGeneElem(pos, elem, true);
        }

    }

    /**
     * <p>
     * Returns if the individual of the population has been evaluated
     * </p>
     *
     * @param num_indiv Position of the individual
     */
    public boolean getIndivEvaluated(int num_indiv) {
        return indivi[num_indiv].getIndivEvaluated();
    }

    /**
     * <p>
     * Sets the value for de evaluated attribute of the individual
     * </p>
     *
     * @param num_indiv Position of the individual
     * @param val Value of the individual
     */
    public void setIndivEvaluated(int num_indiv, boolean val) {
        indivi[num_indiv].setIndivEvaluated(val);
    }


    /**
     * <p>
     * Returns de hole cromosoma of the selected individual
     * </p>
     *
     * @param num_indiv Position of the individual
     * @return DNF chromosome
     */
    public CromDNF getIndivCromDNF(int num_indiv) {
        return indivi[num_indiv].getIndivCromDNF();
    }

    /*
     * <p>
     * Return the number of the evaluation with the last change
     * </p>
     * @return                    Number of the last evaluation
     */
    public int getLastChangeEval() {
        return ult_cambio_eval;
    }

    /**
     * <p>
     * This function marks the examples covered by the actual population.
     * </p>
     *
     * @param neje Number of examples
     * @param trials Number of trials performed
     */
    public void examplesCoverPopulation(int neje, int trials) {
        // Copy the structure
        BitSet cubiertos_antes = new BitSet(neje);
        cubiertos_antes.clear(0,neje);
        cubiertos_antes.or(ej_cubiertos);
        ej_cubiertos.clear(0,neje);
       
        for (Individual ind : indivi){
            if(ind.getRank() == 0){
                 ej_cubiertos.or(ind.cubre);
            }
        }
        
        // Comparison of both structures
        BitSet aux = new BitSet(ej_cubiertos.size());
        aux.clear(0,ej_cubiertos.size());
        
        aux.xor(ej_cubiertos);
        cubiertos_antes.flip(0,neje);
        aux.and(cubiertos_antes);
        
        if(aux.cardinality() > 0){
            ult_cambio_eval = trials;
        }
    }

    /**
     * <p>
     * Prints population individuals
     * </p>
     *
     * @param nFile File to write the population
     * @param v Vector which indicates if the individual if repeated
     */
    public void Print(String nFile, Vector v) {

        int marca;

        for (int i = 0; i < num_indiv; i++) {
            marca = (Integer) v.get(i);
            if (marca != 1) {
                indivi[i].Print(nFile);
            }
        }
    }

    /**
     * Apply the token competition procedure to this population.
     * 
     * NOTE: The population must be evaluated before calling this method.
     * @param Examples the Examples of the dataset
     * @param Variables the variables of the dataset
     * @param GA the actual genetic algorithm execution
     * @return A new population with the token competition applied
     */
    public Population tokenCompetition(ArrayList<Instance> Examples, Genetic GA) {
        Instance aux = Examples.get(0);
        // Sort population by the diversity function
        Population actual = sortByDiversity(Examples, GA);
        ArrayList<Individual> rules = new ArrayList<>(); // Indivuals to add in new population

        BitSet tokens = new BitSet(Examples.size());
        tokens.clear(0, Examples.size());

        int conta = 0;
        boolean todosCubiertos = false;
        // Apply token competition procedure
        do {
            // Habría que ver si se hace el token competition por clase 
            BitSet cubiertoRegla = actual.indivi[conta].cubre;
//            boolean cubreNuevo = false;
//            for (int i = 0; i < cubiertoRegla.length; i++) {
//                if (!tokens[i] && cubiertoRegla[i]) {
//                    tokens[i] = true;
//                    cubreNuevo = true;
//                }
//            }
            // Check if there are new covered examples
            BitSet aux1 = (BitSet)tokens.clone();
            BitSet aux2 = (BitSet)tokens.clone();
            aux1.xor(cubiertoRegla);
            aux2.flip(0,Examples.size());
            aux2.and(aux1);
                
            // Update the tokens
            tokens.or(cubiertoRegla);
            
            // if the individual cover new examples not covered by other rules, add it to the result population
            if (aux2.cardinality() > 0) {
                rules.add(actual.indivi[conta]);
            }
            // check if all examples are actually covered 
             if(tokens.cardinality() == tokens.size()){
                 todosCubiertos = true;
             }

            conta++;
        } while (conta < getNumIndiv() && !todosCubiertos);

        // Creates the result population
        Population result = new Population(rules.size(), aux.numInputAttributes(), GA.getNumObjectives(), Examples.size(), aux);
       
        for (int i = 0; i < result.num_indiv; i++) {
            result.CopyIndiv(i, Examples.size(), GA.getNumObjectives(), rules.get(i));
        }
        return result;
    }
    
    
    
    /**
     * Sorts the population according to the diversity function
     * @param Examples  the examples
     * @param Variables the variables 
     * @param GA the genetic algorithm
     * @return A sorted population
     */
    public Population sortByDiversity(ArrayList<Instance> Examples, Genetic GA) {
        Instance aux = Examples.get(0);
        Population result = new Population(getNumIndiv(), aux.numInputAttributes(), GA.getNumObjectives() , Examples.size(), aux);
        
        // Sort population with the diversity function
        double[] ordenado = new double[getNumIndiv()];
        int izq = 0;
        int der = getNumIndiv() - 1;
        int indices[] = new int[getNumIndiv()];
        for (int i = 0; i < getNumIndiv(); i++) {
            indices[i] = i;
            ordenado[i] = getIndiv(i).diversityMeasure.getValue();
        }
        Utils.OrCrecIndex(ordenado, izq, der, indices);

        for (int i = 0; i < indices.length; i++) {
            result.CopyIndiv(i, Examples.size(), GA.getNumObjectives(), getIndiv(indices[i]));
        }
        return result;
    }
    
    /**
     * Perfoms the join of two dataset (it does not remove duplicates)
     * @param other The other population to join with
     * @param Examples The examples of the dataset
     * @param Variables The variables of the dataset
     * @param GA The actual genetic algorithm execution
     * @return  A new joined population
     */
    public Population join(Population other, ArrayList<Instance> Examples, Genetic GA){
       Instance aux = Examples.get(0);
       Population result = new Population(num_indiv + other.num_indiv, aux.numInputAttributes(), GA.getNumObjectives(), Examples.size());
       int conta = 0;
       for(int i = 0; i < num_indiv; i++){
           result.CopyIndiv(conta, Examples.size(), GA.getNumObjectives(), indivi[i]);
           conta++;
       }
       for(int i = 0; i < other.num_indiv; i++){
           result.CopyIndiv(conta, Examples.size(), GA.getNumObjectives(), other.indivi[i]);
           conta++;
       }
       
       return result;
    }

    public Population removeRepeated(Vector marcas, ArrayList<Instance> Examples, Genetic GA){
        Instance aux = Examples.get(0);
        int cuenta = 0;
        ArrayList<Individual> reglas = new ArrayList<Individual>();
        for(int i = 0; i < marcas.size(); i++){
            if((Integer)marcas.get(i) == 1) {
                cuenta++;
            } else {
                reglas.add(indivi[i]);
            }
        }
        Population result = new Population(num_indiv - cuenta, aux.numInputAttributes(), GA.getNumObjectives(), Examples.size());
        for(int i = 0; i < reglas.size(); i++){
            result.CopyIndiv(i, Examples.size(), GA.getNumObjectives(), reglas.get(i));
        }
        
        return result;
    }
}
