package com.aniketudit.udit.pmma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dell PC on 12-01-2017.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "NetworkChangeReceiver";
    private static boolean isConnected = false;
    public  ConnectivityReceiver()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.v(LOG_TAG, "Receieved notification about network status");
        isNetworkAvailable(context);
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            //Log.v(LOG_TAG, "You are connected to Internet!");
            //Toast.makeText(context, "Internet availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            //Log.v(LOG_TAG, "You are not connected to Internet!");
            //Toast.makeText(context, "Internet NOT availablle via Broadcast receiver", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}

