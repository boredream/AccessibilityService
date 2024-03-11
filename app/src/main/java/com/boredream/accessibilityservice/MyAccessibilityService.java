package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyAccessibilityService extends AccessibilityService {

    private WechatHelper helper;
    private HelperFloatView helperFloatView;

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new WechatHelper(this);
        helperFloatView = new HelperFloatView(this);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        helper.onAccessibilityEvent(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {
        System.out.println("onInterrupt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        helperFloatView.removeView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOverlayEvent(OverlayEvent event) {
        helperFloatView.addView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOverLayCtrlEvent(OverLayCtrlEvent event) {
        helper.onEvent(event);
    }

}
