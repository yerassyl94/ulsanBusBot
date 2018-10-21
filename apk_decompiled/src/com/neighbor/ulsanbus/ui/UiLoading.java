package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Database;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.Action;
import com.neighbor.ulsanbus.network.ParseVersion;
import com.neighbor.ulsanbus.network.ParseXML;
import com.neighbor.ulsanbus.network.ProcessBus;
import com.neighbor.ulsanbus.network.ProcessNotice;
import com.neighbor.ulsanbus.network.ProcessStop;
import com.neighbor.ulsanbus.network.ProcessTCard;
import com.neighbor.ulsanbus.util.PreferenceManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.xmlpull.v1.XmlPullParserException;

public class UiLoading extends Activity implements OnClickListener {
    private static final int CONNECT_ERR = -2;
    private static final String GET_NOTICE = "NOTICE";
    private static final String GET_ROUTE = "ROUTE";
    private static final String GET_STOP = "STOP";
    private static final String GET_TCARD = "Tcard";
    private static final int PARSE_ERR = -1;
    private static final int REQUEST_MAX = 4;
    private static final int RESULT_0K = 0;
    private static boolean bLoad = false;
    private final long FINSH_INTERVAL_TIME = 2000;
    private boolean bEmergency;
    private long backPressedTime = 0;
    private ImageView background;
    private Handler hDataHandler = new C02812();
    private Handler hVersion = new C02801();
    private ProgressBar loadingProgress;
    String mAction;
    private LinearLayout mButtonLayout;
    private SQLiteDatabase mDb;
    int mElipse = 0;
    ProgressBar mLoading;
    ParseXML mParse;
    private TextView mProgText;
    Queue<String> mTodo;
    private boolean mUpdateNotice = false;
    private Database mVerion;
    private Database mVersionList;
    private SharedPreferences prefs;
    int processCnt = 0;
    private LinearLayout proglayout;
    SyncVersion syncVersion;

    /* renamed from: com.neighbor.ulsanbus.ui.UiLoading$1 */
    class C02801 extends Handler {
        C02801() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -2:
                    UiLoading.this.proglayout.setVisibility(8);
                    UiLoading.this.dispError(UiLoading.this.getResources().getString(C0258R.string.connect_error));
                    return;
                case -1:
                    UiLoading.this.proglayout.setVisibility(8);
                    UiLoading.this.dispError(UiLoading.this.getResources().getString(C0258R.string.message_error));
                    return;
                case 0:
                    UiLoading.this.mElipse = UiLoading.this.compare();
                    if (UiLoading.this.mElipse > 0) {
                        UiLoading.this.proglayout.setVisibility(0);
                        UiLoading.this.doAction();
                        return;
                    }
                    UiLoading.this.proglayout.setVisibility(8);
                    UiLoading.this.visibleButton();
                    UiLoading.this.checkPermission();
                    UiLoading.bLoad = true;
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiLoading$2 */
    class C02812 extends Handler {
        C02812() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -2:
                    UiLoading.this.dispError(UiLoading.this.getResources().getString(C0258R.string.connect_error));
                    return;
                case -1:
                    UiLoading.this.dispError(UiLoading.this.getResources().getString(C0258R.string.connect_error));
                    Log.e("handleMessage: ", "2");
                    return;
                case 0:
                    UiLoading.this.doAction();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiLoading$3 */
    class C02823 implements DialogInterface.OnClickListener {
        C02823() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            UiLoading.this.finish();
        }
    }

    private class Process extends AsyncTask<Object, Object, Object> {
        Context ctx = null;
        Action mAct;
        int mOrder;
        String mode = null;

        public Process(String sort, Context context, int order) {
            this.mode = sort;
            this.ctx = context;
            this.mOrder = order;
        }

