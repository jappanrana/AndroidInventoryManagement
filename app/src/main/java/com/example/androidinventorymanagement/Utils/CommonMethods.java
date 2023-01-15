package com.example.androidinventorymanagement.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class CommonMethods {

    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
