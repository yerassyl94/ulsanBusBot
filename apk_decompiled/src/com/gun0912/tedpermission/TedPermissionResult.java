package com.gun0912.tedpermission;

import com.gun0912.tedpermission.util.ObjectUtils;
import java.util.ArrayList;

public class TedPermissionResult {
    private ArrayList<String> deniedPermissions;
    private boolean granted;

    public TedPermissionResult(ArrayList<String> deniedPermissions) {
        this.granted = ObjectUtils.isEmpty(deniedPermissions);
        this.deniedPermissions = deniedPermissions;
    }

    public boolean isGranted() {
        return this.granted;
    }

    public ArrayList<String> getDeniedPermissions() {
        return this.deniedPermissions;
    }
}
