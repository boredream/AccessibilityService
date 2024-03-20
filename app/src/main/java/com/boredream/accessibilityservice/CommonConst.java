package com.boredream.accessibilityservice;

import com.boredream.accessibilityservice.event.ChangeHelperTaskEvent;

import org.greenrobot.eventbus.EventBus;

public class CommonConst {

    public static final String TARGET_WX_BREAD_FREE = "微信-面包-抢购";
    public static final String TARGET_WRN_W_CHECK_IN = "微信-薇诺娜白-签到";

    private static HelperTask target;

    public static HelperTask getTarget() {
        return target;
    }

    public static void setTarget(HelperTask target) {
        CommonConst.target = target;
        EventBus.getDefault().post(new ChangeHelperTaskEvent());
    }


}
