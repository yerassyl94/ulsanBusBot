package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TabSecond extends NavigationActivity {
    public static TabSecond TAB2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiSearchBusStop.class);
        TAB2 = this;
        replaceActivity("uisearchbusstop", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            ((UiSearchBusStop) getLocalActivityManager().getCurrentActivity()).onActivityResult(requestCode, resultCode, data);
        }
    }
}
