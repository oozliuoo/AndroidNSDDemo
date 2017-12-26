package com.example.zhexuanliu.androidnsdservicedemo;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.R.attr.port;
import static android.content.ContentValues.TAG;

/**
 * Created by zhexuanliu on 12/26/17.
 */

public class NSDServer
{
    private ServerSocket mServerSocket = null;

    private Thread mThread = null;

    private int mPort = -1;

    public NSDServer() {
        mThread = new Thread(new ServerThread());
        mThread.start();
    }

    public void tearDown() {
        mThread.interrupt();
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
        }
    }

    class ServerThread implements Runnable {

        @Override
        public void run() {

            try {
                // Since discovery will happen via Nsd, we don't need to care which port is
                // used.  Just grab an available one  and advertise it via Nsd.
                mServerSocket = new ServerSocket(0);
                setLocalPort(mServerSocket.getLocalPort());

                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                    Socket socket = mServerSocket.accept();
                    Log.d(TAG, "Connected with" + socket.getInetAddress() + ":" + socket.getPort());
                    //if multi-devices join this server needs
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }

    private void setLocalPort(int port)
    {
        this.mPort = port;
    }

    public int getLocalPort()
    {
        return this.mPort;
    }

    public ServerSocket getSocket()
    {
        return this.mServerSocket;
    }
}
