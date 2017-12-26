package com.example.zhexuanliu.androidnsdservicedemo.listeners;

import android.app.Activity;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.zhexuanliu.androidnsdservicedemo.NetworkServiceDiscoveryManager;
import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import android.os.Handler;

import java.util.HashMap;

/**
 * Created by zhexuanliu on 12/25/17.
 */

/**
 * Class that implements the NsdManager.RegistrationListener interface for this demo
 */
public class NSDRegistrationListener implements NsdManager.RegistrationListener
{
    /**
     * Store the suffix
     */
    private int suffix;

    /**
     * manager to be recorded down/removed when successful/failed
     */
    private HashMap<String, NetworkServiceDiscoveryManager> mMap;

    /**
     * suffix used to distinguish different listeners
     */
    private int mSuffix;

    /**
     * Map to track all managers
     */
    private NetworkServiceDiscoveryManager mManager;

    /**
     * Handler for main activity
     */
    private Handler mHandler;

    /**
     * Constructor
     * @param map - map to track all managers
     * @param manager - manager to be recorded down/removed when successful/failed
     * @param suffix - suffix of this listener
     * @param handler - the main thread handler
     */
    public NSDRegistrationListener(
        HashMap<String, NetworkServiceDiscoveryManager> map,
        NetworkServiceDiscoveryManager manager,
        int suffix,
        Handler handler
    )
    {
        this.mMap = map;
        this.mManager = manager;
        this.mSuffix = suffix;
        this.mHandler = handler;
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo NsdServiceInfo)
    {
        // mark down the manager
        this.mMap.put(NsdServiceInfo.getServiceName(), this.mManager);

        Toast.makeText(this.mManager.mContext, Constants.REGISTER_SUCCESSFUL, Toast.LENGTH_SHORT).show();
        this.mHandler.obtainMessage(Constants.MSG_UPDATE_REGISTERED_SERVICE, NsdServiceInfo.getServiceName()).sendToTarget();
        Log.d(Constants.LOG_TAG, "Service registered: " + NsdServiceInfo.getServiceName());
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo arg0, int arg1)
    {
        Log.d(Constants.LOG_TAG, "Service registration failed: " + arg1);
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo arg0)
    {
        this.mMap.remove(arg0.getServiceName());

        Log.d(Constants.LOG_TAG, "Service unregistered: " + arg0.getServiceName());
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode)
    {
        Log.d(Constants.LOG_TAG, "Service unregistration failed: " + errorCode);
    }
}
