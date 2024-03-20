package com.boredream.accessibilityservice.helper;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.boredream.accessibilityservice.BaseHelper;
import com.boredream.accessibilityservice.MyUtils;

public class WxWrnHelper extends BaseHelper {

   private final Handler handler;

   public WxWrnHelper(AccessibilityService service) {
      super(service);
      handler = new Handler();
   }

   @Override
   public void start() {
      step1();
   }

   private void step1() {
      Log.i("DDD", "step1");
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

      handler.postDelayed(this::step2, 3000);
   }

   private void step2() {
      Log.i("DDD", "step2");
      // 我的会员权益 WebView
      int x = 500;
      int y = 1300;
      boolean click = MyUtils.clickAtPosition(service, x, y);
      if(!click) return;

      handler.postDelayed(this::step3, 4000);
   }

   private void step3() {
      // 签到图片入口
      Log.i("DDD", "step3");
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
   }

   @Override
   public void onAccessibilityEvent(AccessibilityEvent event) {

   }

}
