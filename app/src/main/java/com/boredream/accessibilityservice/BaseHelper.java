package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public abstract class BaseHelper {

   protected boolean stopFlag;
   protected AccessibilityService service;

   public BaseHelper(AccessibilityService service) {
      this.service = service;
   }

   public abstract void start();

   public abstract void onAccessibilityEvent(AccessibilityEvent event);
}
