package com.google.zxing.client.android.history;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import com.google.zxing.Result;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.CaptureActivity;
import java.util.List;

final class HistoryClickListener implements OnClickListener {
    private final CaptureActivity activity;
    private final HistoryManager historyManager;
    private final List<Result> items;

    HistoryClickListener(HistoryManager historyManager, CaptureActivity activity, List<Result> items) {
        this.historyManager = historyManager;
        this.activity = activity;
        this.items = items;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == this.items.size()) {
            Uri historyFile = HistoryManager.saveHistory(this.historyManager.buildHistory().toString());
            if (historyFile == null) {
                Builder builder = new Builder(this.activity);
                builder.setMessage(C0224R.string.msg_unmount_usb);
                builder.setPositiveButton(C0224R.string.button_ok, null);
                builder.show();
                return;
            }
            Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
            intent.addFlags(524288);
            String subject = this.activity.getResources().getString(C0224R.string.history_email_title);
            intent.putExtra("android.intent.extra.SUBJECT", subject);
            intent.putExtra("android.intent.extra.TEXT", subject);
            intent.putExtra("android.intent.extra.STREAM", historyFile);
            intent.setType("text/csv");
            this.activity.startActivity(intent);
        } else if (i == this.items.size() + 1) {
            this.historyManager.clearHistory();
        } else {
            Message.obtain(this.activity.getHandler(), C0224R.id.decode_succeeded, (Result) this.items.get(i)).sendToTarget();
        }
    }
}
