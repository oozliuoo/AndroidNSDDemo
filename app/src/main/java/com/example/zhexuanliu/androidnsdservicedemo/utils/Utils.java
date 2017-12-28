package com.example.zhexuanliu.androidnsdservicedemo.utils;

/**
 * Created by zhexuanliu on 12/28/17.
 */

public class Utils
{
    /**
     * Converts a byte data to string
     * @param data - byte data to be converted
     * @return - resulting string
     */
    public static String convertBytesToString(byte[] data)
    {
        String s = "";

        for (int i = 0; i < data.length; i ++)
        {
            if (data[i] != 0x00)
            {
                s += (char)data[i];
            }
        }

        return s;
    }
}
