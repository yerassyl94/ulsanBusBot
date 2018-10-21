package com.gun0912.tedpermission;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import com.gun0912.tedpermission.util.ObjectUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class TedPermissionActivity extends AppCompatActivity {
    public static final String EXTRA_DENIED_DIALOG_CLOSE_TEXT = "denied_dialog_close_text";
    public static final String EXTRA_DENY_MESSAGE = "deny_message";
    public static final String EXTRA_DENY_TITLE = "deny_title";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_RATIONALE_CONFIRM_TEXT = "rationale_confirm_text";
    public static final String EXTRA_RATIONALE_MESSAGE = "rationale_message";
    public static final String EXTRA_RATIONALE_TITLE = "rationale_title";
    public static final String EXTRA_SETTING_BUTTON = "setting_button";
    public static final String EXTRA_SETTING_BUTTON_TEXT = "setting_button_text";
    public static final int REQ_CODE_PERMISSION_REQUEST = 10;
    public static final int REQ_CODE_REQUEST_SETTING = 20;
    public static final int REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST = 30;
    public static final int REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING = 31;
    private static PermissionListener listener;
    String deniedCloseButtonText;
    CharSequence denyMessage;
    CharSequence denyTitle;
    boolean hasSettingButton;
    boolean isShownRationaleDialog;
    String packageName;
    String[] permissions;
    String rationaleConfirmText;
    CharSequence rationaleTitle;
    CharSequence rationale_message;
    String settingButtonText;

    /* renamed from: com.gun0912.tedpermission.TedPermissionActivity$4 */
    class C02544 implements OnClickListener {
        C02544() {
        }

        public void onClick(DialogInterface dialog, int which) {
            try {
                TedPermissionActivity.this.startActivityForResult(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.parse("package:" + TedPermissionActivity.this.packageName)), 20);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                TedPermissionActivity.this.startActivityForResult(new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS"), 20);
            }
        }
    }

    /* renamed from: com.gun0912.tedpermission.TedPermissionActivity$5 */
    class C02555 implements OnClickListener {
        C02555() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            TedPermissionActivity.this.checkPermissions(false);
        }
    }

    /* renamed from: com.gun0912.tedpermission.TedPermissionActivity$6 */
    class C02566 implements OnClickListener {
        C02566() {
        }

        @TargetApi(23)
        public void onClick(DialogInterface dialog, int which) {
            TedPermissionActivity.this.startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.fromParts("package", TedPermissionActivity.this.packageName, null)), 31);
        }
    }

    public static void startActivity(Context context, Intent intent, PermissionListener listener) {
        context.startActivity(intent);
        listener = listener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(16);
        setupFromSavedInstanceState(savedInstanceState);
        if (needWindowPermission()) {
            requestWindowPermission();
        } else {
            checkPermissions(false);
        }
    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.permissions = savedInstanceState.getStringArray(EXTRA_PERMISSIONS);
            this.rationaleTitle = savedInstanceState.getCharSequence(EXTRA_RATIONALE_TITLE);
            this.rationale_message = savedInstanceState.getCharSequence(EXTRA_RATIONALE_MESSAGE);
            this.denyTitle = savedInstanceState.getCharSequence(EXTRA_DENY_TITLE);
            this.denyMessage = savedInstanceState.getCharSequence(EXTRA_DENY_MESSAGE);
            this.packageName = savedInstanceState.getString(EXTRA_PACKAGE_NAME);
            this.hasSettingButton = savedInstanceState.getBoolean(EXTRA_SETTING_BUTTON, true);
            this.rationaleConfirmText = savedInstanceState.getString(EXTRA_RATIONALE_CONFIRM_TEXT);
            this.deniedCloseButtonText = savedInstanceState.getString(EXTRA_DENIED_DIALOG_CLOSE_TEXT);
            this.settingButtonText = savedInstanceState.getString(EXTRA_SETTING_BUTTON_TEXT);
            return;
        }
        Intent intent = getIntent();
        this.permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS);
        this.rationaleTitle = intent.getCharSequenceExtra(EXTRA_RATIONALE_TITLE);
        this.rationale_message = intent.getCharSequenceExtra(EXTRA_RATIONALE_MESSAGE);
        this.denyTitle = intent.getCharSequenceExtra(EXTRA_DENY_TITLE);
        this.denyMessage = intent.getCharSequenceExtra(EXTRA_DENY_MESSAGE);
        this.packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
        this.hasSettingButton = intent.getBooleanExtra(EXTRA_SETTING_BUTTON, true);
        this.rationaleConfirmText = intent.getStringExtra(EXTRA_RATIONALE_CONFIRM_TEXT);
        this.deniedCloseButtonText = intent.getStringExtra(EXTRA_DENIED_DIALOG_CLOSE_TEXT);
        this.settingButtonText = intent.getStringExtra(EXTRA_SETTING_BUTTON_TEXT);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(EXTRA_PERMISSIONS, this.permissions);
        outState.putCharSequence(EXTRA_RATIONALE_TITLE, this.rationaleTitle);
        outState.putCharSequence(EXTRA_RATIONALE_MESSAGE, this.rationale_message);
        outState.putCharSequence(EXTRA_DENY_TITLE, this.denyTitle);
        outState.putCharSequence(EXTRA_DENY_MESSAGE, this.denyMessage);
        outState.putString(EXTRA_PACKAGE_NAME, this.packageName);
        outState.putBoolean(EXTRA_SETTING_BUTTON, this.hasSettingButton);
        outState.putString(EXTRA_SETTING_BUTTON, this.deniedCloseButtonText);
        outState.putString(EXTRA_RATIONALE_CONFIRM_TEXT, this.rationaleConfirmText);
        outState.putString(EXTRA_SETTING_BUTTON_TEXT, this.settingButtonText);
        super.onSaveInstanceState(outState);
    }

    private void permissionResult(ArrayList<String> deniedPermissions) {
        Log.v(TedPermission.TAG, "permissionResult(): " + deniedPermissions);
        if (ObjectUtils.isEmpty(deniedPermissions)) {
            listener.onPermissionGranted();
        } else {
            listener.onPermissionDenied(deniedPermissions);
        }
        listener = null;
        finish();
        overridePendingTransition(0, 0);
    }

    private boolean needWindowPermission() {
        String[] strArr = this.permissions;
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            if (!strArr[i].equals("android.permission.SYSTEM_ALERT_WINDOW")) {
                i++;
            } else if (hasWindowPermission()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @TargetApi(23)
    private boolean hasWindowPermission() {
        return Settings.canDrawOverlays(getApplicationContext());
    }

    @TargetApi(23)
    private void requestWindowPermission() {
        final Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.fromParts("package", this.packageName, null));
        if (TextUtils.isEmpty(this.rationale_message)) {
            startActivityForResult(intent, 30);
            return;
        }
        new Builder(this, C0249R.style.Theme_AppCompat_Light_Dialog_Alert).setMessage(this.rationale_message).setCancelable(false).setNegativeButton(this.rationaleConfirmText, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                TedPermissionActivity.this.startActivityForResult(intent, 30);
            }
        }).show();
        this.isShownRationaleDialog = true;
    }

    private void checkPermissions(boolean fromOnActivityResult) {
        ArrayList<String> needPermissions = new ArrayList();
        for (String permission : this.permissions) {
            if (permission.equals("android.permission.SYSTEM_ALERT_WINDOW")) {
                if (!hasWindowPermission()) {
                    needPermissions.add(permission);
                }
            } else if (ContextCompat.checkSelfPermission(this, permission) != 0) {
                needPermissions.add(permission);
            }
        }
        if (needPermissions.isEmpty()) {
            permissionResult(null);
        } else if (fromOnActivityResult) {
            permissionResult(needPermissions);
        } else if (needPermissions.size() == 1 && needPermissions.contains("android.permission.SYSTEM_ALERT_WINDOW")) {
            permissionResult(needPermissions);
        } else if (this.isShownRationaleDialog || TextUtils.isEmpty(this.rationale_message)) {
            requestPermissions(needPermissions);
        } else {
            showRationaleDialog(needPermissions);
        }
    }

    public void requestPermissions(ArrayList<String> needPermissions) {
        ActivityCompat.requestPermissions(this, (String[]) needPermissions.toArray(new String[needPermissions.size()]), 10);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> deniedPermissions = new ArrayList();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == -1) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty()) {
            permissionResult(null);
        } else {
            showPermissionDenyDialog(deniedPermissions);
        }
    }

    private void showRationaleDialog(final ArrayList<String> needPermissions) {
        new Builder(this, C0249R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(this.rationaleTitle).setMessage(this.rationale_message).setCancelable(false).setNegativeButton(this.rationaleConfirmText, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                TedPermissionActivity.this.requestPermissions(needPermissions);
            }
        }).show();
        this.isShownRationaleDialog = true;
    }

    public void showPermissionDenyDialog(final ArrayList<String> deniedPermissions) {
        if (TextUtils.isEmpty(this.denyMessage)) {
            permissionResult(deniedPermissions);
            return;
        }
        Builder builder = new Builder(this, C0249R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(this.denyTitle).setMessage(this.denyMessage).setCancelable(false).setNegativeButton(this.deniedCloseButtonText, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                TedPermissionActivity.this.permissionResult(deniedPermissions);
            }
        });
        if (this.hasSettingButton) {
            if (TextUtils.isEmpty(this.settingButtonText)) {
                this.settingButtonText = getString(C0249R.string.tedpermission_setting);
            }
            builder.setPositiveButton(this.settingButtonText, new C02544());
        }
        builder.show();
    }

    public boolean shouldShowRequestPermissionRationale(ArrayList<String> needPermissions) {
        if (needPermissions == null) {
            return false;
        }
        Iterator it = needPermissions.iterator();
        while (it.hasNext()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, (String) it.next())) {
                return false;
            }
        }
        return true;
    }

    public void showWindowPermissionDenyDialog() {
        Builder builder = new Builder(this, C0249R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setMessage(this.denyMessage).setCancelable(false).setNegativeButton(this.deniedCloseButtonText, new C02555());
        if (this.hasSettingButton) {
            if (TextUtils.isEmpty(this.settingButtonText)) {
                this.settingButtonText = getString(C0249R.string.tedpermission_setting);
            }
            builder.setPositiveButton(this.settingButtonText, new C02566());
        }
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 20:
                checkPermissions(true);
                return;
            case 30:
                if (hasWindowPermission() || TextUtils.isEmpty(this.denyMessage)) {
                    checkPermissions(false);
                    return;
                } else {
                    showWindowPermissionDenyDialog();
                    return;
                }
            case 31:
                checkPermissions(false);
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                return;
        }
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
