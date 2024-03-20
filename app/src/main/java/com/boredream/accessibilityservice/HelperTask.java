package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;

import java.lang.reflect.Constructor;

public class HelperTask {

    private String target;
    private BaseHelper helper;
    private Class<? extends BaseHelper> helperClass;

    public HelperTask(String target, Class<? extends BaseHelper> helperClass) {
        this.target = target;
        this.helperClass = helperClass;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BaseHelper getHelper(AccessibilityService service) {
        if (helper == null) {
            try {
                Constructor<?> constructor = helperClass.getConstructor(AccessibilityService.class);
                helper = (BaseHelper) constructor.newInstance(service);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return helper;
    }

    public void setHelperClass(Class<? extends BaseHelper> helperClass) {
        this.helperClass = helperClass;
    }

    @Override
    public String toString() {
        return target;
    }

}
