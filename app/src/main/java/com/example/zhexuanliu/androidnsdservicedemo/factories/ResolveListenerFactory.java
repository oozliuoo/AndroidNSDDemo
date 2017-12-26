package com.example.zhexuanliu.androidnsdservicedemo.factories;

import android.net.nsd.NsdServiceInfo;

import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDResolveListener;

/**
 * Created by zhexuanliu on 12/23/17.
 */

/**
 * Factory class responsible to create a ResolveListener instance
 */
public class ResolveListenerFactory extends ListenerBaseFactory
{
    /**
     * Method to return a resolveListener wrapper
     * @param serviceName - name of service the listener is listening to
     * @param serviceInfo - serviceInfo passed to construct resolveListener
     * @param sameMachineDiscover - Indicates whether this is a discovery request from the same machine
     *
     * @return the resulting ResolveListener object
     */
    public static NSDResolveListener createResolveListener(
            String serviceName,
            NsdServiceInfo serviceInfo,
            boolean sameMachineDiscover
    )
    {
        final int currentSuffix = ResolveListenerFactory.listenerSuffix + 1;

        return new NSDResolveListener(serviceName, serviceInfo, sameMachineDiscover);
    }
}