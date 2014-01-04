package com.kpp.epsonmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Geral on 16-10-2013.
 */

public class EpsonStateFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static ProgressBar progresswaitman=null;
    public static ToggleButton manbt=null;
    public static TextView txtRbState=null;
    public static TextView txtRbMsg=null;



    public EpsonStateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_epsonstate, container, false);

        rootView = inflater.inflate(R.layout.fragment_epsonstate, container, false);
        manbt= (ToggleButton)rootView.findViewById(R.id.btmanmode);
        progresswaitman= (ProgressBar)rootView.findViewById(R.id.progressWaitMan);
        txtRbState =(TextView)rootView.findViewById(R.id.textRbState);
        txtRbMsg=(TextView)rootView.findViewById(R.id.textMsgRb);
        manbt.setEnabled(false);
//            spinnerPontos=(Spinner)rootView.findViewById(R.id.spinnerPontos);
//
//            ListPointsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item);
//            spinnerPontos.setAdapter(ListPointsAdapter);

//            ListPointsAdapter = new ArrayAdapter(this.getActivity(),
//                    android.R.layout.simpe,R.array.pontos);
//            spinnerPontos.setAdapter(ListPointsAdapter);


        progresswaitman.setVisibility(View.GONE);
        manbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manbt.isChecked()==true){
                    Epson selected=Propriedades.getInstance().getSelectedEpson();
                    selected.getTcpClient().sendMessage("SET|MANMODE");
                    progresswaitman.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }
}