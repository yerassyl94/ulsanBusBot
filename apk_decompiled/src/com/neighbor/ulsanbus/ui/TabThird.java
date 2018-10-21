package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TabThird extends NavigationActivity {
    public static TabThird TAB3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiFavorite.class);
        TAB3 = this;
        replaceActivity("uifavorite", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }
}
