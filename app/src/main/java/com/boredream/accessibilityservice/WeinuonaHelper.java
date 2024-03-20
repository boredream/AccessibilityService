package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WeinuonaHelper {

    protected AccessibilityService service;

    public WeinuonaHelper(AccessibilityService service) {
        this.service = service;
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        CharSequence packageName = accessibilityEvent.getPackageName();
        if (packageName != null && !"com.tencent.mm".contentEquals(packageName)) {
            return;
        }

        int eventType = accessibilityEvent.getEventType();
        if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // 过滤不必要的event
            return;
        }
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            // 看需求放开
            return;
        }
        Log.i("DDD", accessibilityEvent + "\n === click source " + accessibilityEvent.getSource());
    }

    private void startTask() {
        if (1==1) {
            MyUtils.clickAtPosition(service, 500, 1400);
            return;
        }
        AccessibilityNodeInfo mineBtn = MyUtils.findTargetNode(service, node -> {
            // 判断组件类型
            if (!node.getClassName().equals("android.view.View")) {
                return false;
            }

            // 判断位置
            String targetBoundsInParent = "(0, 0 - 72, 34)";
            boolean samePosition = MyUtils.targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
            if (!samePosition) return false;

            CharSequence description = node.getContentDescription();
            if(description == null || !"我的".equals(description.toString())) return false;

            return true;
        });
        if(mineBtn == null) return;
        mineBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        MyUtils.delay(3000);

        // 我的会员权益 WebView
        boolean click = MyUtils.clickAtPosition(service, 500, 1000);
        if(!click) return;
        MyUtils.delay(3000);

        // 签到图片入口
        AccessibilityNodeInfo goToCheckInBtn = MyUtils.findTargetNode(service, node -> {
            // 判断组件类型
            if (!node.getClassName().equals("android.widget.Image")) {
                return false;
            }

            // 判断位置
            String targetBoundsInParent = "(0, 212 - 360, 350)";
            boolean samePosition = MyUtils.targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
            if (!samePosition) return false;
            return true;
        });
        if(goToCheckInBtn == null) return;
        goToCheckInBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        MyUtils.delay(3000);
    }

    public void onEvent(OverLayCtrlEvent event) {
        if ("getViewTree".equals(event.getCommand())) {
            MyUtils.printAllNode(service.getRootInActiveWindow());
        } else if ("start".equals(event.getCommand())) {
            startTask();
        }
    }

}
