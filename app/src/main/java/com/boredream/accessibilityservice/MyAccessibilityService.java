package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.boredream.accessibilityservice.event.ChangeHelperTaskEvent;
import com.boredream.accessibilityservice.event.OverLayCtrlEvent;
import com.boredream.accessibilityservice.event.OverlayEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyAccessibilityService extends AccessibilityService {

    private BaseHelper helper;
    private HelperFloatView helperFloatView;

    @Override
    public void onCreate() {
        super.onCreate();

        helperFloatView = new HelperFloatView(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnChangeHelperTaskEvent(ChangeHelperTaskEvent event) {
        // 切换helper后，重新绑定
        helper = null;
        HelperTask target = CommonConst.getTarget();
        helper = target.getHelper(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (helper != null) {
            helper.onAccessibilityEvent(accessibilityEvent);
        }
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

    }

}
