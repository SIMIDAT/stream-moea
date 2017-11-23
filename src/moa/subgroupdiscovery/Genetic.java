/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package moa.subgroupdiscovery;

import com.yahoo.labs.samoa.instances.Instance;
import org.core.*;
import java.util.*;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

public class Genetic {

    /**
     * <p>
     * Methods to define the genetic algorithm and to apply operators and
     * reproduction schema
     * </p>
     */
    private ArrayList<Population> poblac;     // Main Population
    private ArrayList<Population> best;       // Best population
    private ArrayList<Population> offspring;  // Offspring population
    private ArrayList<Population> union;      // Main+Offspring populations

    private int indivPerClass;    // number of individuals per class
    private ArrayList<QualityMeasure> objectives; // The objective quality measures
    private int long_poblacion;   // Number of individuals of the population
    private int n_eval;           // Number of evaluations per ejecution
    private float prob_cruce;     // Cross probability
    private float prob_mutacion;  // Mutation probability
    private int Gen;		  // Number of generations performed by the GA
    private int Trials;		  // Number of evaluated chromosomes

    private String StrictDominance = "no";
    private String RulesRep = "DNF";
//    private String Tuning = "no";
//    private String SmallDisjunct = "no";

    //private String onePerClass = "yes";
    //private String ReInitCob = "no";    // Re-initialization based on coverage for the diversity in the model
    private float porcCob = 1;          // Biased initialization for individuals in ReInitCob
    private float minCnf = 0;
    private QualityMeasure diversity; // The diversity quality measures to use

    /**
     * <p>
     * Gets the number of objectives
     * </p>
     *
     * @return Number of objectives
     */
    public int getNumObjectives() {
        return objectives.size();
    }

    /**
     * <p>
     * Sets the name of an objective
     * </p>
     *
     * @param pos Position of the objective
     * @param value Name of the objective
     */
    public void addObjectives(QualityMeasure q) {
        objectives.add(q);
    }

    /**
     * <p>
     * Gets the name of the objective
     * </p>
     *
     * @param pos Position of the objective
     * @return Name of the objective
     */
    public QualityMeasure getObjective(int pos) {
        return objectives.get(pos);
    }

    /**
     * <p>
     * Sets the lenght of the population
     * </p>
     *
     * @param value Lenght of the population
     */
    public void setLengthPopulation(int value) {
        long_poblacion = value;
    }

    /**
     * <p>
     * Gets the lenght of the population
     * </p>
     *
     * @return Lenght of the population
     */
    public int getLengthPopulation() {
        return long_poblacion;
    }

    /**
     * <p>
     * Sets the number of evaluations of the algorithm
     * </p>
     *
     * @param value Number of evaluations
     */
    public void setNEval(int value) {
        n_eval = value;
    }

    /**
     * <p>
     * Gets the number of evalutions of the algorithms
     * </p>
     *
     * @return Number of evaluations
     */
    public int getNEval() {
        return n_eval;
    }

    /**
     * <p>
     * Sets the cross probability in the algorithm
     * </p>
     *
     * @param value Cross probability
     */
    public void setProbCross(float value) {
        prob_cruce = value;
    }

    /**
     * <p>
     * Gets the cross probability
     * </p>
     *
     * @return Cross probability
     */
    public float getProbCross() {
        return prob_cruce;
    }

    /**
     * <p>
     * Sets the mutation probability
     * </p>
     *
     * @param value Mutation probability
     */
    public void setProbMutation(float value) {
        prob_mutacion = value;
    }

    /**
     * <p>
     * Gets the mutation probability
     * </p>
     *
     * @return Mutation probability
     */
    public float getProbMutation() {
        return prob_mutacion;
    }

    /**
     * <p>
     * Sets the value of a gene
     * </p>
     *
     * @param value Value of the gene
     */
    public void setGen(int value) {
        Gen = value;
    }

    /**
     * <p>
     * Gets the value of a gene
     * </p>
     *
     * @return Value of the gene
     */
    public int getGen() {
        return Gen;
    }

    /**
     * <p>
     * Sets the number of trials in the algorithm
     * </p>
     *
     * @param value Number of trials
     */
    public void setTrials(int value) {
        Trials = value;
    }

    /**
     * <p>
     * Gets the number of trials in the algorithm
     * </p>
     *
     * @return Number of trials
     */
    public int getTrials() {
        return Trials;
    }

    /**
     * <p>
     * Gets the type of diversity of the algorithm
     * </p>
     *
     * @return Type of diversity
     */
    public QualityMeasure getDiversity() {
        return diversity;
    }

    /**
     * <p>
     * Sets the type of diversity of the algorithm
     * </p>
     *
     * @param value Type of diversity
     */
    public void setDiversity(QualityMeasure value) {
        diversity = value;
    }

    /**
     * <p>
     * Gets the percentage of biased initialisation in the re-initialisation
     * based on coverage
     * </p>
     *
     * @return Percentage of biases
     */
    public float getPorcCob() {
        return porcCob;
    }

    /**
     * <p>
     * Sets the percentage of biased initialisation in the re-initialisation
     * based on coverage
     * </p>
     *
     * @param value Value of the percentage
     */
    public void setPorcCob(float value) {
        porcCob = value;
    }

    /**
     * <p>
     * Gets the minimum confidence
     * </p>
     *
     * @return Minimum confidence
     */
    public float getMinCnf() {
        return minCnf;
    }

