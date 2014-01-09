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
import android.widget.Spinner;

/**
 * Created by Geral on 16-10-2013.
 */
public class EpsonPontosFragment extends Fragment implements View.OnClickListener {


    public  Spinner mSpinnerPontos =null;


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
                selected.getTcpClient().sendMessage("SET|POINT|" + mSpinnerPontos.getSelectedItem().toString());
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
    ArrayAdapter<String> adapter=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_epsonpoints, container, false);



        mSpinnerPontos =(Spinner)rootView.findViewById(R.id.spinnerPontos);


        if (adapter==null)
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item);

        mSpinnerPontos.setAdapter(adapter);


        Button savebt=(Button)rootView.findViewById(R.id.btsavept);
        savebt.setOnClickListener(this);

        Button jumpbt=(Button)rootView.findViewById(R.id.bt_jumptopoint);
        jumpbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Epson selected=Propriedades.getInstance().getSelectedEpson();
                try {

                    if (mSpinnerPontos.getSelectedItem()!=null) {
                        selected.getTcpClient().sendMessage("JUMPTO|POINT|"+ mSpinnerPontos.getSelectedItem().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//            ListPointsAdapter = new ArrayAdapter(this.getActivity(),
//                    android.R.layout.simple_spinner_dropdown_item,R.array.pontos);
//            mSpinnerPontos.setAdapter(ListPointsAdapter);



        return rootView;

    }

    public String getTitle() {
        return title;
    }
}