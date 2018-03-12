/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.crossover;

import java.util.ArrayList;
import moa.subgroupdiscovery.IndCAN;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class TwoPointCrossoverCAN extends CrossoverOperator<IndCAN>{

    public TwoPointCrossoverCAN(){
        super.numChildren = 2;
        super.numParents = 2;
    }
    
    @Override
    public ArrayList<IndCAN> doCrossover(ArrayList<IndCAN> parents) {
        if(parents.size() != numParents){
            throw new UnsupportedOperationException("Two point crossover: The number of parents is different than two.");
        }
                
        ArrayList<IndCAN> children = new ArrayList<>();
        children.add((IndCAN) parents.get(0).clone());
        children.add((IndCAN) parents.get(1).clone());
        
        // Get the two point of crossover
        int xpoint1 = Randomize.Randint(0, parents.get(0).getTamano() - 1);
        int xpoint2;
        if(xpoint1 != parents.get(0).getTamano() - 1){
            xpoint2 = Randomize.Randint(xpoint1 + 1, parents.get(0).getTamano() - 1);
        } else {
            xpoint2 = parents.get(0).getTamano() - 1;
        }
        
        // Perform the crossover
        for (int i = xpoint1; i <= xpoint2; i++) {
            children.get(0).setCromElem(i, parents.get(1).getCromElem(i));
            children.get(1).setCromElem(i, parents.get(0).getCromElem(i));
        }
       
        return children;
    }
    
}
