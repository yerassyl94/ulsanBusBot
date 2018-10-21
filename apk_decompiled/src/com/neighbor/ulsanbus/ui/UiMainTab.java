package com.neighbor.ulsanbus.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TabHost;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;

public class UiMainTab extends TabActivity {
    public static TabHost mTabHost;
    private final String Tag = getClass().getSimpleName();
    private boolean bEmergency;
    private SharedPreferences prefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.main);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        boolean bUpdate = this.prefs.getBoolean(DataConst.KEY_PREF_UPDATE, false);
        ((HorizontalScrollView) findViewById(C0258R.id.horizontalScrollView)).setHorizontalScrollBarEnabled(false);
        mTabHost = getTabHost();
        ImageView dummy = new ImageView(this);
        dummy.setMaxHeight(0);
        dummy.setMinimumWidth(0);
        dummy.setScaleType(ScaleType.FIT_XY);
        ImageView routeiv = new ImageView(this);
        routeiv.setScaleType(ScaleType.FIT_XY);
        ImageView stopiv = new ImageView(this);
        stopiv.setScaleType(ScaleType.FIT_XY);
        ImageView favoriteiv = new ImageView(this);
        favoriteiv.setScaleType(ScaleType.FIT_XY);
        ImageView transferiv = new ImageView(this);
        transferiv.setScaleType(ScaleType.FIT_XY);
        ImageView mypostionv = new ImageView(this);
        mypostionv.setScaleType(ScaleType.FIT_XY);
        ImageView infomationiv = new ImageView(this);
        infomationiv.setScaleType(ScaleType.FIT_XY);
        if (this.bEmergency) {
            routeiv.setImageResource(C0258R.drawable.main_tab_emer_route);
            stopiv.setImageResource(C0258R.drawable.main_tab_emer_stop);
            favoriteiv.setImageResource(C0258R.drawable.main_tab_emer_favorites);
            mypostionv.setImageResource(C0258R.drawable.main_tab_emer_mylocation);
            transferiv.setImageResource(C0258R.drawable.main_tab_emer_transfer);
            infomationiv.setImageResource(C0258R.drawable.main_tab_emer_info);
        } else {
            routeiv.setImageResource(C0258R.drawable.main_tab_route);
            stopiv.setImageResource(C0258R.drawable.main_tab_stop);
            favoriteiv.setImageResource(C0258R.drawable.main_tab_favorites);
            mypostionv.setImageResource(C0258R.drawable.main_tab_mylocation);
            transferiv.setImageResource(C0258R.drawable.main_tab_transfer);
            infomationiv.setImageResource(C0258R.drawable.main_tab_info);
        }
        mTabHost.addTab(mTabHost.newTabSpec("").setIndicator(dummy).setContent(new Intent(this, BlankActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(routeiv).setContent(new Intent(this, TabFirst.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(stopiv).setContent(new Intent(this, TabSecond.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(favoriteiv).setContent(new Intent(this, TabThird.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(transferiv).setContent(new Intent(this, TabFourth.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab5").setIndicator(mypostionv).setContent(new Intent(this, TabFiveth.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab6").setIndicator(infomationiv).setContent(new Intent(this, TabSixth.class)));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        for (int tab = 1; tab < mTabHost.getTabWidget().getChildCount(); tab++) {
            mTabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = dm.widthPixels / 5;
            mTabHost.getTabWidget().getChildAt(tab).getLayoutParams().width = dm.widthPixels / 5;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("currenttap").equals("route")) {
                mTabHost.setCurrentTab(1);
            }
            if (extras.getString("currenttap").equals("stop")) {
                mTabHost.setCurrentTab(2);
            }
            if (extras.getString("currenttap").equals(DataConst.TABLE_FAVORITE_INFO)) {
                mTabHost.setCurrentTab(3);
            }
            if (extras.getString("currenttap").equals("transfer")) {
                mTabHost.setCurrentTab(4);
            }
            if (extras.getString("currenttap").equals("myloc")) {
                mTabHost.setCurrentTab(5);
            }
            if (extras.getString("currenttap").equals("info")) {
                mTabHost.setCurrentTab(6);
            }
        }
    }
}
