package com.neighbor.ulsanbus.util;

import android.content.Context;

public interface IFNetworkConnection {
    boolean IsConnected(Context context);

    boolean IsConnected(Context context, boolean z);

    void setMobileDataEnabled(Context context, boolean z) throws Exception;
}
