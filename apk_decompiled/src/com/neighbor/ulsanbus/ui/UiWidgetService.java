package com.neighbor.ulsanbus.ui;

import android.content.Intent;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class UiWidgetService extends RemoteViewsService {
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new UiWidgetListProvider(getApplicationContext(), intent);
    }
}
