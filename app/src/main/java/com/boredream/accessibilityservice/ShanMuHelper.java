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
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(accessibilityEvent.getPackageName().equals("cn.samsclub.app")) {
            super.onAccessibilityEvent(accessibilityEvent);
        }
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
            soundHelper.stopPlay();
            return "首页".contentEquals(textList.get(0));
        }
        return false;
    }

    private boolean hasGoodsInCart = false;

    @Override
    protected void loop() {
        AccessibilityNodeInfo categoryInfo = getByText("分类");
        AccessibilityNodeInfo infoMine = getByText("我的");
        AccessibilityNodeInfo infoCart = getByText("购物车");

        if(!hasGoodsInCart) {
            // 未添加商品
            click(infoMine);
            delay(200);

            click(categoryInfo);
            delay(1500);

            AccessibilityNodeInfo info;
            AccessibilityNodeInfo rv;
            try {
                info = getByText("甄选美味");
                rv = info.getParent().getParent().getParent().getParent().getChild(2).getChild(3)
                        .getChild(0).getChild(0).getChild(0);
                if(rv.getChildCount() <= 2) {
                    // 需要刷新
                    return;
                }
            } catch(Exception e) {
                // 页面未刷新出来
                return;
            }

            AccessibilityNodeInfo xianshi = getByTextFirst("套餐内容:");
            if(xianshi != null) {
                AccessibilityNodeInfo parent = xianshi.getParent();
                List<AccessibilityNodeInfo> list = parent.getChild(0).findAccessibilityNodeInfosByText("补货中");
                boolean lackGoods = list != null && list.size() > 0;
                System.out.println("lack good ? " + lackGoods);

                AccessibilityNodeInfo addCart = parent.getChild(6);

                if(!lackGoods) {
                    System.out.println("发现套餐");
                    click(addCart);
                    hasGoodsInCart = true;
                    delay(500);
                }
            }
        } else {
            click(infoMine);
            delay(200);

            click(infoCart);
            delay(1000);

            AccessibilityNodeInfo info = getByTextFirst("结算(");
            if(info != null) {
                soundHelper.playRingSound();
                click(info);
                delay(2500);

                AccessibilityNodeInfo busyInfo = getByTextFirst("今日运力繁忙");
                if(busyInfo == null) {
                    // 有商品

                }
            }
        }
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
