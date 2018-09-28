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

import moa.subgroupdiscovery.genetic.Individual;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Class that represents a canonical rule, i.e., rule formed by conjuctions of attribute-value pairs
 * 
 * @author Angel Miguel Garcia-Vico (agvico@ujaen.es)
 */
public class IndCAN extends Individual<Integer> {

    public IndCAN(int length, int neje, int clas) {
        ArrayList<QualityMeasure> objectivesArray = StreamMOEAEFEP.getObjectivesArray();

        try {
            this.clas = clas;
            size = length;
            chromosome = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                chromosome.add(0);
            }
            medidas = new ArrayList<>();
            objs = new ArrayList<>();
            for (int i = 0; i < objectivesArray.size(); i++) {
                if (!(objectivesArray.get(i) instanceof NULL)) {
                    objs.add(objectivesArray.get(i).clone());
                }
            }

            conf = new Confidence();
            diversityMeasure = (QualityMeasure) StreamMOEAEFEP.getDiversityMeasure().getClass().newInstance();
            evaluated = false;
            cubre = new BitSet(neje);

            crowdingDistance = 0.0;
            n_eval = 0;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(IndCAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer getCromElem(int pos) {
        return chromosome.get(pos);
    }

    @Override
    public void setCromElem(int pos, Integer val) {
        chromosome.set(pos, val);
    }

    @Override
    public void copyIndiv(Individual indi, int nobj, int neje) {
        if (!(indi instanceof IndCAN)) {
            try {
                throw new InvalidClassException("copyIndiv in IndCAN: \"indi\" is not of class IndCAN");
            } catch (InvalidClassException ex) {
                Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(2);
            }
        }

        IndCAN a = (IndCAN) indi;
        for (int i = 0; i < this.getSize(); i++) {
            this.setCromElem(i, a.getCromElem(i));
        }

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.getCubre().clear(0, neje);
        this.getCubre().or(a.getCubre());

        this.setIndivEvaluated(a.getIndivEvaluated());
        this.setCubre((BitSet) a.getCubre().clone());
        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setRank(a.getRank());

        this.setObjs(new ArrayList<>());
        for (QualityMeasure q : a.getObjs()) {
            QualityMeasure aux = q.clone();

            this.getObjs().add(aux);
        }

        this.setMedidas(new ArrayList<>());
        for (QualityMeasure q : a.getMedidas()) {
            QualityMeasure aux = q.clone();

            this.getMedidas().add(aux);
        }

        this.setConf((Confidence) a.getConf().clone());

        this.setClas(a.getClas());

        this.setNEval(a.getNEval());
    }

    @Override
    public int NumInterv(double valor, int num_var, Instance inst) {
        float pertenencia = 0, new_pert = 0;
        int interv = -1;

        for (int i = 0; i < StreamMOEAEFEP.nLabel; i++) {
            new_pert = StreamMOEAEFEP.Fuzzy(num_var, i, valor);
            if (new_pert > pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;
    }

    @Override
    public String toString(InstancesHeader inst) {
        String content = "";
        for (int i = 0; i < inst.numInputAttributes(); i++) {
            if (inst.inputAttribute(i).isNominal()) {
                // Discrete variable
                if (this.getCromElem(i) < inst.inputAttribute(i).numValues()) {
                    content += "\tVariable " + inst.inputAttribute(i).name() + " = ";
                    content += inst.inputAttribute(i).value(this.getCromElem(i)) + "\n";
                }
            } else {
                // Continuous variable
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    content += "\tVariable " + inst.inputAttribute(i).name() + " = ";
                    content += "Label " + this.getCromElem(i);
                    content += " (" + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX0();
                    content += " " + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX1();
                    content += " " + StreamMOEAEFEP.baseDatos[i][this.getCromElem(i)].getX3() + ")\n";
                }
            }
        }
        content += "\tConsecuent: " + inst.outputAttribute(0).value(getClas());
        return content;
    }

    @Override
    public int getNumVars() {
        int nVars = 0;
        for (int i = 0; i < chromosome.size(); i++) {
            if (StreamMOEAEFEP.instancia.inputAttribute(i).isNominal()) {
                if (this.getCromElem(i) < StreamMOEAEFEP.instancia.inputAttribute(i).numValues()) {
                    nVars++;
                }
            } else {
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    nVars++;
                }
            }
        }

        return nVars;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < chromosome.size(); i++) {
            if (StreamMOEAEFEP.instancia.inputAttribute(i).isNominal()) {
                if (this.getCromElem(i) < StreamMOEAEFEP.instancia.inputAttribute(i).numValues()) {
                    return false;
                }
            } else {
                if (this.getCromElem(i) < StreamMOEAEFEP.nLabel) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean getCromGeneElem(int pos, int elem) {
        throw new UnsupportedOperationException("IndCAN does not contains additional elements on its genes."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCromGeneElem(int pos, int elem, boolean val) {
        throw new UnsupportedOperationException("IndCAN does not contains additional elements on its genes."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual<Integer> clone() {
        IndCAN copia = new IndCAN(this.size, this.cubre.size(), this.clas);

        copia.conf = (Confidence) this.conf.clone();
        copia.crowdingDistance = this.crowdingDistance;
        copia.cubre = (BitSet) this.getCubre().clone();
        copia.diversityMeasure = this.getDiversityMeasure().clone();
        copia.evaluated = this.evaluated;
        copia.medidas = new ArrayList<>();

        for (QualityMeasure q : this.medidas) {
            copia.medidas.add(q.clone());
        }
        copia.n_eval = this.n_eval;

        copia.objs = new ArrayList<>();
        for (QualityMeasure q : this.objs) {
            copia.objs.add(q.clone());
        }

        copia.rank = this.rank;
        copia.size = this.size;

        // Copy the elements of the chromosome
        for (int i = 0; i < chromosome.size(); i++) {
            copia.chromosome.set(i, this.chromosome.get(i));
        }

        return copia;
    }

    @Override
    public int hashCode() {
        return chromosome.hashCode() + this.clas;
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
        
        final IndCAN other = (IndCAN) obj;
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(this.chromosome.get(i), other.chromosome.get(i))) {
                return false;
            }
        }

        if (this.clas != other.clas) {
            return false;
        }
        
        return this.clas == other.clas;
    }

}
