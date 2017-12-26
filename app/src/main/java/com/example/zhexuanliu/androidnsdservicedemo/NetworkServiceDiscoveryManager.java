package com.example.zhexuanliu.androidnsdservicedemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import com.example.zhexuanliu.androidnsdservicedemo.factories.DiscoveryListenerFactory;
import com.example.zhexuanliu.androidnsdservicedemo.factories.RegistrationListenerFactory;
import com.example.zhexuanliu.androidnsdservicedemo.factories.ResolveListenerFactory;
import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDDiscoveryListener;
import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDRegistrationListener;
import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDResolveListener;

import java.util.HashMap;

import static android.R.attr.port;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.SERVICE_TYPE;

/**
 * Created by zhexuanliu on 12/23/17.
 */

public class NetworkServiceDiscoveryManager {

    /**
     * The application context
     */
    public Context mContext;

    /**
     * The NSD manager object
     */
    private NsdManager mNSDManager;

    /**
     * ResolveListener object listening to the NSD manager
     */
    private NSDResolveListener mResolveListener;

    /**
     * DiscoveryListener object listening to the NSD manager
     */
    private NSDDiscoveryListener mDiscoveryListener;

    /**
     * RegistrationListener object listening to the NSD manager
     */
    private NSDRegistrationListener mRegistrationListener;

    /**
     * Stores the service info in NSD
     */
    private NsdServiceInfo mServiceInfo;

    /**
     * Handler to update Main Activity
     */
    private Handler mHandler;

    /**
     * Constructor of the NetworkServiceDiscoveryManager class
     * @param context - application context passed in externally
     * @param handler - the main thread handler
     */
    public NetworkServiceDiscoveryManager(Context context, Handler handler)
    {
        this.mContext = context;
        this.mNSDManager = (NsdManager) this.mContext.getSystemService(Context.NSD_SERVICE);
        this.mHandler = handler;
    }

    /**
     * Initialize the discoveryListener by calling related factory
     *
     * @param serviceName - name of service to be discovered
     * @param sameMachineDiscover - indicates if this discovery is requested by the same machine registering
     */
    public void initializeDiscoveryListener(String serviceName, boolean sameMachineDiscover)
    {
        if (mResolveListener == null)
        {
            Log.e(Constants.LOG_TAG, "ResolveListener not initialized before initializing discoveryListener");
        }
        this.mDiscoveryListener = DiscoveryListenerFactory.createDiscoveryListener(
                serviceName,
                this.mNSDManager,
                this.mResolveListener,
                this.mServiceInfo,
                this.mHandler,
                sameMachineDiscover
        );
    }

    /**
     * Initialize the resolveListener by calling related factory
     *
     * @param serviceName - name of service to be resolved
     * @param sameMachineDiscover - Indicates whether this is a discovery request from the same machine
     */
    public void initializeResolveListener(String serviceName, boolean sameMachineDiscover)
    {
        this.mResolveListener = ResolveListenerFactory.createResolveListener(serviceName, this.mServiceInfo, sameMachineDiscover);
    }

    /**
     * Initialize the registrationListener by calling related factory
     *
     * @param map - map to track all managers
     */
    public void initializeRegistrationListener(
        final HashMap<String, NetworkServiceDiscoveryManager> map
    )
    {
        this.mRegistrationListener = RegistrationListenerFactory.createRegistrationListener(map, this, this.mHandler);
    }

    /**
     * Register a service
     *
     * @param port - port binds to the service
     * @param serviceName - name of the service
     * @param map - map to track all managers
     *
     */
    public void registerService(
            int port,
            String serviceName,
            HashMap<String, NetworkServiceDiscoveryManager> map
    )
    {
        // first clean up existing registeration
        this.tearDown();

        // init registration listener
        this.initializeRegistrationListener(map);

        mNSDManager.registerService(
                createServiceInfo(port, serviceName),
                NsdManager.PROTOCOL_DNS_SD,
                this.mRegistrationListener
        );
    }

    /**
     * Creates a new ServiceInfo object, and sets corresponding parameters
     *
     * @param port - port of serviceInfo
     * @param serviceName - serviceName in serviceInfo
     *
     * @return - the created serviceInfo
     */
    private NsdServiceInfo createServiceInfo(int port, String serviceName)
    {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceType(Constants.SERVICE_TYPE);
        serviceInfo.setServiceName(serviceName);

        return serviceInfo;
    }

    /**
     * Method to discover a service
     *
     * @param serviceName - name of service to be discovered
     * @param sameMachineDiscover - indicates if this discovery is requested by the same machine registering
     *
     */
    public void discoverService(String serviceName, boolean sameMachineDiscover)
    {
        // Cancel any existing discovery request
        stopDiscovery();

        this.initializeResolveListener(serviceName, sameMachineDiscover);
        this.initializeDiscoveryListener(serviceName, sameMachineDiscover);
        this.mNSDManager.discoverServices(
                Constants.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, this.mDiscoveryListener);
    }

    /**
     * Stop service discovery
     */
    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                this.mNSDManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            this.mDiscoveryListener = null;
        }
    }

    /**
     * Stop any ongoing services
     */
    public void tearDown() {
        if (mRegistrationListener != null) {
            try {
                mNSDManager.unregisterService(mRegistrationListener);
            } finally {
                mRegistrationListener = null;
            }
        }
        if (mDiscoveryListener != null){
            try{
                mNSDManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
                mDiscoveryListener = null;
            }
        }
    }

    /**
     * returns the resolveListener
     * @return the resolveListener
     */
    public NSDResolveListener getResolveListener()
    {
        return this.mResolveListener;
    }
}
