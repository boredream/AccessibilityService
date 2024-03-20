package com.boredream.accessibilityservice;

import com.boredream.accessibilityservice.event.ChangeHelperTaskEvent;
import com.boredream.accessibilityservice.helper.WxWrnHelper;

import org.greenrobot.eventbus.EventBus;

public class CommonConst {

    public static final String TARGET_WX_WRNW_CHECKIN = "微信-薇诺娜白-签到";
    public static final String TARGET_WX_BREAD_FREE = "微信-面包-抢购";

    private static HelperTask target = new HelperTask(TARGET_WX_WRNW_CHECKIN, WxWrnHelper.class);

    public static HelperTask getTarget() {
        return target;
    }

    public static void setTarget(HelperTask target) {
        CommonConst.target = target;
        EventBus.getDefault().post(new ChangeHelperTaskEvent());
    }


}
