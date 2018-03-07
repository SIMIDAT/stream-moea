/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.core.exceptions;

import moa.subgroupdiscovery.qualitymeasures.ContingencyTable;

/**
 *
 * @author agvico
 */
public class InvalidContingencyTableException extends Exception{
    
    public InvalidContingencyTableException(ContingencyTable t){
        super("Invalid contingency table: " + t.toString());
    }
}
