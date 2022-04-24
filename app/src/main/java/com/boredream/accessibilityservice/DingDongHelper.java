package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class DingDongHelper extends MaiCaiHelper {

    public DingDongHelper(AccessibilityService service) {
        super(service);
    }

    @Override
    protected boolean toggleStart(AccessibilityEvent event) {
        List<CharSequence> textList = event.getText();
        if(textList.size() > 0) {
            return "吃什么".contentEquals(textList.get(0));
        }
        return false;
    }

    @Override
    protected boolean toggleEnd(AccessibilityEvent event) {
        List<CharSequence> textList = event.getText();
        if(textList.size() > 0) {
            return "首页".contentEquals(textList.get(0));
        }
        return false;
    }

    @Override
    protected void loop() {
        AccessibilityNodeInfo infoMine = getByText("我的");
        AccessibilityNodeInfo infoCart = getByText("购物车");

        click(infoMine);
        delay(500);

        click(infoCart);
        delay(1500);

        List<AccessibilityNodeInfo> list = service.getRootInActiveWindow().findAccessibilityNodeInfosByText("去结算(");
        if(list != null && list.size() > 0) {
            // 购物车有商品
            click(list.get(0));
            delay(1500);

            AccessibilityNodeInfo commitInfo = getByText("立即支付");
            if (commitInfo == null) {
                back();
                delay(1000);
                return;
            }

            click(commitInfo);
            delay(1000);

            if(getByText("选择送达时间") != null) {
                // 判断今天是否已约满
                AccessibilityNodeInfo checkInfo = null;
                AccessibilityNodeInfo today = getByText("今天");
                AccessibilityNodeInfo timeList = today.getParent().getParent().getParent().getChild(1);
                for (int i = 0; i < timeList.getChildCount(); i++) {
                    AccessibilityNodeInfo child = timeList.getChild(i);
                    if (child.getChildCount() == 1) {
                        checkInfo = child;
                    }
                }

                if(checkInfo == null) {
                    // 时间都约满了，继续循环
                    back();
                    delay(200);
                    back();
                    delay(1000);
                } else {
                    // 有时间，选中下单
                    start = false;
                    delay(500);
                    click(checkInfo);
                    delay(500);
                    click(commitInfo);

                    soundHelper.playRingSound();
                }
            }
        }
    }
}
