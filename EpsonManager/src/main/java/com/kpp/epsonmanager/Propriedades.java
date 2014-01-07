package com.kpp.epsonmanager;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Geral on 14-10-2013.
 */

public class Propriedades  {




    public interface NewEpsonSelectedListener
    {
        void onNewEpsonSelected(Epson selected);
    }




    NewEpsonSelectedListener EpsonSelectedHandler;
    public void setHandlerListener(NewEpsonSelectedListener listener)
    {
        EpsonSelectedHandler=listener;
    }
    protected void FireNewEpsonSelected(Epson newselected)
    {
        if(EpsonSelectedHandler!=null)
            EpsonSelectedHandler.onNewEpsonSelected(newselected);
    }

    private static Propriedades mInstance= null;

    protected Activity mainactivity;

    public void SetMainActivity(Activity acivity){
        this.mainactivity = acivity;
    }

    public Activity GetMainActivity(){
        return this.mainactivity;
    }

    protected Propriedades(){}

    public static synchronized Propriedades getInstance(){
        if(null == mInstance){
            mInstance = new Propriedades();
        }
        return mInstance;
    }

    private ArrayList<Epson> Epsons;

    private Epson SelectedEpson=null;

    public Epson getSelectedEpson() {
        return SelectedEpson;
    }

    public void setSelectedEpson(Epson selectedEpson) {
        if (this.SelectedEpson!=selectedEpson){
            this.SelectedEpson = selectedEpson;
            FireNewEpsonSelected(this.SelectedEpson);
        }
    }

    public ArrayList<Epson> getEpsons() {
        return Epsons;
    }

    public void setEpsons(ArrayList<Epson> epsons) {
        Epsons = epsons;
    }
}