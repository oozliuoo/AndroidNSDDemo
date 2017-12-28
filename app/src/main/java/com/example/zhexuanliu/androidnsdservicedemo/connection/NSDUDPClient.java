package com.example.zhexuanliu.androidnsdservicedemo.connection;

import android.os.Handler;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import com.example.zhexuanliu.androidnsdservicedemo.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static android.content.ContentValues.TAG;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.MSG_CLIENT_RECEIVE;

/**
 * Created by zhexuanliu on 12/28/17.
 */

public class NSDUDPClient
{
    /**
     * Stores the server address
     */
    private InetAddress mServerAddress;

    /**
     * Stores the server port
     */
    private int mServerPort;

    /**
     * Thread to initialize datagram socket
     */
    private Thread mInitSocketThread;

    /**
     * Thread to receive datapacket
     */
    private Thread mReceiveThread;

    /**
     * Datagram socket for server
     */
    private DatagramSocket mDatagramSocket;

    /**
     * Handler to interact with main thread
     */
    private Handler mMainThreadHandler;

    /**
     * Stores the service name this client binds to
     */
    private String mServiceName;

    /**
     * Stores the client context
     */
    private NSDUDPClient mContext;

    /**
     * Constructor of UDPNSDClient
     * @param address - address to be connected to
     * @param port - port to be connected to
     * @param mainThreadHandler - handler to interact with the main thread
     * @param serviceName - service name this client binds to
     */
    public NSDUDPClient(InetAddress address, int port, Handler mainThreadHandler, String serviceName){
        this.mServerAddress = address;
        this.mServerPort = port;
        this.mMainThreadHandler = mainThreadHandler;
        this.mContext = this;
        this.mServiceName = serviceName;

        //to set socket in another thread instead of mainThread.
        mInitSocketThread = new Thread(new InitSocketThread());
        mInitSocketThread.start();
    }

    class InitSocketThread implements Runnable{

        @Override
        public void run() {
            try {
                if ((mDatagramSocket) == null){
                    mDatagramSocket = new DatagramSocket(mServerPort);

                    sendData(("Hey, I am connecting via `" + mServiceName + "`").getBytes());
                    mMainThreadHandler.obtainMessage(Constants.MSG_CONNECTED_SERVICE, mContext).sendToTarget();
                } else {
                    Log.d(TAG, "DatagramSocket already initialized. skipping!");
                }

                mReceiveThread = new Thread(new ReceivingThread());
                mReceiveThread.start();

            } catch (SocketException e) {
                e.printStackTrace();
            }

        }
    }

    class ReceivingThread implements Runnable{

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted())
                {
                    byte[] buf = new byte[Constants.DATA_BUFFER_SIZE];
                    DatagramPacket dp = new DatagramPacket(buf, buf.length);

                    mDatagramSocket.receive(dp);

                    byte[] data = dp.getData();

                    if (data != null)
                    {
                        mMainThreadHandler.obtainMessage(MSG_CLIENT_RECEIVE, "Message from service `" + mServiceName + "`: " + Utils.convertBytesToString(data)).sendToTarget();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends data over the socket
     * @param data - data to be sent
     */
    public void sendData(byte[] data){
        Log.d(TAG, "client starts to send data");
        try {
            if (mDatagramSocket == null){
                Log.d(Constants.LOG_TAG, "datagram socket not initialized yet");
                return;
            }

            if (mDatagramSocket.isClosed())
            {
                Log.d(Constants.LOG_TAG, "datagram socket closed");
                return;
            }

            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, mServerAddress, mServerPort);
            mDatagramSocket.send(datagramPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the service name
     * @return - service name
     */
    public String getServiceName()
    {
        return this.mServiceName;
    }

    /**
     * Quite client socket
     */
    public void tearDown(){
        mInitSocketThread.interrupt();
        mReceiveThread.interrupt();
        mDatagramSocket.close();
    }
}
