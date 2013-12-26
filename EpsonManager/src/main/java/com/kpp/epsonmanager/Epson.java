package com.kpp.epsonmanager;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

public class Epson {
    public int icon;
    private String title;

    private String Hostname2;
    private String Hostname;
    private boolean Connected=false;
    private TCPClient mTcpClient;
    private OnEpsonStatusChanged mOnEpsonStatusChanged = null;

    private boolean ManMode=false;

    private boolean RobotConneted=false;


    public OnEpsonStatusChanged getmOnEpsonStatusChanged() {
        return mOnEpsonStatusChanged;
    }

    public void setmOnEpsonStatusChanged(OnEpsonStatusChanged mOnEpsonStatusChanged) {
        this.mOnEpsonStatusChanged = mOnEpsonStatusChanged;
    }

    public TCPClient getTcpClient() {
        return mTcpClient;
    }

    public boolean isManMode() {
        return ManMode;
    }

    private void setManMode(boolean manMode) {
        ManMode = manMode;
    }

    public boolean isRobotConneted() {
        return RobotConneted;
    }

    private void setRobotConneted(boolean robotConneted) {
        RobotConneted = robotConneted;
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

    public Epson(){
        super();
    }

    public Epson(int icon, String title,String hostname) {
        super();
        this.icon = icon;
        this.setTitle(title);
        this.Hostname=hostname;
        this.Hostname2="";
    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    public boolean isConnected() {
        return Connected;
    }

    public void setConnected() {
        new connectTask().execute("");
    }

    public String getHostname() {
        return Hostname;
    }

    public void setHostname(String hostname) {
        Hostname = hostname;
    }

    private Epson self=this;

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
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
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
                    Connected=true;
                    if (getmOnEpsonStatusChanged() !=null){
                        getmOnEpsonStatusChanged().EpsonStatusChanged(new OnEpsonStatusChangedEventArgs(self,"Client Connected"));

                    }
                }
            });
            mTcpClient.run();
            mTcpClient.setClientDisconnectedListner(new TCPClient.OnClientDisconnected() {
                @Override
                public void clientDisconnected() {
                    self.Connected=false;
                    if (getmOnEpsonStatusChanged() !=null){
                        getmOnEpsonStatusChanged().EpsonStatusChanged(new OnEpsonStatusChangedEventArgs(self,"Client Disconnected"));

                    }
                }
            });
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
                            setRobotConneted(true);
                        }
                        else if(strlist[1].equals("DISCONNECTED")){

                            EpsonStateFragment.txtRbState.setText("Robot Desligado");
                            EpsonStateFragment.manbt.setEnabled(false);
                            if (MainActivity.mEpsonViewPager.getCurrentItem()>1){
                                MainActivity.mEpsonViewPager.setCurrentItem( MainActivity.mEpsonViewPager.getCurrentItem()-1);
                            }
                            MainActivity.mEpsonPagerAdapter.RemoveFragment(2);
                            setRobotConneted(false);
                        }
                        else if(strlist[1].equals("RB")){
                            EpsonStateFragment.txtRbMsg.setText(strlist[2]);
                        }
                    }

                    else if (strlist[0].equals("SET")){
                       if (strlist[1].equals("MANMODE")){
                           EpsonStateFragment.progresswaitman.setVisibility(View.GONE);
                           EpsonStateFragment.progresswaitman.setEnabled(false);
                           if (strlist[2].equals("OK")){
                               EpsonStateFragment.manbt.setText("Ligado");
                               MainActivity.mEpsonPagerAdapter.AddFragment(new EpsonPontosFragment());

                               MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                               self.setManMode(true);

                           }
                           else{
                               self.setManMode(false);
                               MainActivity.mEpsonPagerAdapter.notifyDataSetChanged();
                           }
                       }
                       else if(strlist[1].equals("POINTLIST")){

                           EpsonPontosFragment.ListPointsAdapter.clear();
                           for(int i=2;i<strlist.length;i++){
                               EpsonPontosFragment.ListPointsAdapter.add(strlist[i]);
                           }

                           EpsonPontosFragment.ListPointsAdapter.notifyDataSetChanged();

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
