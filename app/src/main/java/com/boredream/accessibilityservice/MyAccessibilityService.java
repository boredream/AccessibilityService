package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.boredream.accessibilityservice.event.ChangeHelperTaskEvent;
import com.boredream.accessibilityservice.event.OverLayCtrlEvent;
import com.boredream.accessibilityservice.event.OverlayEvent;
import com.boredream.accessibilityservice.event.OverlayInfoUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyAccessibilityService extends AccessibilityService {

    private BaseHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
        helper = CommonConst.getTarget().getHelper(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnChangeHelperTaskEvent(ChangeHelperTaskEvent event) {
        // 切换helper后，重新绑定
        helper = null;
        HelperTask target = CommonConst.getTarget();
        helper = target.getHelper(this);
    }

    private String currentProgress;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (MyUtils.ignoreEvent(event)) return;
        Log.v("DDD", event.toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Log.v("DDD", "click -> " + event.getSource());
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && event.getPackageName() != null
                && !MyUtils.equals(currentProgress, event.getPackageName())) {
            // 切换进程了
            OverlayInfoUpdateEvent busEvent = new OverlayInfoUpdateEvent();
            busEvent.setProgress(event.getPackageName().toString());
            EventBus.getDefault().post(busEvent);
            currentProgress = event.getPackageName().toString();
        }

        if (helper != null) {
            helper.onAccessibilityEvent(event);
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOverLayCtrlEvent(OverLayCtrlEvent event) {
        if ("getViewTree".equals(event.getCommand())) {
            MyUtils.printAllNode(getRootInActiveWindow());
        } else if ("start".equals(event.getCommand())) {
            if (helper != null) {
                helper.start();
            }
        }
    }

}
