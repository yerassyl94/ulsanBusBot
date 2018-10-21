package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;

public class UiPopupList extends NavigationActivity {
    private String[] StopProjection = new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y, DataConst.KEY_STOP_LIMOUSINE};
    private Stoplist adapter;
    private String[] alphabet;
    private Sectionizer<Cursor> alphabetSectionizer;
    private BeginningChar beginCharAdapter;
    private Cursor cursor;
    private Intent intent;
    private String item;
    private Context m_context;
    private SimpleSectionAdapter<Cursor> sectionAdapter;
    private ListView stoplist;

    /* renamed from: com.neighbor.ulsanbus.ui.UiPopupList$2 */
    class C02882 implements OnItemClickListener {
        C02882() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            String strName = (String) ((TextView) arg1.findViewById(C0258R.id.transfer_search_stop_name)).getText();
            Intent intent = new Intent();
            int position1 = strName.indexOf("[");
            int position2 = strName.indexOf("]");
            intent.putExtra("stopName", strName.subSequence(0, position1));
            intent.putExtra("stopId", strName.subSequence(position1 + 1, position2));
            UiPopupList.this.setResult(-1, intent);
            UiPopupList.this.finish();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiPopupList$3 */
    class C02893 implements OnItemClickListener {
        C02893() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            UiPopupList.this.item = (String) UiPopupList.this.beginCharAdapter.getItem(arg2);
            String selection = null;
            if (UiPopupList.this.item.equals("ㄱ")) {
                selection = "stop_name>='가' and stop_name< '나'";
            } else if (UiPopupList.this.item.equals("ㄴ")) {
                selection = "stop_name>='나' and stop_name< '다'";
            } else if (UiPopupList.this.item.equals("ㄷ")) {
                selection = "stop_name>='다' and stop_name< '라'";
            } else if (UiPopupList.this.item.equals("ㄹ")) {
                selection = "stop_name>='라' and stop_name< '마'";
            } else if (UiPopupList.this.item.equals("ㅁ")) {
                selection = "stop_name>='마' and stop_name< '바'";
            } else if (UiPopupList.this.item.equals("ㅂ")) {
                selection = "stop_name>='바' and stop_name< '사'";
            } else if (UiPopupList.this.item.equals("ㅅ")) {
                selection = "stop_name>='사' and stop_name< '아'";
            } else if (UiPopupList.this.item.equals("ㅇ")) {
                selection = "stop_name>='아' and stop_name< '자'";
            } else if (UiPopupList.this.item.equals("ㅈ")) {
                selection = "stop_name>='자' and stop_name< '카'";
            } else if (UiPopupList.this.item.equals("ㅊ")) {
                selection = "stop_name>='차' and stop_name< '카'";
            } else if (UiPopupList.this.item.equals("ㅋ")) {
                selection = "stop_name>='카' and stop_name< '타'";
            } else if (UiPopupList.this.item.equals("ㅌ")) {
                selection = "stop_name>='타' and stop_name< '파'";
            } else if (UiPopupList.this.item.equals("ㅍ")) {
                selection = "stop_name>='파' and stop_name< '하'";
            } else if (UiPopupList.this.item.equals("ㅎ")) {
                selection = "stop_name>='하' and stop_name< '�K'";
            }
            UiPopupList.this.cursor = UiPopupList.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, UiPopupList.this.StopProjection, selection, null, "stop_name ASC");
            UiPopupList.this.adapter.changeCursor(UiPopupList.this.cursor);
            UiPopupList.this.stoplist.setSelection(0);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiPopupList$4 */
    class C02904 implements OnClickListener {
        C02904() {
        }

        public void onClick(View arg0) {
            UiPopupList.this.setResult(0, new Intent());
            UiPopupList.this.finish();
        }
    }

    class BeginningChar extends ArrayAdapter<String> {
        private Context mContext;
        private String[] mbeginngCharList;

        public BeginningChar(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.mContext = context;
            this.mbeginngCharList = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.transfer_search_stops_char_item, parent, false);
            }
            String beginString = this.mbeginngCharList[position];
            TextView begginchar = (TextView) v.findViewById(C0258R.id.transfer_search_stops_char);
            begginchar.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            begginchar.setText(beginString);
            return v;
        }
    }

    class Stoplist extends CursorAdapter {
        private Cursor cursor;
        private LayoutInflater mInflater;

        public Stoplist(Context context, Cursor c) {
            super(context, c);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.cursor = c;
        }

        public Object getItem(int position) {
            return super.getItem(position);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            TextView stopname = (TextView) view.findViewById(C0258R.id.transfer_search_stop_name);
            if (this.cursor != null && this.cursor.getCount() > 0) {
                StringBuffer txNameId = new StringBuffer();
                txNameId.append(cursor.getString(cursor.getColumnIndex("stop_name")));
                txNameId.append("[");
                txNameId.append(cursor.getString(cursor.getColumnIndex("stop_id")));
                txNameId.append("]");
                stopname.setText(txNameId.toString());
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.transfer_search_stops_item, parent, false);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiPopupList$1 */
    class C04411 implements Sectionizer<Cursor> {
        C04411() {
        }

        public String getSectionTitleForItem(Cursor instance) {
            String stopName = instance.getString(instance.getColumnIndex("stop_name"));
            if (stopName.codePointAt(0) >= "가".codePointAt(0) && stopName.codePointAt(0) < "나".codePointAt(0)) {
                return "ㄱ";
            }
            if (stopName.codePointAt(0) >= "나".codePointAt(0) && stopName.codePointAt(0) < "다".codePointAt(0)) {
                return "ㄴ";
            }
            if (stopName.codePointAt(0) >= "다".codePointAt(0) && stopName.codePointAt(0) < "라".codePointAt(0)) {
                return "ㄷ";
            }
            if (stopName.codePointAt(0) >= "라".codePointAt(0) && stopName.codePointAt(0) < "마".codePointAt(0)) {
                return "ㄹ";
            }
            if (stopName.codePointAt(0) >= "마".codePointAt(0) && stopName.codePointAt(0) < "바".codePointAt(0)) {
                return "ㅁ";
            }
            if (stopName.codePointAt(0) >= "바".codePointAt(0) && stopName.codePointAt(0) < "사".codePointAt(0)) {
                return "ㅂ";
            }
            if (stopName.codePointAt(0) >= "사".codePointAt(0) && stopName.codePointAt(0) < "아".codePointAt(0)) {
                return "ㅅ";
            }
            if (stopName.codePointAt(0) >= "아".codePointAt(0) && stopName.codePointAt(0) < "자".codePointAt(0)) {
                return "ㅇ";
            }
            if (stopName.codePointAt(0) >= "자".codePointAt(0) && stopName.codePointAt(0) < "차".codePointAt(0)) {
                return "ㅈ";
            }
            if (stopName.codePointAt(0) >= "차".codePointAt(0) && stopName.codePointAt(0) < "카".codePointAt(0)) {
                return "ㅊ";
            }
            if (stopName.codePointAt(0) >= "카".codePointAt(0) && stopName.codePointAt(0) < "타".codePointAt(0)) {
                return "ㅋ";
            }
            if (stopName.codePointAt(0) >= "타".codePointAt(0) && stopName.codePointAt(0) < "파".codePointAt(0)) {
                return "ㅌ";
            }
            if (stopName.codePointAt(0) >= "파".codePointAt(0) && stopName.codePointAt(0) < "하".codePointAt(0)) {
                return "ㅍ";
            }
            if (stopName.codePointAt(0) < "하".codePointAt(0) || stopName.codePointAt(0) >= "�K".codePointAt(0)) {
                return null;
            }
            return "ㅎ";
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.transfer_search_stops);
        this.alphabet = getResources().getStringArray(C0258R.array.popup_list_alphabet);
        this.intent = getIntent();
        ((TextView) findViewById(C0258R.id.transfer_stop_title)).setText(this.intent.getStringExtra(BookmarkColumns.TITLE));
        this.stoplist = (ListView) findViewById(C0258R.id.transfer_stops_list);
        this.cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, this.StopProjection, null, null, "stop_name ASC");
        this.adapter = new Stoplist(this, this.cursor);
        this.alphabetSectionizer = new C04411();
        this.sectionAdapter = new SimpleSectionAdapter(this, this.adapter, C0258R.layout.list_common_header, C0258R.id.list_header_title, this.alphabetSectionizer);
        this.stoplist.setAdapter(this.sectionAdapter);
        this.stoplist.setOnItemClickListener(new C02882());
        ListView alphabetList = (ListView) findViewById(C0258R.id.transfer_index_list);
        this.beginCharAdapter = new BeginningChar(this, 0, this.alphabet);
        alphabetList.setAdapter(this.beginCharAdapter);
        alphabetList.setOnItemClickListener(new C02893());
        ((Button) findViewById(C0258R.id.transfer_pop_cancel)).setOnClickListener(new C02904());
    }

    public void onBackPressed() {
        setResult(0, new Intent());
        finish();
    }
}
