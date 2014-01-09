package com.kpp.epsonmanager;

import android.util.Log;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class TCPClient {

    private String serverMessage;
    private String ServerIP;

            //= "PC413-LMONTAGEM26.keyeu.keyplastics.local"; //your computer IP address

    private int ServerPORT; //= 9620;

    private OnMessageReceived mMessageListener = null;
    private OnClientConnected mClientconnectedListner = null;
    private OnClientDisconnected mClientDisconnectedListner= null;

    private OnConnectionError mOnConnectionError=null;

    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(String IP,int Port,OnMessageReceived listener,OnClientConnected ClientconnectedListner) {
        ServerIP=IP;
        ServerPORT=Port;
        mMessageListener = listener;
        mClientconnectedListner=ClientconnectedListner;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Socket socket=null;
    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(ServerIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            //socket = new Socket(serverAddr, ServerPORT);
            socket=new Socket();
            socket.connect(new InetSocketAddress(ServerIP,ServerPORT),2000);

            if (mClientconnectedListner!= null) {
                //call the method messageReceived from MyActivity class
                mClientconnectedListner.clientConnected();
            }
            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();
                    if (serverMessage==null) mRun=false;

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }


            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                if (getClientDisconnectedListner() != null) {
                    //call the method messageReceived from MyActivity class
                    mClientDisconnectedListner.clientDisconnected();
                }
                socket.close();
            }

        }
        catch (ConnectException e){
            if (mOnConnectionError != null) {
                //call the method messageReceived from MyActivity class
                mOnConnectionError.ConnectionError(e.toString());
            }
        }
        catch (SocketTimeoutException e){
            if (mOnConnectionError != null) {
                //call the method messageReceived from MyActivity class
                mOnConnectionError.ConnectionError(e.toString());
            }
        }
        catch (Exception e) {
            if (mOnConnectionError != null) {
                //call the method messageReceived from MyActivity class
                mOnConnectionError.ConnectionError(e.toString());
            }
            Log.e("TCP", "C: Error", e);

        }

    }

    public OnClientDisconnected getClientDisconnectedListner() {
        return mClientDisconnectedListner;
    }

    public void setClientDisconnectedListner(OnClientDisconnected clientDisconnectedListner) {
        this.mClientDisconnectedListner = clientDisconnectedListner;
    }

    public OnConnectionError getmOnConnectionError() {
        return mOnConnectionError;
    }

    public void setmOnConnectionError(OnConnectionError mOnConnectionError) {
        this.mOnConnectionError = mOnConnectionError;
    }


    public interface OnMessageReceived {
        public void messageReceived(String message);
    }


    public interface OnClientDisconnected {
        public void clientDisconnected();
    }

    public interface OnConnectionError {
        public void ConnectionError(String ErrorMessage);
    }

    public interface OnClientConnected {
        public void clientConnected();
    }
}