package com.google.zxing.client.android.result;

import android.view.View;
import android.view.View.OnClickListener;

public final class ResultButtonListener implements OnClickListener {
    private final int index;
    private final ResultHandler resultHandler;

    public ResultButtonListener(ResultHandler resultHandler, int index) {
        this.resultHandler = resultHandler;
        this.index = index;
    }

    public void onClick(View view) {
        this.resultHandler.handleButtonPress(this.index);
    }
}
