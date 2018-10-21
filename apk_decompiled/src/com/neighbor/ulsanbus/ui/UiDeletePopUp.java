package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;

public class UiDeletePopUp extends Activity {
    private Builder alertDialogBuilder;
    private Context mContext;
    private String mode;

    /* renamed from: com.neighbor.ulsanbus.ui.UiDeletePopUp$1 */
    class C02731 implements OnClickListener {
        C02731() {
        }

        public void onClick(DialogInterface dialog, int id) {
            UiDeletePopUp.this.setResult(0, new Intent());
            dialog.dismiss();
            UiDeletePopUp.this.finish();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiDeletePopUp$2 */
    class C02742 implements OnClickListener {
        C02742() {
        }

        public void onClick(DialogInterface dialog, int id) {
            if (UiDeletePopUp.this.mode.equals(DataConst.TABLE_FAVORITE_INFO)) {
                UiDeletePopUp.this.getContentResolver().delete(DataConst.CONTENT_FAVORITES_URI, null, null);
                UiDeletePopUp.this.getContentResolver().notifyChange(DataConst.CONTENT_FAVORITES_URI, null);
            } else if (UiDeletePopUp.this.mode.equals("search_route")) {
                UiDeletePopUp.this.getContentResolver().delete(DataConst.CONTENT_ROUTE_SEARCH_URI, null, null);
                UiDeletePopUp.this.getContentResolver().notifyChange(DataConst.CONTENT_ROUTE_SEARCH_URI, null);
            } else {
                UiDeletePopUp.this.getContentResolver().delete(DataConst.CONTENT_STOP_SEARCH_URI, null, null);
                UiDeletePopUp.this.getContentResolver().notifyChange(DataConst.CONTENT_STOP_SEARCH_URI, null);
            }
            UiDeletePopUp.this.setResult(-1, new Intent());
            Toast.makeText(UiDeletePopUp.this, "리스트가 삭제 되었습니다.", 0).show();
            dialog.dismiss();
            UiDeletePopUp.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mode = bundle.getString("delete_mode");
        }
        this.mContext = this;
        setContentView(C0258R.layout.delete_popup);
    }

    protected void onResume() {
        super.onResume();
        this.alertDialogBuilder = new Builder(this);
        this.alertDialogBuilder.setTitle(" 확인");
        this.alertDialogBuilder.setMessage(this.mode.equals(DataConst.TABLE_FAVORITE_INFO) ? "즐겨찾기를 전체 삭제 하시겠습니까?" : "최근 검색  목록을  전체 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("Yes", new C02742()).setNegativeButton("No", new C02731()).show();
    }
}
