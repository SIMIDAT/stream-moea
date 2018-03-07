/*
 * The MIT License
 *
 * Copyright 2017 Angel Miguel Garcia Vico <agvico at ujaen.es>.
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
package moa.subgroupdiscovery.qualitymeasures;

import org.core.exceptions.InvalidContingencyTableException;

/**
 * Class to represent a contingency table.
 * 
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public class ContingencyTable {
    
    /**
     * The true positives
     */
    private int tp;
    
    /**
     * The false positives
     */
    private int fp;
    
    /**
     * The true negatives
     */
    private int tn;
    
    /**
     * The false negatives
     */
    private int fn;
    
    /**
     * The Contingency Table constructor.
     * 
     * @param tp
     * @param fp
     * @param tn
     * @param fn 
     */
    public ContingencyTable(int tp, int fp, int tn, int fn){
        this.tp = tp;
        this.fn = fn;
        this.fp = fp;
        this.tn = tn;
    }
    

    /**
     * @return the tp
     */
    public int getTp() {
        return tp;
    }

    /**
     * @param tp the tp to set
     */
    public void setTp(int tp) {
        this.tp = tp;
    }

    /**
     * @return the fp
     */
    public int getFp() {
        return fp;
    }

    /**
     * @param fp the fp to set
     */
    public void setFp(int fp) {
        this.fp = fp;
    }

    /**
     * @return the tn
     */
    public int getTn() {
        return tn;
    }

    /**
     * @param tn the tn to set
     */
    public void setTn(int tn) {
        this.tn = tn;
    }

    /**
     * @return the fn
     */
    public int getFn() {
        return fn;
    }

    /**
     * @param fn the fn to set
     */
    public void setFn(int fn) {
        this.fn = fn;
    }
    
    /**
     * Gets the total number of observations in the contingency table
     * 
     * @return 
     */
    public double getTotalExamples(){
        return (double)(fn + tp + tn +fp);
    }
    
    
    public String toString(){
        return "TP: " + tp + "  FP: " + fp + "  TN: " + tn + "  FP: " + fp;
    }
    
    /**
     * It checks if the contingency table is correctly created.
     * @throws InvalidContingencyTableException 
     */
    public void validate() throws InvalidContingencyTableException{
        if(tp < 0 || fp < 0 || tn < 0 || fn < 0){
            throw new InvalidContingencyTableException(this);
        }
    }
}
