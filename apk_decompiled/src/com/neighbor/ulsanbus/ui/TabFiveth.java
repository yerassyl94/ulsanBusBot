package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TabFiveth extends NavigationActivity {
    public static TabFiveth TAB5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiMyPosition.class);
        TAB5 = this;
        replaceActivity("uimyposition", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TabFiveth", "onActivityResult");
    }
}
