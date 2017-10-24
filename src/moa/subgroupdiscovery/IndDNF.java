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
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import org.core.File;

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
     * @param lenght          Lenght of the individual
     * @param neje              Number of examples
     * @param nobj              Number of objectives
     * @param Variables         Variables structure
     */
    public IndDNF(int lenght, int neje, int nobj, Instance inst) {

          tamano = lenght;
          cromosoma = new CromDNF(lenght, inst, StreamMOEAEFEP.nLabel);
          medidas = new ArrayList<>();
          objs = new ArrayList<>();
          conf = new Confidence();

          evaluado = false;
          cubre = new BitSet(neje);

          crowdingDistance = 0.0;
          n_eval = 0;

    }

    /**
     * <p>
     * Creates rangom instance of DNF individual
     * </p>
     * @param Variables             Variables structure
     * @param neje                  Number of exaples
     * @param nFile                 File to write the individual
     */
    @Override
    public void RndInitInd(Instance inst, int neje, String nFile) {
        cromosoma.RndInitCrom();        // Random initialization method
        evaluado = false;               // Individual not evaluated
        cubre.clear(0, neje);
        crowdingDistance = 0.0;
        n_eval = 0;
    }

    /**
     * <p>
     * Creates biased instance of DNF individual
     * </p>
     * @param Variables             Variables structure
     * @param porcVar               Percentage of variables to form the individual
     * @param neje                  Number of exaples
     * @param nFile                 File to write the individual
     */
    @Override
    public void BsdInitInd(Instance inst, float porcVar, int neje, String nFile) {

        cromosoma.BsdInitCrom(inst, porcVar);  // Random initialization method
        evaluado = false;                           // Individual not evaluated
        cubre.set(0,neje);
          
        crowdingDistance = 0.0;
        n_eval = 0;
    }

    /**
     * <p>
     * Creates nstance of DNF individual based on coverage
     * </p>
     * @param pop           Actual population
     * @param Variables     Variables structure
     * @param Examples      Examples structure
     * @param porcCob       Percentage of variables to form the individual
     * @param nobj          Number of objectives
     * @param nFile         File to write the individual
     */
    @Override
    public void CobInitInd(Population pop, ArrayList<Instance> Examples, float porcCob, int nobj, int clas, String nFile) {
        
        cromosoma.CobInitCrom(pop, Examples, porcCob, nobj, clas);
        evaluado = false;

        cubre.clear(0, Examples.size());

        crowdingDistance = 0.0;
        n_eval = 0;
    }

    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromDNF getIndivCrom () {
        return cromosoma;
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @return                  Value of the gene
     */
    @Override
    public boolean getCromGeneElem (int pos, int elem) {
        return cromosoma.getCromGeneElem (pos, elem);
    }


    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the gene
     * @return                  Value of the gene
     */
    @Override
    public int getCromElem(int pos){
        return 0;
    }


    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @param val               Value of the variable
     */
    @Override
    public void setCromGeneElem (int pos, int elem, boolean val) {
        cromosoma.setCromGeneElem(pos, elem, val);
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param val               Value of the variable
     */
    @Override
    public void setCromElem(int pos, int val){ }


    /**
     * <p>
     * Returns the indicated Chromosome
     * </p>
     * @return                  The DNF Chromosome
     */
    @Override
    public CromDNF getIndivCromDNF(){
        return cromosoma;
    }

    /**
     * <p>
     * Copy the indicaded individual in "this" individual
     * </p>
     * @param a              The individual to Copy
     * @param neje              Number of examples
     * @param nobj              Number of objectives
     */
    @Override
    public void copyIndiv (Individual a, int neje, int nobj) {
        int number;
        for (int i=0;i<this.tamano;i++) {
            number = a.getIndivCromDNF().getCromGeneLenght(i);
            for (int j=0;j<=number;j++) {  
            	this.setCromGeneElem(i,j, a.getCromGeneElem(i,j));
           }
        }
        this.setIndivEvaluated(a.getIndivEvaluated());
        a.cubre = (BitSet) this.cubre.clone();
        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setRank(a.getRank());
        
        a.objs = (ArrayList<QualityMeasure>) this.objs.clone();
        a.medidas = (ArrayList<QualityMeasure>) this.medidas.clone();
        a.conf = (Confidence) this.conf.copy();
        
        this.setNEval(a.getNEval());

    }

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     * @param AG                Genetic algorithm
     * @param Variables         Variables structure
     * @param Examples          Examples structure
     */
    public void evalInd (Genetic AG, ArrayList<Instance> Examples) {
        
        /*int ejCompAntFuzzy=0;                // Number of compatible examples with the antecedent of any class - fuzzy version --- unused
        int ejCompAntCrisp=0;                // Number of compatible examples with the antecedent of any class - crisp version
        int ejCompAntClassFuzzy=0;           // Number of compatible examples (antecedent and class) - fuzzy version
        int ejCompAntClassCrisp=0;           // Number of compatible examples (antecedent and class) - crisp version
        //int ejCompAntClassNewFuzzy=0;        // Number of new covered compatible examples (antec and class) - fuzzy version
        //int ejCompAntClassNewCrisp=0;        // Number of new covered compatible examples (antec and class) - crisp version

        float gradoCompAntFuzzy=0;           // Total compatibility degree with the antecedent - fuzzy version
        float gradoCompAntClassFuzzy=0;      // Tot compatibility degree with antecedent and class - fuzzy version
        float gradoCompAntClassNewEjFuzzy=0; // Tot compatibility degree with antecedent and class of new covered examples - fuzzy version

        float disparoFuzzy;    // Final compatibility degree of the example with the individual - fuzzy version
        float disparoCrisp;    // Final compatibility degree of the example with the individual - crisp version

        float completitud, fsupport, csupport, confianza, cconfianza;
        float unusualness, coverage, accuracy, significance;

        float valorConf, valorComp;   // Variables to store the selected measures

        float tp = 0;
        float fp = 0;
        */
        //int ejClase[] = new int[Variables.getNClass()];
        double disparoFuzzy, disparoCrisp;
        ContingencyTable confMatrix = new ContingencyTable(0, 0, 0, 0);
        int cubreClase[] = new int[StreamMOEAEFEP.EjClass.size()];
        for (int i=0; i < cubreClase.length; i++) {
            cubreClase[i]=0;
        }

        //int por_cubrir;        // Number of examples of the class not covered yet - for fuzzy version

        int numVarNoInterv=0;  // Number of variables not taking part in the individual
        
        
        for(Instance inst : Examples){
            disparoCrisp = disparoFuzzy = 1;
            numVarNoInterv = 0;
            
            for(int i = 0; i < inst.numInputAttributes(); i++){
                if(inst.attribute(i).isNominal()){
                    // Discrete variable
                    if(cromosoma.getCromGeneElem(i, inst.attribute(i).numValues())){
                        // Variable i participate in the rule
                        Double value = inst.valueInputAttribute(i);
                        if(!cromosoma.getCromGeneElem(i, value.intValue()) && ! inst.isMissing(i)){
                            // The rules does cover the examples
                            disparoCrisp = disparoFuzzy = 0;
                        }
                    } else {
                        numVarNoInterv++; // The variable does not participate in the rule
                    }
                } else {
                    // Continuous variable
                    if(cromosoma.getCromGeneElem(i, StreamMOEAEFEP.nLabel)){
                        // Variable participate in the rule
                        if(! inst.isMissing(i)){
                            // Fuzzy computation
                            float pertenencia = 0;
                            float pert;
                            for(int j = 0; j < StreamMOEAEFEP.nLabel; j++){
                                if(cromosoma.getCromGeneElem(i, j)){
                                    pert = StreamMOEAEFEP.Fuzzy(i, j, inst.valueInputAttribute(i));
                                } else {
                                    pert = 0;
                                }
                                pertenencia = Math.max(pertenencia, pert);
                            }
                            disparoFuzzy = Math.min(disparoFuzzy, pertenencia);
                            
                            // Crisp conputation
                            if(!cromosoma.getCromGeneElem(i, NumInterv(inst.valueInputAttribute(i), i, inst))){
                                disparoCrisp = 0;
                            }
                        }
                    } else {
                        numVarNoInterv++;
                    }
                }
            }
            
            // Update counters
            if(disparoFuzzy > 0){
                if( ((Double) inst.classValue()).intValue() == this.clas){
                   confMatrix.setTp(confMatrix.getTp() + 1);
                } else {
                   confMatrix.setFp(confMatrix.getFp() + 1);
                }
            } else {
                if( ((Double) inst.classValue()).intValue() == this.clas){
                    confMatrix.setFn(confMatrix.getFn() + 1);
                } else {
                    confMatrix.setTn(confMatrix.getTn() + 1);
                }
            }
            
            /*if(disparoCrisp > 0){
                Double v = inst.classValue();
                cubreClase[v.intValue()]++;
            }*/
            
        }
        
        // Compute the objective quality measures
        


        // Compute the measures

        for(int j=0; j<AG.getNumObjectives(); j++){

            if(AG.getNObjectives(j).compareTo("AUC")==0){
                float success = (1+((tp)/(Examples.getExamplesClass(Variables.getNumClassObj())))-((fp)/(Examples.getExamplesClass(Variables.getNumClassObj()))))/2;
                medidas.setObjectiveValue(j, success);
            }
            if(AG.getNObjectives(j).compareTo("COMP")==0){
                if (Examples.getExamplesClassObj() != 0)
                    completitud = ((float)ejCompAntClassFuzzy/Examples.getExamplesClassObj());
                else
                    completitud = 0;
                valorComp = completitud;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, valorComp);
            }
            if(AG.getNObjectives(j).compareTo("CSUP")==0){
                csupport = ((float)ejCompAntClassCrisp/Examples.getNEx());
                valorComp = csupport;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, valorComp);
            }
            if(AG.getNObjectives(j).compareTo("FSUP")==0){
                if (Examples.getNEx() != 0)
                    fsupport = ((float)gradoCompAntClassFuzzy/Examples.getNEx());
                else
                    fsupport = 0;
                valorComp = fsupport;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, valorComp);
            }
            if(AG.getNObjectives(j).compareTo("CCNF")==0){
                if (ejCompAntCrisp != 0)
                    cconfianza = (float)ejCompAntClassCrisp/ejCompAntCrisp;
                else
                    cconfianza = 0;
                valorConf = cconfianza;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, valorConf);
            }
            if(AG.getNObjectives(j).compareTo("FCNF")==0){
                if (gradoCompAntFuzzy != 0)
                    confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
                else
                    confianza = 0;
                valorConf = confianza;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, valorConf);
            }
            if(AG.getNObjectives(j).compareTo("UNUS")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                if (ejCompAntCrisp==0)
                    unusualness = 0;
                else
                    unusualness =  coverage * ( (float)ejCompAntClassCrisp/ejCompAntCrisp - (float)Examples.getExamplesClassObj()/Examples.getNEx());

                float normUnus = unusualness + (float)Examples.getExamplesClassObj()/Examples.getNEx();
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, normUnus);
            }
            if(AG.getNObjectives(j).compareTo("SIGN")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                float sumaSignClase=0;
                for (int aux=0; aux<Variables.getNClass(); aux++) {
                    if (cubreClase[aux]!=0)
                        sumaSignClase += cubreClase[aux] * Math.log10 ((float)cubreClase[aux]/(ejClase[aux]*coverage));
                }
                significance = 2 * sumaSignClase;
                float maxSignif=0;
                for (int a=0; a<Variables.getNClass(); a++) {
                    if (cubreClase[a]!=0 && coverage!=0)
                        maxSignif += cubreClase[a] * Math.log10 ((float)1/coverage);
                }
                maxSignif = 2*maxSignif ;  
                float normSignif=0;
                if (maxSignif!=0)
                   normSignif = significance/maxSignif;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, normSignif);
            }
            if(AG.getNObjectives(j).compareTo("ACCU")==0){
                accuracy = (float)(ejCompAntClassCrisp+1) / (ejCompAntCrisp + Variables.getNClass());
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, accuracy);
            }
            if(AG.getNObjectives(j).compareTo("COVE")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(j, 0);
                else medidas.setObjectiveValue(j, coverage);
            }

        }

        if (gradoCompAntFuzzy != 0)
            confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
        else
            confianza = 0;

        valorConf = confianza;
        medidas.setCnf(valorConf);

        evaluado = true;

    }

    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which belongs
     * the value. It is performed seeking the greater belonging degree of the
     * value to the fuzzy sets defined for the variable
     * </p>
     * @param valor                 Value to calculate
     * @param num_var               Number of the variable
     * @param Variables             Variables structure
     * @return                      Number of the interval
     */
    @Override
    public int NumInterv (double valor, int num_var, Instance inst) {
        float pertenencia=0, new_pert=0;
        int interv = -1;

        for (int i=0; i < StreamMOEAEFEP.nLabel; i++) {
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
     * @param nFile             File to write the individual
     */
    public void Print(String nFile) {
        String contents;
        cromosoma.Print(nFile);

        contents = "DistanceCrowding "+ this.getCrowdingDistance()+ "\n";
        contents+= "Evaluated - " + evaluado + "\n";
        contents+= "Evaluacion Generado " + n_eval + "\n\n";
        if (nFile=="")
            System.out.print (contents);
        else
           File.AddtoFile(nFile, contents);
    }


}
