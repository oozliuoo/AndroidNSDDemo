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

/**
 * Created by zhexuanliu on 12/28/17.
 */

public class NSDUDPServer
{
    /**
     * Datagram socket of the UDP server
     */
    private DatagramSocket mDatagramSocket = null;

    /**
     * Main thread of the UDP server
     */
    private Thread mThread = null;

    /**
     * Stores port information of the datagram socket
     */
    private int mPort = -1;

    /**
     * The main thread handler to interact with main thread
     */
    private Handler mMainThreadHandler;

    /**
     * Store the client's address
     */
    private InetAddress mClientAddress;

    /**
     * Stores port of the client
     */
    private int mClientPort;

    /**
     * Track the instance context
     */
    private NSDUDPServer mNSDUDPServerContext;

    /**
     * Name of service this udp server serves
     */
    private String mServiceName;

    /**
     * Constructor
     */
    public NSDUDPServer(String serviceName, Handler mainThreadHandler){
        this.mNSDUDPServerContext = this;
        this.mMainThreadHandler = mainThreadHandler;
        this.mServiceName = serviceName;

        this.mThread = new Thread( new UdpServerThread());
        this.mThread.start();
    }

    /**
     * Quit the server
     */
    public void tearDown() {
        mThread.interrupt();
        mDatagramSocket.close();
    }

    class UdpServerThread implements Runnable{

        @Override
        public void run() {
            try {
                // grab an available prot and advertise it via Nsd.
                mDatagramSocket = new DatagramSocket();
                mPort = mDatagramSocket.getLocalPort();

                mMainThreadHandler.obtainMessage(Constants.MSG_SERVER_CREATED, mNSDUDPServerContext).sendToTarget();
                while (!Thread.currentThread().isInterrupted())
                {
                    // TODO: need proper size and handling here
                    byte[] buf = new byte[Constants.DATA_BUFFER_SIZE];
                    DatagramPacket dp = new DatagramPacket( buf, buf.length);

                    // listen to the port and wait for message
                    mDatagramSocket.receive(dp);

                    mClientAddress = dp.getAddress();
                    mClientPort = dp.getPort();

                    mMainThreadHandler.obtainMessage(Constants.MSG_CLIENT_RECEIVE, "Message from service `" + mServiceName + "`: " + Utils.convertBytesToString(dp.getData())).sendToTarget();
                }
            } catch (SocketException e) {
                e.printStackTrace();
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

            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, mClientAddress, mPort);
            mDatagramSocket.send(datagramPacket);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the server socket port
     * @return - server socket port
     */
    public int getLocalPort()
    {
        return this.mPort;
    }

    /**
     * Gets the service name
     * @return - service name
     */
    public String getServiceName()
    {
        return this.mServiceName;
    }
}
