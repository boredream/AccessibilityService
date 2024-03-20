package com.boredream.accessibilityservice;

public class HelperTask {

    private String target;
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

    public HelperTask(Class<? extends BaseHelper> helperClass) {
        this.helperClass = helperClass;
    }

    @Override
    public String toString() {
        return target;
    }
}
