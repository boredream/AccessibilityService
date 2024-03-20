package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Random;

public class MyUtils {

    // 判断目标节点是否匹配某个位置
    public static boolean targetNodePositionCheck(AccessibilityNodeInfo node, String targetBoundsInParent, Integer xTolerance, Integer yTolerance) {
        targetBoundsInParent = targetBoundsInParent.replace(" ", "")
                .replace("(", "")
                .replace(")", "");
        String leftTop = targetBoundsInParent.split("-")[0];
        String rightBottom = targetBoundsInParent.split("-")[1];
        Rect rect = new Rect();
        node.getBoundsInParent(rect);
        return comparePosition(rect.left, rect.top, rect.right, rect.bottom,
                Integer.parseInt(leftTop.split(",")[0]),
                Integer.parseInt(leftTop.split(",")[1]),
                Integer.parseInt(rightBottom.split(",")[0]),
                Integer.parseInt(rightBottom.split(",")[1]),
                xTolerance, yTolerance
        );
    }

    public static boolean comparePosition(int left, int top, int right, int bottom,
                                          int targetLeft, int targetTop, int targetRight, int targetBottom,
                                          Integer xTolerance, Integer yTolerance) {
        Log.d("DDD", "compare position: node=" + left + "," + top + "," + right + "," + bottom
                + " target=" + targetLeft + "," + targetTop + "," + targetRight + "," + targetBottom);

        // 精准匹配可以设置 tolerance 容差为 0
        // 如果不需要比较，可以设为 null
        // 允许容差的话，设置 tolerance > 0，即 节点和目标 的差值小于容差都算是匹配
        if (xTolerance != null) {
            if (Math.abs(left - targetLeft) > xTolerance || Math.abs(right - targetRight) > xTolerance) {
                return false;
            }
        }
        if (yTolerance != null) {
            if (Math.abs(top - targetTop) > yTolerance || Math.abs(bottom - targetBottom) > yTolerance) {
                return false;
            }
        }
        return true;
    }

    // 是否是页面加载
    public static boolean isPageLoaded(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return false;
        }
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) return false;

        Rect rect = new Rect();
        source.getBoundsInParent(rect);
        if (rect.right != 1080 || rect.bottom != 2400) {
            return false;
        }
        return true;
    }

    public static void printAllNode(AccessibilityNodeInfo root) {
        LinkedList<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            StringBuilder sbPre = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sbPre.append(" ");
            }
            Log.i("DDD", sbPre + "current level = " + level + " , size = " + size);
            for (int i = 0; i < size; i++) {
                AccessibilityNodeInfo node = queue.poll();
                Log.i("DDD", sbPre + "node = " + node);
                if (node != null) {
                    for (int j = 0; j < node.getChildCount(); j++) {
                        queue.add(node.getChild(j));
                    }
                }
            }
            level++;
        }
    }

    public static void back(AccessibilityService service) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    // 点击
    public static boolean click(AccessibilityNodeInfo node) {
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

    public static void delayRandom(long time) {
        delay(time + new Random().nextInt(1000));
    }

    public static void delay(long time) {
        try {
            Log.i("DDD", "delay = " + time);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean clickAtPosition(AccessibilityService service, int x, int y) {
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke = new GestureDescription.StrokeDescription(clickPath, 0, 1);
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickBuilder.addStroke(clickStroke);
        GestureDescription gesture = clickBuilder.build();
        Log.i("DDD", "perform gesture " + x + "," + y);
        return service.dispatchGesture(gesture, null, null);
    }

    public static AccessibilityNodeInfo findTargetNode(AccessibilityService service, TargetNodeInterface targetNodeInterface) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        LinkedList<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                AccessibilityNodeInfo node = queue.poll();
                if (node != null) {
                    for (int j = 0; j < node.getChildCount(); j++) {
                        queue.add(node.getChild(j));
                    }
                    if (targetNodeInterface.isTarget(node)) {
                        Log.i("DDD", "找到目标节点 " + node);
                        return node;
                    }
                }
            }
        }
        return null;
    }

    public static boolean ignoreEvent(AccessibilityEvent event) {
        // 过滤不必要的event
        if (equals("com.android.systemui", event.getPackageName())
                || equals("com.android.launcher", event.getPackageName())
                || equals("com.android.launcher", event.getPackageName())
                || equals("com.boredream.accessibilityservice", event.getPackageName())) {
            return true;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return true;
        }
        return false;
    }

    public static boolean equals(String target, CharSequence source) {
        return target!= null && source != null && target.contentEquals(source);
    }

    public static interface TargetNodeInterface {
        boolean isTarget(AccessibilityNodeInfo node);
    }
}
