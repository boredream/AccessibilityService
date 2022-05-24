package com.boredream.accessibilityservice;

import android.content.Context;
import android.content.SharedPreferences;

public class SpeedHelper {
    private static volatile SpeedHelper instance = null;
    private final SharedPreferences sp;
    private int speed;

    public static SpeedHelper getInstance() {
        if (instance == null) {
            synchronized (SpeedHelper.class) {
                if (instance == null) {
                    instance = new SpeedHelper();
                }
            }
        }
        return instance;
    }

    private SpeedHelper() {
        // private
        sp = AppKeeper.getApp().getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public int getSpeedInCache() {
        return speed;
    }

    public int getSpeed() {
        speed = sp.getInt("speed", 1);
        return speed;
    }

    public int speedDown() {
        if(speed > 1) {
            speed --;
            sp.edit().putInt("speed", speed).apply();
        }
        return speed;
    }

    public int speedUp() {
        speed ++;
        sp.edit().putInt("speed", speed).apply();
        return speed;
    }

}
