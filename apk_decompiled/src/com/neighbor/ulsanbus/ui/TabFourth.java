package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TabFourth extends NavigationActivity {
    public static TabFourth TAB4;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, UiTransferInput.class);
        TAB4 = this;
        replaceActivity("uisearchroute", intent, this);
    }

    public void replaceActivity(String id, Intent intent, Context context) {
        startChildActivity(id, intent, context);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 || requestCode == 6) {
            ((UiTransferInput) getLocalActivityManager().getCurrentActivity()).onActivityResult(requestCode, resultCode, data);
        }
    }
}
