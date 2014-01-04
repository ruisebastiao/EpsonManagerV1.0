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

public class ListEpsonsFragment extends Fragment {
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

    public  EpsonListAdapter EpsonsAdapter=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_epsons, container, false);




        //Epson[] epsons= (Epson[])Propriedades.getInstance().getEpsons().toArray();
            final EpsonListAdapter adapter = new EpsonListAdapter(getActivity(),
                    R.layout.epson_item_row,(Propriedades.getInstance().getEpsons()));

            EpsonsAdapter=adapter;
            ListEpsons = (ListView)rootView.findViewById(R.id.listViewEpsons);

            //View header = (View)inflater.inflate(R.layout.epsons_header, null);
            //ListEpsons.addHeaderView(header);


            ListEpsons.setAdapter(adapter);
            ListEpsons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListEpsons.setSelection(position);
                    Epson selected=Propriedades.getInstance().getEpsons().get(position);

                    Propriedades.getInstance().setSelectedEpson(selected);
                    if (selected.getmOnEpsonStatusChanged()==null)
                        selected.setmOnEpsonStatusChanged(new Epson.OnEpsonStatusChanged() {
                            @Override
                            public void EpsonStatusChanged(Epson.OnEpsonStatusChangedEventArgs args) {

                                if (args.getmStatus()=="Client Connected"){
                                        getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            MainActivity.mEpsonViewPager.setPagingEnabled(true);
                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                }
                                else if (args.getmStatus()=="Client Disconnected"){
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            MainActivity.mEpsonViewPager.setPagingEnabled(false);
                                            MainActivity.mEpsonViewPager.setCurrentItem(0);
                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                }
                            }
                        });

//                    if (selected.isConnected()==false)
//                        selected.setConnected();
//                    else{
//                        selected.getTcpClient().stopClient();
//
//                    }



                    //view.setBackgroundColor(Color.parseColor("#FEF0D4"));
                }
            });


        int mActivePosition=0;
        ListEpsons.performItemClick(
                ListEpsons.getAdapter().getView(mActivePosition, null, null),
                mActivePosition,
                ListEpsons.getAdapter().getItemId(mActivePosition));

        //TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        //dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }


}