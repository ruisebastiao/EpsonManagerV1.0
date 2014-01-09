package com.kpp.epsonmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.annotation.SuppressLint;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


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
            SaveConfigurations("Configurations.cfg", new EpsonConfigurations());
        }
        return o;

    }

    private String configfilename="";

    public void SaveConfigurations(){
        if (configs!=null){
            SaveConfigurations(configfilename,configs);
        }
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

    private EpsonConfigurations configs=null;
    public EpsonConfigurations LoadConfigurations(String filename){
        try {

            File file =  getApplicationContext().getFileStreamPath(filename);
            configfilename=filename;
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

    public static ListEpsonsFragment mListEpsonsFragment =null;
    public static EpsonStateFragment mEpsonStateFragment=null;
    public static EpsonPontosFragment mEpsonPontosFragment=null;
    public static EpsonJogFragment mEpsonJogFragment =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);


        getLoaderManager().initLoader(0, null, this);


        //SaveConfigurations("Configurations.cfg", new EpsonConfigurations());
        configs=(EpsonConfigurations) LoadConfigurations("Configurations.cfg");

        if (configs!=null){
            if (configs.getEpsons().size()==0){
                configs.getEpsons().add(new Epson(R.drawable.robot, "New Robot", "127.0.0.1"));
            }

        }
        //SaveConfigurations("Configurations.cfg", configs);


        Propriedades.getInstance().setEpsons(configs.getEpsons());
        Propriedades.getInstance().SetMainActivity(this);


        // Set up the ViewPager with the sections adapter.
        mEpsonViewPager = (CustomEpsonViewPager) findViewById(R.id.pager);

        mEpsonViewPager.setPagingEnabled(false);

        mEpsonPagerAdapter = new EpsonPagerAdapter(getSupportFragmentManager(),mEpsonViewPager);

        mEpsonViewPager.setAdapter(mEpsonPagerAdapter);
        mListEpsonsFragment =new ListEpsonsFragment();
        mEpsonStateFragment=new EpsonStateFragment();
        mEpsonPontosFragment=new EpsonPontosFragment();
        mEpsonJogFragment= new EpsonJogFragment();

        mEpsonPagerAdapter.AddFragment(mListEpsonsFragment);
        mEpsonPagerAdapter.AddFragment(mEpsonStateFragment);
        mEpsonPagerAdapter.AddFragment(mEpsonPontosFragment);
        mEpsonPagerAdapter.AddFragment(mEpsonJogFragment);
        mEpsonPagerAdapter.notifyDataSetChanged();

        Propriedades.getInstance().setHandlerListener(new Propriedades.NewEpsonSelectedListener() {
            @Override
            public void onNewEpsonSelected(Epson selected) {

                if(selected==null)
                    mEpsonViewPager.setPagingEnabled(false);
                else{



                    if (selected.getStat()== Epson.ConnectionState.CONNECTED){
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
    Fragment selectedfrag=null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:

                SaveConfigurations();

                return true;
            case R.id.action_addrb:

                configs.getEpsons().add(new Epson(R.drawable.robot, "New Robot", "127.0.0.1"));
                selectedfrag=mEpsonPagerAdapter.getItem(mEpsonViewPager.getCurrentItem());

                if (selectedfrag!=null) {
                    if (selectedfrag instanceof ListEpsonsFragment){
                        if (((ListEpsonsFragment)selectedfrag).ListEpsons.getAdapter()!=null) {
                            ((EpsonListAdapter)((ListEpsonsFragment)selectedfrag).ListEpsons.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }


                return true;
            case R.id.action_delrb:


                selectedfrag=mEpsonPagerAdapter.getItem(mEpsonViewPager.getCurrentItem());

                if (selectedfrag!=null) {
                    if (selectedfrag instanceof ListEpsonsFragment){
                        if (((ListEpsonsFragment)selectedfrag).ListEpsons.getAdapter()!=null) {

                            configs.getEpsons().remove(Propriedades.getInstance().getSelectedEpson());
                            ((EpsonListAdapter)((ListEpsonsFragment)selectedfrag).ListEpsons.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }

                mEpsonPagerAdapter.notifyDataSetChanged();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);




        return super.onCreateOptionsMenu(menu);
    }
    


    public class EpsonPagerAdapter extends FragmentStatePagerAdapter  {

        private List<Fragment> mFragments = new ArrayList<Fragment>();

        public EpsonPagerAdapter(FragmentManager fm,ViewPager vp) {
            super(fm);

        }


        public  void AddFragment(Fragment frag){

            mFragments.add(frag);
        }

//        public  void RemoveFragment(Fragment frag){
//            mFragments.remove(frag);
//        }


        public ArrayAdapter<Epson> EpsonsAdapter=null;
        @Override
        public Fragment getItem(int position) {

            Fragment selected=mFragments.get(position);

            return selected;


        }

        @Override
        public int getCount() {
//
            return mFragments.size();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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
                case 3:
                    return "JOG";
                default:
                    return "Not Set";
            }
            //return "Not Set";
        }
    }



}
