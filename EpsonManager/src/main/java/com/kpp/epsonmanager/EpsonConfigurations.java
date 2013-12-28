package com.kpp.epsonmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geral on 14-10-2013.
 */


public  class EpsonConfigurations implements Serializable {

    private static final long serialVersionUID = 1;

    private String selectedEpson="NONE";

    public String getSelectedEpson() {
        return selectedEpson;
    }

    public void setSelectedEpson(String selectedEpson) {
        this.selectedEpson = selectedEpson;
    }


    public EpsonConfigurations(){

    }

    public EpsonConfigurations(String Selected){
        selectedEpson=Selected;
    }

    //private List<Epson> Epsons= new ArrayList<Epson>();
    //public List<Epson> getEpsonList() {
        //return Epsons;
    //}
}