    /**
     * <p>
     * Sets the minimum confidence
     * </p>
     *
     * @param value Minimum confidence
     */
    public void setMinCnf(float value) {
        minCnf = value;
    }

    /**
     * <p>
     * Gets if the algorithm considers strict dominance
     * </p>
     *
     * @return The value of strict dominance
     */
    public String getStrictDominance() {
        return StrictDominance;
    }

    /**
     * <p>
     * Sets if the algorithm considers strict dominance
     * </p>
     *
     * @param value The value of strict dominance
     */
    public void setStrictDominance(String value) {
        StrictDominance = value;
    }

    /**
     * <p>
     * Joins two populations
     * </p>
     *
     * @param neje Number of examples
     */
    public void JoinTemp(int neje) {
        int i, j, k;
        for (i = 0; i < union.size(); i++) {
            for (j = 0; j < long_poblacion; j++) {
                union.get(i).CopyIndiv(i, neje, getNumObjectives(), poblac.get(i).getIndiv(j));
            }
            for (k = 0; k < long_poblacion; k++) {
                union.get(i).CopyIndiv(j, neje, getNumObjectives(), offspring.get(i).getIndiv(k));
                j++;
            }
        }
    }

    /**
     * <p>
     * Applies the selection schema of the genetic algorithm. Binary tournament
     * selection from elite to inter
     * </p>
     *
     * @return Position of the individual selected
     */
    public int Select(int clas) {
        int winner;

        int opponent1 = Randomize.Randint(0, poblac.get(clas).getNumIndiv() - 1);
        int opponent2 = opponent1;
        while ((opponent2 == opponent1) && (poblac.get(clas).getNumIndiv() > 1)) {
            opponent2 = Randomize.Randint(0, long_poblacion - 1);
        }

        winner = opponent1;

        if (poblac.get(clas).getIndiv(opponent2).getRank() < poblac.get(clas).getIndiv(opponent1).getRank()) {
            winner = opponent2;
        } else if (poblac.get(clas).getIndiv(opponent2).getRank() > poblac.get(clas).getIndiv(opponent1).getRank()) {
            winner = opponent1;
        } else {
            if (poblac.get(clas).getIndiv(opponent2).getCrowdingDistance() > poblac.get(clas).getIndiv(opponent1).getCrowdingDistance()) {
                winner = opponent2;
            } else if (poblac.get(clas).getIndiv(opponent2).getCrowdingDistance() <= poblac.get(clas).getIndiv(opponent1).getCrowdingDistance()) {
                winner = opponent1;
            }
        }
        return winner;

    }

    /**
     * <p>
     * Cross operator for the genetic algorithm
     * </p>
     *
     * @param inst An instance to get variables information
     * @param dad Position of the daddy
     * @param mom Position of the mummy
     * @param contador Position to insert the son
     * @param neje Number of examples
     */
    public void CrossMultipoint(Instance inst, int clas, int dad, int mom, int contador, int neje) {

        int i, xpoint1, xpoint2;
        double cruce;

        // Copy the individuals to cross
        offspring.get(clas).CopyIndiv(contador * 2, neje, getNumObjectives(), poblac.get(clas).getIndiv(mom));
        offspring.get(clas).CopyIndiv(contador * 2 + 1, neje, getNumObjectives(), poblac.get(clas).getIndiv(dad));

        // Perform crossover
        cruce = Randomize.RanddoubleClosed(0.0, 1.0);

        if (cruce <= getProbCross()) {
            // Generation of the two point of cross
            xpoint1 = Randomize.RandintClosed(0, (inst.numInputAttributes() - 1));
            if (xpoint1 != inst.numInputAttributes() - 1) {
                xpoint2 = Randomize.Randint((xpoint1 + 1), (inst.numInputAttributes() - 1));
            } else {
                xpoint2 = inst.numInputAttributes() - 1;
            }

            // Cross the parts between both points
            for (i = xpoint1; i <= xpoint2; i++) {
                int number = offspring.get(clas).getIndivCromDNF(contador * 2).getCromGeneLenght(i);
                for (int ii = 0; ii <= number; ii++) {
                    offspring.get(clas).setCromElem((contador * 2), i, ii, poblac.get(clas).getCromElem(dad, i, ii));
                    offspring.get(clas).setCromElem((contador * 2) + 1, i, ii, poblac.get(clas).getCromElem(mom, i, ii));
                }
                int aux1 = 0;
                int aux2 = 0;
                for (int ii = 0; ii < number; ii++) {
                    if (offspring.get(clas).getCromElem((contador * 2), i, ii) == 1) {
                        aux1++;
                    }
                    if (offspring.get(clas).getCromElem((contador * 2) + 1, i, ii) == 1) {
                        aux2++;
                    }
                }
                if ((aux1 == number) || (aux1 == 0)) {
                    offspring.get(clas).setCromElem((contador * 2), i, number, 0);
                } else {
                    offspring.get(clas).setCromElem((contador * 2), i, number, 1);
                }
                if ((aux2 == number) || (aux2 == 0)) {
                    offspring.get(clas).setCromElem((contador * 2) + 1, i, number, 0);
                } else {
                    offspring.get(clas).setCromElem((contador * 2) + 1, i, number, 1);
                }
            }
            offspring.get(clas).setIndivEvaluated(contador*2, false);
            offspring.get(clas).setIndivEvaluated(contador*2 + 1, false);
        }
        
        

    }

