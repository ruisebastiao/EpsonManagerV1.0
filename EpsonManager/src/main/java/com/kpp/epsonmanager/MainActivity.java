package com.kpp.epsonmanager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<SharedPreferences> {

    private static final String KEY = "prefs";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    static EpsonPagerAdapter mEpsonPagerAdapter =null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static CustomEpsonViewPager mEpsonViewPager =null;

    @Override
    public Loader<SharedPreferences> onCreateLoader(int id, Bundle args) {
        return (new PreferencesLoader(this));
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onLoadFinished(Loader<SharedPreferences> loader,
                               SharedPreferences prefs) {
        int value = prefs.getInt(KEY, 0);
        value += 1;
        //display in long period of time
        Toast.makeText(getApplicationContext(),String.valueOf(value), Toast.LENGTH_LONG).show();


        // update value
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY, value);
        PreferencesLoader.persist(editor);
    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> loader) {
        // NOT used
    }

    public static final String EPSONSLIST = "epsonslist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);

        String connectionsJSONString = getPreferences(MODE_PRIVATE).getString(EPSONSLIST, null);
        //Type type = new TypeToken < List < EPSONSLIST>> () {}.getType();
       // List < EPSONSLIST> connections = new  Gson().fromJson(connectionsJSONString, type)

        Epson epsons[] = new Epson[]

                {
                        new Epson(R.drawable.robot, "MGB Gear Wheels","PC432-Automacao")

                };

        Propriedades.getInstance().setEpsons(epsons);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.


        // Set up the ViewPager with the sections adapter.
        mEpsonViewPager = (CustomEpsonViewPager) findViewById(R.id.pager);

        mEpsonViewPager.setPagingEnabled(false);

        mEpsonPagerAdapter = new EpsonPagerAdapter(getSupportFragmentManager(),mEpsonViewPager);

        mEpsonViewPager.setAdapter(mEpsonPagerAdapter);

        mEpsonPagerAdapter.AddFragment(new ListEpsonsFragment());
        mEpsonPagerAdapter.AddFragment(new EpsonStateFragment());
        mEpsonPagerAdapter.notifyDataSetChanged();

        Propriedades.getInstance().setHandlerListener(new Propriedades.NewEpsonSelectedListener() {
            @Override
            public void onNewEpsonSelected(Epson selected) {

                if(selected==null)
                    mEpsonViewPager.setPagingEnabled(false);
                else{



                    if (selected.isConnected()){
                        mEpsonViewPager.setPagingEnabled(true);
                    }
                    else{
                        mEpsonViewPager.setPagingEnabled(false);
                    }
                }

                mEpsonPagerAdapter.notifyDataSetChanged();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    public class EpsonPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<Fragment>();

        public EpsonPagerAdapter(FragmentManager fm,ViewPager vp) {
            super(fm);
        }

        public  void AddFragment(Fragment frag){
            mFragments.add(frag);
        }

        public  void RemoveFragment(Fragment frag){
            mFragments.remove(frag);
        }

        public  void RemoveFragment(int pos){
            if (mFragments.size()>pos)
                mFragments.remove(pos);

            this.notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ListEpsonsFragment (defined as a static inner class
            // below) with the page number as its lone argument.

//            Fragment fragment = new ListEpsonsFragment();
//            Bundle args = new Bundle();
//            args.putInt(ListEpsonsFragment.ARG_SECTION_NUMBER, position + 1);
//            fragment.setArguments(args);
//            if ( Propriedades.getInstance().getSelectedEpson()!=null){
//                Epson epson=Propriedades.getInstance().getSelectedEpson();
//                if (epson.isConnected()==false)
//                    return  null;
//            }
           // if (position>1) return  null;
            if (position>=mFragments.size())
                return null;

            return mFragments.get(position);

            //return mFragments.get(position);
        }

        @Override
        public int getCount() {
//
            return mFragments.size();
        }



        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            switch (position) {
                case 0:
                    return getString(R.string.title_section_ligacoes).toUpperCase(l);
                case 1:
                    if ( Propriedades.getInstance().getSelectedEpson()!=null){
                        Epson epson=Propriedades.getInstance().getSelectedEpson();
                        return epson.title;
                    }
                    else
                        return  "";
                    //return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return "Pontos";
            }
            return null;
        }
    }



}
