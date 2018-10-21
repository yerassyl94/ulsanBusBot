package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MergeCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
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
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UiSearchRoute extends Activity implements TextWatcher, OnClickListener {
    private static final int REQUEST_ROUTE_DELELE_ALL = 10;
    private String Tag = ("[Ulsan]" + getClass().getSimpleName());
    private boolean bEmergency;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button bt4;
    private Button bt5;
    private Button bt6;
    private Sectionizer<Cursor> headerSectionizer;
    private InputMethodManager imm;
    private Context mContext;
    private RelativeLayout mInputBackgroundImg;
    private TextView mRecentHeader;
    private RouteItemAdapter mRouteAdapter;
    private Cursor mRouteCursor;
    private ListView mSearchListView;
    private EditText mSearchNumberEditText;
    private LinearLayout mSearchRouteBg;
    private ArrayList<String> mSearchlist;
    private String mSelectedMode;
    private SharedPreferences prefs;
    private ListView recentSearch;
    private SimpleSectionAdapter<Cursor> sectionAdapter;

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$1 */
    class C03131 implements OnMenuItemClickListener {
        C03131() {
        }

        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(UiSearchRoute.this, UiDeletePopUp.class);
            intent.putExtra("delete_mode", "search_route");
            UiSearchRoute.this.startActivityForResult(intent, 10);
            return true;
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$3 */
    class C03143 implements OnItemClickListener {
        C03143() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            Cursor curRoute = (Cursor) UiSearchRoute.this.sectionAdapter.getItem(arg2);
            Intent intent = new Intent(UiSearchRoute.this, UiRouteInformation.class);
            intent.putExtra("route_id", curRoute.getString(curRoute.getColumnIndex("route_id")));
            String rId = curRoute.getString(curRoute.getColumnIndex("route_id"));
            String rNo = curRoute.getString(curRoute.getColumnIndex("route_no"));
            Editor editor = UiSearchRoute.this.prefs.edit();
            editor.putString(DataConst.BUS_ID_TAB1, rId);
            editor.putString(DataConst.KEY_PREF_ROUTE_NO, rNo);
            editor.commit();
            ContentValues value = new ContentValues();
            value.put("route_id", curRoute.getString(curRoute.getColumnIndex("route_id")));
            value.put(DataConst.KEY_SEARCHED_MILLI, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
            UiSearchRoute.this.getContentResolver().insert(DataConst.CONTENT_ROUTE_SEARCH_URI, value);
            UiSearchRoute.this.mSearchNumberEditText.setText("", BufferType.EDITABLE);
            if (UiSearchRoute.this.imm.isActive()) {
                UiSearchRoute.this.imm.hideSoftInputFromWindow(UiSearchRoute.this.getWindow().getDecorView().getWindowToken(), 0);
            }
            UiSearchRoute.this.StartNewActivity("UisearchRoute", intent, UiSearchRoute.this);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$4 */
    class C03154 implements OnItemClickListener {
        C03154() {
        }

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String recentRoute = (String) arg0.getAdapter().getItem(arg2);
            String rNo = null;
            String[] RouteProjection = new String[]{DataConst.KEY_NOTICE_ID, "route_no"};
            Cursor busNoCursor = UiSearchRoute.this.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, RouteProjection, "route_id= " + recentRoute, null, null);
            if (busNoCursor.moveToFirst() && busNoCursor.getCount() > 0) {
                rNo = busNoCursor.getString(busNoCursor.getColumnIndex("route_no"));
                Log.e("onItemClick: ", rNo);
            }
            Editor editor = UiSearchRoute.this.prefs.edit();
            editor.putString(DataConst.BUS_ID_TAB1, recentRoute);
            if (rNo != null) {
                editor.putString(DataConst.KEY_PREF_ROUTE_NO, rNo);
            }
            editor.commit();
            Intent intent = new Intent(UiSearchRoute.this, UiRouteInformation.class);
            intent.putExtra("route_id", recentRoute);
            if (UiSearchRoute.this.imm.isActive()) {
                UiSearchRoute.this.imm.hideSoftInputFromWindow(UiSearchRoute.this.getWindow().getDecorView().getWindowToken(), 0);
            }
            UiSearchRoute.this.StartNewActivity("UisearchRoute", intent, UiSearchRoute.this);
            UiSearchRoute.this.mSearchNumberEditText.setText("", BufferType.EDITABLE);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$5 */
    class C03185 implements OnItemLongClickListener {

        /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$5$1 */
        class C03161 implements DialogInterface.OnClickListener {
            C03161() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }

        C03185() {
        }

        public boolean onItemLongClick(final AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
            if (UiSearchRoute.this.mSearchNumberEditText.getEditableText().length() < 1 && arg0.getAdapter().getCount() > 0) {
                new Builder(UiSearchRoute.this.getParent()).setMessage(UiSearchRoute.this.getResources().getString(C0258R.string.search_root_textview_recent_delete)).setPositiveButton(UiSearchRoute.this.getResources().getString(C0258R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UiSearchRoute.this.getContentResolver().delete(DataConst.CONTENT_ROUTE_SEARCH_URI, "route_id = " + ((String) arg0.getAdapter().getItem(arg2)), null);
                        UiSearchRoute.this.getConditionOfRouteList();
                        dialog.dismiss();
                    }
                }).setNegativeButton(UiSearchRoute.this.getResources().getString(C0258R.string.cancel), new C03161()).show();
            }
            return true;
        }
    }

    class RecentSearchAdapter extends ArrayAdapter<String> {
        private int[] resIds;
        private ArrayList<String> searchlist;
        private Context searchlistContext;

        public RecentSearchAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.searchlistContext = context;
            this.searchlist = (ArrayList) objects;
            TypedArray busType = context.getResources().obtainTypedArray(C0258R.array.bus_type);
            this.resIds = new int[busType.length()];
            for (int i = 0; i < this.resIds.length; i++) {
                this.resIds[i] = busType.getResourceId(i, 0);
            }
            busType.recycle();
        }

        public String getItem(int position) {
            return (String) super.getItem(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.searchlistContext.getSystemService("layout_inflater")).inflate(C0258R.layout.search_route_item, parent, false);
            }
            Cursor cursor = UiSearchRoute.this.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, null, "route_id = " + ((String) this.searchlist.get(position)), null, null);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                int busType = Integer.parseInt(cursor.getString(cursor.getColumnIndex("route_bus_type")));
                int busNo = cursor.getInt(cursor.getColumnIndex("route_no"));
                String[] colorArray = UiSearchRoute.this.getResources().getStringArray(C0258R.array.bus_type_color);
                String[] BusTypeArray = UiSearchRoute.this.getResources().getStringArray(C0258R.array.stop_info_header_type);
                StringBuffer description = new StringBuffer();
                description.append(cursor.getString(cursor.getColumnIndex(DataConst.KEY_BRT_APP_REMARK)));
                RelativeLayout row = (RelativeLayout) v.findViewById(C0258R.id.search_route_item_row);
                ImageView mBusIcon = (ImageView) v.findViewById(C0258R.id.search_route_item_bus_icon);
                TextView mNumber = (TextView) v.findViewById(C0258R.id.search_route_item_number);
                TextView mType = (TextView) v.findViewById(C0258R.id.search_route_item_type);
                TextView mNode = (TextView) v.findViewById(C0258R.id.search_route_item_node);
                ((TextView) v.findViewById(C0258R.id.search_route_item_interval)).setVisibility(4);
                mBusIcon.setImageResource(this.resIds[busType]);
                mType.setText(BusTypeArray[busType]);
                mType.setTextColor(Color.parseColor(colorArray[busType]));
                mNumber.setText(new Integer(busNo).toString());
                mNumber.setTextColor(Color.parseColor(colorArray[busType]));
                mNode.setText(description.toString());
                mNode.setEllipsize(TruncateAt.END);
                String interval = cursor.getString(cursor.getColumnIndex("route_interval"));
                StringBuffer strInterval = new StringBuffer();
                strInterval.append(UiSearchRoute.this.changeTime(interval));
                strInterval.append(" ");
                strInterval.append(UiSearchRoute.this.getResources().getString(C0258R.string.route_info_time_interval));
                if (UiSearchRoute.this.bEmergency) {
                    row.setBackgroundResource(C0258R.drawable.list_emer_selector);
                } else {
                    row.setBackgroundResource(C0258R.drawable.list_selector);
                }
            }
            return v;
        }
    }

    class RouteItemAdapter extends CursorAdapter {
        private Cursor mCursor;
        private LayoutInflater mInflater;
        private int[] resIds;

        public Object getItem(int position) {
            return super.getItem(position);
        }

        public RouteItemAdapter(Context context, Cursor c) {
            super(context, c);
            this.mCursor = c;
            TypedArray busType = context.getResources().obtainTypedArray(C0258R.array.bus_type);
            this.resIds = new int[busType.length()];
            for (int i = 0; i < this.resIds.length; i++) {
                this.resIds[i] = busType.getResourceId(i, 0);
            }
            busType.recycle();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void bindView(View view, Context context, Cursor cursor) {
            if (this.mCursor != null && this.mCursor.getCount() > 0) {
                int busType = Integer.parseInt(cursor.getString(cursor.getColumnIndex("route_bus_type")));
                int busNo = cursor.getInt(cursor.getColumnIndex("route_no"));
                String[] colorArray = UiSearchRoute.this.getResources().getStringArray(C0258R.array.bus_type_color);
                String[] BusTypeArray = UiSearchRoute.this.getResources().getStringArray(C0258R.array.stop_info_header_type);
                StringBuffer description = new StringBuffer();
                description.append(cursor.getString(cursor.getColumnIndex(DataConst.KEY_BRT_APP_REMARK)));
                RelativeLayout row = (RelativeLayout) view.findViewById(C0258R.id.search_route_item_row);
                ImageView mBusIcon = (ImageView) view.findViewById(C0258R.id.search_route_item_bus_icon);
                TextView mNumber = (TextView) view.findViewById(C0258R.id.search_route_item_number);
                TextView mType = (TextView) view.findViewById(C0258R.id.search_route_item_type);
                TextView mNode = (TextView) view.findViewById(C0258R.id.search_route_item_node);
                ((TextView) view.findViewById(C0258R.id.search_route_item_interval)).setVisibility(4);
                mBusIcon.setImageResource(this.resIds[busType]);
                mType.setText(BusTypeArray[busType]);
                mType.setTextColor(Color.parseColor(colorArray[busType]));
                mNumber.setText(new Integer(busNo).toString());
                mNumber.setTextColor(Color.parseColor(colorArray[busType]));
                mNode.setText(description.toString());
                mNode.setEllipsize(TruncateAt.END);
                String interval = cursor.getString(cursor.getColumnIndex("route_interval"));
                StringBuffer strInterval = new StringBuffer();
                strInterval.append(UiSearchRoute.this.changeTime(interval));
                strInterval.append(" ");
                strInterval.append(UiSearchRoute.this.getResources().getString(C0258R.string.route_info_time_interval));
                if (UiSearchRoute.this.bEmergency) {
                    row.setBackgroundResource(C0258R.drawable.list_emer_selector);
                } else {
                    row.setBackgroundResource(C0258R.drawable.list_selector);
                }
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.search_route_item, parent, false);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiSearchRoute$2 */
    class C04452 implements Sectionizer<Cursor> {
        C04452() {
        }

        public String getSectionTitleForItem(Cursor instance) {
            try {
                String mBusType = instance.getString(instance.getColumnIndex("route_bus_type"));
                if (mBusType.equals("0")) {
                    return "일반";
                }
                if (mBusType.equals("1")) {
                    return "좌석";
                }
                if (mBusType.equals("2")) {
                    return "리무진";
                }
                if (mBusType.equals("3")) {
                    return "마을";
                }
                if (mBusType.equals("4")) {
                    return "지선";
                }
                return null;
            } catch (CursorIndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    private void setFocusedBusType(String selectMode) {
        this.mSelectedMode = selectMode;
    }

    private String getFocusedBusType() {
        return this.mSelectedMode;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu mMenu = menu;
        String childId = TabHistory.getInstance().getFrontId(UiMainTab.mTabHost.getCurrentTabTag());
        Cursor recentlistCursor = getContentResolver().query(DataConst.CONTENT_ROUTE_SEARCH_URI, null, null, null, "millitime DESC");
        if (this.mSearchNumberEditText.length() != 0 || recentlistCursor.getCount() <= 0) {
            return false;
        }
        recentlistCursor.close();
        if (mMenu.size() == 0) {
            mMenu.add(0, 0, 0, "전체삭제").setIcon(C0258R.drawable.icon_delete_recent_list).setOnMenuItemClickListener(new C03131());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onResume() {
        super.onResume();
        if (this.mSearchNumberEditText.getEditableText().length() > 0) {
            this.mSearchNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.mSearchListView.setVisibility(0);
            return;
        }
        this.mSearchNumberEditText.setCompoundDrawablesWithIntrinsicBounds(C0258R.drawable.icon_search, 0, 0, 0);
        this.mSearchNumberEditText.requestFocus();
        getConditionOfRouteList();
    }

    private Cursor searchcursor(String type) {
        String condition;
        if (this.bEmergency) {
            condition = "route_dir != 2 and route_bus_type= " + type + ") GROUP BY (" + "route_no" + ") HAVING MIN(" + "route_id";
        } else {
            condition = "route_dir != 2 and route_bus_type= " + type + " and " + DataConst.KEY_ROUTE_TYPE + " != 9 ) GROUP BY (" + "route_no" + ") HAVING MIN(" + "route_id";
        }
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, null, condition, null, "route_id ASC");
    }

    protected void onStart() {
        super.onStart();
        MergeCursor mergeCursor = new MergeCursor(new Cursor[]{searchcursor("0"), searchcursor("1"), searchcursor("2"), searchcursor("3"), searchcursor("4")});
        try {
            if (mergeCursor.getCount() > 0 && mergeCursor.moveToFirst()) {
                this.mRouteAdapter = new RouteItemAdapter(this, mergeCursor);
                this.headerSectionizer = new C04452();
                this.sectionAdapter = new SimpleSectionAdapter(this, this.mRouteAdapter, C0258R.layout.list_common_header, C0258R.id.list_header_title, this.headerSectionizer);
                this.mSearchListView.setAdapter(this.sectionAdapter);
            }
            this.mSearchNumberEditText.setText("", BufferType.EDITABLE);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("onStart: ", e.getMessage());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.search_route);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.mContext = this;
        this.mInputBackgroundImg = (RelativeLayout) findViewById(C0258R.id.edit_input_background);
        this.mSearchNumberEditText = (EditText) findViewById(C0258R.id.search_root_edittext);
        this.mSearchNumberEditText.addTextChangedListener(this);
        this.mSearchNumberEditText.setCompoundDrawablesWithIntrinsicBounds(C0258R.drawable.icon_search, 0, 0, 0);
        this.mSearchNumberEditText.requestFocus();
        this.mSearchRouteBg = (LinearLayout) findViewById(C0258R.id.search_route_background);
        if (this.bEmergency) {
            this.mSearchRouteBg.setBackgroundResource(C0258R.drawable.background_emer);
            this.mInputBackgroundImg.setBackgroundResource(C0258R.drawable.emer_edit_input_background);
        } else {
            this.mSearchRouteBg.setBackgroundResource(C0258R.drawable.background_nomal);
            this.mInputBackgroundImg.setBackgroundResource(C0258R.drawable.edit_input_background);
        }
        this.mRecentHeader = (TextView) findViewById(C0258R.id.search_route_textview);
        this.mRecentHeader.setVisibility(8);
        this.mSearchListView = (ListView) findViewById(C0258R.id.search_root_listview);
        this.mSearchListView.setOnItemClickListener(new C03143());
        this.recentSearch = (ListView) findViewById(C0258R.id.search_recent_searchlistview);
        this.recentSearch.setOnItemClickListener(new C03154());
        this.recentSearch.setOnItemLongClickListener(new C03185());
        this.bt1 = (Button) findViewById(C0258R.id.search_root_total_button);
        this.bt2 = (Button) findViewById(C0258R.id.search_root_ib_button);
        this.bt3 = (Button) findViewById(C0258R.id.search_root_js_button);
        this.bt4 = (Button) findViewById(C0258R.id.search_root_gs_button);
        this.bt5 = (Button) findViewById(C0258R.id.search_root_me_button);
        this.bt6 = (Button) findViewById(C0258R.id.search_root_lg_button);
        if (this.bEmergency) {
            this.bt1.setBackgroundResource(C0258R.drawable.btn_emer_route_all);
            this.bt2.setBackgroundResource(C0258R.drawable.btn_emer_route_bus);
            this.bt3.setBackgroundResource(C0258R.drawable.btn_emer_route_express);
            this.bt4.setBackgroundResource(C0258R.drawable.btn_emer_route_jisun);
            this.bt5.setBackgroundResource(C0258R.drawable.btn_emer_route_town);
            this.bt6.setBackgroundResource(C0258R.drawable.btn_emer_route_limousine);
        } else {
            this.bt1.setBackgroundResource(C0258R.drawable.btn_route_all);
            this.bt2.setBackgroundResource(C0258R.drawable.btn_route_bus);
            this.bt3.setBackgroundResource(C0258R.drawable.btn_route_express);
            this.bt4.setBackgroundResource(C0258R.drawable.btn_route_jisun);
            this.bt5.setBackgroundResource(C0258R.drawable.btn_route_town);
            this.bt6.setBackgroundResource(C0258R.drawable.btn_route_limousine);
        }
        this.bt1.setOnClickListener(this);
        this.bt2.setOnClickListener(this);
        this.bt3.setOnClickListener(this);
        this.bt4.setOnClickListener(this);
        this.bt5.setOnClickListener(this);
        this.bt6.setOnClickListener(this);
        this.bt1.setSelected(true);
        setFocusedBusType(null);
        this.mSearchlist = new ArrayList();
        this.recentSearch.setVisibility(8);
        this.mRecentHeader.setVisibility(8);
    }

    public void afterTextChanged(Editable arg0) {
        Log.d(this.Tag, "afterTextChanged");
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        Log.d(this.Tag, "beforeTextChanged");
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(this.Tag, "beforeTextChanged");
        if (start == 0 && count == 0) {
            this.mSearchNumberEditText.setCompoundDrawablesWithIntrinsicBounds(C0258R.drawable.icon_search, 0, 0, 0);
            getConditionOfRouteList();
            return;
        }
        this.mSearchNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (!(this.mRouteCursor == null || this.mRouteCursor.isClosed())) {
            this.mRouteCursor.close();
        }
        try {
            this.mRouteCursor = queryRouteNumber(this.mSearchNumberEditText.getEditableText().toString());
            if (this.mRouteCursor.getCount() <= 0 || !this.mRouteCursor.moveToFirst()) {
                this.recentSearch.setVisibility(8);
                this.mSearchListView.setVisibility(8);
                this.mRecentHeader.setVisibility(8);
                return;
            }
            this.mRouteAdapter.changeCursor(this.mRouteCursor);
            this.mRouteAdapter.notifyDataSetChanged();
            this.mSearchListView.setSelection(0);
            if (this.mSearchListView.getVisibility() == 8) {
                this.recentSearch.setVisibility(8);
                this.mRecentHeader.setVisibility(8);
                this.mSearchListView.setVisibility(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getConditionOfRouteList() {
        Log.d(this.Tag, "getConditionOfRouteList");
        if (this.mSearchlist.size() != 0) {
            this.mSearchlist.clear();
        }
        Cursor searchcursor = getContentResolver().query(DataConst.CONTENT_ROUTE_SEARCH_URI, null, null, null, "millitime DESC");
        String focusedBusType;
        MergeCursor mergeCursor;
        if (searchcursor == null) {
            focusedBusType = getFocusedBusType();
            mergeCursor = new MergeCursor(new Cursor[]{getcursor(focusedBusType, "0"), getcursor(focusedBusType, "1"), getcursor(focusedBusType, "2"), getcursor(focusedBusType, "3"), getcursor(focusedBusType, "4")});
            if (mergeCursor != null) {
                Log.d(this.Tag, "mRouteCursor != null");
                if (mergeCursor.getCount() <= 0 || !mergeCursor.moveToFirst()) {
                    this.mRouteAdapter.changeCursor(mergeCursor);
                    this.mRouteAdapter.notifyDataSetChanged();
                    return;
                }
                this.mRouteAdapter.changeCursor(mergeCursor);
                this.mRouteAdapter.notifyDataSetChanged();
                this.mSearchListView.setSelection(0);
                this.recentSearch.setVisibility(8);
                this.mRecentHeader.setVisibility(8);
                this.mSearchListView.setVisibility(0);
                this.mSearchListView.invalidate();
            }
        } else if (searchcursor.getCount() <= 0 || !searchcursor.moveToFirst()) {
            focusedBusType = getFocusedBusType();
            mergeCursor = new MergeCursor(new Cursor[]{getcursor(focusedBusType, "0"), getcursor(focusedBusType, "1"), getcursor(focusedBusType, "2"), getcursor(focusedBusType, "3"), getcursor(focusedBusType, "4")});
            if (mergeCursor == null) {
                return;
            }
            if (mergeCursor.getCount() <= 0 || !mergeCursor.moveToFirst()) {
                this.mRouteAdapter.changeCursor(mergeCursor);
                this.mRouteAdapter.notifyDataSetChanged();
                return;
            }
            this.mRouteAdapter.changeCursor(mergeCursor);
            this.mRouteAdapter.notifyDataSetChanged();
            this.mSearchListView.setSelection(0);
            this.recentSearch.setVisibility(8);
            this.mRecentHeader.setVisibility(8);
            this.mSearchListView.setVisibility(0);
        } else {
            do {
                this.mSearchlist.add(searchcursor.getString(searchcursor.getColumnIndex("route_id")));
            } while (searchcursor.moveToNext());
            searchcursor.close();
            this.recentSearch.setAdapter(new RecentSearchAdapter(this, 0, this.mSearchlist));
            this.recentSearch.setVisibility(0);
            this.mRecentHeader.setVisibility(0);
            this.mSearchListView.setVisibility(8);
        }
    }

    private Cursor getcursor(String focusedBusType, String type) {
        String condition;
        if (this.bEmergency) {
            condition = focusedBusType == null ? "route_dir != 2 " : "route_bus_type=" + focusedBusType + " AND " + DataConst.KEY_ROUTE_DIR + " != 2  and " + "route_bus_type" + "= " + type;
        } else if (focusedBusType == null) {
            condition = "route_dir != 2  and route_type != 9 and route_bus_type= " + type;
        } else {
            condition = "route_bus_type=" + focusedBusType + " AND " + DataConst.KEY_ROUTE_DIR + " != 2  and " + DataConst.KEY_ROUTE_TYPE + " != 9 and " + "route_bus_type" + "= " + type;
        }
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, null, condition + ") GROUP BY (" + "route_no" + ") HAVING MIN(" + "route_id", null, "route_bus_type ASC");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mSearchNumberEditText.getEditableText().length() > 0) {
            this.mSearchListView.setVisibility(8);
        } else {
            this.recentSearch.setVisibility(8);
            this.mSearchListView.setVisibility(8);
            this.mRecentHeader.setVisibility(8);
        }
        this.mRouteAdapter = null;
        this.mSearchListView = null;
        if (this.mSearchlist != null) {
            this.mSearchlist = null;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        TabFirst.TAB1.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void StartNewActivity(String id, Intent intent, Context context) {
        TabFirst.TAB1.replaceActivity(id, intent, context);
    }

    private Cursor mergeCursor_query(String inputText, String type) {
        StringBuffer selCondition = new StringBuffer();
        if (inputText != null && inputText.length() > 0) {
            selCondition.append("route_no Like '" + inputText + "%' AND ");
        }
        if (getFocusedBusType() == null) {
            selCondition.append("route_dir != 2");
        } else {
            selCondition.append("route_bus_type = " + getFocusedBusType());
            selCondition.append(" AND route_dir != 2");
        }
        if (!this.bEmergency) {
            selCondition.append(" and route_type != 9 ");
        }
        selCondition.append(" and route_bus_type = " + type);
        selCondition.append(") GROUP BY (route_no");
        selCondition.append(") HAVING MIN(route_id");
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, null, selCondition.toString(), null, "route_bus_type ASC");
    }

    private Cursor queryRouteNumber(String inputText) {
        MergeCursor searchCursor = new MergeCursor(new Cursor[]{mergeCursor_query(inputText, "0"), mergeCursor_query(inputText, "1"), mergeCursor_query(inputText, "2"), mergeCursor_query(inputText, "3"), mergeCursor_query(inputText, "4")});
        return (searchCursor == null || searchCursor.getCount() <= 0 || searchCursor.moveToFirst()) ? searchCursor : searchCursor;
    }

    void initialize_Btn_Selected() {
        this.bt1.setSelected(false);
        this.bt2.setSelected(false);
        this.bt3.setSelected(false);
        this.bt4.setSelected(false);
        this.bt5.setSelected(false);
        this.bt6.setSelected(false);
    }

    public void onClick(View v) {
        initialize_Btn_Selected();
        switch (v.getId()) {
            case C0258R.id.search_root_gs_button:
                this.bt4.setSelected(true);
                setFocusedBusType("4");
                break;
            case C0258R.id.search_root_ib_button:
                this.bt2.setSelected(true);
                setFocusedBusType("0");
                break;
            case C0258R.id.search_root_js_button:
                this.bt3.setSelected(true);
                setFocusedBusType("1");
                break;
            case C0258R.id.search_root_lg_button:
                setFocusedBusType("2");
                this.bt6.setSelected(true);
                break;
            case C0258R.id.search_root_me_button:
                this.bt5.setSelected(true);
                setFocusedBusType("3");
                break;
            case C0258R.id.search_root_total_button:
                this.bt1.setSelected(true);
                setFocusedBusType(null);
                break;
        }
        if (this.mSearchNumberEditText.getText().length() > 0) {
            this.mRouteCursor = queryRouteNumber(this.mSearchNumberEditText.getEditableText().toString());
            if (this.mRouteCursor == null || this.mRouteCursor.getCount() <= 0) {
                this.mSearchListView.setVisibility(8);
                return;
            } else if (this.mRouteCursor.getCount() <= 0 || !this.mRouteCursor.moveToFirst()) {
                this.mSearchListView.setVisibility(8);
                return;
            } else {
                this.recentSearch.setVisibility(8);
                this.mRecentHeader.setVisibility(8);
                this.mRouteAdapter.changeCursor(this.mRouteCursor);
                this.mSearchListView.setSelection(0);
                this.mSearchListView.setVisibility(0);
                return;
            }
        }
        getConditionOfRouteList();
    }

    private String changeTime(String time) {
        StringBuffer totalTime = new StringBuffer();
        long a = (long) Double.valueOf(time).doubleValue();
        int b = ((int) a) / 60;
        long c = a - (((long) b) * 60);
        if (b <= 0) {
            totalTime.append(c);
            totalTime.append(getResources().getString(C0258R.string.route_info_time_min));
        } else if (c < 1) {
            totalTime.append(b);
            totalTime.append(getResources().getString(C0258R.string.route_info_time_hour));
        } else {
            totalTime.append(b);
            totalTime.append(getResources().getString(C0258R.string.route_info_time_hour));
            totalTime.append(c);
            totalTime.append(getResources().getString(C0258R.string.route_info_time_min));
        }
        return totalTime.toString();
    }

    protected void onRestart() {
        super.onRestart();
    }
}
