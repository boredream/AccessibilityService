package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class WechatHelper {
    private boolean stopFlag;
    protected AccessibilityService service;
    public MediaSoundHelper soundHelper;

    public WechatHelper(AccessibilityService service) {
        this.service = service;
        this.soundHelper = new MediaSoundHelper(service.getApplication());
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // 过滤不必要的event
            return;
        }

        if(isPageLoaded(accessibilityEvent)) {
            // 如果页面加载了，尝试去找到目标按钮并点击，适当延迟
            if(!stopFlag) {
                getTicket();
            }
        }
    }

    // 是否是页面加载
    private boolean isPageLoaded(AccessibilityEvent event) {
        if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return false;
        }
        AccessibilityNodeInfo source = event.getSource();
        if(source == null) return false;

        Rect rect = new Rect();
        source.getBoundsInParent(rect);
        if(rect.right != 1080 || rect.bottom != 2400) {
           return false;
        }
        return true;
    }

    private void printAllNode(AccessibilityNodeInfo root) {
        LinkedList<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;
        while(!queue.isEmpty()) {
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
            level ++;
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
        for (AccessibilityNodeInfo info : infos) {
            if (info.getText().equals(text)) {
                return info;
            }
        }
        return null;
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

    protected void delay(long time) {
        try {
            int speed = SpeedHelper.getInstance().getSpeedInCache();
            Thread.sleep(time * speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(OverLayCtrlEvent event) {
        readyToGetTicket();
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

    // 准备抢票，在既定时间点击按钮
    private void readyToGetTicket() {
        stopFlag = false;
        Log.i("DDD", "readyToGetTicket");
        // 按要求找到target
        final AccessibilityNodeInfo targetNode = findTargetNode(new TargetNodeInterface() {
            @Override
            public boolean isTarget(AccessibilityNodeInfo node) {
                // FIXME: 2024/3/11
                return node != null && node.getText() != null && node.getText().toString().equals("查看所有订单");
//                if (node.getClassName().equals("android.widget.Image")) {
//                    Rect rect = new Rect();
//                    node.getBoundsInScreen(rect);
//                    return rect.bottom == 1080 && rect.right == 1080;
//                }
//                return false;
            }
        });
        if(targetNode == null) {
            Log.i("DDD", "未找到抢票入口！");
            return;
        }

        // 定时执行target
//        Date date = getTodayTime(12, 0, 0, -1000);
        Date date = new Date(System.currentTimeMillis() + 5000);
        executeTaskAtTime(date, new Runnable() {
            @Override
            public void run() {
                Log.i("DDD", "进入抢券模块！");
                targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });
    }

    // 抢票，在既定时间点击按钮
    private synchronized void getTicket() {
        // 延迟？循环？
        delay(100);

        AccessibilityNodeInfo targetNode = findTargetNode(new TargetNodeInterface() {
            @Override
            public boolean isTarget(AccessibilityNodeInfo node) {
                return node != null && node.getText() != null && node.getText().toString().equals("去点单");
//                return "android.widget.Button".contentEquals(node.getClassName());
            }
        });
        if (targetNode == null) {
            // 因为可能加载多个页面，其中有按钮的很少
//            Log.i("DDD", "未找到抢票按钮！");
            return;
        }

        Log.i("DDD", "点击抢票按钮！");
        targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        stopFlag = true;
    }

//    private void printAll() {
//        AccessibilityNodeInfo root = service.getRootInActiveWindow();
//        // DFS
//        Queue<AccessibilityNodeInfo> queue = new LinkedList<>();
//        queue.add(root);
//        int level = 0;
//        while(!queue.isEmpty()) {
//            StringBuilder sbPre = new StringBuilder();
//            for (int i = 0; i < level; i++) {
//                sbPre.append(" ");
//            }
//            int size = queue.size();
//            Log.i("DDD", sbPre + "current level = " + level + " , size = " + size);
//            for (int i = 0; i < size; i++) {
//                AccessibilityNodeInfo poll = queue.poll();
//                Log.i("DDD", sbPre + "node = " + poll);
//                if (poll != null) {
//                    for (int j = 0; j < poll.getChildCount(); j++) {
//                        queue.add(poll.getChild(j));
//                    }
//                }
//            }
//            level ++;
//        }
//    }

    private AccessibilityNodeInfo findTargetNode(TargetNodeInterface targetNodeInterface) {
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        LinkedList<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;
        while(!queue.isEmpty()) {
            int size = queue.size();
//            StringBuilder sbPre = new StringBuilder();
//            for (int i = 0; i < level; i++) {
//                sbPre.append(" ");
//            }
//            Log.i("DDD", sbPre + "current level = " + level + " , size = " + size);
            for (int i = 0; i < size; i++) {
                AccessibilityNodeInfo node = queue.poll();
//                Log.i("DDD", sbPre + "node = " + poll);
                if (node != null) {
                    for (int j = 0; j < node.getChildCount(); j++) {
                        queue.add(node.getChild(j));
                    }
                    if(targetNodeInterface.isTarget(node)) {
                        Log.i("DDD", "找到目标节点 " + node);
                        return node;
                    }
                }
            }
            level ++;
        }
        return null;
    }

    // DFS
//    private void stepNode(int level, AccessibilityNodeInfo node, TargetNodeInterface targetNodeInterface) {
//        if (node == null) return;
//        if (targetNodeInterface.isTarget(node)) {
//            EventBus.getDefault().post(new LogEvent("find target"));
//            targetNode = node;
//            Log.i("DDD", "找到目标node " + targetNode);
//            return;
//        }
//
////        StringBuilder sbPre = new StringBuilder("| ");
////        for (int i = 0; i < level; i++) {
////            sbPre.append("-- ");
////        }
////        Log.i("DDD", sbPre + "node = " + node);
//        for (int i = 0; i < node.getChildCount(); i++) {
//            stepNode(level + 1, node.getChild(i), targetNodeInterface);
//        }
//    }

    private interface TargetNodeInterface {
        boolean isTarget(AccessibilityNodeInfo node);
    }
}
