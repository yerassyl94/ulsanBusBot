package com.google.zxing.client.android.share;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import com.kakao.util.maps.helper.CommonProtocol;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class LoadPackagesAsyncTask extends AsyncTask<List<String[]>, Void, List<String[]>> {
    private static final String[] PKG_PREFIX_BLACKLIST = new String[]{"com.android.", CommonProtocol.OS_ANDROID, "com.google.android.", "com.htc"};
    private static final String[] PKG_PREFIX_WHITELIST = new String[]{"com.google.android.apps."};
    private final AppPickerActivity activity;

    private static class ByFirstStringComparator implements Comparator<String[]>, Serializable {
        private ByFirstStringComparator() {
        }

        public int compare(String[] o1, String[] o2) {
            return o1[0].compareTo(o2[0]);
        }
    }

    LoadPackagesAsyncTask(AppPickerActivity activity) {
        this.activity = activity;
    }

    protected List<String[]> doInBackground(List<String[]>... objects) {
        List<String[]> labelsPackages = objects[0];
        PackageManager packageManager = this.activity.getPackageManager();
        for (ApplicationInfo appInfo : packageManager.getInstalledApplications(0)) {
            if (appInfo.loadLabel(packageManager) != null) {
                if (!isHidden(appInfo.packageName)) {
                    labelsPackages.add(new String[]{label.toString(), appInfo.packageName});
                }
            }
        }
        Collections.sort(labelsPackages, new ByFirstStringComparator());
        return labelsPackages;
    }

    private static boolean isHidden(String packageName) {
        if (packageName == null) {
            return true;
        }
        for (String prefix : PKG_PREFIX_WHITELIST) {
            if (packageName.startsWith(prefix)) {
                return false;
            }
        }
        for (String prefix2 : PKG_PREFIX_BLACKLIST) {
            if (packageName.startsWith(prefix2)) {
                return true;
            }
        }
        return false;
    }

    protected synchronized void onPostExecute(List<String[]> results) {
        List<String> labels = new ArrayList(results.size());
        for (String[] result : results) {
            labels.add(result[0]);
        }
        this.activity.setListAdapter(new ArrayAdapter(this.activity, 17367043, labels));
    }
}
