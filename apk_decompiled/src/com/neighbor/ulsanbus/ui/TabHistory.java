package com.neighbor.ulsanbus.ui;

import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TabHistory {
    private static String mCurrentTab = "tab0";
    private static HashMap<String, ArrayList<ManageChild>> mHashTab = new HashMap();
    private static TabHistory mTabhistory;

    public static class ManageChild {
        String ChildId;
        Intent ChildIntent;
    }

    public static TabHistory getInstance() {
        if (mTabhistory == null) {
            mTabhistory = new TabHistory();
            mHashTab.put("tab1", new ArrayList());
            mHashTab.put("tab2", new ArrayList());
            mHashTab.put("tab3", new ArrayList());
            mHashTab.put("tab4", new ArrayList());
            mHashTab.put("tab5", new ArrayList());
            mHashTab.put("tab6", new ArrayList());
        }
        return mTabhistory;
    }

    private static void setDefaultIntent() {
        Iterator iter = mHashTab.keySet().iterator();
        do {
            String key = (String) iter.next();
            Intent baseIntent = new Intent();
            if (key.equals("tab1")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiSearchRoute");
            } else if (key.equals("tab2")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiSearchBusStop");
            } else if (key.equals("tab3")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiFavorite");
            } else if (key.equals("tab4")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiTransferInput");
            } else if (key.equals("tab5")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiMyPosition");
            } else if (key.equals("tab6")) {
                baseIntent.setClassName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiInformation");
            }
            ArrayList<ManageChild> Tabhistory = (ArrayList) mHashTab.get(key);
            ManageChild child = new ManageChild();
            child.ChildId = "base";
            child.ChildIntent = baseIntent;
            Tabhistory.add(child);
            mHashTab.put(key, Tabhistory);
        } while (iter.hasNext());
    }

    public int getHistoryCountOfTab(String tabid) {
        return ((ArrayList) mHashTab.get(tabid)).size();
    }

    public void addHistoryInTab(String tabId, String name, Intent intent) {
        ArrayList<ManageChild> childlist = (ArrayList) mHashTab.get(tabId);
        ManageChild child = new ManageChild();
        child.ChildId = name;
        child.ChildIntent = intent;
        childlist.add(child);
        mHashTab.put(tabId, childlist);
    }

    public void changeTab(String tabTag) {
        mCurrentTab = tabTag;
    }

    public String getCurrentTab() {
        return mCurrentTab;
    }

    public String setCurTabDeleteTop() {
        return mCurrentTab;
    }

    public String getFrontId(String tabId) {
        ArrayList<ManageChild> childlist = (ArrayList) mHashTab.get(tabId);
        return ((ManageChild) childlist.get(childlist.size() - 1)).ChildId;
    }

    public void removeAllHistoryIntab(String tabId) {
        Log.d("TabHistory", "====== removeAllHistoryIntab ======");
        Log.d("TabHistory", "====== tabId ======" + tabId);
        ArrayList<ManageChild> childlist = (ArrayList) mHashTab.get(tabId);
        for (int listLen = childlist.size() - 1; listLen > 0; listLen--) {
            String childId = ((ManageChild) childlist.get(listLen)).ChildId;
            childlist.remove(listLen);
        }
    }

    public ManageChild removeHistoryIntab(String tabId) {
        ArrayList<ManageChild> childlist = (ArrayList) mHashTab.get(tabId);
        Log.d("TabHistory", "====== removeHistoryIntab ======");
        Log.d("TabHistory", "====== tabId ======" + tabId);
        int removeIdx = 0;
        try {
            if (childlist.size() > 1) {
                removeIdx = childlist.size() - 1;
                String childId = ((ManageChild) childlist.get(removeIdx)).ChildId;
                childlist.remove(removeIdx);
                removeIdx--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TabHistory", "removeHistoryIntab");
        Log.d("TabHistory", "removeIdx" + removeIdx);
        return (ManageChild) childlist.get(removeIdx);
    }
}
