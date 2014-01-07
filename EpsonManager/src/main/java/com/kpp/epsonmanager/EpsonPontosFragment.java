package com.kpp.epsonmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Geral on 16-10-2013.
 */
public class EpsonPontosFragment extends Fragment implements View.OnClickListener {


    public  Spinner spinnerPontos=null;


    private String title="Pontos";

    public EpsonPontosFragment(){
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

        alert.setTitle("Confirmar opção...");
        alert.setMessage("Salvar ponto?");

// Set an EditText view to get user input
      //  final EditText input = new EditText(this);
       // alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              //  String value = input.getText();
                // Do something with value!
                Epson selected=Propriedades.getInstance().getSelectedEpson();
                selected.getTcpClient().sendMessage("SET|POINT|"+spinnerPontos.getSelectedItem().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_epsonstate, container, false);

        rootView = inflater.inflate(R.layout.fragment_epsonpoints, container, false);


        spinnerPontos=(Spinner)rootView.findViewById(R.id.spinnerPontos);

        Propriedades.getInstance().getSelectedEpson().ListPointsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item);
        spinnerPontos.setAdapter(Propriedades.getInstance().getSelectedEpson().ListPointsAdapter);


        Button savebt=(Button)rootView.findViewById(R.id.btsavept);
        savebt.setOnClickListener(this);

        Button jumpbt=(Button)rootView.findViewById(R.id.bt_jumptopoint);
        jumpbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Epson selected=Propriedades.getInstance().getSelectedEpson();
                try {

                    if (spinnerPontos.getSelectedItem()!=null) {
                        selected.getTcpClient().sendMessage("JUMPTO|POINT|"+spinnerPontos.getSelectedItem().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//            ListPointsAdapter = new ArrayAdapter(this.getActivity(),
//                    android.R.layout.simple_spinner_dropdown_item,R.array.pontos);
//            spinnerPontos.setAdapter(ListPointsAdapter);



        return rootView;

    }

    public String getTitle() {
        return title;
    }
}