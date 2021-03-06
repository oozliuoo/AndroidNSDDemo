package com.example.zhexuanliu.androidnsdservicedemo;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import com.example.zhexuanliu.androidnsdservicedemo.connection.NSDClient;
import com.example.zhexuanliu.androidnsdservicedemo.connection.NSDServer;
import com.example.zhexuanliu.androidnsdservicedemo.connection.NSDUDPClient;
import com.example.zhexuanliu.androidnsdservicedemo.connection.NSDUDPServer;
import com.example.zhexuanliu.androidnsdservicedemo.factories.NSDManagerFactory;
import com.example.zhexuanliu.androidnsdservicedemo.utils.ServiceListAdapter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class UDPNSDActivity extends AppCompatActivity
{
    /**
     * Edit Textview for serviceName
     */
    @BindView(R.id.service_edit_text)
    TextView mServiceEditTv;

    /**
     * Edit Textview for sendMessage
     */
    @BindView(R.id.send_message_tv)
    TextView mSendMessageEditTv;

    /**
     * Register button
     */
    @BindView(R.id.register_btn)
    Button mRegisterButton;

    /**
     * Discover button
     */
    @BindView(R.id.discover_btn)
    Button mDiscoverButton;

    /**
     * Connect button
     */
    @BindView(R.id.connect_btn)
    Button mConnectButton;

    /**
     * Send button
     */
    @BindView(R.id.send_btn)
    Button mSendButton;

    /**
     * List view for registered services
     */
    @BindView(R.id.register_list_view)
    ListView mRegisterListView;

    /**
     * List view for discovered services
     */
    @BindView(R.id.discover_list_view)
    ListView mDiscoverListView;

    /**
     * List view for connected service
     */
    @BindView(R.id.connected_list_view)
    ListView mConnectedListView;

    /**
     * List view for received messages
     */
    @BindView(R.id.receive_message_list)
    ListView mReceiveMessageListView;
    /**
     * Mapping between NSD Service name and NSDManager
     */
    private HashMap<String, NetworkServiceDiscoveryManager> mNSDManagerMap;

    /**
     * Mapping between NSD Service name and NSDUDPServer
     */
    private HashMap<String, NSDUDPServer> mNSDServerMap;

    /**
     * Mapping between NSD Service name and NSDUDPClient
     */
    private HashMap<String, NSDUDPClient> mNSDClientMap;

    /**
     * Adapter for registered service list
     */
    private ServiceListAdapter mRegisterServiceListAdapter;

    /**
     * Adapter for discovered service list
     */
    private ServiceListAdapter mDiscoveredServiceListAdapter;

    /**
     * Adapter for connected service list
     */
    private ServiceListAdapter mConnectedServiceListAdapter;

    /**
     * Adapter for received message list
     */
    private ServiceListAdapter mReceiveMessageListAdapter;

    /**
     * Background thread pass down
     */
    HandlerThread mBackgroundThread;

    /**
     * Background handler
     */
    Handler mBackgroundHandler;

    /**
     * Main thread handler passes down
     */
    Handler mMainThreadHandler;

    /**
     * Track the context
     */
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udpnsd);
        ButterKnife.bind(this);

        this.mContext = this;

        // initialize hashmap
        this.mNSDManagerMap = new HashMap<String, NetworkServiceDiscoveryManager>();
        this.mNSDServerMap = new HashMap<String, NSDUDPServer>();
        this.mNSDClientMap = new HashMap<String, NSDUDPClient>();

        this.initListAdapters();
        this.initThreads();
    }

    /**
     * Initialize all list adapters
     */
    private void initListAdapters()
    {
        // bind list adapter
        ArrayList<String> registeredList = new ArrayList<String>();
        mRegisterServiceListAdapter = new ServiceListAdapter(
                this,
                android.R.layout.simple_list_item_1,
                registeredList
        );
        mRegisterListView.setAdapter(mRegisterServiceListAdapter);

        ArrayList<String> discoveredList = new ArrayList<String>();
        mDiscoveredServiceListAdapter = new ServiceListAdapter(
                this,
                android.R.layout.simple_list_item_1,
                discoveredList
        );
        mDiscoverListView.setAdapter(mDiscoveredServiceListAdapter);

        ArrayList<String> connectedList = new ArrayList<String>();
        mConnectedServiceListAdapter = new ServiceListAdapter(
                this,
                android.R.layout.simple_list_item_1,
                connectedList
        );
        mConnectedListView.setAdapter(mConnectedServiceListAdapter);

        ArrayList<String> receiveMessageList = new ArrayList<String>();
        mReceiveMessageListAdapter = new ServiceListAdapter(
                this,
                android.R.layout.simple_list_item_1,
                receiveMessageList
        );
        mReceiveMessageListView.setAdapter(mReceiveMessageListAdapter);
    }

    @Override
    protected void onStop() {
        Log.d(Constants.LOG_TAG, "onStop");

        // clear NSDManager map
        Object[] valArr = mNSDManagerMap.values().toArray();
        for (int i = 0; i < valArr.length; i ++)
        {
            ((NetworkServiceDiscoveryManager)(valArr[i])).tearDown();
        }
        mNSDManagerMap.clear();

        // clear NSDUDPServer map
        valArr = mNSDServerMap.values().toArray();
        for (int i = 0; i < valArr.length; i ++)
        {
            ((NSDUDPServer)(valArr[i])).tearDown();
        }
        mNSDServerMap.clear();

        // clear NSDUDPClient map
        valArr = mNSDClientMap.values().toArray();
        for (int i = 0; i < valArr.length; i ++)
        {
            ((NSDUDPClient)(valArr[i])).tearDown();
        }
        mNSDClientMap.clear();

        // Clear lists
        mRegisterServiceListAdapter.clear();
        mDiscoveredServiceListAdapter.clear();
        mConnectedServiceListAdapter.clear();
        mReceiveMessageListAdapter.clear();
        super.onStop();
    }

    /**
     * Initialize threads for this activity
     */
    private void initThreads(){
        mBackgroundThread = new HandlerThread(Constants.BACKGROUND_HANDLER_THREAD);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Constants.MSG_SEND_MESSAGE:
                        sendMessage();

                        break;
                    default:
                        break;
                }
            }

            private void sendMessage()
            {
                String serviceName = mServiceEditTv.getText().toString();
                String messageToSend = mSendMessageEditTv.getText().toString();

                if (!validateServiceName(serviceName))
                {
                    return;
                }

                boolean isHost = false;
                boolean connected = false;

                for (int i = 0; i < mRegisterServiceListAdapter.getCount(); i ++)
                {
                    if (mRegisterServiceListAdapter.getItem(i).equals(serviceName))
                    {
                        isHost = true;
                    }
                }
                for (int i = 0; i < mConnectedServiceListAdapter.getCount(); i ++)
                {
                    if (mConnectedServiceListAdapter.getItem(i).equals(serviceName))
                    {
                        connected = true;
                    }
                }

                if (!isHost && !connected)
                {
                    Toast.makeText(mContext, Constants.SERVICE_NOT_CONNECTED_PROMPT, Toast.LENGTH_LONG).show();
                    return;
                }

                // if its host, then it should call sendMessage method in NSDUDPServer
                if (isHost)
                {
                    NSDUDPServer server = mNSDServerMap.get(serviceName);
                    server.sendData(messageToSend.getBytes());
                }

                if (connected)
                {
                    NSDUDPClient client = mNSDClientMap.get(serviceName);
                    client.sendData(messageToSend.getBytes());
                }
            }
        };

        mMainThreadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.MSG_UPDATE_REGISTERED_SERVICE:
                        String registeredServiceName = (String) msg.obj;

                        mRegisterServiceListAdapter.add(registeredServiceName);
                        break;
                    case Constants.MSG_DISCOVERED_SERVICE:
                        String discoveredServiceName = (String) msg.obj;

                        if (mDiscoveredServiceListAdapter.getPosition(discoveredServiceName) == -1)
                        {
                            mDiscoveredServiceListAdapter.add(discoveredServiceName);
                        }

                        break;
                    case Constants.MSG_CONNECTED_SERVICE:
                        NSDUDPClient client = (NSDUDPClient) msg.obj;

                        mNSDClientMap.put(client.getServiceName(), client);

                        mConnectedServiceListAdapter.add(client.getServiceName());

                        Toast.makeText(mContext, Constants.SERVICE_CONNECTED_PROMPT, Toast.LENGTH_LONG).show();
                        break;
                    case Constants.MSG_SERVER_CREATED:
                        NSDUDPServer server = (NSDUDPServer) msg.obj;
                        mNSDServerMap.put(server.getServiceName(), server);

                        NetworkServiceDiscoveryManager manager = NSDManagerFactory.create(mContext, mMainThreadHandler);
                        manager.registerService(server.getLocalPort(), server.getServiceName(), mNSDManagerMap);

                        break;
                    case Constants.MSG_CLIENT_RECEIVE:
                        String message = (String) msg.obj;

                        mReceiveMessageListAdapter.add(message);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * Click event handler for discover button
     * @param view - the discover button view
     */
    @OnClick(R.id.discover_btn)
    public void onDiscoverClick(View view)
    {
        String serviceName = mServiceEditTv.getText().toString();

        if (!this.validateServiceName(serviceName))
        {
            return;
        }

        NetworkServiceDiscoveryManager manager = NSDManagerFactory.create(this, mMainThreadHandler);

        // check if its trying to discover a service created by itself
        boolean sameMachineDiscover = false;

        for (int i = 0; i < this.mRegisterServiceListAdapter.getCount(); i ++)
        {
            if (this.mRegisterServiceListAdapter.getItem(i).equals(serviceName))
            {
                sameMachineDiscover = true;
            }
        }

        manager.discoverService(serviceName, sameMachineDiscover);

        // TODO: need to separate this via threading handler
        mNSDManagerMap.put(serviceName, manager);
    }

    /**
     * Click event handler for send button
     * @param view - the send button view
     */
    @OnClick(R.id.send_btn)
    public void onSendClick(View view)
    {
        mBackgroundHandler.obtainMessage(Constants.MSG_SEND_MESSAGE).sendToTarget();
    }

    /**
     * Click event handler connect button
     * @param view - the connect button view
     */
    @OnClick(R.id.connect_btn)
    public void onConnectClick(View view)
    {
        String serviceName = mServiceEditTv.getText().toString();

        if (!this.validateServiceName(serviceName))
        {
            return;
        }

        if (mDiscoveredServiceListAdapter.getPosition(serviceName) == -1)
        {
            Toast.makeText(this, Constants.SERVICE_NOT_DISCOVERED_PROMPT, Toast.LENGTH_SHORT).show();
            Log.e(Constants.LOG_TAG, "Service not discovered yet: " + serviceName);
            return;
        }

        NetworkServiceDiscoveryManager nsdManager = this.mNSDManagerMap.get(serviceName);

        if (nsdManager == null || nsdManager.getResolveListener() == null)
        {
            Toast.makeText(this, Constants.SERVICE_NOT_DISCOVERED_PROMPT, Toast.LENGTH_SHORT).show();
            Log.e(Constants.LOG_TAG, "nsdManager not found when connecting");
            return;
        }

        int port = nsdManager.getResolveListener().getServiceInfo().getPort();
        InetAddress host = nsdManager.getResolveListener().getServiceInfo().getHost();

        // connect to the found service
        NSDUDPClient client = new NSDUDPClient(host, port, mMainThreadHandler, serviceName);
    }

    /**
     * Click event handler for register button
     * @param view - the register button view
     */
    @OnClick(R.id.register_btn)
    public void onRegisterClick(View view)
    {
        String serviceName = mServiceEditTv.getText().toString();

        if (!this.validateServiceName(serviceName))
        {
            return;
        }

        if (mNSDManagerMap.containsKey(serviceName))
        {
            Toast.makeText(this, Constants.SERVICE_NAME_REGISTERED_PROMPT, Toast.LENGTH_SHORT).show();
            return;
        }

        NSDUDPServer server = new NSDUDPServer(serviceName, mMainThreadHandler);
    }

    @OnTouch(R.id.receive_message_list)
    public boolean onMessageListTouch(View view, MotionEvent event)
    {
        view.getParent().requestDisallowInterceptTouchEvent(true);

        return false;
    }

    /**
     * Validates the given serviceName (check if it is empty)
     * @param serviceName - serviceName to be validated
     *
     * @return true if it is valid (i.e. not empty)
     */
    private boolean validateServiceName(String serviceName)
    {
        if (serviceName.isEmpty())
        {
            Toast.makeText(this, Constants.SERVICE_NAME_EMPTY_PROMPT, Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
