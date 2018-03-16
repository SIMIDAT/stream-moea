/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moa.subgroupdiscovery.genetic.individual;

import moa.subgroupdiscovery.genetic.Individual;
import com.yahoo.labs.samoa.instances.Instance;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import moa.subgroupdiscovery.StreamMOEAEFEP;
import moa.subgroupdiscovery.qualitymeasures.Confidence;
import moa.subgroupdiscovery.qualitymeasures.NULL;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * Class that represents an individual that represents a rule in disjunctive normal form (DNF)
 * 
 * @author agvico
 */
public final class IndDNF extends Individual<GeneDNF> {

    public IndDNF(int lenght, int neje, Instance inst, int clas) {
        ArrayList<QualityMeasure> objectivesArray = StreamMOEAEFEP.getObjectivesArray();
        try {
            super.tamano = lenght;
            super.chromosome = new ArrayList<>();
            for (int i = 0; i < lenght; i++) {
                if (inst.attribute(i).isNominal()) {
                    super.chromosome.add(new GeneDNF(inst.attribute(i).numValues()));
                } else {
                    super.chromosome.add(new GeneDNF(StreamMOEAEFEP.nLabel));
                }
            }

            medidas = new ArrayList<>();
            
            objs = new ArrayList<>();
            for(int i = 0; i < objectivesArray.size(); i++){
                if(!(objectivesArray.get(i) instanceof NULL)){
                    objs.add(objectivesArray.get(i).clone());
                }
            }
            
            conf = new Confidence();
            diversityMeasure = (QualityMeasure) StreamMOEAEFEP.getDiversityMeasure().getClass().newInstance();
            this.clas = clas;

            evaluado = false;
            cubre = new BitSet(neje);

            crowdingDistance = 0.0;
            n_eval = 0;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public GeneDNF getCromElem(int pos) {
        return chromosome.get(pos);
    }

    @Override
    public void setCromElem(int pos, GeneDNF val) {
        chromosome.set(pos, val);
    }

    @Override
    public boolean getCromGeneElem(int pos, int elem) {
        return chromosome.get(pos).getGeneElem(elem);
    }

    @Override
    public void setCromGeneElem(int pos, int elem, boolean val) {
        chromosome.get(pos).setGeneElem(elem, val);
    }

    @Override
    public void copyIndiv(Individual indi, int nobj, int neje) {

        if (!(indi instanceof IndDNF)) {
            try {
                throw new InvalidClassException("copyIndiv in IndDNF: \"indi\" is not of class IndDNF");
            } catch (InvalidClassException ex) {
                Logger.getLogger(IndDNF.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(2);
            }
        }

        IndDNF a = (IndDNF) indi;

        int number;
        for (int i = 0; i < this.getTamano(); i++) {
            number = a.getCromElem(i).getGeneLenght();
            for (int j = 0; j <= number; j++) {
                this.getCromElem(i).setGeneElem(j, a.getCromElem(i).getGeneElem(j));
            }
        }
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

        this.setNEval(a.getNEval());

        this.setClas(a.getClas());
    }

    @Override
    public int NumInterv(double valor, int num_var, Instance inst) {
        float pertenencia = 0, new_pert;
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
    public String toString(Instance inst) {
        String content = "";
        for (int i = 0; i < inst.numInputAttributes(); i++) {
            if (!this.getCromElem(i).isNonParticipant()) {
                if (inst.attribute(i).isNominal()) {
                    // discrete variable
                    content = "\tVariable " + inst.attribute(i).name() + " = ";
                    for (int j = 0; j < inst.attribute(i).numValues(); j++) {
                        if (this.getCromElem(i).getGeneElem(j)) {
                            content += inst.attribute(i).value(j) + "  ";
                        }
                    }
                    content += "\n";
                } else {
                    // continuous variable
                    content += "\tVariable " + inst.attribute(i).name() + " = ";
                    for (int j = 0; j < StreamMOEAEFEP.nLabel; j++) {
                        if (this.getCromElem(i).getGeneElem(j)) {
                            content += "Label " + j;
                            content += " (" + StreamMOEAEFEP.baseDatos[i][j].getX0();
                            content += " " + StreamMOEAEFEP.baseDatos[i][j].getX1();
                            content += " " + StreamMOEAEFEP.baseDatos[i][j].getX3() + ")\t";
                        }
                    }
                    content += "\n";
                }
            }
        }

        content += "\tConsecuent: " + inst.outputAttribute(0).value(getClas()) + "\n\n";
        return content;
    }

    @Override
    public int getNumVars() {
        int n_vars = 0;
        for(int i = 0; i < chromosome.size(); i++){
            if(!chromosome.get(i).isNonParticipant())
                n_vars++;
        }
        return n_vars;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < chromosome.size(); i++){
            if(! chromosome.get(i).isNonParticipant()){
                return false;
            }
        }
        return true;
    }

    @Override
    public Individual<GeneDNF> clone() {
        IndDNF copia = new IndDNF(this.tamano, this.getCubre().size(), StreamMOEAEFEP.instancia, this.clas);
        
        copia.conf = (Confidence) this.conf.clone();
        copia.crowdingDistance = this.crowdingDistance;
        copia.cubre = (BitSet) this.getCubre().clone();
        copia.diversityMeasure = this.getDiversityMeasure().clone();
        copia.evaluado = this.evaluado;
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
        copia.tamano = this.tamano;

        // Copy the elements of the chromosome
        for (int i = 0; i < chromosome.size(); i++) {
            for(int j = 0; j < chromosome.get(i).getGeneLenght(); j++){
                 copia.setCromGeneElem(i, j, this.getCromGeneElem(i, j));
            }
        }

        return copia;
    }

    @Override
    public int hashCode() {
        return this.chromosome.hashCode();
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
        final IndDNF other = (IndDNF) obj;
        
        if(this.getTamano() != other.getTamano()){
            return false;
        }
        
        for(int i = 0; i < chromosome.size(); i++){
            if(! this.chromosome.get(i).equals(other.chromosome.get(i))){
                return false;
            }
        }
        
        return true;
    }

}
