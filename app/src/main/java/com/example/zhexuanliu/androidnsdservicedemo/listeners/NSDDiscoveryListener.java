package com.example.zhexuanliu.androidnsdservicedemo.listeners;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;

import static android.content.ContentValues.TAG;
import static com.example.zhexuanliu.androidnsdservicedemo.commons.Constants.SERVICE_TYPE;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by zhexuanliu on 12/26/17.
 */

public class NSDDiscoveryListener implements NsdManager.DiscoveryListener
{
    /**
     * Service name to be discovered
     */
    private String mServiceName;

    /**
     * The corresponding NsdManager
     */
    private NsdManager mManager;

    /**
     * ResolveListener passed into disocveryListener
     */
    private NSDResolveListener mResolveListener;

    /**
     * ServiceInfo passed in eternally, will be set here
     */
    private NsdServiceInfo mServiceInfo;

    /**
     * Handler to interact with MainActivity
     */
    private Handler mHandler;

    /**
     * Indicates if this discovery is requested by the same machine registering
     */
    private boolean mSameMachineDiscover;

    /**
     * Constructor
     * @param serviceName
     * @param manager
     * @param resolveListener
     * @param serviceInfo
     * @param handler
     * @param sameMachineDiscover - indicates if this discovery is requested by the same machine registering
     */
    public NSDDiscoveryListener(
            String serviceName,
            NsdManager manager,
            NSDResolveListener resolveListener,
            NsdServiceInfo serviceInfo,
            Handler handler,
            boolean sameMachineDiscover
    )
    {
        this.mServiceName = serviceName;
        this.mManager = manager;
        this.mResolveListener = resolveListener;
        this.mServiceInfo = serviceInfo;
        this.mHandler = handler;
        this.mSameMachineDiscover = sameMachineDiscover;
    }

    @Override
    public void onDiscoveryStarted(String regType)
    {
        Log.d(Constants.LOG_TAG, "Service discovery started");
    }

    @Override
    public void onServiceFound(NsdServiceInfo service)
    {
        Log.d(Constants.LOG_TAG, "Service discovery success" + service);
        if (!service.getServiceType().equals(SERVICE_TYPE))
        {
            Log.d(Constants.LOG_TAG, "Unknown Service Type: " + service.getServiceType());
        }
        else if (service.getServiceName().equals(mServiceName) && this.mSameMachineDiscover)
        {
            Log.d(Constants.LOG_TAG, "Same machine: " + mServiceName);
        }
        else if (service.getServiceName().equals(mServiceName) && !this.mSameMachineDiscover)
        {
            this.mHandler.obtainMessage(Constants.MSG_DISCOVERED_SERVICE, mServiceName).sendToTarget();

            mManager.resolveService(service, mResolveListener);
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo service)
    {
        Log.e(Constants.LOG_TAG, "service lost" + service);
        if (mServiceInfo == service)
        {
            mServiceInfo = null;
        }
    }

    @Override
    public void onDiscoveryStopped(String serviceType)
    {
        Log.i(Constants.LOG_TAG, "Discovery stopped: " + serviceType);
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode)
    {
        Log.e(Constants.LOG_TAG, "Discovery failed: Error code:" + errorCode);
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode)
    {
        Log.e(Constants.LOG_TAG, "Discovery failed: Error code:" + errorCode);
    }
}
