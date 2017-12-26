package com.example.zhexuanliu.androidnsdservicedemo.factories;

import android.app.Activity;
import android.net.Network;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.sip.SipAudioCall;
import android.util.Log;
import android.widget.Toast;

import com.example.zhexuanliu.androidnsdservicedemo.NetworkServiceDiscoveryManager;
import com.example.zhexuanliu.androidnsdservicedemo.commons.Constants;
import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDRegistrationListener;

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import android.os.Handler;

/**
 * Factory class responsible to create a RegistrationListener instance
 */
/**
 * Created by zhexuanliu on 12/23/17.
 */

public class RegistrationListenerFactory extends ListenerBaseFactory
{
    /**
     * Method to return a resolveListener
     *
     * @param map - map to track all managers
     * @param manager - manager to be recorded down/removed when successful/failed
     * @param handler - the main thread handler
     *
     * @return the resulting RegistrationListener object
     */
    public static NSDRegistrationListener createRegistrationListener(
            HashMap<String, NetworkServiceDiscoveryManager> map,
            NetworkServiceDiscoveryManager manager,
            Handler handler
    )
    {
        int currentSuffix = RegistrationListenerFactory.listenerSuffix + 1;

        return new NSDRegistrationListener(map, manager, currentSuffix, handler);
    }
}
