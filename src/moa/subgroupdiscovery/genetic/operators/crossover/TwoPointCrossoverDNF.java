/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.operators.crossover;

import java.util.ArrayList;
import moa.subgroupdiscovery.IndCAN;
import moa.subgroupdiscovery.genetic.individual.*;
import moa.subgroupdiscovery.genetic.operators.CrossoverOperator;
import org.core.Randomize;

/**
 *
 * @author agvico
 */
public class TwoPointCrossoverDNF extends CrossoverOperator<IndDNF> {

    public TwoPointCrossoverDNF() {
        super.numChildren = 2;
        super.numParents = 2;
    }

    @Override
    public ArrayList<IndDNF> doCrossover(ArrayList<IndDNF> parents) {
        if (parents.size() != numParents) {
            throw new UnsupportedOperationException("Two point crossover: The number of parents is different than two.");
        }

        ArrayList<IndDNF> children = new ArrayList<>();
        children.add((IndDNF) parents.get(0).clone());
        children.add((IndDNF) parents.get(1).clone());

        // Get the two point of crossover
        int xpoint1 = Randomize.Randint(0, parents.get(0).getTamano() - 1);
        int xpoint2;
        if (xpoint1 != parents.get(0).getTamano() - 1) {
            xpoint2 = Randomize.Randint(xpoint1 + 1, parents.get(0).getTamano() - 1);
        } else {
            xpoint2 = parents.get(0).getTamano() - 1;
        }

        // Perform the crossover
        for (int i = xpoint1; i <= xpoint2; i++) {
           for(int j = 0; j < parents.get(0).getCromElem(i).getGeneLenght(); j++){
               children.get(0).setCromGeneElem(i, j, parents.get(1).getCromGeneElem(i, j));
               children.get(1).setCromGeneElem(i, j, parents.get(0).getCromGeneElem(i, j));
           }
        }

        return children;
    }

}
