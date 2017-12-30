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
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.File;
import org.core.Randomize;

public class IndCAN extends Individual {

    /**
     * <p>
     * Defines the individual of the population
     * </p>
     */
    public CromCAN cromosoma;   // Individual contents

    /**
     * <p>
     * Creates new instance of Canonical individual
     * </p>
     */
    public IndCAN() {
    }

    /**
     * </p>
     * Creates new instance of Canonical individual
     * </p>
     *
     * @param lenght Lenght of the individual
     * @param neje Number of examples
     * @param nobj Number of objectives
     */
    public IndCAN(int lenght, int neje, int nobj) {

        tamano = lenght;
        cromosoma = new CromCAN(lenght);
        medidas = new ArrayList<>();
        objs = new ArrayList<>();
        conf = new Confidence();
        diversityMeasure = (QualityMeasure) StreamMOEAEFEP.diversityMeasure.copy();
        evaluado = false;
        cubre = new BitSet(neje);

        crowdingDistance = 0.0;
        n_eval = 0;

    }

    /**
     * <p>
     * Creates random instance of Canonical individual
     * </p>
     *
     * @param Variables Variables structure
     * @param neje Number of exaples
     * @param nFile File to write the individual
     */
    @Override
    public void RndInitInd(Instance inst, int neje, String nFile, int clas) {
        cromosoma.RndInitCrom(inst, StreamMOEAEFEP.nLabel);  // Random initialization method
        evaluado = false;                  // Individual not evaluated
        cubre.clear(0, neje);
        crowdingDistance = 0.0;
        n_eval = 0;
        this.clas = clas;
    }

    /**
     * <p>
     * Creates biased instance of Canonical individual for small disjunct
     * </p>
     *
     * @param Variables Variables structure
     * @param porcVar Percentage of variables to form the individual
     * @param neje Number of exaples
     * @param nFile File to write the individual
     */
    @Override
    public void BsdInitInd(Instance inst, float porcVar, int neje, String nFile, int clas) {

        cromosoma.BsdInitCrom(inst, porcVar, StreamMOEAEFEP.nLabel);  // Random initialization method
        evaluado = false;                           // Individual not evaluated
        cubre.clear(0, neje);
        crowdingDistance = 0.0;
        n_eval = 0;
    }

    /**
     * <p>
     * Creates random instance of Canonical individual for small disjunct
     * </p>
     *
     * @param Variables Variables structure
     * @param neje Number of exaples
     * @param subgroup Subgroup obtained
     */
    /* public void RndInitIndSmall(TableVar Variables, int neje, Individual subgroup) {
        cromosoma.RndInitCromSmall(Variables, subgroup);  // Random initialization method
        evaluado = false;                  // Individual not evaluated
        for (int i=0; i<neje; i++){
            cubre[i] = false;
        }

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;
    }*/
    /**
     * <p>
     * Creates biased instance of Canonical individual
     * </p>
     *
     * @param Variables Variables structure
     * @param porcVar Percentage of variables to form the individual
     * @param neje Number of exaples
     * @param subgroup Subgroup obtained
     */
    /*public void BsdInitIndSmall(TableVar Variables, float porcVar, int neje, Individual subgroup) {

        cromosoma.BsdInitCromSmall(Variables, porcVar, subgroup);  // Random initialization method
        evaluado = false;                           // Individual not evaluated
        for (int i=0; i<neje; i++){
            cubre[i] = false;
        }

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;
    }*/
    /**
     * <p>
     * Creates instance of Canonical individual based on coverage
     * </p>
     *
     * @param pop Actual population
     * @param Variables Variables structure
     * @param Examples Examples structure
     * @param porcCob Percentage of variables to form the individual
     * @param nobj Number of objectives
     * @param nFile File to write the individual
     */
    @Override
    public void CobInitInd(Population pop, ArrayList<Instance> Examples, float porcCob, int nobj, int clas, String nFile) {
        cromosoma.CobInitCrom(pop, Examples, porcCob, nobj, clas);

        evaluado = false;
        cubre.clear(0, Examples.size());
        crowdingDistance = 0.0;
        n_eval = 0;

        this.clas = clas;
    }