    /**
     * <p>
     * Mutates an individual
     * </p>
     *
     * @param Variables Variables structure
     * @param pos Position of the individual to mutate
     */
    public void Mutation(Instance inst, int clas, int pos) {

        double mutar;
        int posiciones, eliminar;

        posiciones = inst.numInputAttributes();

        if (getProbMutation() > 0) {
            for (int i = 0; i < posiciones; i++) {
                mutar = Randomize.Randdouble(0.00, 1.00);
                if (mutar <= getProbMutation()) {
                    eliminar = Randomize.Randint(0, 10);
                    if (eliminar <= 5) {
                        if (inst.inputAttribute(i).isNumeric()) {
                            int number = StreamMOEAEFEP.nLabel;
                            for (int l = 0; l <= number; l++) {
                                offspring.get(clas).setCromElem(pos, i, l, 0);
                            }
                        } else {
                            int number = inst.attribute(i).numValues();
                            for (int l = 0; l <= number; l++) {
                                offspring.get(clas).setCromElem(pos, i, l, 0);
                            }
                        }
                    } else {
                        if (!inst.inputAttribute(i).isNumeric()) {
                            int number = inst.attribute(i).numValues();
                            int cambio = Randomize.Randint(0, number - 1);
                            if (offspring.get(clas).getCromElem(pos, i, cambio) == 0) {
                                offspring.get(clas).setCromElem(pos, i, cambio, 1);
                                int aux1 = 0;
                                for (int ii = 0; ii < number; ii++) {
                                    if (offspring.get(clas).getCromElem(pos, i, ii) == 1) {
                                        aux1++;
                                    }
                                }
                                if ((aux1 == number) || (aux1 == 0)) {
                                    offspring.get(clas).setCromElem(pos, i, number, 0);
                                } else {
                                    offspring.get(clas).setCromElem(pos, i, number, 1);
                                }
                            } else {
                                for (int k = 0; k <= number; k++) {
                                    offspring.get(clas).setCromElem(pos, i, k, 0);
                                }
                            }

                        } else {
                            int number = StreamMOEAEFEP.nLabel;
                            int cambio = Randomize.Randint(0, number - 1);
                            if (offspring.get(clas).getCromElem(pos, i, cambio) == 0) {
                                offspring.get(clas).setCromElem(pos, i, cambio, 1);
                                int aux1 = 0;
                                for (int ii = 0; ii < number; ii++) {
                                    if (offspring.get(clas).getCromElem(pos, i, ii) == 1) {
                                        aux1++;
                                    }
                                }
                                if ((aux1 == number) || (aux1 == 0)) {
                                    offspring.get(clas).setCromElem(pos, i, number, 0);
                                } else {
                                    offspring.get(clas).setCromElem(pos, i, number, 1);
                                }
                            } else {
                                for (int k = 0; k <= number; k++) {
                                    offspring.get(clas).setCromElem(pos, i, k, 0);
                                }
                            }
                        }
                    }

                    // Marks the chromosome as not evaluated
                    offspring.get(clas).setIndivEvaluated(pos, false);
                }
            }
        }

    }

