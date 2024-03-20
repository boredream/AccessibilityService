package com.boredream.accessibilityservice.helper;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.boredream.accessibilityservice.BaseHelper;
import com.boredream.accessibilityservice.MyUtils;

public class WxWrnWhiteHelper extends BaseHelper {

   private final Handler handler;

   public WxWrnWhiteHelper(AccessibilityService service) {
      super(service);
      handler = new Handler();
   }

   @Override
   public void start() {
      step1();
   }

   private void step1() {
      Log.i("DDD", "step1");
      // 我的会员权益 WebView
      int x = 530;
      int y = 2320;
      boolean click = MyUtils.clickAtPosition(service, x, y);
      if(!click) return;
      handler.postDelayed(this::step2, 2000);
   }

   private void step2() {
      Log.i("DDD", "step2");
      // android.view.accessibility.AccessibilityNodeInfo@2c78b9; boundsInParent: Rect(0, 256 - 119, 413); boundsInScreen: Rect(0, 1715 - 357, 2186); packageName: com.tencent.mm; className: android.widget.TextView; text: ; error: null; maxTextLength: -1; stateDescription: null; contentDescription: null; tooltipText: null; viewIdResName: null; uniqueId: null; checkable: false; checked: false; focusable: false; focused: false; selected: false; clickable: false; longClickable: false; contextClickable: false; enabled: true; password: false; scrollable: false; importantForAccessibility: false; visible: true; actions: [AccessibilityAction: ACTION_SHOW_ON_SCREEN - null, AccessibilityAction: ACTION_CONTEXT_CLICK - null, AccessibilityAction: ACTION_NEXT_HTML_ELEMENT - null, AccessibilityAction: ACTION_PREVIOUS_HTML_ELEMENT - null, AccessibilityAction: ACTION_ACCESSIBILITY_FOCUS - null]; isTextSelectable: false
      AccessibilityNodeInfo mineBtn = MyUtils.findTargetNode(service, node -> {
         // 判断组件类型
         if (!node.getClassName().equals("android.widget.TextView")) {
            return false;
         }

         // 判断位置
         String targetBoundsInParent = "(0, 256 - 119, 413)";
         boolean samePosition = MyUtils.targetNodePositionCheck(node, targetBoundsInParent, 0, 0);
         if (!samePosition) return false;
         return true;
      });
      if(mineBtn == null) return;
      mineBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);

      handler.postDelayed(this::step3, 4000);
   }

   private void step3() {
      Log.i("DDD", "step2");
      // 我的会员权益 WebView
      int x = 550;
      int y = 550;
      boolean click = MyUtils.clickAtPosition(service, x, y);
      if(!click) return;

      Log.i("DDD", "Done!");
   }

   @Override
   public void onAccessibilityEvent(AccessibilityEvent event) {

   }

}
