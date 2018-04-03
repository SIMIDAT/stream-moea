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
package moa.subgroupdiscovery.genetic.dominancecomparators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import moa.subgroupdiscovery.genetic.Individual;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Fast-non dominated sorting algorithm presented in the NSGA-II algorithm
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public final class FastNonDominatedSorting extends DominanceComparator<Individual> {

    public FastNonDominatedSorting(boolean strictDominance) {
        super.StrictDominance = strictDominance;
        super.fronts = new ArrayList<>();
    }

    @Override
    public void doDominanceRanking(ArrayList<Individual> elements) {
        
        fronts.clear();
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

    @Override
    public ArrayList<Individual> returnNextPopulation(int tamPopulation) {
        if (fronts.isEmpty()) {
            throw new UnsupportedOperationException("FastNonDominantedSorting: Return next population before the execution of the ranking!");
        }
        
        int remain = tamPopulation;
        int index = 0;
        ArrayList<Individual> resultPopulation = new ArrayList<>();

        ArrayList<Individual> front = getSubFront(index);

        int counter = 0;

        while ((remain > 0) && (remain >= front.size())) {
            CalculateDistanceCrowding(front);

            // Add the individuals of this front
            for (int k = 0; k < front.size(); k++) {
                resultPopulation.add(front.get(k).clone());
                counter++;
            }

            //Decrement remain
            remain = remain - front.size();

            //Obtain the next front
            index++;
            if (remain > 0) {
                if (fronts.size() == index) {
                    // In this case, generate "front" by the re-initialisation based on coverage
                    //front = new ArrayList<>();
                    //front = new Population(remain, inst.numInputAttributes(), getNumObjectives(), instances.size(), inst);
                    //front = ReInitCoverage(front, instances, nFile);
                    remain = 0;
                } else {
                    front = fronts.get(index);
                }
            } // if
        }

        // remain is less than front(index).size, insert only the best one
        //System.out.println("DEBUG: Adding remaining individuals of a front...");
        if (remain > 0) {  // front contains individuals to insert                        

            // Assign diversity function to individuals
            CalculateDistanceCrowding(front);

            // Sort population with the diversity function
            // CHECK IF THE SORT IS EQUAL TO THE USED BEFORE
            front.sort((x, y) -> Double.compare(x.getCrowdingDistance(), y.getCrowdingDistance()));
            /*
            double[] ordenado = new double[front.size()];
            int izq = 0;
            int der = front.getNumIndiv() - 1;
            int indices[] = new int[front.getNumIndiv()];
            for (int i = 0; i < front.getNumIndiv(); i++) {
                indices[i] = i;
                ordenado[i] = front.getIndiv(i).getCrowdingDistance();
            }
            Utils.OrCrecIndex(ordenado, izq, der, indices); */
            int i = front.size() - 1;

            for (int k = remain - 1; k >= 0; k--) {
                resultPopulation.add(front.get(k).clone());
                //poblac.get(clas).CopyIndiv(contador, instances.size(), getNumObjectives(), front.getIndiv(indices[i]));
                i--;
                counter++;
            } // for

        }

        return resultPopulation;
    }

    /**
     * It calculates the crowding distance of the individuals of the given
     * front.
     *
     * @param front
     */
    private void CalculateDistanceCrowding(ArrayList<Individual> front) {

        int size = front.size();
        int nobj = front.get(0).getObjs().size();

        if (size == 0) {
            return;
        }

        if (size == 1) {
            front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if

        if (size == 2) {
            front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            front.get(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
            return;
        } // if       

        for (int i = 0; i < size; i++) {
            front.get(i).setCrowdingDistance(0.0);
        }

        double objetiveMaxn;
        double objetiveMinn;
        double distance;

        int ini, fin;

        for (int i = 0; i < nobj; i++) {
            final int index = i;
            front.sort((x, y) -> {
                QualityMeasure a = (QualityMeasure) x.getObjs().get(index);
                QualityMeasure b = (QualityMeasure) y.getObjs().get(index);
                return Double.compare(a.getValue(), b.getValue());
            });
            /*double[] ordenado = new double[pop.getNumIndiv()];
            int izq = 0;
            int der = pop.getNumIndiv() - 1;
            int indices[] = new int[pop.getNumIndiv()];
            //QualityMeasures medidas = new QualityMeasures(nobj);
            for (int j = 0; j < pop.getNumIndiv(); j++) {
                indices[j] = j;
                //medidas = pop.getIndiv(j).getMeasures();
                ordenado[j] = pop.getIndiv(j).getObjs().get(i).getValue();//medidas.getObjectiveValue(i);
            }
            Utils.OrCrecIndex(ordenado, izq, der, indices);*/

            //ini = indices
            //fin = indices[pop.getNumIndiv() - 1];

            //medidas = pop.getIndiv(ini).getMeasures();
            objetiveMinn = ((QualityMeasure) front.get(0).getObjs().get(i)).getValue(); //medidas.getObjectiveValue(i);
            //medidas = pop.getIndiv(fin).getMeasures();
            objetiveMaxn = ((QualityMeasure) front.get(front.size() - 1).getObjs().get(i)).getValue(); //medidas.getObjectiveValue(i);

            //Set de crowding distance            
            front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            front.get(front.size() - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

            double a, b;

            for (int j = 1; j < size - 1; j++) {
                //medidas = pop.getIndiv(indices[j + 1]).getMeasures();
                a = ((QualityMeasure) front.get(j + 1).getObjs().get(i)).getValue();
                //a = pop.getIndiv(indices[j + 1]).getObjs().get(i).getValue(); //medidas.getObjectiveValue(i);
                //medidas = pop.getIndiv(indices[j - 1]).getMeasures();
                b = ((QualityMeasure) front.get(j - 1).getObjs().get(i)).getValue();
                //b = pop.getIndiv(indices[j - 1]).getObjs().get(i).getValue();//medidas.getObjectiveValue(i);
                distance = a - b;
                if (distance != 0) {
                    distance = distance / (objetiveMaxn - objetiveMinn);
                }
                distance += front.get(j).getCrowdingDistance();
                front.get(j).setCrowdingDistance(distance);
            } // for
        } // for   
    }

}
