package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class MaiCaiHelper {

    protected boolean start;
    protected AccessibilityService service;
    public MediaSoundHelper soundHelper;

    public MaiCaiHelper(AccessibilityService service) {
        this.service = service;
        this.soundHelper = new MediaSoundHelper(service.getApplication());
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        if(eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED || eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return;
        }

        if(toggleStart(accessibilityEvent)) {
            startLoop();
        } else if(toggleEnd(accessibilityEvent)) {
            start = false;
            System.out.println("stop loop");
        }
    }

    protected boolean toggleStart(AccessibilityEvent event) {
        return false;
    }

    protected boolean toggleEnd(AccessibilityEvent event) {
        return false;
    }

    private synchronized void startLoop() {
        if(start) return;
        System.out.println("start loop");

        start = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(start) {
                    System.out.println("loop once");
                    loop();
                }
            }
        }).start();
    }

    protected void loop() {

    }

    protected void back() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    // 点击
    protected static boolean click(AccessibilityNodeInfo node) {
        if (node == null) return false;
        if (node.isClickable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else if (node.isCheckable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_SELECT);
        } else {
            click(node.getParent());
        }
        return false;
    }

    protected AccessibilityNodeInfo getByText(String text) {
        // 通过text来获取某个控件，模糊匹配
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if(root == null) return null;
        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(text);
        for (AccessibilityNodeInfo info : infos) {
            if(info.getText().equals(text)) {
                return info;
            }
        }
        return null;
    }

    protected AccessibilityNodeInfo getByTextFirst(String text) {
        // 通过text来获取某个控件，模糊匹配
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if(root == null) return null;
        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(text);
        if(infos != null && infos.size() > 0) {
            return infos.get(0);
        }
        return null;
    }

    protected void delay(long time) {
        try {
            int speed = SpeedHelper.getInstance().getSpeedInCache();
            Thread.sleep(time * speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
