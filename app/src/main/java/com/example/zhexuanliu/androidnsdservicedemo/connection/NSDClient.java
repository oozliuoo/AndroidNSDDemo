package com.example.zhexuanliu.androidnsdservicedemo.connection;

import android.os.Handler;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.R.id.message;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.MSG_CLIENT_RECEIVE;

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
     * Thread handling message receive
     */
    private Thread mReceiveThread;

    /**
     * Service name this client is serving to
     */
    private String mServiceName;

    /**
     * Track the instance context
     */
    private NSDClient mContext;

    /**
     * Main thread handler
     */
    private Handler mMainThreadHandler;

    private InputStream mInputStream;

    /**
     * Constructor
     * @param address - address to be connected to
     * @param port - port will be used to connect
     * @param mainThreadHandler - main thread handler (for communication and operations)
     * @param serviceName - service name this client is serving to
     */
    public NSDClient(InetAddress address, int port, Handler mainThreadHandler, String serviceName)
    {

        this.mAddress = address;
        this.PORT = port;
        this.mMainThreadHandler = mainThreadHandler;
        this.mServiceName = serviceName;
        this.mContext = this;

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
                    mInputStream = mSocket.getInputStream();
                    Log.d(Constants.LOG_TAG, "Client-side socket initialized.");
                    mMainThreadHandler.obtainMessage(Constants.MSG_CONNECTED_SERVICE, mContext).sendToTarget();
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "Socket already initialized. skipping!");
                }

                mReceiveThread = new Thread(new ReceiveThread());
                mReceiveThread.start();
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

    /**
     * Class implementing the receive thread
     */
    class ReceiveThread implements Runnable {

        @Override
        public void run() {

            BufferedReader input;
            try {
                input = new BufferedReader(
                    new InputStreamReader(mInputStream)
                );
                while (!Thread.currentThread().isInterrupted()) {

                    String messageStr = null;
                    messageStr = input.readLine();
                    if (messageStr != null) {
                        Log.d(Constants.LOG_TAG, "Read from the stream: " + messageStr);
                        mMainThreadHandler.obtainMessage(MSG_CLIENT_RECEIVE, "Message from service `" + mServiceName + "`: " + messageStr).sendToTarget();
                    } else {
                        Log.d(Constants.LOG_TAG, "Awaiting for next message");
                    }
                }
                /*if (!mSocket.isInputShutdown())
                {
                    mSocket.shutdownInput();
                }*/

            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Server loop error: ", e);
            }
        }
    }

    /**
     * Tear down the client
     */
    public void tearDown() {
        /*try {
            mSocket.close();

        } catch (IOException ioe) {
            Log.e(Constants.LOG_TAG, "Error when closing server socket.");
        }*/
    }

    /**
     * Server send message via socket
     * @param message - message sent to target via socket
     */
    public void sendMessage(String message)
    {
        OutputStream outputStream;

        try
        {
            outputStream = mSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(message + "\n");
            printStream.flush();
            // mSocket.shutdownOutput();
            // printStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();;
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
}