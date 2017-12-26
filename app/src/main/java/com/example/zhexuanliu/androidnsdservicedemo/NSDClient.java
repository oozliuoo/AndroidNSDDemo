package com.example.zhexuanliu.androidnsdservicedemo;

import android.os.Handler;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by zhexuanliu on 12/27/17.
 */

public class NSDClient
{

    /**
     * Host address to be connected to
     */
    private InetAddress mAddress;

    /**
     * Port to be connected to
     */
    private int PORT;

    /**
     * the socket object for client
     */
    private Socket mSocket;

    /**
     * Init socket thread
     */
    private Thread mInitSocketThread;

    /**
     * Main thread handler
     */
    private Handler mMainThreadHandler;

    public NSDClient(InetAddress address, int port, Handler mainThreadHandler)
    {

        this.mAddress = address;
        this.PORT = port;
        this.mMainThreadHandler = mainThreadHandler;

        mInitSocketThread = new Thread(new InitSocketThread());
        mInitSocketThread.start();
    }

    class InitSocketThread implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                if (mSocket == null)
                {
                    mSocket = new Socket(mAddress, PORT);
                    Log.d(Constants.LOG_TAG, "Client-side socket initialized.");
                    mMainThreadHandler.obtainMessage(Constants.MSG_CONNECTED_SERVICE).sendToTarget();
                } else
                {
                    Log.d(Constants.LOG_TAG, "Socket already initialized. skipping!");
                }
            }
            catch (UnknownHostException e)
            {
                Log.d(Constants.LOG_TAG, "Initializing socket failed, UHE", e);
            }
            catch (IOException e)
            {
                Log.d(Constants.LOG_TAG, "Initializing socket failed, IOE.", e);
            }

        }
    }
}