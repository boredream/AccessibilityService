package com.boredream.accessibilityservice;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskFileManager {

    /** @noinspection ResultOfMethodCallIgnored*/
    public static File getDir() {
        String path = PathUtils.getExternalAppFilesPath();
        File dir = new File(path, "tasker");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String readFile(String fileName) {
        File file = new File(getDir(), fileName);
        String content = FileIOUtils.readFile2String(file);
        LogUtils.i("file, path = " + file.getAbsolutePath() + ", content = " + content);
        return content;
    }

    public static List<String> getAllTaskFile() {
        List<String> list = new ArrayList<>();
        File[] files = getDir().listFiles();
        if(files != null) {
            for (File file : files) {
                list.add(file.getName());
            }
        }
        return list;
    }

}
