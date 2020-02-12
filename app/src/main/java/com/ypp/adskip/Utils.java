package com.ypp.adskip;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class Utils {
    public static boolean isServiceRunning(Context context) {
        return getSharedPreferences(context).getBoolean("service",false);
    }

    public static void setServiceRunning(Context context, boolean isRunning){
        getSharedPreferences(context).edit().putBoolean("service", isRunning).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("config",Context.MODE_PRIVATE);
    }
}
