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

public class EpsonStateFragment extends Fragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public  ProgressBar progresswaitman=null;
    public  ToggleButton manbt=null;
    public  TextView txtRbState=null;
    public  TextView txtRbMsg=null;



    public EpsonStateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void UpdateContents(){

    }

    private Epson mEpsonSelected;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_epsonstate, container,false);
        mEpsonSelected =Propriedades.getInstance().getSelectedEpson();

        if (mEpsonSelected !=null) {


            manbt= (ToggleButton)rootView.findViewById(R.id.btmanmode);
            manbt.setEnabled(mEpsonSelected.getRobotState()== Epson.RobotState.ONLINE);

            manbt.setOnClickListener(this);

            progresswaitman= (ProgressBar)rootView.findViewById(R.id.progressWaitMan);
            switch (mEpsonSelected.getRobotManState()){
                case MANMODEON:
                    manbt.setText("Ligado");
                    progresswaitman.setVisibility(View.GONE);
                    break;
                case MANMODEOFF:
                    progresswaitman.setVisibility(View.GONE);
                    break;
                case WAITMANMODEON:
                    progresswaitman.setVisibility(View.VISIBLE);
                    break;
            }

            txtRbState =(TextView)rootView.findViewById(R.id.textRbState);
            switch (mEpsonSelected.getRobotState()){
                case ONLINE:
                    txtRbState.setText("Robot Ligado");
                    progresswaitman.setVisibility(View.GONE);

                    break;
                case OFFLINE:
                    txtRbState.setText("Robot Desligado");
                    progresswaitman.setVisibility(View.GONE);
                    manbt.setEnabled(false);
                    break;

            }

            txtRbMsg=(TextView)rootView.findViewById(R.id.textMsgRb);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(mEpsonSelected.getRobotManState()== Epson.RobotManState.MANMODEOFF){

            mEpsonSelected.getTcpClient().sendMessage("SET|MANMODE");
            if (progresswaitman!=null) {
                progresswaitman.setVisibility(View.VISIBLE);
            }
        }
    }
}