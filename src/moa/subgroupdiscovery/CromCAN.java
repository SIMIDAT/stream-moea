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
import org.core.*;

public class CromCAN {
    /**
     * <p>
     * Defines the structure and manage the contents of a rule
     * This implementation uses only integer values to store the gens.
     * So, variables values must be discretized (if they are continuous)
     * or translated into integers (if they are enumerated)
     * </p>
     */

      private int num_genes;      // Number of genes
      private int cromosoma [];   // Individual content - integer representation

      
    /**
     * <p>
     * Creates new instance of chromosome, no initialization
     * </p>
     * @param lenght          Length of the chromosome
     */
    public CromCAN(int lenght) {
      num_genes = lenght;
      cromosoma = new int [lenght];
    }


    /**
     * <p>
     * Random initialization of an existing chromosome
     * </p>
     * @param Variables		Contents the type of the variable, and the number of labels.
     */
    public void RndInitCrom(Instance inst, int nlabels) {

        for (int i=0; i<num_genes; i++)  {
            if(inst.attribute(i).isNumeric())
                cromosoma[i] = Randomize.RandintClosed(0, nlabels);
            else 
                cromosoma[i] = Randomize.RandintClosed(0, inst.attribute(i).numValues());
        }
        
    }


    /**
     * <p>
     * Biased Random initialization of an existing chromosome
     * </p>
     * @param Variables		Contents the type of the variable, and the number of labels.
     * @param porcVar           Percentage of participating variables
     */
    public void BsdInitCrom(Instance inst, float porcVar, int nlabels) {

        int num_var;

        // This array indicates if every chromosome has been initialised
        boolean crom_inic[]= new boolean[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = false;

        // Firtly, we obtain the numbero of variable which are in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcVar*inst.numInputAttributes()));

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.RandintClosed (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                
                if(inst.attribute(num_var).isNumeric())
                    cromosoma[num_var] = Randomize.Randint (0, nlabels);
                else 
                    cromosoma[num_var] = Randomize.Randint(0, inst.attribute(num_var).numValues());
                
                crom_inic[num_var]=true;
                var++;
            }
        }

        // Initialise the rest variables
        for (int i=0; i<num_genes; i++)  {
            if (crom_inic[i]==false) {
                if(inst.attribute(i).isNumeric())
                    cromosoma[i] = nlabels;
                else
                    cromosoma[i] = inst.attribute(i).numValues();
            }
        }
    }


    /**
     * <p>
     * Random initialization of an existing chromosome for small disjunct
     * </p>
     * @param Variables		Contents the type of the variable, and the number of labels.
     */
    /*public void RndInitCromSmall(TableVar Variables, Individual subgroup) {

        CromCAN sd = subgroup.getIndivCromCAN();

        for (int i=0; i<num_genes; i++)  {
            if(sd.getCromElem(i)<Variables.getNLabelVar(i)){
                cromosoma[i] = sd.getCromElem(i);
            } else {
                cromosoma[i] = Randomize.Randint (0, Variables.getNLabelVar(i));
            }
        }
    }*/


    /**
     * <p>
     * Biased Random initialization of an existing chromosome for small disjunct
     * </p>
     * @param Variables		Contents the type of the variable, and the number of labels.
     * @param porcVar           Percentage of participating variables
     */
    /*public void BsdInitCromSmall(TableVar Variables, float porcVar, Individual subgroup) {

        int num_var;

        CromCAN sd = subgroup.getIndivCromCAN();

        // This array indicates if every chromosome has been initialised
        boolean crom_inic[]= new boolean[num_genes];
        for (int i=0; i<num_genes; i++){
            if(sd.getCromElem(i)<Variables.getNLabelVar(i)) crom_inic[i] = true;
            else crom_inic[i] = false;
        }

        // Firtly, we obtain the numbero of variable which are in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcVar*Variables.getNVars()));

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                cromosoma[num_var] = Randomize.Randint (0, Variables.getNLabelVar(num_var)-1);
                crom_inic[num_var]=true;
                var++;
            }
        }

        // Initialise the rest variables
        for (int i=0; i<num_genes; i++)  {
            if (crom_inic[i]==false) {
                cromosoma[i] = Variables.getNLabelVar(i);
            }
        }
    }*/


    /**
     * <p>
     * Initialization based on coverage
     * </p>
     * @param pop               Main population
     * @param Variables		Contents the type of the variable, and the number of labels.
     * @param Examples          Dataset
     * @param porcCob           Percentage of participating variables
     * @param nobj              Number of objectives of the algorithm
     */
    public void CobInitCrom(Population pop, ArrayList<Instance> instances, float porcCob, int nobj, int clas) {
        Instance aux = instances.get(0);
        int num_var;

        boolean crom_inic[] = new boolean[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = false;

        // Number of participating variables in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcCob * aux.numInputAttributes()));

        boolean centi = false;
        int aleatorio = 0;
        int ii=0;
        while((!centi)&&(ii<instances.size())){
            aleatorio = Randomize.Randint(0, instances.size()-1);
            if((! pop.ej_cubiertos.get(aleatorio)) && (instances.get(aleatorio).classIndex() == clas))
                centi = true;
            ii++;
        }

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                if (aux.attribute(num_var).isNumeric()) { //Continuous variable
                    // Put in the correspondent interval 
                    float pertenencia=0, new_pert=0;
                    int interv = Variables.getNLabelVar(num_var);
                    for (int i=0; i < Variables.getNLabelVar(num_var); i++) {
                        new_pert = Variables.Fuzzy(num_var,i,(int) Examples.getDat(aleatorio, num_var));
                        if (new_pert>pertenencia) {
                            interv = i;
                            pertenencia = new_pert;
                        }
                    }
                    cromosoma[num_var] = interv;
                } else { //Discrete variable
                    // Put in the correspondent value //
                    cromosoma[num_var] = (int) Examples.getDat(aleatorio, num_var);
                }
                crom_inic[num_var]=true;
                var++;
            }
        }

        // Initialise the rest variables
        for (int i=0; i<num_genes; i++)  {
            if (crom_inic[i]==false) {
                if(Variables.getContinuous(i)) cromosoma[i] = Variables.getNLabelVar(i);
                else cromosoma[i] = (int) Variables.getMax(i)+1;
            }
        }
    }

    
    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos      Position of the gene
     * @return              Value of the gene
     */
    public int getCromElem (int pos) {
      return cromosoma[pos];
    }
    
    
    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos      Position of the gene
     * @param value         Value of the gene
     */
    public void setCromElem (int pos, int value) {
      cromosoma[pos] = value;
    }
    
    
    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return          Gets the lenght of the chromosome
     */
    public int getCromLength () {
      return num_genes;
    }
   
   
    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile         File to write the cromosome
     */
    public void Print(String nFile) {
        String contents;
        contents = "Chromosome: ";
        for(int i=0; i<num_genes; i++)
            contents+= cromosoma[i] + " ";
        contents+= "\n";
        if (nFile=="")
            System.out.print (contents);
        else
           File.AddtoFile(nFile, contents);
    }
    
}