        protected Object doInBackground(Object[] params) {
            if (this.mode.equals(UiLoading.GET_STOP)) {
                this.mAct = new ProcessStop(this.ctx);
            } else if (this.mode.equals(UiLoading.GET_ROUTE)) {
                this.mAct = new ProcessBus(this.ctx);
            } else if (this.mode.equals(UiLoading.GET_TCARD)) {
                this.mAct = new ProcessTCard(this.ctx);
            } else if (this.mode.equals(UiLoading.GET_NOTICE)) {
                this.mAct = new ProcessNotice(this.ctx);
            }
            try {
                this.mAct.parse();
                this.mAct.save();
                int steps = 100 / UiLoading.this.mElipse;
                int start = (this.mOrder - 1) * steps;
                for (int i = start; i < start + steps; i++) {
                    publishProgress(new Object[]{Integer.valueOf(i)});
                }
                UiLoading.this.hDataHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                Log.e("UiLoading", "" + e.toString());
                UiLoading.this.hDataHandler.sendEmptyMessage(-1);
            }
            return null;
        }

        protected void onProgressUpdate(Object[] values) {
            UiLoading.this.loadingProgress.setProgress(((Integer) values[0]).intValue());
            UiLoading.this.mProgText.setText(Integer.toString(((Integer) values[0]).intValue()) + " %");
            Log.e("aaa", "progressupdate");
        }

        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("aaa", "post");
        }
    }

    private class SyncVersion extends Thread {
        private SyncVersion() {
        }

