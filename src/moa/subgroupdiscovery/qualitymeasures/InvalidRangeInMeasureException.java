/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.qualitymeasures;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.Individual;

/**
 *
 * @author agvico
 */
public class InvalidRangeInMeasureException extends Exception {

    public InvalidRangeInMeasureException(QualityMeasure q) {
        super("Invalid Range in measure \"" + q.short_name + "\": " + q.value + " -> Contingency Table: " + q.table.toString());
    }

    /**
     * It shows the message in the estandard error and the stack trace and exit
     * the program with error code = 2.
     * 
     * @param clas The class where the error ocurred, in order to be shown in the stack trace
     */
    public void showAndExit(Object clas) {
        Logger.getLogger(clas.getClass().getName()).log(Level.SEVERE, null, this);
        System.err.println(super.getMessage());
        System.exit(2);
    }
}
