package com.kpp.epsonmanager;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class Epson implements Serializable{

    public ConnectionState getStat() {
        return mStat;
    }

    private void setStat(ConnectionState mStat) {

        this.mStat = mStat;


        if (mOnEpsonStatusChanged!=null){
            mOnEpsonStatusChanged.EpsonStatusChanged(new OnEpsonStatusChangedEventArgs(this,mStat));
        }

    }

    public enum ConnectionState{
        DISCONNECTED(0),
        CONNECTED(1),
        CONNECTING(2),
        ;

        private final int id;

        ConnectionState(int id) { this.id = id;}

        public int getValue() { return id; }
    }

    private static final long serialVersionUID = 1;

    private String title;
    private String Hostname;

    public transient int icon;

    private transient TCPClient mTcpClient;
    private transient OnEpsonStatusChanged mOnEpsonStatusChanged = null;
    private transient boolean ManMode=false;
    private transient boolean RobotOnline=false;

    protected transient ConnectionState mStat = ConnectionState.DISCONNECTED;

    public TCPClient getTcpClient() {
        return mTcpClient;
    }
    public OnEpsonStatusChanged getmOnEpsonStatusChanged() {
        return mOnEpsonStatusChanged;
    }
    public void setmOnEpsonStatusChanged(OnEpsonStatusChanged mOnEpsonStatusChanged) {
        this.mOnEpsonStatusChanged = mOnEpsonStatusChanged;
    }


    public transient ArrayAdapter<String> ListPointsAdapter=null;


    public boolean isManMode() {
        return ManMode;
    }

    private void setManMode(boolean manMode) {
        ManMode = manMode;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRobotOnline() {
        return RobotOnline;
    }

    private void setRobotOnline(boolean robotOnline) {
        RobotOnline = robotOnline;
    }


    public interface OnEpsonStatusChanged
    {
        void EpsonStatusChanged(OnEpsonStatusChangedEventArgs args);
    }

    private OnEpsonStatusChanged mEpsonStatusChangedListner = null;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        mStat=ConnectionState.DISCONNECTED;

    }

    public Epson(){
        super();
        mStat=ConnectionState.DISCONNECTED;
    }

    public Epson(int icon, String title,String hostname) {
        super();
        this.icon = icon;
        this.setTitle(title);
        this.Hostname=hostname;
        mStat=ConnectionState.DISCONNECTED;
    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    public void setRobotConnected(Boolean state) {
        if (state){
            if (mStat == ConnectionState.DISCONNECTED){
                new connectTask().execute();
            }
        }
        else
            mTcpClient.stopClient();
    }

    public String getHostname() {
        return Hostname;
    }

    public void setHostname(String hostname) {
        Hostname = hostname;

    }


    public class OnEpsonStatusChangedEventArgs{


        private Epson mEpson=null;
        private ConnectionState mState=null;

        public  OnEpsonStatusChangedEventArgs(Epson epson,ConnectionState state){
            mEpson=epson;
            mState=state;
        }

        public Epson getEpson() {
            return mEpson;
        }
        public  ConnectionState getState(){return mState;}


    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {


        protected void onPostExecute(TCPClient result) {
            if (result!=null){

            }
        }





        protected TCPClient doInBackground(String... message) {


            //we create a TCPClient object and
            mTcpClient = new TCPClient(Hostname,9620,new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            },
            new TCPClient.OnClientConnected() {
                @Override
                public void clientConnected() {
                    setStat(ConnectionState.CONNECTED);
                }
            });

            mTcpClient.setmOnConnectionError(new TCPClient.OnConnectionError() {
                @Override
                public void ConnectionError(String ErrorMessage) {

                    final String message=ErrorMessage;
                    Propriedades.getInstance().GetMainActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Propriedades.getInstance().GetMainActivity(), String.valueOf(message), Toast.LENGTH_LONG).show();
                        }
                    });

                    setStat(ConnectionState.DISCONNECTED);

                }
            });
            mTcpClient.setClientDisconnectedListner(new TCPClient.OnClientDisconnected() {
                @Override
                public void clientDisconnected() {
                    setStat(ConnectionState.DISCONNECTED);
                }
            });
            setStat(ConnectionState.CONNECTING);
            mTcpClient.run();

            return mTcpClient;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
                String strval=values[0];


                String[] strlist=TextUtils.split(strval, "\\|");
                if (strlist.length>0){
                    if(strlist[0].equals("STATUS")){
                        if(strlist[1].equals("CONNECTED")){
                            EpsonStateFragment.txtRbState.setText("Robot Ligado");
                            EpsonStateFragment.manbt.setEnabled(true);

                            setRobotOnline(true);
                        }
                        else if(strlist[1].equals("DISCONNECTED")){

                            EpsonStateFragment.txtRbState.setText("Robot Desligado");
                            EpsonStateFragment.manbt.setEnabled(false);
                            if (MainActivity.mEpsonViewPager.getCurrentItem()>1){
                                MainActivity.mEpsonViewPager.setCurrentItem( MainActivity.mEpsonViewPager.getCurrentItem()-1);
                            }
                            MainActivity.mEpsonPagerAdapter.RemoveFragment(2);
                            setRobotOnline(false);
                        }
                        else if(strlist[1].equals("RB")){
                            EpsonStateFragment.txtRbMsg.setText(strlist[2]);
                        }
                    }

                    else {
                        if (strlist[0].equals("SET")) {
                            if (strlist[1].equals("MANMODE")) {
                                EpsonStateFragment.progresswaitman.setVisibility(View.GONE);
                                EpsonStateFragment.progresswaitman.setEnabled(false);
                                if (strlist[2].equals("OK")) {

                                    EpsonStateFragment.manbt.setChecked(true);
                                    EpsonStateFragment.manbt.setText("Ligado");
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                                    setManMode(true);
                                } else {
                                    setManMode(false);

                                    EpsonStateFragment.manbt.setChecked(false);
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                                }
                            } else if (strlist[1].equals("POINTLIST")) {

                                Propriedades.getInstance().getSelectedEpson().ListPointsAdapter.clear();
                                for (int i = 2; i < strlist.length; i++) {
                                    Propriedades.getInstance().getSelectedEpson().ListPointsAdapter.add(strlist[i]);
                                }

                                Propriedades.getInstance().getSelectedEpson().ListPointsAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }

            //in the arrayList we add the messaged received from server
            //arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            //mAdapter.notifyDataSetChanged();
        }



    }
}