    /**
     * <p>
     * Composes the genetic algorithm applying the operators
     * </p>
     *
     * @param instances A set of instances
     * @param nFile File to write the process
     * @return Final Pareto population
     */
    public Population GeneticAlgorithm(ArrayList<Instance> instances, String nFile) {
        Instance inst = instances.get(0);
        String contents;
        float porcVar = (float) 0.25;
        float porcPob = (float) 0.75;
        int indivPerClass = long_poblacion / inst.numClasses();
        int modulus = long_poblacion % inst.numClasses();
        
       
        
        // Initialises the population
        poblac = new ArrayList<>(inst.numClasses());
        offspring = new ArrayList<>(inst.numClasses());
        union = new ArrayList<>(inst.numClasses());
        
        System.out.println("DEBUG: initializing genetic algorithm");
        System.out.println("DEBUG: NumObjectives: " + getNumObjectives());
        System.out.println("DEBUG: NumClass: " + inst.numClasses());
        
        for (int i = 0; i < inst.numClasses(); i++) {
            poblac.add(new Population(long_poblacion, inst.numInputAttributes(), getNumObjectives(), instances.size(), inst));
            poblac.get(i).BsdInitPob(inst, porcVar, porcPob, instances.size(), nFile);
        }
        
        System.out.println("DEBUG: poblacSize: " + poblac.size());
        System.out.println("DEBUG: Populations created and intialised correctly.");
        
        
        Trials = 0;
        Gen = 0;

        //Evaluates the population
        System.out.println("DEBUG: Starting the evaluation of the Populations...");
        System.out.println("DEBUG: poblacSize: " + poblac.size());
        for (int i = 0; i < poblac.size(); i++) {
            Trials += poblac.get(i).evalPop(this, instances, objectives);
        } 

        do { // Genetic Algorithm General cycle

            Gen++;
            offspring.clear();
            union.clear();
            
            // Creates offspring and union
            System.out.println("DEBUG: Generating offspring and union...");
            for (int i = 0; i < inst.numClasses(); i++) {
                offspring.add(new Population(long_poblacion, inst.numInputAttributes(), getNumObjectives(), instances.size(), inst));
                union.add(new Population(2 * long_poblacion, inst.numInputAttributes(), getNumObjectives(), instances.size(), inst));
            }

            for (int clas = 0; clas < inst.numClasses(); clas++) {
                for (int conta = 0; conta < long_poblacion / 2; conta++) {

                    // Select the daddy and mummy
                    System.out.println("DEBUG: Select...");
                    int dad = Select(clas);
                    int mum = Select(clas);
                    while ((dad == mum) && (poblac.get(clas).getNumIndiv() > 1)) {
                        mum = Select(clas);
                    }

                    // Crosses
                    System.out.println("DEBUG: Cross...");
                    CrossMultipoint(inst, clas, dad, mum, conta, instances.size());

                    // Mutates
                    System.out.println("DEBUG: Mutation...");
                    Mutation(inst, clas, (conta * 2));
                    Mutation(inst, clas, (conta * 2) + 1);
                }

                if (long_poblacion % 2 == 1) {
                    int dad = Select(clas);
                    offspring.get(clas).CopyIndiv(long_poblacion - 1, instances.size(), getNumObjectives(), poblac.get(clas).getIndiv(dad));
                }
            }

            // Evaluates the offspring
            System.out.println("DEBUG: Evaluation of the offspring...");
            for (int clas = 0; clas < inst.numClasses(); clas++) {
                Trials += offspring.get(clas).evalPop(this, instances, objectives);
            }

            // Join population and offspring in union population
            System.out.println("DEBUG: Join offspring and pop in union...");
            JoinTemp(instances.size());
            
            /*
            for(int i = 0; i < union.size(); i++)
                for (int j = 0; j < union.get(i).getNumIndiv(); j++)
                     System.out.println("DEBUG: UNION (" + i + "," + j + "): " + union.get(i).getIndiv(i).objs.size());
            */
           
            
            for (int clas = 0; clas < inst.numClasses(); clas++) {
                // Makes the ranking of union
                System.out.println("DEBUG: Ranking for class..." + clas);
                Ranking ranking = new Ranking(union.get(clas), inst, getNumObjectives(), instances.size(), RulesRep, StrictDominance);

                int remain = poblac.get(clas).getNumIndiv();
                int index = 0;

                // Obtains the Pareto front
                Population front = ranking.getSubfront(index);

                int contador = 0;
                
                System.out.println("DEBUG: Adding fronts...");
                while ((remain > 0) && (remain >= front.getNumIndiv())) {

                    CalculateDistanceCrowding(front, getNumObjectives());

                    // Add the individuals of this front
                    for (int k = 0; k < front.getNumIndiv(); k++) {
                        poblac.get(clas).CopyIndiv(contador, instances.size(), getNumObjectives(), front.getIndiv(k));
                        contador++;
                    }

                    //Decrement remain
                    remain = remain - front.getNumIndiv();
                    //Obtain the next front
                    index++;
                    if (remain > 0) {
                        if (ranking.getNumberOfSubfronts() == index) {
                            front = new Population(remain, inst.numInputAttributes(), getNumObjectives(), instances.size(), inst);
                            front = ReInitCoverage(front, instances, nFile);
                            remain = 0;
                        } else {
                            front = ranking.getSubfront(index);
                        }
                    } // if
                } // while
                // remain is less than front(index).size, insert only the best one
                System.out.println("DEBUG: Adding remaining individuals of a front...");
                if (remain > 0) {  // front contains individuals to insert                        

                    // Assign diversity function to individuals
                    CalculateDistanceCrowding(front, getNumObjectives());

                    // Sort population with the diversity function
                    double[] ordenado = new double[front.getNumIndiv()];
                    int izq = 0;
                    int der = front.getNumIndiv() - 1;
                    int indices[] = new int[front.getNumIndiv()];
                    for (int i = 0; i < front.getNumIndiv(); i++) {
                        indices[i] = i;
                        ordenado[i] = front.getIndiv(i).getCrowdingDistance();
                    }
                    Utils.OrCrecIndex(ordenado, izq, der, indices);
                    int i = front.getNumIndiv() - 1;

                    for (int k = remain - 1; k >= 0; k--) {
                        poblac.get(clas).CopyIndiv(contador, instances.size(), getNumObjectives(), front.getIndiv(indices[i]));
                        i--;
                        contador++;
                    } // for
                }

                // Gets the best population
                if (Gen == 1) {
                    // if it is the first generation, best is the actual one.
                    best.set(clas, poblac.get(clas));
                } else {
                    // If it is not the first generation:
                    // See if the population evolves (i.e. it covers new examples)
                    System.out.println("DEBUG: Check reinit...");
                    poblac.get(clas).examplesCoverPopulation(instances.size(), Trials);
                    double pctCambio = (n_eval * 5) / 100;
                    // If the population does not evolve for a 5 % of the total evaluations
                    if (Trials - poblac.get(clas).getLastChangeEval() > pctCambio) {
                        System.out.println("DEBUG: Elite... JOIN");
                        // Join the elite population and the pareto front
                        Population join = best.get(clas).join(ranking.getSubfront(0), instances, this);
                        System.out.println("DEBUG: Elite... Token Competition");
                        // best is a new population made by the token competition procedure.
                        best.set(clas, join.tokenCompetition(instances, this));
                    }
                }

                // Re-initialisation based on coverage
                poblac.set(clas, ReInitCoverage(poblac.get(clas), instances, nFile));
                
            }
         

        } while (Trials <= n_eval);
        
        for(int i = 0; i < inst.numClasses(); i++){
            Ranking ranking = new Ranking(poblac.get(i),inst, getNumObjectives(), instances.size(), RulesRep, StrictDominance);
            Population join = ranking.getSubfront(0).join(best.get(i), instances, this);
            // now, apply the token competition to get the best population.
            best.set(i, join.tokenCompetition(instances, this));
        }
    


        contents = "\nGenetic Algorithm execution finished\n";
        contents += "\tNumber of Generations = " + Gen + "\n";
        contents += "\tNumber of Evaluations = " + Trials + "\n";
        File.AddtoFile(nFile, contents);

        //return ranking.getSubfront(0);
        // join all the individuals in a single population
        Population toReturn = new Population(long_poblacion * inst.numClasses(), inst.numInputAttributes(), getNumObjectives(), instances.size(), inst);
        int conta = 0;
        for(Population pop: best){
            for(int i = 0; i < pop.getNumIndiv(); i++){
                toReturn.CopyIndiv(conta, instances.size(), getNumObjectives(), pop.getIndiv(i));
                conta++;
            }
        }
        
        return toReturn;
    }

