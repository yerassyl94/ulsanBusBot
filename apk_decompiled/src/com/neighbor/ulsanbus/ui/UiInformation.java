package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Database;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UiInformation extends NavigationActivity implements OnClickListener, OnItemClickListener {
    private boolean bIsEmergency;
    private InputMethodManager imm;
    private LayoutInflater inflater;
    private Context mContext;
    private String[] mDBProjction = new String[]{DataConst.KEY_VERSION_ROUTE, DataConst.KEY_VERSION_STOP, DataConst.KEY_VERSION_TIME};
    private LinearLayout mDynamicLayout;
    private Button mEmergencyButton;
    private ListView mInfoList;
    private Database mInfoVersion;
    private Button mManualButton;
    private NotiAdapter mNotiAdapter;
    private Button mNoticeButton;
    private ArrayList<NotificationHolder> mNoticeList;
    private DataSAXParser mSaxHelper;
    private Button mSubmitComplain;
    private Button mVerInforButton;
    private FrameLayout noticeTitle;
    private SharedPreferences prefs;

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        int mGalleryItemBackground;
        private Integer[] mImageIds = new Integer[]{Integer.valueOf(C0258R.drawable.information_emergency_content1), Integer.valueOf(C0258R.drawable.information_emergency_content2)};

        public ImageAdapter(Context c) {
            this.mContext = c;
        }

        public int getCount() {
            return this.mImageIds.length;
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(this.mContext);
            i.setLayoutParams(new LayoutParams(-1, -1));
            i.setImageResource(this.mImageIds[position].intValue());
            i.setScaleType(ScaleType.FIT_XY);
            return i;
        }
    }

    class ManualAdapter extends BaseAdapter {
        private Context mContext;
        int mGalleryItemBackground;
        private Integer[] mImageIds = new Integer[]{Integer.valueOf(C0258R.drawable.manual_content1), Integer.valueOf(C0258R.drawable.manual_content2), Integer.valueOf(C0258R.drawable.manual_content3), Integer.valueOf(C0258R.drawable.manual_content4), Integer.valueOf(C0258R.drawable.manual_content5), Integer.valueOf(C0258R.drawable.manual_content6), Integer.valueOf(C0258R.drawable.manual_content7), Integer.valueOf(C0258R.drawable.manual_content8), Integer.valueOf(C0258R.drawable.manual_content9), Integer.valueOf(C0258R.drawable.manual_content10)};

        public ManualAdapter(Context c) {
            this.mContext = c;
        }

        public int getCount() {
            return this.mImageIds.length;
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(this.mContext);
            i.setLayoutParams(new LayoutParams(-1, -1));
            i.setImageResource(this.mImageIds[position].intValue());
            i.setScaleType(ScaleType.FIT_XY);
            return i;
        }
    }

    class NotiAdapter extends ArrayAdapter<NotificationHolder> {
        private Context mContext;
        private ArrayList<NotificationHolder> mNotiHolder;

        public NotiAdapter(Context context, int resource, int textViewResourceId, List<NotificationHolder> objects) {
            super(context, resource, textViewResourceId, objects);
            this.mContext = context;
            this.mNotiHolder = (ArrayList) objects;
        }

        public void setItemSelect(int position, boolean selected) {
            ((NotificationHolder) this.mNotiHolder.get(position)).mExpanded = Boolean.valueOf(selected);
        }

        public boolean getItemSelect(int position) {
            return ((NotificationHolder) this.mNotiHolder.get(position)).mExpanded.booleanValue();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.information_notice_item, parent, false);
            }
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.information_notice_item, parent, false);
            }
            final NotificationHolder item = (NotificationHolder) this.mNotiHolder.get(position);
            LinearLayout row = (LinearLayout) v.findViewById(C0258R.id.notice_item_row);
            if (UiInformation.this.bIsEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            ((TextView) v.findViewById(C0258R.id.notice_item_title)).setText(item.NoticeTitle);
            ImageView mNoticefile = (ImageView) v.findViewById(C0258R.id.notice_item_file_image);
            if (TextUtils.isEmpty(item.NoticeFile)) {
                mNoticefile.setVisibility(4);
            } else {
                mNoticefile.setVisibility(0);
            }
            TextView mDate = (TextView) v.findViewById(C0258R.id.notice_item_date);
            StringBuffer date = new StringBuffer();
            date.append(item.NoticeDate.substring(0, 8));
            date.insert(4, "/");
            date.insert(7, "/");
            mDate.setText(date.toString());
            TextView mNoticeContents = (TextView) v.findViewById(C0258R.id.notice_item_content);
            TextView mNoticeContentsFile = (TextView) v.findViewById(C0258R.id.notice_item_content_file);
            if (TextUtils.isEmpty(item.NoticeFile)) {
                mNoticeContents.setText(item.NoticeContent);
            } else {
                mNoticeContents.setText(item.NoticeContent + "\n\n<첨부파일>");
            }
            if (TextUtils.isEmpty(item.NoticeFile)) {
                mNoticeContentsFile.setText("");
            } else {
                mNoticeContentsFile.setPaintFlags(8);
                mNoticeContentsFile.setText(item.NoticeFile);
                mNoticeContentsFile.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        try {
                            String a = URLEncoder.encode(item.NoticeFile, "utf-8").replace("+", "%20");
                            Log.e("onClick: ", "http://its.ulsan.kr/itsHelper/download.do?fileName=" + a);
                            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(Uri.decode("http://its.ulsan.kr/itsHelper/download.do?fileName=" + a)));
                            intent.setPackage("com.android.chrome");
                            UiInformation.this.startActivity(intent);
                        } catch (Exception e) {
                        }
                    }
                });
            }
            if (item.mExpanded.booleanValue()) {
                mNoticeContents.setVisibility(0);
                mNoticeContentsFile.setVisibility(0);
            } else {
                mNoticeContents.setVisibility(8);
                mNoticeContentsFile.setVisibility(8);
            }
            return v;
        }
    }

    class NotificationHolder {
        String NoticeContent;
        String NoticeDate;
        String NoticeFile;
        String NoticeTitle;
        Boolean mExpanded;

        NotificationHolder() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bIsEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.inflater = (LayoutInflater) getSystemService("layout_inflater");
        ViewGroup mMainInformation = (ViewGroup) this.inflater.inflate(C0258R.layout.information, null);
        ImageView Imgbg = (ImageView) mMainInformation.findViewById(C0258R.id.information_bg);
        if (this.bIsEmergency) {
            Imgbg.setImageResource(C0258R.drawable.background_emer);
        } else {
            Imgbg.setImageResource(C0258R.drawable.background_nomal);
        }
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.mDynamicLayout = (LinearLayout) mMainInformation.findViewById(C0258R.id.information_dynamic_layout);
        this.mDynamicLayout.addView(this.inflater.inflate(C0258R.layout.information_notice, null));
        this.mInfoList = (ListView) this.mDynamicLayout.findViewById(C0258R.id.notice_title_list);
        this.mNoticeList = new ArrayList();
        Cursor cursor = getContentResolver().query(DataConst.CONTENT_NOTICE_URI, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                NotificationHolder holder = new NotificationHolder();
                holder.NoticeTitle = cursor.getString(cursor.getColumnIndex(DataConst.KEY_NOTICE_TITLE));
                holder.NoticeDate = cursor.getString(cursor.getColumnIndex(DataConst.KEY_NOTICE_DATE));
                holder.NoticeContent = cursor.getString(cursor.getColumnIndex(DataConst.KEY_NOTICE_CONTENT));
                if (!cursor.isNull(cursor.getColumnIndex(DataConst.KEY_NOTICE_FILE))) {
                    holder.NoticeFile = cursor.getString(cursor.getColumnIndex(DataConst.KEY_NOTICE_FILE));
                }
                holder.mExpanded = Boolean.valueOf(false);
                this.mNoticeList.add(holder);
            } while (cursor.moveToNext());
            this.mNotiAdapter = new NotiAdapter(this, 0, 0, this.mNoticeList);
            this.mInfoList.setAdapter(this.mNotiAdapter);
            this.mInfoList.setOnItemClickListener(this);
        }
        cursor.close();
        this.mContext = this;
        setContentView(mMainInformation);
        setUI();
    }

    void initSelection() {
        this.mNoticeButton.setSelected(false);
        this.mVerInforButton.setSelected(false);
        this.mEmergencyButton.setSelected(false);
        this.mManualButton.setSelected(false);
    }

    String setVersionName(String kindofVer) {
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(C0258R.string.info_version_prefix));
        sb.append(" ");
        sb.append(kindofVer);
        return sb.toString();
    }

    public void onClick(View v) {
        this.mDynamicLayout.removeAllViews();
        switch (v.getId()) {
            case C0258R.id.information_emergency_button:
                initSelection();
                this.mEmergencyButton.setSelected(true);
                this.mDynamicLayout.addView(this.inflater.inflate(C0258R.layout.information_emergency, null));
                ((Gallery) this.mDynamicLayout.findViewById(C0258R.id.information_emergency_gallery)).setAdapter(new ImageAdapter(this));
                return;
            case C0258R.id.information_manual_button:
                initSelection();
                this.mManualButton.setSelected(true);
                this.mDynamicLayout.addView(this.inflater.inflate(C0258R.layout.information_emergency, null));
                ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_header)).setText(getResources().getString(C0258R.string.information_manual_title));
                ((Gallery) this.mDynamicLayout.findViewById(C0258R.id.information_emergency_gallery)).setAdapter(new ManualAdapter(this));
                return;
            case C0258R.id.information_notice_button:
                initSelection();
                this.mNoticeButton.setSelected(true);
                this.mDynamicLayout.addView(this.inflater.inflate(C0258R.layout.information_notice, null));
                this.mInfoList = (ListView) this.mDynamicLayout.findViewById(C0258R.id.notice_title_list);
                this.mInfoList.setAdapter(this.mNotiAdapter);
                this.mInfoList.setOnItemClickListener(this);
                return;
            case C0258R.id.information_verinfor_button:
                initSelection();
                this.mVerInforButton.setSelected(true);
                this.mDynamicLayout.addView(this.inflater.inflate(C0258R.layout.information_version, null));
                this.mInfoVersion = new Database();
                Cursor cursor = getContentResolver().query(DataConst.CONTENT_DB_URI, this.mDBProjction, null, null, null);
                StringBuffer sb;
                if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                    cursor.close();
                    sb = new StringBuffer();
                    try {
                        ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_app_version_textview)).setText(setVersionName(getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_route_version_textview)).setText(setVersionName(this.mInfoVersion.getRoute_Ver()));
                    ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_stop_version_textview)).setText(setVersionName(this.mInfoVersion.getStop_Ver()));
                    ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_bustime_version_textview)).setText(setVersionName(this.mInfoVersion.getTime_Ver()));
                    return;
                }
                do {
                    this.mInfoVersion.setRoute_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_ROUTE)));
                    this.mInfoVersion.setStop_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_STOP)));
                    this.mInfoVersion.setTime_Ver(cursor.getString(cursor.getColumnIndex(DataConst.KEY_VERSION_TIME)));
                } while (cursor.moveToNext());
                cursor.close();
                sb = new StringBuffer();
                ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_app_version_textview)).setText(setVersionName(getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
                ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_route_version_textview)).setText(setVersionName(this.mInfoVersion.getRoute_Ver()));
                ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_stop_version_textview)).setText(setVersionName(this.mInfoVersion.getStop_Ver()));
                ((TextView) this.mDynamicLayout.findViewById(C0258R.id.information_bustime_version_textview)).setText(setVersionName(this.mInfoVersion.getTime_Ver()));
                return;
            default:
                return;
        }
    }

    private void setUI() {
        this.mNoticeButton = (Button) findViewById(C0258R.id.information_notice_button);
        this.mVerInforButton = (Button) findViewById(C0258R.id.information_verinfor_button);
        this.mEmergencyButton = (Button) findViewById(C0258R.id.information_emergency_button);
        this.mManualButton = (Button) findViewById(C0258R.id.information_manual_button);
        if (this.bIsEmergency) {
            this.mNoticeButton.setBackgroundResource(C0258R.drawable.tab_info_emer_notice);
            this.mVerInforButton.setBackgroundResource(C0258R.drawable.tab_info_emer_version);
            this.mEmergencyButton.setBackgroundResource(C0258R.drawable.tab_info_emer_emergen);
            this.mManualButton.setBackgroundResource(C0258R.drawable.tab_info_emer_manual);
        } else {
            this.mNoticeButton.setBackgroundResource(C0258R.drawable.tab_info_notice);
            this.mVerInforButton.setBackgroundResource(C0258R.drawable.tab_info_version);
            this.mEmergencyButton.setBackgroundResource(C0258R.drawable.tab_info_emergen);
            this.mManualButton.setBackgroundResource(C0258R.drawable.tab_info_manual);
        }
        this.mNoticeButton.setOnClickListener(this);
        this.mNoticeButton.setSelected(true);
        this.mVerInforButton.setOnClickListener(this);
        this.mEmergencyButton.setOnClickListener(this);
        this.mManualButton.setOnClickListener(this);
        this.noticeTitle = (FrameLayout) findViewById(C0258R.id.notice_title_layout);
        this.noticeTitle.setVisibility(0);
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
        TextView mNoticeContent = (TextView) arg1.findViewById(C0258R.id.notice_item_content);
        TextView mNoticeContentsFile = (TextView) arg1.findViewById(C0258R.id.notice_item_content_file);
        if (this.mNotiAdapter.getItemSelect(arg2)) {
            this.mNotiAdapter.setItemSelect(arg2, false);
            mNoticeContent.setVisibility(8);
            mNoticeContentsFile.setVisibility(8);
            return;
        }
        this.mNotiAdapter.setItemSelect(arg2, true);
        mNoticeContent.setVisibility(0);
        mNoticeContentsFile.setVisibility(0);
    }
}
