package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import com.google.zxing.client.android.Intents.Scan;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Stop;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class UiSearchBusStop extends Activity implements OnItemClickListener, TextWatcher {
    private static final int RECENTLIST_DELETE_REQUEST = 11;
    private static final String SCAN_INTENT = "com.neighbor.ulsanbus.SCAN";
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    private static final int ZBAR_SCANNER_REQUEST = 0;
    private String[] StopProjection = new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", DataConst.KEY_STOP_LIMOUSINE, DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y, "stop_remark"};
    private String Tag = getClass().getSimpleName();
    private boolean bEmergency;
    private Cursor cursor;
    private InputMethodManager imm;
    private LinearLayout mBackGround;
    private LinearLayout mBgImage;
    private LinearLayout mEditInputbg;
    private TextView mLastSearch;
    private ListView mListView;
    private ArrayList<RecentStopInfo> mRecentArray;
    private ListView mRecentlist;
    private ArrayList<String> mSearchlist;
    private StopListAdapter mStopListAdapter;
    private EditText mStopNameEditText;
    private SharedPreferences prefs;

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$1 */
    class C03061 implements OnClickListener {
        C03061() {
        }

        public void onClick(View v) {
            if (UiSearchBusStop.this.imm.isActive()) {
                UiSearchBusStop.this.imm.hideSoftInputFromWindow(UiSearchBusStop.this.getWindow().getDecorView().getWindowToken(), 0);
            }
            UiSearchBusStop.this.mStopNameEditText.setText("", BufferType.EDITABLE);
            Intent intent = new Intent(UiSearchBusStop.this, UiBusStopPosition.class);
            intent.putExtra("stop_name", UiSearchBusStop.this.mStopNameEditText.getText().toString());
            intent.putExtra("stop_id", "");
            TabSecond.TAB2.replaceActivity("BusStop", intent, UiSearchBusStop.this);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$4 */
    class C03084 implements OnItemClickListener {
        C03084() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            RecentStopInfo recentStop = (RecentStopInfo) UiSearchBusStop.this.mRecentArray.get(arg2);
            Intent intent = new Intent(UiSearchBusStop.this, UiStopInformation.class);
            intent.putExtra("stop_id", recentStop.stopId);
            intent.putExtra("stop_name", recentStop.stopName);
            intent.putExtra("stop_coord_x", recentStop.stopX);
            intent.putExtra("stop_coord_y", recentStop.stopY);
            intent.putExtra("stop_remark", recentStop.stopRemark);
            if (UiSearchBusStop.this.imm.isActive()) {
                UiSearchBusStop.this.imm.hideSoftInputFromWindow(UiSearchBusStop.this.getWindow().getDecorView().getWindowToken(), 0);
            }
            UiSearchBusStop.this.StartNewActivity(intent);
            UiSearchBusStop.this.mStopNameEditText.setText("", BufferType.EDITABLE);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$5 */
    class C03115 implements OnItemLongClickListener {

        /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$5$1 */
        class C03091 implements DialogInterface.OnClickListener {
            C03091() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }

        C03115() {
        }

        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
            if (UiSearchBusStop.this.mStopNameEditText.length() < 1 && arg0.getAdapter().getCount() > 0) {
                new Builder(UiSearchBusStop.this.getParent()).setMessage(UiSearchBusStop.this.getResources().getString(C0258R.string.search_root_textview_recent_delete)).setPositiveButton(UiSearchBusStop.this.getResources().getString(C0258R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UiSearchBusStop.this.getContentResolver().delete(DataConst.CONTENT_STOP_SEARCH_URI, "search_stop_id = " + ((RecentStopInfo) UiSearchBusStop.this.mRecentArray.get(arg2)).stopId, null);
                        UiSearchBusStop.this.getConditionOfStopList();
                        dialog.dismiss();
                    }
                }).setNegativeButton(UiSearchBusStop.this.getResources().getString(C0258R.string.cancel), new C03091()).show();
            }
            return true;
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$6 */
    class C03126 implements OnMenuItemClickListener {
        C03126() {
        }

        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(UiSearchBusStop.this, UiDeletePopUp.class);
            intent.putExtra("delete_mode", "search_stop");
            UiSearchBusStop.this.startActivityForResult(intent, 11);
            return true;
        }
    }

    class RecentSeachAdapter extends ArrayAdapter<RecentStopInfo> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<RecentStopInfo> mRecentlist = new ArrayList();

        public RecentSeachAdapter(Context context, int resource, int textViewResourceId, List<RecentStopInfo> objects) {
            super(context, resource, textViewResourceId, objects);
            this.mRecentlist = objects;
            this.mContext = context;
        }

        public void add(RecentStopInfo object) {
            super.add(object);
        }

        public void addAll(RecentStopInfo... items) {
            super.addAll(items);
        }

        public void clear() {
            super.clear();
        }

        public RecentStopInfo getItem(int position) {
            return (RecentStopInfo) super.getItem(position);
        }

        public int getPosition(RecentStopInfo item) {
            return super.getPosition(item);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.search_bus_stop_item, parent, false);
            }
            RecentStopInfo recentStopItem = (RecentStopInfo) this.mRecentlist.get(position);
            RelativeLayout row = (RelativeLayout) v.findViewById(C0258R.id.search_bus_stop_row);
            ImageView mStopImageView = (ImageView) v.findViewById(C0258R.id.search_bus_stop_limusine);
            TextView mName = (TextView) v.findViewById(C0258R.id.search_bus_stop_item_name);
            TextView mId = (TextView) v.findViewById(C0258R.id.search_bus_stop_item_id);
            if (UiSearchBusStop.this.bEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            if (recentStopItem.stopLimusine.equals("1")) {
                mStopImageView.setBackgroundResource(C0258R.drawable.icon_bus_stop_limousine);
                mStopImageView.setVisibility(0);
            } else {
                mStopImageView.setVisibility(4);
            }
            String mStopName = recentStopItem.stopName;
            String mDestName = recentStopItem.stopRemark;
            StringBuffer sb = new StringBuffer();
            sb.append(mStopName);
            if (!(mDestName == null || mDestName.equals(""))) {
                sb.append(" ( ");
                sb.append(mDestName);
                sb.append(" )");
            }
            String mStopId = recentStopItem.stopId;
            if (mStopId != null) {
                mName.setText(sb.toString());
                mId.setText(mStopId);
            }
            return v;
        }
    }

    private class RecentStopInfo {
        String stopId;
        String stopLimusine;
        String stopName;
        String stopRemark;
        String stopX;
        String stopY;

        private RecentStopInfo() {
        }
    }

    class StopListAdapter extends CursorAdapter {
        private Cursor cursor;
        private LayoutInflater mInflater;

        public int getCount() {
            return getCursor().getCount();
        }

        public Object getItem(int position) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            Stop stopInfo = new Stop();
            stopInfo.setStopID(cursor.getString(cursor.getColumnIndex("stop_id")));
            stopInfo.setStopLimousine(cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE)));
            stopInfo.setStopName(cursor.getString(cursor.getColumnIndex("stop_name")));
            stopInfo.setStopX(cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_X)));
            stopInfo.setStopY(cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_Y)));
            stopInfo.setStopRemark(cursor.getString(cursor.getColumnIndex("stop_remark")));
            return stopInfo;
        }

        public StopListAdapter(Context context, Cursor c) {
            super(context, c);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.cursor = c;
        }

        public void bindView(View view, Context context, Cursor cursor) {
            RelativeLayout row = (RelativeLayout) view.findViewById(C0258R.id.search_bus_stop_row);
            ImageView mStopImageView = (ImageView) view.findViewById(C0258R.id.search_bus_stop_limusine);
            TextView mName = (TextView) view.findViewById(C0258R.id.search_bus_stop_item_name);
            TextView mId = (TextView) view.findViewById(C0258R.id.search_bus_stop_item_id);
            if (UiSearchBusStop.this.bEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            if (this.cursor != null && this.cursor.getCount() > 0) {
                if (cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE)).equals("1")) {
                    mStopImageView.setBackgroundResource(C0258R.drawable.icon_bus_stop_limousine);
                    mStopImageView.setVisibility(0);
                } else {
                    mStopImageView.setVisibility(4);
                }
                String mStopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                String mDestName = cursor.getString(cursor.getColumnIndex("stop_remark"));
                StringBuffer sb = new StringBuffer();
                sb.append(mStopName);
                if (!mDestName.equals("")) {
                    sb.append(" ( ");
                    sb.append(mDestName);
                    sb.append(" )");
                }
                if (cursor.getString(cursor.getColumnIndex("stop_id")) != null) {
                    mName.setText(sb.toString());
                    mId.setText(cursor.getString(cursor.getColumnIndex("stop_id")));
                }
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.search_bus_stop_item, parent, false);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchBusStop$2 */
    class C04442 implements PermissionListener {
        C04442() {
        }

        public void onPermissionGranted() {
            UiSearchBusStop.this.mStopNameEditText.setText("", BufferType.EDITABLE);
            if (UiSearchBusStop.this.imm.isActive()) {
                UiSearchBusStop.this.imm.hideSoftInputFromWindow(UiSearchBusStop.this.getWindow().getDecorView().getWindowToken(), 0);
            }
            if (UiSearchBusStop.this.isCameraAvailable()) {
                Intent intentScan = new Intent("com.neighbor.ulsanbus.SCAN");
                intentScan.addCategory("android.intent.category.DEFAULT");
                UiSearchBusStop.this.getParent().startActivityForResult(intentScan, 1);
                return;
            }
            Toast.makeText(UiSearchBusStop.this, "Rear Facing Camera Unavailable", 0).show();
        }

        public void onPermissionDenied(ArrayList<String> arrayList) {
            Toast.makeText(UiSearchBusStop.this, "카메라 권한을 거부하면 이용하실 수 없습니다.", 0).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            String result = data.getStringExtra(Scan.RESULT);
            Log.d("UiSearchBusStop", "[[ UrlData" + result);
            String stopId = result.substring(result.length() - 5).trim();
            String Stopresult = "";
            for (int i = 0; i < stopId.length(); i++) {
                if (Character.isDigit(stopId.charAt(i))) {
                    Stopresult = Stopresult + stopId.charAt(i);
                }
            }
            if (Stopresult.equals(stopId)) {
                Cursor cursor = getContentResolver().query(DataConst.CONTENT_STOP_SEARCH_URI, null, "search_stop_id = " + stopId, null, null);
                if (cursor.getCount() == 0) {
                    cursor.close();
                    ContentValues value = new ContentValues();
                    value.put(DataConst.KEY_SEARCH_STOP_ID, stopId);
                    value.put(DataConst.KEY_SEARCHED_MILLI, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
                    getContentResolver().insert(DataConst.CONTENT_STOP_SEARCH_URI, value);
                }
                this.mStopNameEditText.setText("", BufferType.EDITABLE);
                if (this.imm.isActive()) {
                    this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                Cursor stopCursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, null, "stop_id = " + stopId, null, null);
                if (stopCursor.getCount() > 0 && stopCursor.moveToFirst()) {
                    Intent intent = new Intent(this, UiStopInformation.class);
                    String stop_id = stopCursor.getString(stopCursor.getColumnIndex("stop_id"));
                    String stop_name = stopCursor.getString(stopCursor.getColumnIndex("stop_name"));
                    String stop_x = stopCursor.getString(stopCursor.getColumnIndex(DataConst.KEY_STOP_X));
                    String stop_y = stopCursor.getString(stopCursor.getColumnIndex(DataConst.KEY_STOP_Y));
                    String stop_remark = stopCursor.getString(stopCursor.getColumnIndex("stop_remark"));
                    intent.putExtra("stop_id", stop_id);
                    intent.putExtra("stop_name", stop_name);
                    intent.putExtra("stop_coord_x", stop_x);
                    intent.putExtra("stop_coord_y", stop_y);
                    intent.putExtra("stop_remark", stop_remark);
                    StartNewActivity(intent);
                }
                stopCursor.close();
                return;
            }
            Toast.makeText(this, "정류장 코드가 올바르지 않습니다.", 0).show();
        }
    }

    public boolean isCameraAvailable() {
        return getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    private void getConditionOfStopList() {
        ArrayList<String> savedStopId = new ArrayList();
        if (this.mStopNameEditText.getEditableText().length() == 0) {
            Cursor searchcursor = getContentResolver().query(DataConst.CONTENT_STOP_SEARCH_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_SEARCH_STOP_ID, DataConst.KEY_SEARCHED_MILLI}, null, null, "millitime DESC");
            if (searchcursor.getCount() <= 0 || !searchcursor.moveToFirst()) {
                this.cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, null, null, null, "stop_name ASC");
                if (this.cursor != null) {
                    Log.d(this.Tag, "stopcursor != null");
                    if (this.cursor.getCount() > 0 && this.cursor.moveToFirst()) {
                        if (this.mStopListAdapter != null) {
                            this.mStopListAdapter.changeCursor(this.cursor);
                        } else {
                            this.mStopListAdapter = new StopListAdapter(this, this.cursor);
                            this.mListView.setAdapter(this.mStopListAdapter);
                        }
                        this.mListView.setSelection(0);
                        this.mRecentlist.setVisibility(8);
                        this.mLastSearch.setVisibility(8);
                        this.mListView.setVisibility(0);
                        return;
                    }
                    return;
                }
                return;
            }
            do {
                savedStopId.add(searchcursor.getString(searchcursor.getColumnIndex(DataConst.KEY_SEARCH_STOP_ID)));
            } while (searchcursor.moveToNext());
            searchcursor.close();
            this.mRecentArray.clear();
            Iterator it = savedStopId.iterator();
            while (it.hasNext()) {
                Cursor cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, null, "stop_id = " + ((String) it.next()), null, null);
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    RecentStopInfo info = new RecentStopInfo();
                    info.stopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                    info.stopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                    info.stopLimusine = cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE));
                    info.stopX = cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_X));
                    info.stopY = cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_X));
                    info.stopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
                    this.mRecentArray.add(info);
                }
                cursor.close();
            }
            this.mRecentlist.setAdapter(new RecentSeachAdapter(this, 0, 0, this.mRecentArray));
            this.mRecentlist.setVisibility(0);
            this.mLastSearch.setVisibility(0);
            this.mListView.setVisibility(8);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.search_bus_stop);
        Log.d("UiSearchBusStop", "[[[[[[    onCreate       ]]]]]]]");
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.mBgImage = (LinearLayout) findViewById(C0258R.id.search_bus_stop_background_layout);
        this.mEditInputbg = (LinearLayout) findViewById(C0258R.id.stop_input_background);
        if (this.bEmergency) {
            this.mBgImage.setBackgroundResource(C0258R.drawable.background_emer);
            this.mEditInputbg.setBackgroundColor(Color.parseColor("#D7ADA7"));
        } else {
            this.mBgImage.setBackgroundResource(C0258R.drawable.background_nomal);
            this.mEditInputbg.setBackgroundColor(Color.parseColor("#77ADD1"));
        }
        ((Button) findViewById(C0258R.id.search_bus_stop_mapbutton)).setOnClickListener(new C03061());
        final PermissionListener permissionlistener = new C04442();
        ((Button) findViewById(C0258R.id.search_bus_stop_qrbutton)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ((TedPermission.Builder) ((TedPermission.Builder) ((TedPermission.Builder) TedPermission.with(UiSearchBusStop.this).setPermissionListener(permissionlistener)).setDeniedMessage((CharSequence) "카메라 권한이 없으면 QR코드를 이용 할 수 없습니다.\n [Setting] > [Permission] 에서 허용 가능합니다.")).setPermissions("android.permission.CAMERA")).check();
            }
        });
        this.mStopNameEditText = (EditText) findViewById(C0258R.id.search_bus_stop_edittext);
        this.mStopNameEditText.setText("", BufferType.EDITABLE);
        this.mStopNameEditText.addTextChangedListener(this);
        this.mLastSearch = (TextView) findViewById(C0258R.id.search_bus_stop_textview);
        this.mLastSearch.setVisibility(8);
        this.mListView = (ListView) findViewById(C0258R.id.search_bus_stop_listview);
        this.mListView.setOnItemClickListener(this);
        this.mRecentArray = new ArrayList();
        this.mRecentlist = (ListView) findViewById(C0258R.id.search_bus_stop_recent_listview);
        this.mRecentlist.setOnItemClickListener(new C03084());
        this.mRecentlist.setOnItemLongClickListener(new C03115());
        if (this.imm.isActive()) {
            this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void afterTextChanged(Editable s) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start == 0 && count == 0) {
            this.mStopNameEditText.setCompoundDrawablesWithIntrinsicBounds(C0258R.drawable.icon_search, 0, 0, 0);
            getConditionOfStopList();
            return;
        }
        this.mStopNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        try {
            this.cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, this.StopProjection, "stop_name Like '%" + s + "%' or " + "stop_id" + " Like '%" + s + "%'", null, "stop_name ASC");
            if (this.cursor != null && this.cursor.getCount() > 0 && this.cursor.moveToFirst()) {
                this.mRecentlist.setVisibility(8);
                this.mLastSearch.setVisibility(8);
                if (this.mStopListAdapter != null) {
                    this.mStopListAdapter.changeCursor(this.cursor);
                } else {
                    this.mStopListAdapter = new StopListAdapter(this, this.cursor);
                    this.mListView.setAdapter(this.mStopListAdapter);
                }
                if (this.mListView.getVisibility() != 0) {
                    this.mListView.setVisibility(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        TabSecond.TAB2.onBackPressed();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu mMenu = menu;
        String childId = TabHistory.getInstance().getFrontId(UiMainTab.mTabHost.getCurrentTabTag());
        Cursor recentlistCursor = getContentResolver().query(DataConst.CONTENT_STOP_SEARCH_URI, null, null, null, "millitime DESC");
        if (this.mStopNameEditText.length() != 0 || recentlistCursor.getCount() <= 0) {
            return false;
        }
        recentlistCursor.close();
        if (mMenu.size() == 0) {
            mMenu.add(0, 0, 0, "전체삭제").setIcon(C0258R.drawable.icon_delete_recent_list).setOnMenuItemClickListener(new C03126());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Stop mStop = (Stop) arg0.getAdapter().getItem(arg2);
        Intent intent = new Intent(this, UiStopInformation.class);
        intent.putExtra("stop_id", mStop.getStopID());
        intent.putExtra("stop_name", mStop.getStopName());
        intent.putExtra("stop_coord_x", mStop.getStopX());
        intent.putExtra("stop_coord_y", mStop.getStopY());
        intent.putExtra("stop_remark", mStop.getStopRemark());
        Cursor cursor = getContentResolver().query(DataConst.CONTENT_STOP_SEARCH_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_SEARCH_STOP_ID}, "search_stop_id = " + mStop.getStopID(), null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            ContentValues value = new ContentValues();
            value.put(DataConst.KEY_SEARCH_STOP_ID, mStop.getStopID());
            value.put(DataConst.KEY_SEARCHED_MILLI, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
            getContentResolver().insert(DataConst.CONTENT_STOP_SEARCH_URI, value);
        }
        this.mStopNameEditText.setText("", BufferType.EDITABLE);
        this.mStopNameEditText.requestFocus();
        if (this.imm.isActive()) {
            this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        StartNewActivity(intent);
    }

    private void onSaveText() {
        String mCurText = this.mStopNameEditText.getText().toString();
    }

    private void StartNewActivity(Intent intent) {
        TabSecond.TAB2.replaceActivity("BusStop", intent, this);
    }

    protected void onStart() {
        super.onStart();
        Log.d("UiSearchBusStop", "[[[[[[    onStart       ]]]]]]]");
        getConditionOfStopList();
    }

    protected void onResume() {
        Log.d("UiSearchBusStop", "[[[[[[    onResume       ]]]]]]]");
        super.onResume();
        if (this.mStopNameEditText.getEditableText().length() > 0) {
            this.mStopNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.mListView.setVisibility(0);
            return;
        }
        this.mStopNameEditText.setCompoundDrawablesWithIntrinsicBounds(C0258R.drawable.icon_search, 0, 0, 0);
        this.mStopNameEditText.setText("", BufferType.EDITABLE);
        this.mStopNameEditText.requestFocus();
        getConditionOfStopList();
    }
}
