package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class ShanMuHelper extends MaiCaiHelper {

    public ShanMuHelper(AccessibilityService service) {
        super(service);
    }

    @Override
    protected boolean toggleStart(AccessibilityEvent event) {
        List<CharSequence> textList = event.getText();
        if(textList.size() > 0) {
            return "发现".contentEquals(textList.get(0));
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
        delay(200);

        click(infoCart);
        delay(1000);
    }

    protected boolean hasGoods() {
        AccessibilityNodeInfo info = getByText("补货中");
        return info == null;
    }

    protected void commitOrder() {
        // 先判断是否添加商品
        AccessibilityNodeInfo info = getByText("全选");
        if(!info.isChecked()) {
            info.performAction(AccessibilityNodeInfo.ACTION_SELECT);
            delay(1000);
        }

        // 已选中，继续
        info = getByTextFirst("结算(");
        if(info != null && info.isCheckable()) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            // TODO: chunyang 4/24/22 提示成功

        }
    }

}
