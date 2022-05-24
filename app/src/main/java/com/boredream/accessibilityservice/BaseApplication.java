package com.boredream.accessibilityservice;


import android.app.Application;


public class BaseApplication extends Application {

    public static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        AppKeeper.init(this);
    }

}
