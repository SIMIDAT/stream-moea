/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.dominancecomparators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 *
 * @author agvico
 */
public class FastNonDominatedSorting extends DominanceComparator<Individual> {

    public FastNonDominatedSorting(boolean strongDominance) {
        super.StrictDominance = strongDominance;
        super.fronts = new ArrayList<>();
    }

    @Override
    public void doDominanceRanking(ArrayList<Individual> elements) {

        // dominateMe[i] contains the number of solutions dominating i        
        int[] dominateMe = new int[elements.size()];

        // iDominate[k] contains the list of solutions dominated by k
        List<Integer>[] iDominate = new List[elements.size()];

        // front[i] contains the list of individuals belonging to the front i
        List<Integer>[] front = new List[elements.size() + 1];

        // flagDominate is an auxiliar variable
        int flagDominate;

        // Initialize the fronts 
        for (int i = 0; i < front.length; i++) {
            front[i] = new LinkedList<Integer>();
        }

        // Fast non dominated sorting algorithm
        for (int p = 0; p < elements.size(); p++) {
            // Initialice the list of individuals that i dominate and the number
            // of individuals that dominate me
            iDominate[p] = new LinkedList<Integer>();
            dominateMe[p] = 0;
            // For all q individuals , calculate if p dominates q or vice versa
            boolean centi = false;
            for (int q = 0; q < elements.size(); q++) {
                flagDominate = 0;//compareConstraint(pop.getIndiv(p), pop.getIndiv(q));
                //flagDominate = constraint_.compare(solutionSet.get(p),solutionSet.get(q));
                if (flagDominate == 0) {
                    //System.out.println("DEBUG: Ranking -> nobj: " + nobj + " - " + p + " - " + q);
                    //System.out.println(pop.getIndiv(p).objs.get(0).getValue());
                    flagDominate = compareDominance(elements.get(p), elements.get(q));
                }

                if (flagDominate == -1) {
                    iDominate[p].add(new Integer(q));
                } else if (flagDominate == 1) {
                    dominateMe[p]++;
                } else {
                    iDominate[p].add(new Integer(q));
                }
            }

            // If nobody dominates p, p belongs to the first front
            if (dominateMe[p] == 0) {
                front[0].add(new Integer(p));
                elements.get(p).setRank(0);
            }
        }

        //Obtain the rest of fronts
        int i = 0;
        Iterator<Integer> it1, it2; // Iterators
        while (front[i].size() != 0) {
            i++;
            it1 = front[i - 1].iterator();
            while (it1.hasNext()) {
                it2 = iDominate[it1.next().intValue()].iterator();
                while (it2.hasNext()) {
                    int index = it2.next().intValue();
                    dominateMe[index]--;
                    if (dominateMe[index] == 0) {
                        front[i].add(new Integer(index));
                        elements.get(index).setRank(i);
                        //pop.getIndiv(index).setRank(i);
                    }
                }
            }
        }

        int contador;
        //0,1,2,....,i-1 are front, then i fronts
        for (int j = 0; j < i; j++) {
            ArrayList<Individual> newFront = new ArrayList<>();
            //ranking[j] = new Population(front[j].size(), inst.numInputAttributes(), nobj, neje, inst);
            it1 = front[j].iterator();
            contador = 0;
            while (it1.hasNext()) {
                newFront.add(elements.get(it1.next().intValue()).clone());
                contador++;
            }
            fronts.add(newFront);
            //ranking[j].CopyIndiv(contador, neje, nobj, pop.getIndiv(it1.next().intValue()));
            //contador++;

        }

    }
    
    
    /**
     *
     * @param a
     * @param b
     * @return
     */
    public int compareDominance(Individual a, Individual b) {
        //NOTE for this algorithm:
        // If A   domains   B THEN flag == -1
        // If A   equals    B THEN flag == 0
        // Si A  !domains   B THEN flag == 1
        if (a == null) {
            return 1;
        } else if (b == null) {
            return -1;
        }

        int dominate1; // dominate1 indicates if some objective of solution1
        // dominates the same objective in solution2. dominate2
        int dominate2; // is the complementary of dominate1.
        Individual solution1 = (Individual) a;
        Individual solution2 = (Individual) b;

        dominate1 = 0;
        dominate2 = 0;

        int flag = 0; //stores the result of the comparation

        // Equal number of violated constraint. Apply a dominance Test
        double value1, value2;

        //QualityMeasures medidas = new QualityMeasures(nobj);
        ArrayList<QualityMeasure> a1 = a.getObjs();
        ArrayList<QualityMeasure> b1 = b.getObjs();
        for (int i = 0; i < a1.size(); i++) {
            //medidas = solution1.getMeasures();
            value1 = a1.get(i).getValue();//medidas.getObjectiveValue(i));
            //medidas = solution2.getMeasures();
            value2 = b1.get(i).getValue();//medidas.getObjectiveValue(i);
            if (StrictDominance) {
                if (value1 < value2) {
                    flag = 1;
                } else if (value1 > value2) {
                    flag = -1;
                } else {
                    flag = 0;
                }
            } else {
                if (value1 < value2) {
                    flag = 1;
                } else {
                    flag = -1;
                }
            }

            if (flag == -1) {
                dominate1 = 1;
            }

            if (flag == 1) {
                dominate2 = 1;
            }

        }

        if (dominate1 == dominate2) {
            return 0; //No one dominate the other
        }
        if (dominate1 == 1) {
            return -1; // solution1 dominate
        }
        return 1;    // solution2 dominate
    }

}
