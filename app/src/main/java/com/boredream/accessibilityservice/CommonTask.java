package com.boredream.accessibilityservice;

import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.boredream.accessibilityservice.MyAccessibilityService;
import com.boredream.accessibilityservice.MyUtils;
import com.boredream.accessibilityservice.TaskFileManager;
import com.boredream.accessibilityservice.TaskStep;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Locale;

public class CommonTask {

    private String taskName;
    private final List<TaskStep> stepList;
    private MyAccessibilityService service;

    public String getTaskName() {
        return taskName;
    }

    public void setService(MyAccessibilityService service) {
        this.service = service;
    }

    public CommonTask(String taskName) {
        this.taskName = taskName;
        String json = TaskFileManager.readFile(taskName);
        stepList = new Gson().fromJson(json, new TypeToken<List<TaskStep>>() {
        }.getType());
    }

    private int curStep = 0;
    private Handler handler = new Handler();

    public void start() {
        if (CollectionUtils.isEmpty(stepList)) {
            ToastUtils.showShort("step 加载失败");
            return;
        }

        curStep = 0;
        loopStep();
    }

    private void loopStep() {
        if (curStep >= stepList.size()) {
            String log = String.format(Locale.getDefault(), "task done ! step count = %d ", stepList.size());
            LogUtils.i(log);
            ToastUtils.showShort(log);
            return;
        }

        TaskStep taskStep = stepList.get(curStep);
        if (TaskStep.TYPE_CLICK.equals(taskStep.getType())) {
            performClickStep(taskStep);
        } else if (TaskStep.TYPE_DELAY.equals(taskStep.getType())) {
            performDelayStep(taskStep);
        }
    }

    private void performClickStep(TaskStep taskStep) {
        List<TaskStep.Rule> ruleList = taskStep.getRule();
        if (TaskStep.Rule.TYPE_POSITION.equals(ruleList.get(0).getRuleType())) {
            // position 的只会是一个 rule
            String position = ruleList.get(0).getRuleValue();
            int x = Integer.parseInt(position.split(",")[0]);
            int y = Integer.parseInt(position.split(",")[1]);
            boolean click = MyUtils.clickAtPosition(service, x, y);
            if (!click) {
                String log = String.format(Locale.getDefault(), "step %d fail", curStep);
                LogUtils.i(log);
                ToastUtils.showShort(log);
            } else {
                curStep++;
                loopStep();
            }
        } else {
            // 非position的，list里所有rule都需要满足
            AccessibilityNodeInfo mineBtn = MyUtils.findTargetNode(service, node -> {
                for (TaskStep.Rule rule : ruleList) {
                    if (TaskStep.Rule.TYPE_CLASSNAME.equals(rule.getRuleType())) {
                        // 判断组件类型
                        String className = rule.getRuleValue();
                        if (!node.getClassName().equals(className)) {
                            return false;
                        }
                    } else if (TaskStep.Rule.TYPE_BOUNDS_IN_PARENT.equals(rule.getRuleType())) {
                        // 判断位置
                        String targetBoundsInParent = rule.getRuleValue();
                        boolean samePosition = MyUtils.targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
                        if (!samePosition) {
                            return false;
                        }
                    }
                }
                return true;
            });
            if (mineBtn == null) {
                String log = String.format(Locale.getDefault(), "该条件未匹配到按钮 %s", new Gson().toJson(ruleList));
                LogUtils.i(log);
                ToastUtils.showShort(log);
            } else {
                boolean click = mineBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (!click) {
                    String log = String.format(Locale.getDefault(), "step %d fail", curStep);
                    LogUtils.i(log);
                    ToastUtils.showShort(log);
                } else {
                    curStep++;
                    loopStep();
                }
            }
        }
    }

    private void performDelayStep(TaskStep taskStep) {
        long delay = Long.parseLong(taskStep.getValue());
        handler.postDelayed(() -> {
            curStep++;
            loopStep();
        }, delay);
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public String toString() {
        return taskName;
    }
}