    /**
     * <p>
     * Function of the re-initialisation based on coverage
     * </p>
     *
     * @param poblac The actual population
     * @param Variables Variables structure
     * @param Examples Examples structure
     * @param nFile File to write the process
     * @return The new population for the next generation
     */
    private Population ReInitCoverage(Population poblac, ArrayList<Instance> instances, String nFile) {
        Instance aux = instances.get(0);
        System.out.println("DEBUG: Check for reinitialization...");
        poblac.examplesCoverPopulation(instances.size(), Trials);

        // Checks the difference between the last and actual evaluations
        double porc_cambio = (n_eval * 5) / 100;
        if ((Trials - poblac.getLastChangeEval()) >= porc_cambio) {
//            Vector marcas;
//            if (RulesRep.compareTo("CAN") == 0) {
//                marcas = RemoveRepeatedCAN(poblac);
//            } else {
//                marcas = RemoveRepeatedDNF(poblac, Variables);
//            }
            // Generates new individuals
            System.out.println("DEBUG: Reinitialize...");
            for (int conta = 0; conta < poblac.getNumIndiv(); conta++) {

                Individual indi = null;
                if (getRulesRep().compareTo("CAN") == 0) {
                    //indi = new IndCAN(Variables.getNVars(), Examples.getNEx(), num_objetivos);
                } else {
                    indi = new IndDNF(aux.numInputAttributes(), instances.size(), getNumObjectives(), aux, 0);
                }
                indi.CobInitInd(poblac, instances, porcCob, getNumObjectives(), poblac.getIndiv(0).getClas(), nFile);
                
                
                /**/ // Esto de aquí hay que verlo CON EXTREMO CUIDADO!!
                indi.evalInd(instances, objectives);
                
                
               
                indi.setIndivEvaluated(true);
                indi.setNEval(Trials);
                Trials++;
                // Copy the individual in the population
                poblac.CopyIndiv(conta, instances.size(), getNumObjectives(), indi);
                for (int j = 0; j < instances.size(); j++) {
                    if ((poblac.getIndiv(conta).getIndivCovered(j) == true) && (!poblac.ej_cubiertos.get(j))) {
                        poblac.ej_cubiertos.set(j);
                        poblac.ult_cambio_eval = Trials;
                    }
                }

            }
        }
        return poblac;

    }

    /**
     * <p>
     * Calculates the crowding distance
     * </p>
     *
     * @param population The actual population
     * @param nobj The number of objectives
     */
    private void CalculateDistanceCrowding(Population pop, int nobj) {

        int size = pop.getNumIndiv();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        if (size == 2) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if       

        for (int i = 0; i < size; i++) {
            pop.getIndiv(i).setCrowdingDistance(0.0);
        }

        double objetiveMaxn;
        double objetiveMinn;
        double distance;

        int ini, fin;

        for (int i = 0; i < nobj; i++) {
            double[] ordenado = new double[pop.getNumIndiv()];
            int izq = 0;
            int der = pop.getNumIndiv() - 1;
            int indices[] = new int[pop.getNumIndiv()];
            //QualityMeasures medidas = new QualityMeasures(nobj);
            for (int j = 0; j < pop.getNumIndiv(); j++) {
                indices[j] = j;
                //medidas = pop.getIndiv(j).getMeasures();
                ordenado[j] = pop.getIndiv(j).objs.get(i).getValue();//medidas.getObjectiveValue(i);
            }
            Utils.OrCrecIndex(ordenado, izq, der, indices);

            ini = indices[0];
            fin = indices[pop.getNumIndiv() - 1];

            //medidas = pop.getIndiv(ini).getMeasures();
            objetiveMinn = pop.getIndiv(ini).objs.get(i).getValue(); //medidas.getObjectiveValue(i);
            //medidas = pop.getIndiv(fin).getMeasures();
            objetiveMaxn = pop.getIndiv(fin).objs.get(i).getValue(); //medidas.getObjectiveValue(i);

            //Set de crowding distance            
            pop.getIndiv(ini).setCrowdingDistance(Double.POSITIVE_INFINITY);
            pop.getIndiv(fin).setCrowdingDistance(Double.POSITIVE_INFINITY);

            double a, b;

            for (int j = 1; j < size - 1; j++) {
                //medidas = pop.getIndiv(indices[j + 1]).getMeasures();
                a = pop.getIndiv(indices[j+1]).objs.get(i).getValue(); //medidas.getObjectiveValue(i);
                //medidas = pop.getIndiv(indices[j - 1]).getMeasures();
                b = pop.getIndiv(indices[j-1]).objs.get(i).getValue();//medidas.getObjectiveValue(i);
                distance = a - b;
                if (distance != 0) {
                    distance = distance / (objetiveMaxn - objetiveMinn);
                }
                distance += pop.getIndiv(indices[j]).getCrowdingDistance();
                pop.getIndiv(indices[j]).setCrowdingDistance(distance);
            } // for
        } // for        
    }

