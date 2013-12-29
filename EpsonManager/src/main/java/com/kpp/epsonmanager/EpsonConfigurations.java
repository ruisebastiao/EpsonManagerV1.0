package com.kpp.epsonmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geral on 14-10-2013.
 */


public  class EpsonConfigurations implements Serializable {

    private static final long serialVersionUID = 1;


    public EpsonConfigurations(){

    }


    private ArrayList<Epson> Epsons= new ArrayList<Epson>();

    public ArrayList<Epson> getEpsons() {
        return Epsons;
    }

    public void setEpsons(ArrayList<Epson> epsons) {
        Epsons = epsons;
    }

    //public List<Epson> getEpsonList() {
        //return Epsons;
    //}
}
