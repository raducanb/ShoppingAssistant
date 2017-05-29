package com.example.raducanbogdan.shoppingassistant;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by raducanb on 28/05/2017.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
