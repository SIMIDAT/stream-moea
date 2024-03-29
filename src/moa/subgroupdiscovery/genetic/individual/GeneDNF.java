/* 
 * The MIT License
 *
 * Copyright 2018 Ángel Miguel García Vico.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package moa.subgroupdiscovery.genetic.individual;

import java.util.Arrays;
import org.core.*;
import java.util.LinkedList;

public final class GeneDNF {

    /**
     * <p>
     * This implementation uses boolean values to store the genes values It is
     * used to store DNF rules, so that each variable can can get more than one
     * value at a time Each gene is an array of boolean values, false indicates
     * that the value is not present, true indicates that the value is present
     * </p>
     */
    private int num_elem;       // Number of elem in the gene
    private boolean gen[];      // Gene content - boolean representation

    private int numTRUE, numFALSE;  // Number of elements in the gene sets to TRUE and FALSE
    /**
     * <p>
     * Creates new instance of gene
     * </p>
     *
     * @param lenght Number of posibles values for the variable
     */
    public GeneDNF(int lenght) {
        num_elem = lenght;
        gen = new boolean[lenght + 1];
        numTRUE = 0;
        numFALSE = lenght;
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     *
     * @param pos Position of the gene
     * @param value Value of the gene
     */
    public void setGeneElem(int pos, boolean value) {
        if (gen[pos] != value) {
            if (pos < num_elem) {
                if (value) {
                    numTRUE++;
                    numFALSE--;
                } else {
                    numFALSE++;
                    numTRUE--;
                }
                
                if(numTRUE == num_elem || numFALSE == num_elem){
                    gen[num_elem] = false;
                } else {
                    gen[num_elem] = true;
                }
                
            }
            gen[pos] = value;
        }
    }

    /**
     * <p>
     * Random initialization of an existing gene
     * </p>
     */
    public void RndInitGene() {
        double aux;
        int interv = 0;
        for (int i = 0; i < num_elem; i++) {  // Gene num_elem
            aux = Randomize.Randdouble(0.0, 1.0);
            // Rand returns a random doble from 0 to 1, including 0 but excluding 1
            if (aux < 0.5) {
                setGeneElem(i, false);
            } else {
                setGeneElem(i, true);
                interv++;  // Counts the number of 1 of the variable
            }
        }
        // If number of 1 equals 0 or num of values, the variable does not take part
        if (interv == 0 || interv == num_elem) {
            gen[num_elem] = false;
        } else {
            gen[num_elem] = true;
        }
    }

    /**
     * <p>
     * Non-intervene Initialization of an existing gene
     * </p>
     */
    public void NoTakeInitGene() {

        // All the values are 0
        for (int i = 0; i < num_elem; i++) {
            setGeneElem(i, false);
        }

        // The variable does not take part, so mark with 0 the element "num_elem"
        gen[num_elem] = false;

    }

    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     *
     * @param pos Position of the gene
     * @return Value of the gene
     */
    public boolean getGeneElem(int pos) {
        return gen[pos];
    }

    /**
     * It checks whether the gene participates in the rule or not.
     *
     * @return
     */
    public boolean isNonParticipant() {
        return numTRUE == num_elem || numFALSE == num_elem;
    }

    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     *
     * @return Lenght of the gene
     */
    public int getGeneLenght() {
        return num_elem;
    }

    /**
     * <p>
     * Prints the gene
     * </p>
     *
     * @param nFile Name of the file to write the gene
     */
    public void Print(String nFile) {
        String contents;
        contents = "Gene: ";
        for (int i = 0; i < num_elem; i++) {
            if (gen[i] == true) {
                contents += "1 ";
            } else {
                contents += "0 ";
            }
        }
        contents += "\n";
        File.AddtoFile(nFile, contents);
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeneDNF other = (GeneDNF) obj;
        
        if(gen.length != other.gen.length){
            return false;
        }
        
        for(int i = 0; i < gen.length; i++){
            if(this.gen[i] != other.gen[i]){
                return false;
            }
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Arrays.hashCode(this.gen);
        return hash;
    }

}
