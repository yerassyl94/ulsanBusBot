package com.neighbor.ulsanbus.util;

import com.neighbor.ulsanbus.ui.UiMainTab;

public class Util {
    public static int getCurrentTabNum() {
        return UiMainTab.mTabHost.getCurrentTab();
    }
}
