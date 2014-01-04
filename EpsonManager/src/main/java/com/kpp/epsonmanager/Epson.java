package com.kpp.epsonmanager;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import java.io.Serializable;

public class Epson implements Serializable{

    private static final long serialVersionUID = 1;

    private String title;
    private String Hostname;

    public transient int icon;

    private transient TCPClient mTcpClient;
    private transient OnEpsonStatusChanged mOnEpsonStatusChanged = null;
    private transient boolean ManMode=false;
    private transient boolean RobotOnline=false;

    public TCPClient getTcpClient() {
        return mTcpClient;
    }
    public OnEpsonStatusChanged getmOnEpsonStatusChanged() {
        return mOnEpsonStatusChanged;
    }
    public void setmOnEpsonStatusChanged(OnEpsonStatusChanged mOnEpsonStatusChanged) {
        this.mOnEpsonStatusChanged = mOnEpsonStatusChanged;
    }


    private transient boolean isConnected=false;

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

    private void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected){
            if (mOnEpsonStatusChanged!=null){
                mOnEpsonStatusChanged.EpsonStatusChanged(new OnEpsonStatusChangedEventArgs(this, "Client Connected"));

            }
        }
        else{
            if (mOnEpsonStatusChanged!=null){
                mOnEpsonStatusChanged.EpsonStatusChanged(new OnEpsonStatusChangedEventArgs(this, "Client Disconnected"));

            }
        }
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

    public Epson(){
        super();


    }

    public Epson(int icon, String title,String hostname) {
        super();
        this.icon = icon;
        this.setTitle(title);
        this.Hostname=hostname;

    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setRobotConnected(Boolean state) {
        if (state){
            new connectTask().execute();
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
        private  String mStatus="";

        public  OnEpsonStatusChangedEventArgs(Epson epson,String status){
            mEpson=epson;
            mStatus=status;
        }

        public Epson getmEpson() {
            return mEpson;
        }

        public String getmStatus() {
            return mStatus;
        }
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
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
                    setConnected(true);

                }
            });


            mTcpClient.setClientDisconnectedListner(new TCPClient.OnClientDisconnected() {
                @Override
                public void clientDisconnected() {
                    setConnected(false);

                }
            });
            mTcpClient.run();

            return null;
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
                                    //EpsonStateFragment.manbt.setText("Ligado");
                                    EpsonStateFragment.manbt.setChecked(true);
                                    EpsonStateFragment.manbt.setText("Ligado");
                                    MainActivity.mEpsonPagerAdapter.AddFragment(new EpsonPontosFragment());
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                                    setManMode(true);
                                } else {
                                    setManMode(false);

                                    EpsonStateFragment.manbt.setChecked(false);
                                    MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                                }
                            } else if (strlist[1].equals("POINTLIST")) {

                                EpsonPontosFragment.ListPointsAdapter.clear();
                                for (int i = 2; i < strlist.length; i++) {
                                    EpsonPontosFragment.ListPointsAdapter.add(strlist[i]);
                                }

                                EpsonPontosFragment.ListPointsAdapter.notifyDataSetChanged();

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
