package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TabFirst extends NavigationActivity {
    public static TabFirst TAB1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiSearchRoute.class);
        TAB1 = this;
        replaceActivity("uisearchroute", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }
}
