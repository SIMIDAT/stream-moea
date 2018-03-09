/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package moa.subgroupdiscovery;

import moa.subgroupdiscovery.genetic.individual.GeneDNF;
import moa.subgroupdiscovery.genetic.Individual;
import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.options.ClassOption;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.File;
import org.core.ClassLoader;
import org.core.Randomize;

public class IndDNF extends Individual {

    /**
     * <p>
     * Defines the DNF individual of the population
     * </p>
     */
    public CromDNF cromosoma;   // Individual contents

    /**
     * <p>
     * Creates new instance of Individual
     * </p>
     *
     * @param lenght Lenght of the individual
     * @param neje Number of examples
     * @param nobj Number of objectives
     * @param Variables Variables structure
     */
    public IndDNF(int lenght, int neje, int nobj, Instance inst, int clas) {

        try {
            tamano = lenght;
            cromosoma = new CromDNF(lenght, inst, StreamMOEAEFEP.nLabel);
            medidas = new ArrayList<>();
            objs = new ArrayList<>();
            conf = new Confidence();
            diversityMeasure = (QualityMeasure) StreamMOEAEFEP.getDiversityMeasure().getClass().newInstance();
            this.clas = clas;

            evaluado = false;
            cubre = new BitSet(neje);

            crowdingDistance = 0.0;
            n_eval = 0;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * <p>
     * Creates rangom instance of DNF individual
     * </p>
     *
     * @param Variables Variables structure
     * @param neje Number of exaples
     * @param nFile File to write the individual
     */
    @Override
    public void RndInitInd(Instance inst, int neje, String nFile, int clas) {
        cromosoma.RndInitCrom();        // Random initialization method
        setEvaluado(false);               // Individual not evaluated
        getCubre().clear(0, neje);
        setCrowdingDistance(0.0);
        setN_eval(0);
        this.setClas(clas);
    }

    /**
     * <p>
     * Creates biased instance of DNF individual
     * </p>
     *
     * @param Variables Variables structure
     * @param porcVar Percentage of variables to form the individual
     * @param neje Number of exaples
     * @param nFile File to write the individual
     */
    @Override
    public void BsdInitInd(Instance inst, float porcVar, int neje, String nFile, int clas) {

        cromosoma.BsdInitCrom(inst, porcVar);  // Random initialization method
        setEvaluado(false);                           // Individual not evaluated
        getCubre().set(0, neje);
        this.setClas(clas);
        setCrowdingDistance(0.0);
        setN_eval(0);
    }

    /**
     * <p>
     * Creates nstance of DNF individual based on coverage
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
        setEvaluado(false);
        this.setClas(clas);
        getCubre().clear(0, Examples.size());

        setCrowdingDistance(0.0);
        setN_eval(0);
    }

    /**
     * <p>
     * Returns the Chromosome
     * </p>
     *
     * @return Chromosome
     */
    public CromDNF getIndivCrom() {
        return cromosoma;
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     *
     * @param pos Position of the variable
     * @param elem Position of the gene
     * @return Value of the gene
     */
    @Override
    public boolean getCromGeneElem(int pos, int elem) {
        return cromosoma.getCromGeneElem(pos, elem);
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
        return 0;
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
        cromosoma.setCromGeneElem(pos, elem, val);
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
        return cromosoma;
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
        int number;
        for (int i = 0; i < this.getTamano(); i++) {
            number = a.getIndivCromDNF().getCromGeneLenght(i);
            for (int j = 0; j <= number; j++) {
                this.setCromGeneElem(i, j, a.getCromGeneElem(i, j));
            }
        }
        this.setIndivEvaluated(a.getIndivEvaluated());
        this.setCubre((BitSet) a.getCubre().clone());
        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setRank(a.getRank());

        this.setObjs(new ArrayList<>());
        for (QualityMeasure q : a.getObjs()) {
            QualityMeasure aux = q.clone();
            this.getObjs().add(aux);
        }

        this.setMedidas(new ArrayList<>());
        for (QualityMeasure q : a.getMedidas()) {
            QualityMeasure aux = q.clone();
            this.getMedidas().add(aux);
        }
        this.setConf((Confidence) a.getConf().clone());

        this.setNEval(a.getNEval());

        this.setClas(a.getClas());

    }

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     *
     * @param Variables Variables structure
     * @param Examples Examples structure
     */
    @Override
    public void evalInd(ArrayList<Instance> Examples, ArrayList<QualityMeasure> objs, boolean isTrain) {

        float disparoFuzzy, disparoCrisp;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);

        int numVarNoInterv = 0;  // Number of variables not taking part in the individual

        for (int k = 0; k < Examples.size(); k++) {
            Instance inst = Examples.get(k);
            disparoCrisp = disparoFuzzy = 1;
            numVarNoInterv = 0;

            for (int i = 0; i < inst.numInputAttributes(); i++) {
                if (inst.attribute(i).isNominal()) {
                    // Discrete variable
                    if (cromosoma.getCromGeneElem(i, inst.attribute(i).numValues())) {
                        // Variable i participate in the rule
                        Double value = inst.valueInputAttribute(i);
                        if (!cromosoma.getCromGeneElem(i, value.intValue()) && !inst.isMissing(i)) {
                            // The rules does cover the examples
                            disparoCrisp = disparoFuzzy = 0;
                        }
                    } else {
                        numVarNoInterv++; // The variable does not participate in the rule
                    }
                } else {
                    // Continuous variable
                    if (cromosoma.getCromGeneElem(i, StreamMOEAEFEP.nLabel)) {
                        // Variable participate in the rule
                        if (!inst.isMissing(i)) {
                            // Fuzzy computation
                            float pertenencia = 0;
                            float pert;
                            for (int j = 0; j < StreamMOEAEFEP.nLabel; j++) {
                                if (cromosoma.getCromGeneElem(i, j)) {
                                    pert = StreamMOEAEFEP.Fuzzy(i, j, inst.valueInputAttribute(i));
                                } else {
                                    pert = 0;
                                }
                                pertenencia = Math.max(pertenencia, pert);
                            }
                            disparoFuzzy = Math.min(disparoFuzzy, pertenencia);

                            // Crisp conputation
                            if (!cromosoma.getCromGeneElem(i, NumInterv(inst.valueInputAttribute(i), i, inst))) {
                                disparoCrisp = 0;
                            }
                        }
                    } else {
                        numVarNoInterv++;
                    }
                }
            }

            // Update counters
            if (numVarNoInterv < inst.numInputAttributes()) {
                if (disparoFuzzy > 0) {
                    getCubre().set(k); // Cambiar dos líneas más abajo si el token competition se va a hacer por clase.
                    if (((Double) inst.classValue()).intValue() == this.getClas()) {
                        confMatrix.setTp(confMatrix.getTp() + 1);
                    } else {
                        confMatrix.setFp(confMatrix.getFp() + 1);
                    }
                } else {
                    getCubre().clear(k);
                    if (((Double) inst.classValue()).intValue() == this.getClas()) {
                        confMatrix.setFn(confMatrix.getFn() + 1);
                    } else {
                        confMatrix.setTn(confMatrix.getTn() + 1);
                    }
                }
            }
        }

        // calculate the quality measures
        this.calculateMeasures(confMatrix, objs, isTrain);

        // Set individual as evaluated
        setEvaluado(true);
    }

    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which
     * belongs the value. It is performed seeking the greater belonging degree
     * of the value to the fuzzy sets defined for the variable
     * </p>
     *
     * @param valor Value to calculate
     * @param num_var Number of the variable
     * @param Variables Variables structure
     * @return Number of the interval
     */
    @Override
    public int NumInterv(double valor, int num_var, Instance inst) {
        float pertenencia = 0, new_pert = 0;
        int interv = -1;

        for (int i = 0; i < StreamMOEAEFEP.nLabel; i++) {
            new_pert = StreamMOEAEFEP.Fuzzy(num_var, i, valor);
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
    public void Print(String nFile) {
        String contents;
        cromosoma.Print(nFile);

        contents = "DistanceCrowding " + this.getCrowdingDistance() + "\n";
        contents += "Evaluated - " + isEvaluado() + "\n";
        contents += "Evaluacion Generado " + getN_eval() + "\n\n";
        if (nFile == "") {
            System.out.print(contents);
        } else {
            File.AddtoFile(nFile, contents);
        }
    }

    @Override
    public CromCAN getIndivCromCAN() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Performs a two-point crossover between the elements of two indivualds. It
     * returns two individuals of the same population
     *
     * @param dad
     * @param mom
     * @param inst
     * @return
     */
    public static Individual[] crossTwoPoints(Individual dad, Individual mom, Instance inst) {
        IndDNF dad1 = (IndDNF) dad;
        IndDNF mom1 = (IndDNF) mom;

        Individual[] offspring = {
            new IndDNF(dad1.cromosoma.getCromLenght(), dad1.getCubre().size(), dad1.getObjs().size(), inst, 0),
            new IndDNF(mom1.cromosoma.getCromLenght(), mom1.getCubre().size(), mom1.getObjs().size(), inst, 0)
        };

        offspring[0].copyIndiv(dad, dad1.getObjs().size(), dad1.getCubre().size());
        offspring[1].copyIndiv(mom, mom1.getObjs().size(), mom1.getCubre().size());

        // Generation of the two point of cross
        int xpoint1 = Randomize.Randint(0, (dad1.cromosoma.getCromLenght() - 1));
        int xpoint2;
        if (xpoint1 != dad1.cromosoma.getCromLenght() - 1) {
            xpoint2 = Randomize.Randint((xpoint1 + 1), (dad1.cromosoma.getCromLenght() - 1));
        } else {
            xpoint2 = dad1.cromosoma.getCromLenght() - 1;
        }

        // Cross the parts between both points
        for (int i = xpoint1; i <= xpoint2; i++) {
            int number = offspring[0].getIndivCromDNF().getCromGeneLenght(i);

            for (int ii = 0; ii <= number; ii++) {
                offspring[0].setCromGeneElem(i, ii, mom.getCromGeneElem(i, ii));
                offspring[1].setCromGeneElem(i, ii, dad.getCromGeneElem(i, ii));
            }
        }

        offspring[0].setIndivEvaluated(false);
        offspring[1].setIndivEvaluated(false);

        return offspring;
    }

    @Override
    public void mutate(Instance inst, float mutProb) {
        for (int pos = 0; pos < inst.numInputAttributes(); pos++) {
            if (((Double) Randomize.RandClosed()).floatValue() < mutProb) {
                if (Randomize.Randint(0, 10) <= 5) {
                    // REMOVE THE SELECTED VARIABLE 
                    cromosoma.eraseVariable(pos);
                } else {
                    // SETS A RANDOM VALUE ON THE VARIABLE
                    cromosoma.randomChange(pos);

                }
            }
        }
        // Set indiv as non-evaluated
        setEvaluado(false);
    }

    @Override
    public void mutate(Instance inst, int pos) {

        if (Randomize.Randint(0, 10) <= 5) {
            // REMOVE THE SELECTED VARIABLE 
            cromosoma.eraseVariable(pos);
        } else {
            // SETS A RANDOM VALUE ON THE VARIABLE
            cromosoma.randomChange(pos);
        }

        // Set indiv as non-evaluated
        setEvaluado(false);

    }

    @Override
    public boolean isEmpty() {
        for (GeneDNF g : cromosoma.cromosoma) {
            if (!g.isNonParticipant()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString(Instance inst) {
        CromDNF regla = this.cromosoma;
        String content = "";
        for (int i = 0; i < inst.numInputAttributes(); i++) {
            if (!regla.isNonParticipant(i)) {
                if (inst.attribute(i).isNominal()) {
                    // discrete variable
                    content = "\tVariable " + inst.attribute(i).name() + " = ";
                    for (int j = 0; j < inst.attribute(i).numValues(); j++) {
                        if (regla.getCromGeneElem(i, j)) {
                            content += inst.attribute(i).value(j) + "  ";
                        }
                    }
                    content += "\n";
                } else {
                    // continuous variable
                    content += "\tVariable " + inst.attribute(i).name() + " = ";
                    for (int j = 0; j < StreamMOEAEFEP.nLabel; j++) {
                        if (regla.getCromGeneElem(i, j)) {
                            content += "Label " + j;
                            content += " (" + StreamMOEAEFEP.baseDatos[i][j].getX0();
                            content += " " + StreamMOEAEFEP.baseDatos[i][j].getX1();
                            content += " " + StreamMOEAEFEP.baseDatos[i][j].getX3() + ")\t";
                        }
                    }
                    content += "\n";
                }
            }
        }

        content += "\tConsecuent: " + inst.outputAttribute(0).value(getClas()) + "\n\n";
        return content;
    }

    @Override
    public int getNumVars() {
        int vars = 0;
        for (int i = 0; i < this.cromosoma.getCromLenght(); i++) {
            if(! this.cromosoma.isNonParticipant(i)){
                vars++;
            }
        }
        return vars;
    }

}
