package com.kpp.epsonmanager;

import android.app.Application;

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


    protected Propriedades(){}

    public static synchronized Propriedades getInstance(){
        if(null == mInstance){
            mInstance = new Propriedades();
        }
        return mInstance;
    }

    private Epson[] Epsons;

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

    public Epson[] getEpsons() {
        return Epsons;
    }

    public void setEpsons(Epson[] epsons) {
        Epsons = epsons;
    }
}