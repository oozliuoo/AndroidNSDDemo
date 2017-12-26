package com.example.zhexuanliu.androidnsdservicedemo.listeners;

/**
 * Created by zhexuanliu on 12/26/17.
 */


import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;

/**
 * Class that implements the NsdManager.ResolveListener interface for this demo
 */
public class NSDResolveListener implements NsdManager.ResolveListener
{

    /**
     * Store the serviceName
     */
    private String mServiceName;

    /**
     * Service info passed in externally, will be set when resolved
     */
    private NsdServiceInfo mServiceInfo;

    /**
     * Indicates whether this is a discovery request from the same machine
     */
    private boolean mSameMachineDiscover;

    /**
     * Constructor
     * @param serviceName - serviceName binds with the resolveListener
     * @param sameMachineDiscover - Indicates whether this is a discovery request from the same machine
     */
    public NSDResolveListener(
            String serviceName,
            NsdServiceInfo serviceInfo,
            boolean sameMachineDiscover
    )
    {
        this.mServiceName = serviceName;
        this.mServiceInfo = serviceInfo;
        this.mSameMachineDiscover = sameMachineDiscover;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(Constants.LOG_TAG, "Resolve failed" + errorCode);
    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        Log.e(Constants.LOG_TAG, "Resolve Succeeded. " + serviceInfo);

        if (serviceInfo.getServiceName().equals(this.mServiceName) && this.mSameMachineDiscover) {
            Log.d(Constants.LOG_TAG, "Same IP.");
            return;
        }
        else if (serviceInfo.getServiceName().equals(this.mServiceName) && !this.mSameMachineDiscover)
        {
            Log.d(Constants.LOG_TAG, "Found service");
            this.mServiceInfo = serviceInfo;
        }
    }

    /**
     * returns the service name
     * @return - service name of this resolveListener
     */
    public String getServiceName()
    {
        return this.mServiceName;
    }

    /**
     * returns service info
     * @return - service info of this resolveListener
     */
    public NsdServiceInfo getServiceInfo()
    {
        return this.mServiceInfo;
    }
}
