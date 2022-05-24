package com.boredream.accessibilityservice;

import android.content.Context;

public class AppKeeper {

    public static boolean isNormalEnter;

    private AppKeeper() {

    }

    public static boolean isForeground = true;
    public static int screenWidth;

    private static Context app;

    public static void init(Context app) {
        AppKeeper.app = app;
    }

    public static Context getApp() {
        if(AppKeeper.app == null) {
            throw new RuntimeException("you must call AppKeeper.init(application) in your Application class first");
        }
        return app;
    }

}
