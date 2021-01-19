package com.optimus.eds;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import java.lang.reflect.Method;

import androidx.multidex.MultiDexApplication;

public class EdsApplication extends MultiDexApplication  {

    private static EdsApplication instance;


    public static EdsApplication getInstance() {
        return instance==null?new EdsApplication():instance;

    }

    public static Context getContext() {
          return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);

    }

}
