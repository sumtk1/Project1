package com.abcaaa.photopicker.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by Dlt on 2017/3/3 13:39
 */
public class FileUtils {

    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        Log.e("TMG",path+"file not exists");
        return true;
    }
}
