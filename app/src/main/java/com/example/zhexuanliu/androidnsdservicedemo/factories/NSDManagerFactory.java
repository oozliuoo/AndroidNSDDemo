package com.example.zhexuanliu.androidnsdservicedemo.factories;

/**
 * Created by zhexuanliu on 12/24/17.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.example.zhexuanliu.androidnsdservicedemo.NetworkServiceDiscoveryManager;

/**
 * Factory class for creating NSDManager object
 */
public class NSDManagerFactory
{

    /**
     * Creates and return a NSDManager instance
     * @param context - context passed to constructor of NSDManager
     * @param handler - the main thread handler
     *
     * @return - the created NSDManager
     */
    public static NetworkServiceDiscoveryManager create(Context context, Handler handler)
    {
        return new NetworkServiceDiscoveryManager(context, handler);
    }
}
