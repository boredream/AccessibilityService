package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private MaiCaiHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DingDongHelper(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        helper.onAccessibilityEvent(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {
        System.out.println("onInterrupt");
    }
}