        public void run() {
            UiLoading.this.mParse = new ParseVersion();
            try {
                UiLoading.this.mParse.parse();
                UiLoading.this.mVerion = (Database) UiLoading.this.mParse.getObject();
                UiLoading.this.hVersion.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                UiLoading.this.hVersion.sendEmptyMessage(-1);
            } catch (IOException e2) {
                UiLoading.this.hVersion.sendEmptyMessage(-2);
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiLoading$4 */
    class C04404 implements PermissionListener {
        C04404() {
        }

        public void onPermissionGranted() {
        }

        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            if (deniedPermissions != null && deniedPermissions.size() != 0) {
                int i = 0;
                while (i < deniedPermissions.size()) {
                    if (((String) deniedPermissions.get(i)).equals("android.permission.ACCESS_FINE_LOCATION") || ((String) deniedPermissions.get(i)).equals("android.permission.ACCESS_COARSE_LOCATION")) {
                        Toast.makeText(UiLoading.this, "위치 권한을 거부하여 앱이 종료합니다.", 0).show();
                        UiLoading.this.finish();
                    } else if (((String) deniedPermissions.get(i)).equals("android.permission.READ_PHONE_STATE")) {
                        PreferenceManager.getInstance().setStringPreference(UiLoading.this, DataConst.PHONESTATE_PERMISSION, DataConst.PHONESTATE_DENYED);
                    }
                    i++;
                }
            }
        }
    }

    protected void onStart() {
        super.onStart();
        try {
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_DB_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (true) {
                    this.mVersionList.setTCardStore(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_TCARDSTORE)));
                    this.mVersionList.setRoute_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_ROUTE)));
                    this.mVersionList.setNotice_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_NOTICE)));
                    this.mVersionList.setStop_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_STOP)));
                    this.mVersionList.setTime_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_TIME)));
                    this.mVersionList.setEmergency_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_EMERGENCY)));
                    this.mVersionList.setEmergency_Mode(cursor.getString(cursor.getColumnIndex(DataConst.KEY_EMERGENCY_MODE)));
                    this.mVersionList.setInternal_Notice(cursor.getInt(cursor.getColumnIndex(DataConst.KEY_VERSION_NOTICE_INTER)));
                    this.mVersionList.setInternal_TcardStore(cursor.getInt(cursor.getColumnIndex(DataConst.KEY_VERSION_TCARDSTORE_INTER)));
                    DataConst.DATABASE_ROUTE_VER = Integer.parseInt(this.mVersionList.getRoute_Ver());
                    DataConst.DATABASE_STOP_VER = Integer.parseInt(this.mVersionList.getStop_Ver());
                    DataConst.DATABASE_NOTICE_VER = this.mVersionList.getInternal_Notice();
                    DataConst.DATABASE_STORE_VER = this.mVersionList.getInternal_TcardStore();
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
            cursor.close();
        } catch (NullPointerException e) {
            Log.e("UiLoading", "onStart" + e.getMessage());
        } finally {
            this.syncVersion = new SyncVersion();
            this.syncVersion.start();
        }
    }

    int compare() {
        boolean z = true;
        int request = 0;
        try {
            Editor editor = this.prefs.edit();
            if (this.mVerion.getTCardStore().compareTo(this.mVersionList.getTCardStore()) != 0) {
                this.mTodo.offer(GET_TCARD);
                request = 0 + 1;
            }
            Log.e("aa", "compare: " + this.mVerion.getRoute_Ver());
            if (this.prefs.getBoolean("isFirstRun", true) || this.mVerion.getRoute_Ver().compareTo(this.mVersionList.getRoute_Ver()) != 0 || this.prefs.getBoolean(DataConst.IS_COLUMN_ADDED, true)) {
                editor.putBoolean(DataConst.IS_COLUMN_ADDED, false);
                editor.putBoolean("isFirstRun", false);
                this.mTodo.offer(GET_ROUTE);
                request++;
            }
            if (this.mVerion.getStop_Ver().compareTo(this.mVersionList.getStop_Ver()) != 0) {
                this.mTodo.offer(GET_STOP);
                request++;
            }
            if (this.mVerion.getNotice_Ver().compareTo(this.mVersionList.getNotice_Ver()) != 0) {
                this.mTodo.offer(GET_NOTICE);
                this.mUpdateNotice = true;
                request++;
            }
            String emode = this.mVerion.getEmergency_Mode();
            String str = DataConst.KEY_PREF_EMER_MODE;
            if (!emode.equals("1")) {
                z = false;
            }
            editor.putBoolean(str, z);
            editor.commit();
            return request;
        } catch (NullPointerException e) {
            updateAll();
            return 4;
        }
    }

    void updateAll() {
        this.mTodo.offer(GET_TCARD);
        this.mTodo.offer(GET_ROUTE);
        this.mTodo.offer(GET_STOP);
        this.mTodo.offer(GET_NOTICE);
        this.mUpdateNotice = true;
    }

    void doAction() {
        if (this.mTodo.peek() != null) {
            this.mAction = (String) this.mTodo.poll();
        } else {
            this.mAction = "";
        }
        if (this.mAction.equals("")) {
            getContentResolver().delete(DataConst.CONTENT_DB_URI, null, null);
            ContentValues cv = new ContentValues();
            cv.put(DataConst.KEY_VERSION_TCARDSTORE, this.mVerion.getTCardStore());
            cv.put(DataConst.KEY_VERSION_ROUTE, this.mVerion.getRoute_Ver());
            cv.put(DataConst.KEY_VERSION_NOTICE, this.mVerion.getNotice_Ver());
            cv.put(DataConst.KEY_VERSION_STOP, this.mVerion.getStop_Ver());
            cv.put(DataConst.KEY_VERSION_TIME, this.mVerion.getTime_Ver());
            cv.put(DataConst.KEY_VERSION_EMERGENCY, this.mVerion.getEmergency_Ver());
            cv.put(DataConst.KEY_EMERGENCY_MODE, this.mVerion.getEmergency_Mode());
            cv.put(DataConst.KEY_VERSION_NOTICE_INTER, Integer.valueOf(DataConst.DATABASE_NOTICE_VER));
            cv.put(DataConst.KEY_VERSION_TCARDSTORE_INTER, Integer.valueOf(DataConst.DATABASE_STORE_VER));
            getContentResolver().insert(DataConst.CONTENT_DB_URI, cv);
            if (this.mUpdateNotice) {
                this.proglayout.setVisibility(8);
                visibleButton();
                startActivityForResult(new Intent(this, UiNoticePopup.class), 100);
            } else {
                this.proglayout.setVisibility(8);
                visibleButton();
                checkPermission();
            }
            bLoad = true;
            return;
        }
        this.processCnt++;
        new Process(this.mAction, this, this.processCnt).execute(new Object[0]);
    }

    void dispError(String message) {
        Builder alert_confirm = new Builder(this);
        alert_confirm.setMessage(message).setCancelable(false).setNeutralButton(getResources().getString(C0258R.string.ok), new C02823());
        alert_confirm.create().show();
    }

    void init() {
        this.background = (ImageView) findViewById(C0258R.id.loading_backgroud);
        this.mButtonLayout = (LinearLayout) findViewById(C0258R.id.loading_button_layout);
        this.mButtonLayout.setVisibility(8);
        this.loadingProgress = (ProgressBar) findViewById(C0258R.id.loading_progbar);
        this.proglayout = (LinearLayout) findViewById(C0258R.id.loading_prog_layout);
        this.mProgText = (TextView) findViewById(C0258R.id.loading_per_text);
        ImageButton transferbutton = (ImageButton) findViewById(C0258R.id.loading_transfer_search_button);
        ImageButton mylocbutton = (ImageButton) findViewById(C0258R.id.loading_mylocation_search_button);
        ImageButton routebutton = (ImageButton) findViewById(C0258R.id.loading_route_search_button);
        ImageButton favoritebutton = (ImageButton) findViewById(C0258R.id.loading_favorite_search_button);
        ImageButton infobutton = (ImageButton) findViewById(C0258R.id.loading_info_button);
        ((ImageButton) findViewById(C0258R.id.loading_stop_search_button)).setOnClickListener(this);
        transferbutton.setOnClickListener(this);
        mylocbutton.setOnClickListener(this);
        routebutton.setOnClickListener(this);
        favoritebutton.setOnClickListener(this);
        infobutton.setOnClickListener(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.loading);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.mVersionList = new Database();
        this.mTodo = new LinkedList();
        init();
    }

    private void visibleButton() {
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        if (this.bEmergency) {
            this.background.setImageResource(C0258R.drawable.android_main_emer);
        } else {
            this.background.setImageResource(C0258R.drawable.android_main);
        }
        this.mButtonLayout.setVisibility(0);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, UiMainTab.class);
        switch (view.getId()) {
            case C0258R.id.loading_favorite_search_button:
                intent.putExtra("currenttap", DataConst.TABLE_FAVORITE_INFO);
                break;
            case C0258R.id.loading_info_button:
                intent.putExtra("currenttap", "info");
                break;
            case C0258R.id.loading_mylocation_search_button:
                intent.putExtra("currenttap", "myloc");
                break;
            case C0258R.id.loading_route_search_button:
                intent.putExtra("currenttap", "route");
                break;
            case C0258R.id.loading_stop_search_button:
                intent.putExtra("currenttap", "stop");
                break;
            case C0258R.id.loading_transfer_search_button:
                intent.putExtra("currenttap", "transfer");
                break;
        }
        startActivity(intent);
    }

    public void onBackPressed() {
        if (bLoad) {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - this.backPressedTime;
            if (0 > intervalTime || 2000 < intervalTime) {
                this.backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), getResources().getString(C0258R.string.loading_end), 0).show();
                return;
            }
            bLoad = false;
            super.onBackPressed();
        }
    }

    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == -1) {
            PermissionListener locationPerListener = new C04404();
            if (PreferenceManager.getInstance().getStringPreference(this, DataConst.PHONESTATE_PERMISSION).equals("")) {
                ((TedPermission.Builder) ((TedPermission.Builder) TedPermission.with(this).setPermissionListener(locationPerListener)).setPermissions("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE")).check();
                return;
            }
            ((TedPermission.Builder) ((TedPermission.Builder) TedPermission.with(this).setPermissionListener(locationPerListener)).setPermissions("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")).check();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            checkPermission();
        }
    }
}