    /**
     * <p>
     * Calculates the knee value. This function is only valid for two objectives
     * </p>
     *
     * @param population The actual population
     * @param nobj The number of objectives
     */
    /**private void CalculateKnee(Population pop, int nobj) {

        int i, j;
        int izq, der;
        double a, b, c;
        double pi2 = 1.5707963267948966;

        int size = pop.getNumIndiv();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        if (size == 2) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        for (i = 0; i < size; i++) {
            pop.getIndiv(i).setCrowdingDistance(0.0);
        }

        double[] ordenado = new double[size];
        double[] ordenado2 = new double[size];
        int indices[] = new int[size];
        int indices2[] = new int[size];

        i = 0;

        izq = 0;
        der = size - 1;
        QualityMeasures medidas = new QualityMeasures(nobj);
        for (j = 0; j < size; j++) {
            indices[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado[j] = medidas.getObjectiveValue(0);
        }
        i = 1;
        izq = 0;
        der = size - 1;
        for (j = 0; j < size; j++) {
            indices2[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado2[j] = medidas.getObjectiveValue(1);
        }
        Utils.OrCrecIndex(ordenado, izq, der, indices);
        Utils.OrCrecIndex(ordenado2, izq, der, indices2);

        for (j = 0; j < pop.getNumIndiv(); j++) {
            for (izq = j - 1;
                    izq >= 0
                    && pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(1) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1)
                    && pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(0) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0); izq--);
            for (der = j;
                    der < size
                    && pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(1) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1)
                    && pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0); der++);

            pop.getIndiv(indices2[j]).setCrowdingDistance(pi2);

            if (izq < 0) {
                double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
                pop.getIndiv(indices2[j]).setCrowdingDistance(valor + pi2);
            } else {
                b = (pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(0) - pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0))
                        / (pop.getIndiv(indices[pop.getNumIndiv() - 1]).getMeasures().getObjectiveValue(0) - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(0));
                c = (pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1) - pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(1))
                        / (pop.getIndiv(indices2[pop.getNumIndiv() - 1]).getMeasures().getObjectiveValue(1) - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(0) * 1.0);
                a = Math.sqrt(b * b + c * c);
                double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
                pop.getIndiv(indices2[j]).setCrowdingDistance(valor + Math.asin(b / a));
            }

            if (der >= pop.getNumIndiv()) {
                double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
                pop.getIndiv(indices2[j]).setCrowdingDistance(valor + pi2);
            } else {
                b = (pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0) - pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0))
                        / (pop.getIndiv(indices[pop.getNumIndiv() - 1]).getMeasures().getObjectiveValue(0) - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(0));
                c = (pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0) - pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1))
                        / (pop.getIndiv(indices2[pop.getNumIndiv() - 1]).getMeasures().getObjectiveValue(1) - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(1) * 1.0);
                a = Math.sqrt(b * b + c * c);
                double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
                pop.getIndiv(indices2[j]).setCrowdingDistance(valor + Math.asin(c / a));
            }
        }

    }*/

    /**
     * <p>
     * Calculates the utility value. This function is only valid for two
     * objectives
     * </p>
     *
     * @param population The actual population
     * @param nobj The number of objectives
     */
   /* private void CalculateUtility(Population pop, int nobj) {

        int size = pop.getNumIndiv();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        if (size == 2) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        for (int i = 0; i < size; i++) {
            pop.getIndiv(i).setCrowdingDistance(0.0);
        }

        double[] ordenado = new double[size];
        double[] ordenado2 = new double[size];
        int indices[] = new int[size];
        int indices2[] = new int[size];

        int izq = 0;
        int der = size - 1;
        QualityMeasures medidas = new QualityMeasures(nobj);
        for (int j = 0; j < size; j++) {
            indices[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado[j] = medidas.getObjectiveValue(0);
        }

        izq = 0;
        der = size - 1;
        for (int j = 0; j < size; j++) {
            indices2[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado2[j] = medidas.getObjectiveValue(1);
        }
        Utils.OrCrecIndex(ordenado, izq, der, indices);
        Utils.OrCrecIndex(ordenado2, izq, der, indices2);

        if (pop.getIndiv(indices2[size - 1]).getMeasures().getObjectiveValue(1) != pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(1)) {

            for (int i = 0; i < long_lambda; i++) {
                double min = 0;
                for (int k = 0; k < nobj; k++) {
                    if (k == 0) {
                        min += lambda[i][k] * ((pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)
                                - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k))
                                / (pop.getIndiv(indices[size - 1]).getMeasures().getObjectiveValue(k)
                                - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k)));
                    } else {
                        min += lambda[i][k] * ((pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)
                                - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k))
                                / (pop.getIndiv(indices2[size - 1]).getMeasures().getObjectiveValue(k)
                                - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)));
                    }
                }
                int posmin = 0;
                double second = Double.POSITIVE_INFINITY;
                int possecond = -1;
                for (int j = 1; j < size; j++) {
                    double temp = 0.0;
                    for (int k = 0; k < nobj; k++) {
                        if (k == 0) {
                            temp += lambda[i][k] * ((pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(k)
                                    - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k))
                                    / (pop.getIndiv(indices[size - 1]).getMeasures().getObjectiveValue(k)
                                    - pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k)));
                        } else {
                            temp += lambda[i][k] * ((pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(k)
                                    - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k))
                                    / (pop.getIndiv(indices2[size - 1]).getMeasures().getObjectiveValue(k)
                                    - pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)));
                        }
                    }
                    if (temp < min) {
                        second = min;
                        possecond = posmin;
                        min = temp;
                        posmin = j;
                    } else {
                        if (temp < second) {
                            second = temp;
                            possecond = j;
                        }
                    }
                }
                double crowding = pop.getIndiv(indices2[posmin]).getCrowdingDistance();
                pop.getIndiv(indices2[posmin]).setCrowdingDistance(crowding + (second - min));
            }
        }
    }*/

