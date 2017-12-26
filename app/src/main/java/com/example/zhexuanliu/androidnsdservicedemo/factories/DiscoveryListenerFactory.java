package com.example.zhexuanliu.androidnsdservicedemo.factories;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDDiscoveryListener;
import com.example.zhexuanliu.androidnsdservicedemo.listeners.NSDResolveListener;

import static android.content.ContentValues.TAG;
import android.os.Handler;

/**
 * Created by zhexuanliu on 12/23/17.
 */

/**
 * Factory class responsible to create a DiscoveryListener instance
 */
public class DiscoveryListenerFactory extends ListenerBaseFactory
{
    /**
     * Method to return a discoveryListener
     * @param serviceName - name of service the listener is listening to
     * @param manager - the nsd manager instance this listener is attached to
     * @param resolveListener - the resolve listener used when service is found
     * @param serviceInfo - service info object will be used by the discoveryListener
     * @param sameMachineDiscover - indicates if this discovery is requested by the same machine registering
     *
     * @return the resulting DiscoverListener object
     */
    public static NSDDiscoveryListener createDiscoveryListener(
            String serviceName,
            NsdManager manager,
            NSDResolveListener resolveListener,
            NsdServiceInfo serviceInfo,
            Handler handler,
            boolean sameMachineDiscover
    )
    {
        final int currentSuffix = DiscoveryListenerFactory.listenerSuffix + 1;

        return new NSDDiscoveryListener(serviceName, manager, resolveListener, serviceInfo, handler, sameMachineDiscover);
    }
}
