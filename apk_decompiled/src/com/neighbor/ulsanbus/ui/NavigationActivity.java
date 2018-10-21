package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import com.neighbor.ulsanbus.ui.TabHistory.ManageChild;

public class NavigationActivity extends ActivityGroup {
    private static final String TAG = "DEBUG";
    public static NavigationActivity navigation;
    private InputMethodManager imm;
    private String tabTag;
    private TabHistory tabhistory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        getWindow().addFlags(128);
        this.imm = (InputMethodManager) getSystemService("input_method");
        if (this.imm.isActive()) {
            this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        navigation = this;
        this.tabTag = UiMainTab.mTabHost.getCurrentTabTag();
        this.tabhistory = TabHistory.getInstance();
    }

    public void finishFromChild(Activity child) {
        LocalActivityManager manager = getLocalActivityManager();
        this.tabTag = UiMainTab.mTabHost.getCurrentTabTag();
        manager.destroyActivity(this.tabhistory.getFrontId(this.tabTag), true);
        ManageChild restorechild = this.tabhistory.removeHistoryIntab(this.tabTag);
        Window newWindow = manager.startActivity(restorechild.ChildId, restorechild.ChildIntent.addFlags(67108864));
        try {
            setContentView(newWindow.getDecorView());
            if (this.imm.isActive()) {
                this.imm.hideSoftInputFromWindow(newWindow.getDecorView().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startChildActivity(String Id, Intent intent, Context cont) {
        Window window = getLocalActivityManager().startActivity(Id, intent.addFlags(67108864));
        if (window != null) {
            Log.d("NavigationActivity", " window != null");
            this.tabhistory.addHistoryInTab(UiMainTab.mTabHost.getCurrentTabTag(), Id, intent);
            setContentView(window.getDecorView());
            return;
        }
        Log.d("NavigationActivity", " window == null");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("NavigationActivity ", "[[[[[[    onActivityResult       ]]]]]]]");
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        String tabTag = UiMainTab.mTabHost.getCurrentTabTag();
        Log.d("NavigationActivity", "tabTag" + tabTag);
        if (this.tabhistory.getHistoryCountOfTab(tabTag) > 1) {
            finishFromChild(getLocalActivityManager().getCurrentActivity());
        } else {
            super.onBackPressed();
        }
    }
}