    /**
     * <p>
     * Eliminates the repeated individuals for canonical representation
     * </p>
     *
     * @param original A population
     * @return A vector which marks the inviduals repeated
     */
    public Vector RemoveRepeatedCAN(Population original) {

        Vector marcar = new Vector();
        for (int i = 0; i < original.getNumIndiv(); i++) {
            marcar.add(i, 0);
        }

        int repes = 0;
        int tama_cromo;

        for (int i = 0; i < original.getNumIndiv(); i++) {
            Individual ini = original.getIndiv(i);
            CromCAN cini = ini.getIndivCromCAN();
            tama_cromo = cini.getCromLength();
            for (int j = i + 1; j < original.getNumIndiv(); j++) {
                int marca = (Integer) marcar.get(j);
                int cuenta_iguales = 0;
                Individual fin = original.getIndiv(j);
                CromCAN cfin = fin.getIndivCromCAN();
                if (marca == 0) {
                    for (int k = 0; k < tama_cromo; k++) {
                        if (cini.getCromElem(k) == cfin.getCromElem(k)) {
                            cuenta_iguales++;
                        }
                    }
                }
                if (((cuenta_iguales == tama_cromo) && (i < j)) || (fin.getRank() != 0)) {
                    marcar.set(j, 1);
                    repes++;
                }
            }

        }
        return marcar;

    }

    /**
     * <p>
     * Eliminates the repeated individuals for DNF representation
     * </p>
     *
     * @param original A population
     * @return A vector which marks the inviduals repeated
     */
    public Vector RemoveRepeatedDNF(Population original, Instance inst) {

        Vector marcar = new Vector();
        for (int i = 0; i < original.getNumIndiv(); i++) {
            marcar.add(i, 0);
        }

        int repes = 0;
        int tama_cromo;

        for (int i = 0; i < original.getNumIndiv(); i++) {
            Individual ini = original.getIndiv(i);
            CromDNF cini = ini.getIndivCromDNF();
            tama_cromo = cini.getCromLenght();
            for (int j = i + 1; j < original.getNumIndiv(); j++) {
                int marca = (Integer) marcar.get(j);
                int cuenta_iguales = 0;
                Individual fin = original.getIndiv(j);
                CromDNF cfin = fin.getIndivCromDNF();
                if (marca == 0) {
                    for (int k = 0; k < tama_cromo; k++) {
                        boolean genes = true;
                        int number = cini.getCromGeneLenght(k);
                        if ((cini.getCromGeneElem(k, number) == true) && (cfin.getCromGeneElem(k, number) == true)) {
                            for (int l = 0; l < number; l++) {
                                if (cini.getCromGeneElem(k, l) != cfin.getCromGeneElem(k, l)) {
                                    genes = false;
                                }
                            }
                        }
                        if (genes) {
                            cuenta_iguales++;
                        }
                    }
                }
                if (((cuenta_iguales == tama_cromo) && (i < j)) || (fin.getRank() != 0)) {
                    marcar.set(j, 1);
                    repes++;
                }
            }

        }
        return marcar;

    }

