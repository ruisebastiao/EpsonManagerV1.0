package com.kpp.epsonmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.annotation.SuppressLint;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;


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
//        int value = prefs.getInt(KEY, 0);
//        value += 1;
//        //display in long period of time
//        Toast.makeText(getApplicationContext(),String.valueOf(value), Toast.LENGTH_LONG).show();
//
//
//        // update value
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt(KEY, value);
//        PreferencesLoader.persist(editor);
    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> loader) {
        // NOT used
    }

    public Object DoDeserialize(String filename)
    {
        FileInputStream fis= null;
        try {
            fis = getApplicationContext().openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object o = null;
        try {
            o = ois.readObject();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return o;

    }

    public void SaveConfigurations(String filename, Object saveObject){
        try
        {
            FileOutputStream fos=getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos); //Select where you wish to save the file...
            oos.writeObject(saveObject); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save.bin'
            oos.close();// close the stream
        }
        catch(Exception ex)
        {
            Log.v("Configurations reading", ex.getMessage());
            ex.printStackTrace();
        }

    }

    public EpsonConfigurations LoadConfigurations(String filename){
        try {

            File file =  getApplicationContext().getFileStreamPath(filename);
            if(file.exists()){
 
            }
            else{

                SaveConfigurations(filename, new EpsonConfigurations());
            }

           return (EpsonConfigurations)DoDeserialize(filename);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);


        getLoaderManager().initLoader(0, null, this);


        //SaveConfigurations("Configurations.cfg", new EpsonConfigurations());
        EpsonConfigurations configs=(EpsonConfigurations) LoadConfigurations("Configurations.cfg");

        if (configs!=null){
            if (configs.getEpsons().size()==0){
                configs.getEpsons().add(new Epson(R.drawable.robot, "MGB Gear Wheels", "PC432-Automacao"));
            }
        }
        //SaveConfigurations("Configurations.cfg", configs);
//        Epson epsons[] = new Epson[]
//
//                {
//                        new Epson(R.drawable.robot, "MGB Gear Wheels","PC432-Automacao")
//
//                };

        Propriedades.getInstance().setEpsons(configs.getEpsons());

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
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_settings);

        // When using the support library, the setOnActionExpandListener() method is
        // static and accepts the MenuItem object as an argument
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                        return epson.getTitle();
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
