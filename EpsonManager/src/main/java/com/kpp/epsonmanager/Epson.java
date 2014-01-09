package com.kpp.epsonmanager;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class Epson implements Serializable{

    public ConnectionState getStat() {
        return mConnectionState;
    }

    private void setStat(ConnectionState mStat) {

        this.mConnectionState = mStat;


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

    public enum RobotManState{
        MANMODEOFF(0),
        MANMODEON(1),
        WAITMANMODEON(2),
        ;

        private final int id;

        RobotManState(int id) { this.id = id;}

        public int getValue() { return id; }
    }

    public enum RobotState{
        OFFLINE(0),
        ONLINE(0),
        ;

        private final int id;

        RobotState(int id) { this.id = id;}

        public int getValue() { return id; }
    }

    private static final long serialVersionUID = 1;

    private String title;
    private String Hostname;

    public transient int icon;

    private transient TCPClient mTcpClient;
    private transient OnEpsonStatusChanged mOnEpsonStatusChanged = null;
    protected transient RobotState mRobotState=RobotState.OFFLINE;
    protected transient RobotManState mRobotManState=RobotManState.MANMODEOFF;


    protected transient ConnectionState mConnectionState = ConnectionState.DISCONNECTED;

    public TCPClient getTcpClient() {
        return mTcpClient;
    }
    public OnEpsonStatusChanged getmOnEpsonStatusChanged() {
        return mOnEpsonStatusChanged;
    }
    public void setmOnEpsonStatusChanged(OnEpsonStatusChanged mOnEpsonStatusChanged) {
        this.mOnEpsonStatusChanged = mOnEpsonStatusChanged;
    }

    public RobotManState getRobotManState() {
        return mRobotManState;
    }

    private void setRobotManState(RobotManState state) {
        mRobotManState =state;
    }

    public RobotState getRobotState() {
        return mRobotState;
    }

    private void setRobotState(RobotState state) {
        mRobotState =state;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public interface OnEpsonStatusChanged
    {
        void EpsonStatusChanged(OnEpsonStatusChangedEventArgs args);
    }

    private OnEpsonStatusChanged mEpsonStatusChangedListner = null;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        mConnectionState =ConnectionState.DISCONNECTED;
        mRobotState=RobotState.OFFLINE;
        mRobotManState=RobotManState.MANMODEOFF;
    }

    public Epson(){
        super();
        mConnectionState =ConnectionState.DISCONNECTED;
    }

    public Epson(int icon, String title,String hostname) {
        super();
        this.icon = icon;
        this.setTitle(title);
        this.Hostname=hostname;
        mConnectionState =ConnectionState.DISCONNECTED;
    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    public void setRobotConnected(Boolean state) {
        if (state){
            if (mConnectionState == ConnectionState.DISCONNECTED){
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


                String[] strlist=strval.split("\\|");
                if (strlist.length>0){
                    if(strlist[0].equals("STATUS")){
                        if(strlist[1].equals("CONNECTED")){


                            setRobotState(RobotState.ONLINE);
                            MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                        }
                        else if(strlist[1].equals("DISCONNECTED")){


                            setRobotState(RobotState.OFFLINE);
                            MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                        }
                        else if(strlist[1].equals("RB")){
                            //EpsonStateFragment.txtRbMsg.setText(strlist[2]);
                        }
                    }

                    else {
                        if (strlist[0].equals("SET")) {
                            if (strlist[1].equals("MANMODE")) {

                                if (strlist[2].equals("OK")) {

                                    setRobotManState(RobotManState.MANMODEON);
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();

                                } else {
                                    setRobotManState(RobotManState.MANMODEOFF);
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                                }
                            } else if (strlist[1].equals("POINTLIST")) {

                                ((ArrayAdapter<String>)(MainActivity.mEpsonPontosFragment.mSpinnerPontos.getAdapter())).clear();
                                for (int i = 2; i < strlist.length; i++) {
                                    ((ArrayAdapter<String>)(MainActivity.mEpsonPontosFragment.mSpinnerPontos.getAdapter())).add(strlist[i]);
                                }

                                ((ArrayAdapter<String>)(MainActivity.mEpsonPontosFragment.mSpinnerPontos.getAdapter())).notifyDataSetChanged();

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
