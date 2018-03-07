/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic;

import moa.subgroupdiscovery.Gene;

/**
 *
 * @author agvico
 */
public abstract class Chromosome {
    
    /**
     * <p>
     * Defines the structure and manage the contents of a rule
     * This implementation uses only integer values to store the gens.
     * So, variables values must be discretized (if they are continuous)
     * or translated into integers (if they are enumerated)
     * </p>
     */
    protected int num_genes; // Number of genes
    protected Gene[] chromosome; // Individual content - integer representation

    public Chromosome() {
    }

    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos      Position of the gene
     * @return              Value of the gene
     */
    public abstract int getCromElem(int pos);

    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos      Position of the gene
     * @param value         Value of the gene
     */
    public abstract void setCromElem(int pos, int value);

    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return          Gets the lenght of the chromosome
     */
    public abstract int getCromLength();

    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile         File to write the cromosome
     */
    public abstract void Print(String nFile);
    
}
