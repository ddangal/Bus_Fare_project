package com.example.deependra97.bus_fare_project.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.deependra97.bus_fare_project.BuildConfig;

/**
 * Created by deependra97 on 7/22/2017.
 */

public class Utilities {

    public static void log(String s){
        if(BuildConfig.DEBUG){
        Log.d("UTILITIES", s);
        }
    }

    public static void toast(Context c, String s){
        // TODO: 7/20/2017 SHOW TOAST
        Toast.makeText(c, s ,Toast.LENGTH_LONG).show();
    }
}
