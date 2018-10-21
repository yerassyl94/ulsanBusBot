package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;

public class UiNoticePopup extends Activity {
    private RecentNotice mNotice;

    /* renamed from: com.neighbor.ulsanbus.ui.UiNoticePopup$1 */
    class C02871 implements OnClickListener {
        C02871() {
        }

        public void onClick(View v) {
            UiNoticePopup.this.setResult(100);
            UiNoticePopup.this.finish();
        }
    }

    protected static class RecentNotice {
        String content;
        String date;
        String title;

        protected RecentNotice() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.loading_notification);
        TextView titletx = (TextView) findViewById(C0258R.id.notice_title);
        TextView dateTx = (TextView) findViewById(C0258R.id.notice_date);
        TextView contentTx = (TextView) findViewById(C0258R.id.notice_content);
        contentTx.setMovementMethod(ScrollingMovementMethod.getInstance());
        ((Button) findViewById(C0258R.id.btn_cancel)).setOnClickListener(new C02871());
        this.mNotice = new RecentNotice();
        try {
            Cursor cs = getContentResolver().query(DataConst.CONTENT_NOTICE_URI, null, null, null, null);
            if (cs.moveToFirst()) {
                this.mNotice.title = cs.getString(cs.getColumnIndex(DataConst.KEY_NOTICE_TITLE));
                this.mNotice.date = cs.getString(cs.getColumnIndex(DataConst.KEY_NOTICE_DATE));
                this.mNotice.content = cs.getString(cs.getColumnIndex(DataConst.KEY_NOTICE_CONTENT));
            }
            cs.close();
            titletx.setText(this.mNotice.title);
            StringBuffer sb = new StringBuffer();
            sb.append(this.mNotice.date.substring(0, 8));
            sb.insert(4, ".");
            sb.insert(7, ".");
            dateTx.setText(sb.toString());
            contentTx.setText(this.mNotice.content);
        } catch (Exception e) {
            Log.d("Load_NoticeActivity", "" + e.getMessage());
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        setResult(100);
        finish();
    }
}
