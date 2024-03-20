package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.boredream.accessibilityservice.event.OverlayEvent;
import com.boredream.accessibilityservice.helper.WxBreadHelper;
import com.boredream.accessibilityservice.helper.WxWrnHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button btnAccessibilitySetting;
    private Button btnOverlaySetting;
    private AppCompatSpinner spinner;
    private List<HelperTask> targetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAccessibilitySetting = findViewById(R.id.btn_accessibility_setting);
        btnOverlaySetting = findViewById(R.id.btn_overlay_setting);
        spinner = findViewById(R.id.spinner);
        targetList = Arrays.asList(
                new HelperTask(CommonConst.TARGET_WX_BREAD_FREE, WxBreadHelper.class),
                new HelperTask(CommonConst.TARGET_WX_BREAD_FREE, WxWrnHelper.class)
        );
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, R.id.tv_name, targetList));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonConst.setTarget(targetList.get(position));
                Toast.makeText(MainActivity.this, "选择 " + CommonConst.getTarget(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            btnAccessibilitySetting.setOnClickListener(v -> openAccessibilitySetting());
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
            btnOverlaySetting.setOnClickListener(v -> openOverlaySetting());
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