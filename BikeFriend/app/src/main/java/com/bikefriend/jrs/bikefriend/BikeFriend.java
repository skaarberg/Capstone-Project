package com.bikefriend.jrs.bikefriend;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by skaar on 08.11.2017.
 */

public class BikeFriend extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}