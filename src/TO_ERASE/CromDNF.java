/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package moa.subgroupdiscovery;

import moa.subgroupdiscovery.genetic.individual.GeneDNF;
import com.yahoo.labs.samoa.instances.Instance;
import java.util.ArrayList;
import org.core.*;

public class CromDNF {
     /**
      * Defines the structure and manage the contents of a rule
      * This implementation uses disjunctive formal norm to store the gens.
      * So, variables are codified in binary genes
      */

      private int num_genes;      // Number of genes
      protected GeneDNF cromosoma [];   // Individual content - integer representation

    /**
     * <p>
     * Creates new instance of chromosome, no initialization
     * </p>
     * @param lenght      Length of the chromosome
     * @param inst        The instance where information of variables is stored
     * @param nLabels     The number of labels for numeric variables
     */
    public CromDNF(int lenght, Instance inst, int nLabels) {
      num_genes = lenght;
      cromosoma = new GeneDNF[lenght];
      for(int i=0; i<num_genes; i++){
        if(inst.attribute(i).isNumeric())
            cromosoma[i] = new GeneDNF(nLabels);
        else
            cromosoma[i] = new GeneDNF(inst.attribute(i).numValues());
      }
    }


    /**
     * <p>
     * Random initialization of an existing chromosome
     * </p>
     */
    public void RndInitCrom( ) {

        for (int i=0; i<num_genes; i++)
            cromosoma[i].RndInitGene();

    }


    /**
     * <p>
     * Biased Random initialization of an existing chromosome
     * </p>
     * @param Variables 	Contents the type of the variable, and the number of labels.
     * @param porcVar           Participating variables in the chromosom
     */
    public void BsdInitCrom(Instance inst, float porcVar) {

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
                cromosoma[num_var].RndInitGene();
                crom_inic[num_var]=true;
                var++;
            }
        }
        
        // Initialise the rest of genes as not participants
        for(int i = 0; i < crom_inic.length; i++){
            if(!crom_inic[i]){
                cromosoma[i].NoTakeInitGene();
            }
        }

    }


    /**
     * <p>
     * Initialization based on coverage
     * </p>
     * @param pop               Main population
     * @param Variables		Contents the type of the variable, and the number of labels.
     * @param Examples          Dataset
     * @param porcCob           Percentage of participating variables
     * @param number_obj        Number of objectives of the algorithm
     * @param clas the class of the individuals to cover
     */
    public int CobInitCrom(Population pop, ArrayList<Instance> Examples, float porcCob, int nobj, int clas) {
        Instance aux = Examples.get(0);
        int num_var;

        boolean crom_inic[] = new boolean[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = false;

        // Number of participating variables in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcCob*aux.numInputAttributes()));

        boolean centi = false;
        int aleatorio = 0;
        int ii=0;
        while((!centi)&&(ii<Examples.size())){
            aleatorio = Randomize.Randint(0, Examples.size()-1);
            if((!pop.ej_cubiertos.get(aleatorio))&&(Examples.get(aleatorio).classValue()==clas))
                centi = true;
            ii++;
        }

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                
                if (aux.inputAttribute(num_var).isNumeric()) { //Continuous variable
                    // Put in the correspondent interval //
                    float pertenencia=0, new_pert=0;
                    int interv = StreamMOEAEFEP.nLabel;
                    for (int i=0; i < StreamMOEAEFEP.nLabel; i++) {
                        new_pert = StreamMOEAEFEP.Fuzzy(num_var, i, Examples.get(aleatorio).valueInputAttribute(num_var));
                        if (new_pert>pertenencia) {
                            interv = i;
                            pertenencia = new_pert;
                        }
                    }
                    int number = StreamMOEAEFEP.nLabel;
                    
                    // Initialise the gene on this interval (this interval to 1, the rest to 0)
                    cromosoma[num_var].NoTakeInitGene();
                    this.setCromGeneElem(num_var, interv, true);
                    this.setCromGeneElem(num_var, number, true);
                    
                } else { //Discrete variable
                    // Put in the correspondent value //
                    int number = aux.attribute(num_var).numValues();
                    cromosoma[num_var].NoTakeInitGene();
                    Double val =  Examples.get(aleatorio).valueInputAttribute(num_var);
                    this.setCromGeneElem(num_var, val.intValue(), true);
                    this.setCromGeneElem(num_var, number, true);
                }
                crom_inic[num_var]=true;
                var++;
            }
        }

        // Initialise the rest variables
        for (int i=0; i<num_genes; i++)  {
            if (!crom_inic[i]) {
                cromosoma[i].NoTakeInitGene();
            }
        }
        
        // Return the class of the covered example.
        return ((Double) Examples.get(aleatorio).classValue()).intValue();
    }

    /**
     * <p>
     * Retuns the lenght of the chromosome
     * </p>
     * @return          Lenght of the chromosome
     */
    public int getCromLenght () {
      return num_genes;
    }


    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return          Lenght of the gene
     */
    public int getCromGeneLenght (int pos) {
      return cromosoma[pos].getGeneLenght();
    }


    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos      Position of the variable
     * @param elem          Position of the gene
     */
    public boolean getCromGeneElem (int pos, int elem) {
      return cromosoma[pos].getGeneElem(elem);
    }


   /**
    * <p>
    * Sets the value of the indicated gene of the Chromosome
    * </p>
    * @param pos            Position of the variable
    * @param elem           Position of the gene
    * @param val            Value to insert
    */
    public void setCromGeneElem (int pos, int elem, boolean val) {
        cromosoma[pos].setGeneElem(elem, val);
    }

    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile         File to write the chromosome
     */
    public void Print(String nFile) {
        String contents;
        contents = "Chromosome: \n";
        for(int i=0; i<num_genes; i++){
            contents += "Var "+i+": ";
            int neti = getCromGeneLenght(i);
            for(int l=0; l<=neti; l++){
                contents += this.getCromGeneElem(i, l) + " ";
            }
            contents+="\n";
        }
        if (nFile=="")
            System.out.print (contents);
        else
           File.AddtoFile(nFile, contents);
    }
    
    public boolean isNonParticipant(int pos){
        return cromosoma[pos].isNonParticipant();
    }
    
    
    /**
     * It sets the variable as non-participant if it participates in the rule
     * @param pos 
     */
    public void eraseVariable(int pos){
        if(!cromosoma[pos].isNonParticipant()) 
            cromosoma[pos].NoTakeInitGene();
    }
    
    
    /**
     * It randomly flips a value of the variable specified at {@code pos}
     * @param pos 
     */
    public void randomChange(int pos){
        int cambio = Randomize.Randint(0, cromosoma[pos].getGeneLenght() - 1);

        // Flip the value of the gene
        cromosoma[pos].setGeneElem(cambio, ! cromosoma[pos].getGeneElem(cambio));

        // Check the non-participation condition of the variable
        if (cromosoma[pos].isNonParticipant()) {
            cromosoma[pos].setGeneElem(cromosoma[pos].getGeneLenght(), false);
        } else {
            cromosoma[pos].setGeneElem(cromosoma[pos].getGeneLenght(), true);
        }
    }
          

}
