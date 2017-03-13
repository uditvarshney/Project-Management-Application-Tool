package com.aniketudit.udit.pmma;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Dell PC on 13-01-2017.
 */

public class MobileData {

    public boolean setMobileDataEnabled(Context context, boolean enabled) {
        try {
            //create instance of connectivity manager and get system connectivity service
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //create instance of class and get name of connectivity manager system service class
            final Class conmanClass  = Class.forName(conman.getClass().getName());
            //create instance of field and get mService Declared field
            final Field iConnectivityManagerField= conmanClass.getDeclaredField("mService");
            //Attempt to set the value of the accessible flag to true
            iConnectivityManagerField.setAccessible(true);
            //create instance of object and get the value of field conman
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            //create instance of class and get the name of iConnectivityManager field
            final Class iConnectivityManagerClass=  Class.forName(iConnectivityManager.getClass().getName());
            //create instance of method and get declared method and type
            final Method setMobileDataEnabledMethod= iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);
            //Attempt to set the value of the accessible flag to true
            setMobileDataEnabledMethod.setAccessible(true);
            //dynamically invoke the iConnectivityManager object according to your need (true/false)
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (Exception e){
        }
        return true;
    }
}
