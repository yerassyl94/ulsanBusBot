package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TabSixth extends NavigationActivity {
    public static TabSixth TAB6;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiInformation.class);
        TAB6 = this;
        replaceActivity("uiinformation", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }
}
