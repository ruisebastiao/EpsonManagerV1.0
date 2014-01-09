package com.kpp.epsonmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

/**
 * Created by Geral on 15-10-2013.
 */

public class ListEpsonsFragment extends Fragment implements AdapterView.OnItemClickListener,Epson.OnEpsonStatusChanged {
//        public static String ARG_SECTION_NUMBER ;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public ListView ListEpsons;


//    public  static  ArrayAdapter<String> ListPointsAdapter=null;


    public static final String ARG_SECTION_NUMBER = "section_number";



    public ListEpsonsFragment() {




    }

    private EpsonListAdapter adapter=null;
    //public  EpsonListAdapter EpsonsAdapter=null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_epsons, container, false);




            adapter = new EpsonListAdapter(getActivity(),
                    R.layout.epson_item_row,(Propriedades.getInstance().getEpsons()));



            ListEpsons = (ListView)rootView.findViewById(R.id.listViewEpsons);


            ListEpsons.setAdapter(adapter);
            ListEpsons.setOnItemClickListener(this);


        return rootView;
    }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListEpsons.setSelection(position);
            Epson selected=Propriedades.getInstance().getEpsons().get(position);


            Propriedades.getInstance().setSelectedEpson(selected);
            if (selected.getmOnEpsonStatusChanged()==null)
                selected.setmOnEpsonStatusChanged(this);
            //MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void EpsonStatusChanged(Epson.OnEpsonStatusChangedEventArgs args) {
            if (args.getState()== Epson.ConnectionState.CONNECTED){
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.mEpsonViewPager.setPagingEnabled(true);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
            else if (args.getState()== Epson.ConnectionState.DISCONNECTED){
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        MainActivity.mEpsonViewPager.setPagingEnabled(false);
                        MainActivity.mEpsonViewPager.setCurrentItem(0);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
            else if (args.getState()== Epson.ConnectionState.CONNECTING){
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        }
}