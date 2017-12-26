package com.example.zhexuanliu.androidnsdservicedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import com.example.zhexuanliu.androidnsdservicedemo.factories.NSDManagerFactory;
import com.example.zhexuanliu.androidnsdservicedemo.utils.ServiceListAdapter;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    /**
     * Edit Textview for serviceName
     */
    @BindView(R.id.service_edit_text)
    TextView mServiceEditTv;

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
     * Mapping between NSD Service name and NSDManager
     */
    private HashMap<String, NetworkServiceDiscoveryManager> mNSDManagerMap;

    /**
     * Adapter for registered service list
     */
    private ServiceListAdapter mRegisterServiceListAdapter;

    /**
     * Adapter for discovered service list
     */
    private ServiceListAdapter mDiscoveredServiceListAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.mContext = this;

        // initialize hashmap
        this.mNSDManagerMap = new HashMap<String, NetworkServiceDiscoveryManager>();

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
    }

    @Override
    protected void onStop() {
        Log.d(Constants.LOG_TAG, "onStop");

        Object[] valArr = mNSDManagerMap.values().toArray();
        for (int i = 0; i < valArr.length; i ++)
        {
            ((NetworkServiceDiscoveryManager)(valArr[i])).tearDown();
        }

        mNSDManagerMap.clear();
        mRegisterServiceListAdapter.clear();
        mDiscoveredServiceListAdapter.clear();
        super.onStop();
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
        NSDClient client = new NSDClient(host, port, mMainThreadHandler);
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

        NSDServer server = new NSDServer();

        // wait till server is up
        while (server.getLocalPort() == -1);

        NetworkServiceDiscoveryManager manager = NSDManagerFactory.create(this, this.mMainThreadHandler);
        manager.registerService(server.getLocalPort(), serviceName, this.mNSDManagerMap);
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
                    default:
                        break;
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
                        Toast.makeText(mContext, Constants.SERVICE_CONNECTED_PROMPT, Toast.LENGTH_LONG).show();
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
            if (this.mRegisterServiceListAdapter.getItem(i) == serviceName)
            {
                sameMachineDiscover = true;
            }
        }

        manager.discoverService(serviceName, sameMachineDiscover);

        // TODO: need to separate this via threading handler
        mNSDManagerMap.put(serviceName, manager);
    }
}
