package com.example.zhexuanliu.androidnsdservicedemo.connection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.R.attr.port;
import static android.content.ContentValues.TAG;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.MSG_CLIENT_RECEIVE;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.YOU_ARE_CONNECTED;

/**
 * Created by zhexuanliu on 12/26/17.
 */

public class NSDServer
{
    /**
     * Stores server socket
     */
    private ServerSocket mServerSocket = null;

    /**
     * Thread for creating server socket
     */
    private Thread mThread = null;

    /**
     * Thread handling message receive
     */
    private Thread mReceiveThread;

    /**
     * Port of the server socket
     */
    private int mPort = -1;

    /**
     * The main thread handler
     */
    private Handler mMainThreadHandler;

    /**
     * Service name of this server
     */
    private String mServiceName;

    /**
     * Track the instance context
     */
    private NSDServer mNSDServerContext;

    /**
     * Socket after accepting
     */
    private Socket mSocket;

    private OutputStream mOutputStream;

    /**
     * Constructor
     */
    public NSDServer(String serviceName, Handler handler) {
        this.mMainThreadHandler = handler;
        this.mServiceName = serviceName;
        this.mNSDServerContext = this;

        mThread = new Thread(new ServerThread());
        mThread.start();
    }

    /**
     * Method to close the server socket
     */
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
                mServerSocket = new ServerSocket(0);
                setLocalPort(mServerSocket.getLocalPort());

                mMainThreadHandler.obtainMessage(Constants.MSG_SERVER_CREATED, mNSDServerContext).sendToTarget();
                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                    mSocket = mServerSocket.accept();
                    Log.d(TAG, "Connected with" + mSocket.getInetAddress() + ":" + mSocket.getPort());

                    mOutputStream = mSocket.getOutputStream();
                    sendMessage("Connected");

                    // start receiving
                    mReceiveThread = new Thread(new ReceiveThread());
                    mReceiveThread.start();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Class implementing the receive thread
     */
    class ReceiveThread implements Runnable {

        @Override
        public void run() {

            BufferedReader input;
            try {
                input = new BufferedReader(
                        new InputStreamReader(mSocket.getInputStream())
                );
                while (!Thread.currentThread().isInterrupted()) {

                    String messageStr = null;
                    messageStr = input.readLine();
                    if (messageStr != null) {
                        Log.d(Constants.LOG_TAG, "Read from the stream: " + messageStr);
                        mMainThreadHandler.obtainMessage(MSG_CLIENT_RECEIVE, "Message from service `" + mServiceName + "`: " + messageStr).sendToTarget();
                    } else {
                        Log.d(Constants.LOG_TAG, "Client waiting for the message");
                    }
                }
                // mSocket.shutdownInput();
                // input.close();

            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Server loop error: ", e);
            }
        }
    }

    /**
     * Server send message via socket
     * @param message - message sent to target via socket
     */
    public void sendMessage(String message)
    {
        try
        {
            PrintStream printStream = new PrintStream(mOutputStream);
            printStream.print(message + "\n");
            printStream.flush();

            /*if (!mSocket.isOutputShutdown())
            {
                mSocket.shutdownOutput();
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    public String getServiceName() { return this.mServiceName; }
}
