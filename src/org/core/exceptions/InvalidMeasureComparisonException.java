/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.core.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 *
 * @author agvico
 */
public class InvalidMeasureComparisonException extends Exception {

    public InvalidMeasureComparisonException(QualityMeasure one, QualityMeasure other) {
        super("Invalid Comparison. You are trying to compare \"" +
                one.getShort_name() + "\" against \"" + 
                other.getShort_name() + "\". You are doing: " + other.getShort_name() + 
                ".compareTo(" + one.getShortName() + ")");
    }

    /**
     * It shows the message in the estandard error and the stack trace and exit
     * the program with error code = 2.
     *
     * @param clas The class where the error ocurred, in order to be shown in
     * the stack trace
     */
    public void showAndExit(Object clas) {
        Logger.getLogger(clas.getClass().getName()).log(Level.SEVERE, null, this);
        System.err.println(super.getMessage());
        System.exit(2);
    }
}
