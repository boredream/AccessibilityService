package com.boredream.accessibilityservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.boredream.accessibilityservice.event.OverLayCtrlEvent;
import com.boredream.accessibilityservice.event.OverlayInfoUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HelperFloatView {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private View maskView;
    private View mFloatingLayout;
    private View container;
    private TextView tvTarget;
    private TextView tvProgress;

    public HelperFloatView() {
        initWindow();
    }

    @Subscribe
    public void OnOverlayInfoUpdate(OverlayInfoUpdateEvent event) {
        if (event.getProgress() != null) {
            tvProgress.setText("当前进程：" + event.getProgress());
        }
    }

    public void addView() {
        // 添加悬浮窗的视图
        mWindowManager.addView(mFloatingLayout, wmParams);
        EventBus.getDefault().register(this);
    }

    public boolean isShown() {
        return mFloatingLayout.isShown();
    }

    public void removeView() {
        EventBus.getDefault().unregister(this);
        if (mFloatingLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatingLayout);
        }
    }

    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initWindow() {
        mWindowManager = (WindowManager) AppKeeper.getApp().getSystemService(Context.WINDOW_SERVICE);
        wmParams = getParams();//设置好悬浮窗的参数
        wmParams.x = 0;
        wmParams.y = 0;
        // 获取浮动窗口视图所在布局
        mFloatingLayout = LayoutInflater.from(AppKeeper.getApp()).inflate(R.layout.view_float, null, false);
        //悬浮框触摸事件，设置悬浮框可拖动
        container = mFloatingLayout.findViewById(R.id.container);
        tvTarget = mFloatingLayout.findViewById(R.id.tv_target);
        tvProgress = mFloatingLayout.findViewById(R.id.tv_progress);
        container.setOnTouchListener(new FloatingListener());

        mFloatingLayout.findViewById(R.id.btn_start).setOnClickListener(v ->
                EventBus.getDefault().post(new OverLayCtrlEvent("start")));

        mFloatingLayout.findViewById(R.id.btn_get_view_tree).setOnClickListener(v ->
                EventBus.getDefault().post(new OverLayCtrlEvent("getViewTree")));

        // mask
        maskView = LayoutInflater.from(AppKeeper.getApp()).inflate(R.layout.view_mask, null, false);
        TextView tvPosition = maskView.findViewById(R.id.tv_position);
        maskView.findViewById(R.id.tv_close).setOnClickListener(v -> mWindowManager.removeView(maskView));
        maskView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                tvPosition.setText(String.format("%s,%s", event.getX(), event.getY()));
            }
            return false;
        });
        mFloatingLayout.findViewById(R.id.btn_show_mask).setOnClickListener(v -> {
            if (maskView.isShown()) mWindowManager.removeView(maskView);
            else {
                WindowManager.LayoutParams params = getParams();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                mWindowManager.addView(maskView, params);
            }
        });
    }

    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置可以显示在状态栏上
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        //设置悬浮窗口长宽数据
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.alpha = 0.5f;
        return params;
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY; //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;

    public void setTask(HelperTask task) {
        tvTarget.setText("当前任务：" + task.getTarget());
    }

    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }

            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove;
        }
    }
}
