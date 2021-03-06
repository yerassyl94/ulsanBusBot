package com.gun0912.tedpermission;

import android.content.Context;

public class TedPermission {
    public static final String TAG = TedPermission.class.getSimpleName();

    public static class Builder extends PermissionBuilder<Builder> {
        private Builder(Context context) {
            super(context);
        }

        public void check() {
            checkPermissions();
        }
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }
}