    /**
     * <p>
     * Evaluates the population to obtain the output files of training and test
     * for classification
     * </p>
     *
     * @param nameFileOutputTra Output quality file
     * @param pob Array of different populations for rules for different classes
     * @param Examples The instances of the dataset
     * @param Variables The variable definitions
     * @param classificationType How it will classify test instances? "max" ->
     * Maximum compatibility degree of a rule "sum" -> Maximum sum of
     * compatibilities per class "norm_sum" -> Normalized sum of compatibility
     * degrees per class
     * @param classNames Array with names of the classes.
     */
    /*public void CalcPobOutput(String nameFileOutputTra, Population[] pob, TableDat Examples, TableVar Variables, String classificationType, String[] classNames, String contents) { // al genetic

        float pertenencia, pert;
        CromCAN chrome;
        float disparoFuzzy = 1;
        float success = 0;
        float error = 0;
        int[] num_var_no_interv;
        float tp = 0;
        float fp = 0;
        float[] compatibility;
        float[] normsum;

        Files.writeFile(nameFileOutputTra, contents);
        int numRules = 0;
        for (int i = 0; i < pob.length; i++) {
            numRules += pob[i].getNumIndiv();
        }

        compatibility = new float[numRules];
        normsum = new float[Variables.getNClass()];
        int[] reglasDisparadas = new int[Variables.getNClass()];
        for (int i = 0; i < reglasDisparadas.length; i++) {
            reglasDisparadas[i] = 0;
        }
        num_var_no_interv = new int[numRules];
        for (int i = 0; i < numRules; i++) {
            num_var_no_interv[i] = 0;
        }

        // Begin computation
        for (int i = 0; i < Examples.getNEx(); i++) { // For each example
            int counter = 0;
            for (int clase = 0; clase < pob.length; clase++) {
                for (int j = 0; j < pob[clase].getNumIndiv(); j++) { // For each rule
                    disparoFuzzy = 1;
                    chrome = pob[clase].getIndivCromCAN(j);
                    for (int k = 0; k < Variables.getNVars(); k++) { // For each var of the rule

                        if (!Variables.getContinuous(k)) {
                            /* Discrete Variable */
    /*
                            if (chrome.getCromElem(k) <= Variables.getMax(k)) {
                                // Variable j takes part in the rule
                                if ((Examples.getDat(i, k)) != chrome.getCromElem(k) && (!Examples.getLost(Variables, i, k))) {
                                    disparoFuzzy = 0;
                                }
                            } else {
                                num_var_no_interv[counter]++;  // Variable does not take part
                            }
                        } else {
                            // Continuous variable
                            if (chrome.getCromElem(k) < Variables.getNLabelVar(k)) {
                                // Variable takes part in the rule
                                // Fuzzy computation
                                if (!Examples.getLost(Variables, i, k)) {
                                    pertenencia = 0;
                                    pertenencia = Variables.Fuzzy(k, chrome.getCromElem(k), Examples.getDat(i, k));
                                    disparoFuzzy = Utils.Minimum(disparoFuzzy, pertenencia);
                                }
                            } else {
                                num_var_no_interv[counter]++;  // Variable does not take part
                            }
                        }
                    }
                    // Update globals counters
                    compatibility[counter] = disparoFuzzy;
                    counter++;
                }
            }

            int prediccion = 0;
            if (classificationType.equalsIgnoreCase("max")) { // max rule compatibility
                float max = -1;
                int max_actual = 0;
                counter = 0;

                for (int clase = 0; clase < pob.length; clase++) {
                    for (int j = 0; j < pob[clase].getNumIndiv(); j++) {
                        if (max <= compatibility[counter]) {
                            if (max == compatibility[counter]) { // If rule has equal compatibilty but less variables, this is the new rule.
                                if (num_var_no_interv[max_actual] > num_var_no_interv[counter]) {
                                    max = compatibility[counter];
                                    max_actual = counter;
                                    prediccion = clase;
                                }
                            } else {
                                max = compatibility[counter];
                                max_actual = counter;
                                prediccion = clase;
                            }
                        }
                        counter++;
                    }
                }

            } else if (classificationType.equalsIgnoreCase("sum")) { // sum of compatibility per class
                for (int j = 0; j < Variables.getNClass(); j++) {
                    normsum[j] = 0;
                }
                counter = 0;
                for (int clase = 0; clase < pob.length; clase++) {
                    for (int j = 0; j < pob[clase].getNumIndiv(); j++) {
                        normsum[clase] += compatibility[counter];
                        counter++;
                    }
                }

                float max = -1;

                for (int j = 0; j < normsum.length; j++) {
                    if (max < normsum[j]) {
                        max = normsum[j];
                        prediccion = j;
                    }
                }

            } else { // Normalized sum
                for (int j = 0; j < Variables.getNClass(); j++) {
                    normsum[j] = 0;
                    reglasDisparadas[j] = 0;
                }
                counter = 0;
                for (int clase = 0; clase < pob.length; clase++) {
                    for (int j = 0; j < pob[clase].getNumIndiv(); j++) {
                        normsum[clase] += compatibility[counter];
                        if (compatibility[counter] > 0) {
                            reglasDisparadas[clase]++;
                        }
                        counter++;
                    }
                }
                // Normalize sum
                for (int p = 0; p < normsum.length; p++) {
                    normsum[p] /= reglasDisparadas[p];
                }
                float max = -1;

                for (int j = 0; j < normsum.length; j++) {
                    if (max < normsum[j]) {
                        max = normsum[j];
                        prediccion = j;
                    }
                }
            }

            contents = classNames[Examples.getClass(i)] + " " + classNames[prediccion] + "\n";
            Files.addToFile(nameFileOutputTra, contents);
        }

    }*/

    /**
     * @return the RulesRep
     */
    public String getRulesRep() {
        return RulesRep;
    }

    /**
     * @param RulesRep the RulesRep to set
     */
    public void setRulesRep(String RulesRep) {
        this.RulesRep = RulesRep;
    }
    
    public void setObjectives(ArrayList<QualityMeasure> objectives){
        if(this.objectives == null){
            this.objectives = new ArrayList<>();
        }
        for (QualityMeasure obj : objectives){
            if(! (obj instanceof NULL))
               this.objectives.add(obj);
        }
    }

}
