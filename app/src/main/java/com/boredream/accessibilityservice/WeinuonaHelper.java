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

    private GestureDescription buildClick(float x, float y) {
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke = new GestureDescription.StrokeDescription(clickPath, 0, 1);
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickBuilder.addStroke(clickStroke);
        return clickBuilder.build();
    }

    private void startTask() {
        if (1==1) {
            GestureDescription gesture = buildClick(200, 1500);
            boolean dispatch = service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            }, null);
            Toast.makeText(service.getApplicationContext(), "perform gesture " + dispatch, Toast.LENGTH_LONG).show();
            return;
        }

        AccessibilityNodeInfo mineBtn = findTargetNode(node -> {
            // 判断组件类型
            if (!node.getClassName().equals("android.view.View")) {
                return false;
            }

            // 判断位置
            String targetBoundsInParent = "(0, 0 - 72, 34)";
            boolean samePosition = targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
            if (!samePosition) return false;

            CharSequence description = node.getContentDescription();
            if(description == null || !"我的".equals(description.toString())) return false;

            return true;
        });
        if(mineBtn == null) return;
        mineBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        delay(3000);

        AccessibilityNodeInfo memberBtn = findTargetNode(node -> {
            // 判断组件类型
            if (!node.getClassName().equals("android.widget.TextView")) {
                return false;
            }

            CharSequence text = node.getText();
            if(text == null || !"我的会员权益".equals(text.toString())) {
                return false;
            }

            return true;
        });
        if(memberBtn == null) return;
        click(memberBtn);
        delay(3000);

        AccessibilityNodeInfo goToCheckInBtn = findTargetNode(node -> {
            // 判断组件类型
            if (!node.getClassName().equals("android.widget.Image")) {
                return false;
            }

            // 判断位置
            String targetBoundsInParent = "(0, 212 - 360, 350)";
            boolean samePosition = targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
            if (!samePosition) return false;
            return true;
        });
        if(goToCheckInBtn == null) return;
        goToCheckInBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        delay(3000);
        getByText("今天").performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    // 判断目标节点是否匹配某个位置
    private boolean targetNodePositionCheck(AccessibilityNodeInfo node, String targetBoundsInParent, Integer xTolerance, Integer yTolerance) {
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

    private boolean comparePosition(int left, int top, int right, int bottom,
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
    private boolean isPageLoaded(AccessibilityEvent event) {
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

    private void printAllNode(AccessibilityNodeInfo root) {
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
        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(text);
        AccessibilityNodeInfo target = null;
        for (AccessibilityNodeInfo info : infos) {
            if (info.getText().equals(text)) {
                target = info;
                break;
            }
        }
        Log.i("DDD", "getByText " + target);
        return target;
    }

    protected AccessibilityNodeInfo getByTextFirst(String text) {
        // 通过text来获取某个控件，模糊匹配
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) return null;
        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(text);
        if (infos != null && infos.size() > 0) {
            return infos.get(0);
        }
        return null;
    }

    private void delayRandom(long time) {
        delay(time + new Random().nextInt(1000));
    }

    private void delay(long time) {
        try {
            Log.i("DDD", "delay = " + time);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(OverLayCtrlEvent event) {
        if ("getViewTree".equals(event.getCommand())) {
            printAllNode(service.getRootInActiveWindow());
        } else if ("start".equals(event.getCommand())) {
            startTask();
        }
    }

    private void executeTaskAtTime(Date date, final Runnable runnable) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(task, date);
        Log.i("DDD", "Set TimerTask " + date);
    }

    private Date getTodayTime(int hour, int minute, int second, int millisecondOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.add(Calendar.MILLISECOND, millisecondOffset);
        return new Date(calendar.getTimeInMillis());
    }

    private AccessibilityNodeInfo findTargetNode(TargetNodeInterface targetNodeInterface) {
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

    private interface TargetNodeInterface {
        boolean isTarget(AccessibilityNodeInfo node);
    }
}
