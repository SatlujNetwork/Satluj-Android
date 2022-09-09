package com.emobx.news.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermissions {
    public final int ALL_PERMISSIONS = 101;

    public boolean checkPermissions(Activity activity) {

        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, permissions, ALL_PERMISSIONS);
                return false;
            }
        }
        return true;
    }
}