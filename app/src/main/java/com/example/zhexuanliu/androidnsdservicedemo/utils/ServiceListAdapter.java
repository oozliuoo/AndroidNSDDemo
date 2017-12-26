package com.example.zhexuanliu.androidnsdservicedemo.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhexuanliu.androidnsdservicedemo.NetworkServiceDiscoveryManager;
import com.example.zhexuanliu.androidnsdservicedemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhexuanliu on 12/26/17.
 */

/**
 * Adapter for service list
 */
public class ServiceListAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> mData;

    public ServiceListAdapter(
            Context context,
            int textViewResourceId,
            ArrayList<String> list
    )
    {
        super(context, textViewResourceId, list);
        mData  = new ArrayList();
        mData.addAll(list);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