    /**
     * <p>
     * Returns the Chromosome
     * </p>
     *
     * @return Chromosome
     */
    public CromCAN getIndivCrom() {
        return cromosoma;
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     *
     * @param pos Position of the gene
     * @return Value of the gene
     */
    @Override
    public int getCromElem(int pos) {
        return cromosoma.getCromElem(pos);
    }

    /**
     * <p>
     * Returns the value of the indicated gene for the variable
     * </p>
     *
     * @param pos Position of the variable
     * @param elem Position of the gene
     * @return Value of the gene
     */
    @Override
    public boolean getCromGeneElem(int pos, int elem) {
        return false;
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     *
     * @param pos Position of the variable
     * @param val Value of the variable
     */
    @Override
    public void setCromElem(int pos, int val) {
        cromosoma.setCromElem(pos, val);
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     *
     * @param pos Position of the variable
     * @param elem Position of the gene
     * @param val Value of the variable
     */
    @Override
    public void setCromGeneElem(int pos, int elem, boolean val) {
    }

    /**
     * <p>
     * Returns the indicated Chromosome
     * </p>
     *
     * @return The canonical Chromosome
     */
    @Override
    public CromCAN getIndivCromCAN() {
        return cromosoma;
    }

    /**
     * <p>
     * Returns the indicated Chromosome
     * </p>
     *
     * @return The DNF Chromosome
     */
    @Override
    public CromDNF getIndivCromDNF() {
        return null;
    }

    /**
     * <p>
     * Copy the indicaded individual in "this" individual
     * </p>
     *
     * @param a The individual to Copy
     * @param neje Number of examples
     * @param nobj Number of objectives
     */
    @Override
    public void copyIndiv(Individual a, int neje, int nobj) {
        for (int i = 0; i < this.tamano; i++) {
            this.setCromElem(i, a.getCromElem(i));
        }

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.cubre.clear(0, neje);
        this.cubre.or(a.cubre);

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.cubre = (BitSet) a.cubre.clone();
        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setRank(a.getRank());

        this.objs = (ArrayList<QualityMeasure>) a.objs.clone();
        this.medidas = (ArrayList<QualityMeasure>) a.medidas.clone();
        this.conf = (Confidence) a.conf.copy();

        this.clas = a.clas;

        this.setNEval(a.getNEval());
    }

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     *
     * @param AG Genetic algorithm
     * @param Variables Variables structure
     * @param Examples Examples structure
     */
    @Override
    public void evalInd(ArrayList<Instance> Examples, ArrayList<QualityMeasure> objs, boolean isTrain) {

        double disparoFuzzy, disparoCrisp;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);

        int numVarNoInterv = 0;  // Number of variables not taking part in the individual

        for (int i = 0; i < Examples.size(); i++) { // For each example of the dataset
            // Initialization
            Instance inst = Examples.get(i);
            disparoFuzzy = 1;
            disparoCrisp = 1;
            numVarNoInterv = 0;

            // Compute all chromosome values
            for (int j = 0; j < inst.numInputAttributes(); j++) {
                if (!inst.attribute(j).isNumeric()) {  // Discrete Variable
                    if (cromosoma.getCromElem(j) <= inst.attribute(j).numValues()) {
                        // Variable j takes part in the rule
                        if ((((Double) inst.valueInputAttribute(j)).intValue() != cromosoma.getCromElem(j)) && (!inst.isMissing(j))) {
                            // If chromosome value <> example value, and example value is not a lost value
                            disparoFuzzy = 0;
                            disparoCrisp = 0;
                        }
                    } else {
                        numVarNoInterv++;  // Variable does not take part
                    }
                } else {	// Continuous variable
                    if (cromosoma.getCromElem(j) < StreamMOEAEFEP.nLabel) {
                        // Variable takes part in the rule
                        // Fuzzy computation
                        if (!inst.isMissing(j)) {
                            // If the value is not a lost value
                            float pertenencia = StreamMOEAEFEP.Fuzzy(j, cromosoma.getCromElem(j), inst.valueInputAttribute(j));
                            disparoFuzzy = Math.min(disparoFuzzy, pertenencia);
                        }
                        // Crisp computation
                        if (!inst.isMissing(j)) {
                            if (NumInterv(inst.valueInputAttribute(j), j, inst) != cromosoma.getCromElem(j)) {
                                disparoCrisp = 0;
                            }
                        }
                    } else {
                        numVarNoInterv++;  // Variable does not take part
                    }
                }
            } // End FOR all chromosome values

            // Update counters
            if (numVarNoInterv < inst.numInputAttributes()) {
                if (disparoFuzzy > 0) {
                    cubre.set(i); // Cambiar dos líneas más abajo si el token competition se va a hacer por clase.
                    if (((Double) inst.classValue()).intValue() == this.getClas()) {
                        confMatrix.setTp(confMatrix.getTp() + 1);
                    } else {
                        confMatrix.setFp(confMatrix.getFp() + 1);
                    }
                } else {
                    if (((Double) inst.classValue()).intValue() == this.getClas()) {
                        confMatrix.setFn(confMatrix.getFn() + 1);
                    } else {
                        confMatrix.setTn(confMatrix.getTn() + 1);
                    }
                }
            }
            /*if(disparoCrisp > 0){
                Double v = inst.classValue();
                cubreClase[v.intValue()]++;
            }*/

        }

        // Compute the objective quality measures
        if (this.objs.isEmpty()) {
            objs.forEach((q) -> {
                // If it is empty, then the measures are not created, copy the default objects
                // from the objectives array
                this.objs.add(q);
            });
        }

        this.objs.stream().filter((q) -> (!(q instanceof NULL))).forEachOrdered((q) -> {
            // Calculate if it is not the null measure.
            q.getValue(confMatrix);
        });

        // Compute the confidence
        this.conf.getValue(confMatrix);

        // Compute the diversity function
        this.diversityMeasure.getValue(confMatrix);

        evaluado = true;

    }

    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which
     * belongs the value. It is performed seeking the greater belonging degree
     * of the value to the fuzzy sets defined for the variable
     * </p>
     *
     * @param value Value to calculate
     * @param num_var Number of the variable
     * @param Variables Variables structure
     * @return Number of the interval
     */
    @Override
    public int NumInterv(double value, int num_var, Instance inst) {
        float pertenencia = 0, new_pert = 0;
        int interv = -1;

        for (int i = 0; i < StreamMOEAEFEP.nLabel; i++) {
            new_pert = StreamMOEAEFEP.Fuzzy(num_var, i, value);
            if (new_pert > pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;

    }

    /**
     * <p>
     * Method to Print the contents of the individual
     * </p>
     *
     * @param nFile File to write the individual
     */
    @Override
    public void Print(String nFile) {
        String contents;
        cromosoma.Print(nFile);

        contents = "DistanceCrowding " + this.getCrowdingDistance() + "\n";
        contents += "Evaluated - " + evaluado + "\n";
        contents += "Evaluacion Generado " + n_eval + "\n\n";
        if ("".equals(nFile)) {
            System.out.print(contents);
        } else {
            File.AddtoFile(nFile, contents);
        }
    }

    /**
     * Performs a two-point crossover between the elements of two indivualds. It
     * returns two individuals of the same population
     *
     * @param dad
     * @param mom
     * @return
     */
    public static Individual[] crossTwoPoints(Individual dad, Individual mom) {
        IndCAN dad1 = (IndCAN) dad;
        IndCAN mom1 = (IndCAN) mom;

        Individual[] offspring = {
            new IndCAN(dad1.cromosoma.getCromLength(), dad1.cubre.size(), dad1.objs.size()),
            new IndCAN(mom1.cromosoma.getCromLength(), mom1.cubre.size(), mom1.objs.size())
        };

        // Copy parents to the offspring (they will be modified later)
        offspring[0].copyIndiv(dad, dad1.objs.size(), dad1.cubre.size());
        offspring[1].copyIndiv(mom, mom1.objs.size(), mom1.cubre.size());

        // Select the two point to cross
        int xpoint1 = Randomize.Randint(0, dad1.cromosoma.getCromLength() - 1);
        int xpoint2;

        if (xpoint1 != dad1.cromosoma.getCromLength() - 1) {
            xpoint2 = Randomize.Randint(xpoint1 + 1, dad1.cromosoma.getCromLength() - 1);
        } else {
            xpoint2 = dad1.cromosoma.getCromLength() - 1;
        }

        // Perform the cross
        for (int i = xpoint1; i <= xpoint2; i++) {
            offspring[1].setCromElem(i, dad.getCromElem(i));
            offspring[0].setCromElem(i, mom.getCromElem(i));
        }

        offspring[0].setIndivEvaluated(false);
        offspring[1].setIndivEvaluated(false);

        return offspring;
    }

    @Override
    public void mutate(Instance inst, float mutProb) {
        for (int pos = 0; pos < inst.numInputAttributes(); pos++) {
            if (Randomize.Randdouble(0.0, 1.0) <= mutProb) {
                // MUTATES THE GENE
                if (Randomize.Randint(0, 10) <= 5) {
                    // REMOVE THE SELECTED VARIABLE 
                    if (inst.attribute(pos).isNumeric()) {
                        cromosoma.setCromElem(pos, StreamMOEAEFEP.nLabel);
                    } else {
                        cromosoma.setCromElem(pos, inst.attribute(pos).numValues());
                    }
                } else {
                    // SETS A RANDOM VALUE ON THE VARIABLE
                    if (inst.attribute(pos).isNumeric()) {
                        cromosoma.setCromElem(pos, StreamMOEAEFEP.nLabel);
                    } else {
                        cromosoma.setCromElem(pos, inst.attribute(pos).numValues());
                    }
                }
            }
        }

        // Set indiv as non-evaluated
        evaluado = false;
    }

    @Override
    public void mutate(Instance inst, int pos) {

        if (Randomize.Randint(0, 10) <= 5) {
            // REMOVE THE SELECTED VARIABLE 
            if (inst.attribute(pos).isNumeric()) {
                cromosoma.setCromElem(pos, StreamMOEAEFEP.nLabel);
            } else {
                cromosoma.setCromElem(pos, inst.attribute(pos).numValues());
            }
        } else {
            // SETS A RANDOM VALUE ON THE VARIABLE
            if (inst.attribute(pos).isNumeric()) {
                cromosoma.setCromElem(pos, StreamMOEAEFEP.nLabel);
            } else {
                cromosoma.setCromElem(pos, inst.attribute(pos).numValues());
            }
        }

        // Set indiv as non-evaluated
        evaluado = false;

    }

}
