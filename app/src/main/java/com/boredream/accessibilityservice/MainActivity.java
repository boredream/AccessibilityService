package com.boredream.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.boredream.accessibilityservice.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding viewBinding;
    private List<CommonTask> taskList;
    private HelperFloatView helperFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        initView();
        initPermission();
    }

    private void initPermission() {
        String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            refreshTaskerFromFile();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            ToastUtils.showShort("权限加载 shouldShowRequestPermissionRationale " + permission);
        } else {
            requestPermissions(new String[]{permission}, 110);
        }
    }

    private void refreshTaskerFromFile() {
        taskList = new ArrayList<>();
        List<String> list = TaskFileManager.getAllTaskFile();
        for (String fileName : list) {
            taskList.add(new CommonTask(fileName));
        }
        viewBinding.spinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, R.id.tv_name, taskList));
        String log = "加载脚本: " + new Gson().toJson(list);
        ToastUtils.showShort(log);
        LogUtils.i(log);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShort("权限加载成功");
            } else {
                ToastUtils.showShort("权限加载失败");
            }
        }
    }

    private void initView() {
        helperFloatView = new HelperFloatView();
        viewBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonConst.curTask = taskList.get(position);
                helperFloatView.updateTask();
                Toast.makeText(MainActivity.this, "选择 " + CommonConst.curTask.getTaskName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewBinding.btnOverlay.setOnClickListener(v -> {
            if (helperFloatView.isShown()) helperFloatView.removeView();
            else helperFloatView.addView();
            refreshBtn();

            TaskFileManager.readFile("step.json");
        });
        viewBinding.btnRefreshTasker.setOnClickListener(v -> {
            refreshTaskerFromFile();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBtn();
    }

    private void refreshBtn() {
        // 申请使用无障碍服务
        boolean accessibilitySettingsOn = isAccessibilitySettingsOn(MyAccessibilityService.class);
        if (accessibilitySettingsOn) {
            viewBinding.btnAccessibilitySetting.setText("无障碍已开启");
            viewBinding.btnAccessibilitySetting.setEnabled(false);
        } else {
            viewBinding.btnAccessibilitySetting.setText("无障碍未开启");
            viewBinding.btnAccessibilitySetting.setEnabled(true);
            viewBinding.btnAccessibilitySetting.setOnClickListener(v -> openAccessibilitySetting());
        }

        // 申请使用悬浮球
        boolean isOverlayOpen = isOverlayOpen();
        if (isOverlayOpen) {
            viewBinding.btnOverlaySetting.setText("悬浮球权限已开启");
            viewBinding.btnOverlaySetting.setEnabled(false);
        } else {
            viewBinding.btnOverlaySetting.setText("悬浮球权限未开启");
            viewBinding.btnOverlaySetting.setEnabled(true);
            viewBinding.btnOverlaySetting.setOnClickListener(v -> openOverlaySetting());
        }

        if (helperFloatView.isShown()) {
            viewBinding.btnOverlay.setText("关闭悬浮球");
        } else {
            viewBinding.btnOverlay.setText("打开悬浮球");
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