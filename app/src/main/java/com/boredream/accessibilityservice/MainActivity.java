package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String SPEED_INFO = "操作间隔速率 %dx 倍";

    private Button btnAccessibilitySetting;
    private Button btnOverlaySetting;
    private Button btnSpeedDown;
    private TextView tvSpeedInfo;
    private Button btnSpeedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAccessibilitySetting = findViewById(R.id.btn_accessibility_setting);
        btnOverlaySetting = findViewById(R.id.btn_overlay_setting);
        btnSpeedDown = findViewById(R.id.btn_speed_down);
        tvSpeedInfo = findViewById(R.id.tv_speed_info);
        btnSpeedUp = findViewById(R.id.btn_speed_up);

        tvSpeedInfo.setText(String.format(Locale.getDefault(), SPEED_INFO, SpeedHelper.getInstance().getSpeed()));

        btnSpeedDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSpeedInfo.setText(String.format(Locale.getDefault(), SPEED_INFO, SpeedHelper.getInstance().speedDown()));
            }
        });
        btnSpeedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSpeedInfo.setText(String.format(Locale.getDefault(), SPEED_INFO, SpeedHelper.getInstance().speedUp()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSetting();
    }

    private void refreshSetting() {
        // 申请使用无障碍服务
        boolean accessibilitySettingsOn = isAccessibilitySettingsOn(MyAccessibilityService.class);
        if (accessibilitySettingsOn) {
            btnAccessibilitySetting.setText("无障碍已开启");
            btnAccessibilitySetting.setEnabled(false);
        } else {
            btnAccessibilitySetting.setText("无障碍未开启");
            btnAccessibilitySetting.setEnabled(true);
            btnAccessibilitySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAccessibilitySetting();
                }
            });
        }

        // 申请使用悬浮球
        boolean isOverlayOpen = isOverlayOpen();
        if (isOverlayOpen) {
            btnOverlaySetting.setText("悬浮球权限已开启");
            btnOverlaySetting.setEnabled(false);
            EventBus.getDefault().post(new OverlayEvent());
        } else {
            btnOverlaySetting.setText("悬浮球权限未开启");
            btnOverlaySetting.setEnabled(true);
            btnOverlaySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOverlaySetting();
                }
            });
        }
    }
    
    public boolean isAccessibilitySettingsOn(Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void openAccessibilitySetting() {
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            e.printStackTrace();
        }
    }

    public boolean isOverlayOpen() {
        return Settings.canDrawOverlays(this);
    }

    public void openOverlaySetting() {
        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
    }
}