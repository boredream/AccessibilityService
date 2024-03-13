package com.boredream.accessibilityservice;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class HelperFloatView extends FrameLayout {

   private WindowManager mWindowManager;
   private WindowManager.LayoutParams wmParams;
   private View mFloatingLayout;
   private View container;

   public HelperFloatView(@NonNull Context context) {
      this(context, null);
   }

   public HelperFloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public HelperFloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      this(context, attrs, defStyleAttr, 0);
   }

   public HelperFloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      initWindow();
   }

   public void addView() {
      // 添加悬浮窗的视图
      mWindowManager.addView(mFloatingLayout, wmParams);
   }

   public void removeView(){
      if (mFloatingLayout != null) {
         // 移除悬浮窗口
         mWindowManager.removeView(mFloatingLayout);
      }
   }

   /**
    * 设置悬浮框基本参数（位置、宽高等）
    */
   private void initWindow() {
      mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
      wmParams = getParams();//设置好悬浮窗的参数
      // 悬浮窗默认显示以左上角为起始坐标
      wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
      //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
      wmParams.x = 0;
      wmParams.y = 0;
      // 获取浮动窗口视图所在布局
      mFloatingLayout = LayoutInflater.from(getContext()).inflate(R.layout.view_float, this, false);
      //悬浮框触摸事件，设置悬浮框可拖动
      container = mFloatingLayout.findViewById(R.id.container);
      container.setOnTouchListener(new FloatingListener());

      mFloatingLayout.findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
            EventBus.getDefault().post(new OverLayCtrlEvent("start"));
         }
      });

      mFloatingLayout.findViewById(R.id.btn_get_view_tree).setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
            EventBus.getDefault().post(new OverLayCtrlEvent("getViewTree"));
         }
      });
   }

   private WindowManager.LayoutParams getParams() {
      wmParams = new WindowManager.LayoutParams();
      //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
      //wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
      //wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
      if (Build.VERSION.SDK_INT>=26) {//8.0新特性
         wmParams.type= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
      }else{
         wmParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
      }
      //设置可以显示在状态栏上
      wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
              WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
              WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

      //设置悬浮窗口长宽数据
      wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
      wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
      wmParams.alpha = 0.5f;
      return wmParams;
   }

   //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
   private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
   //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
   private int mStartX, mStartY, mStopX, mStopY; //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
   private boolean isMove;

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
