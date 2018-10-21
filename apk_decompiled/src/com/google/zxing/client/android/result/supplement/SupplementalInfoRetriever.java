package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class SupplementalInfoRetriever implements Callable<Void> {
    private static ExecutorService executorInstance = null;
    private final Context context;
    private final Handler handler;
    private final WeakReference<TextView> textViewRef;

    /* renamed from: com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever$1 */
    static class C02331 implements ThreadFactory {
        C02331() {
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    }

    abstract void retrieveSupplementalInfo() throws IOException, InterruptedException;

    private static synchronized ExecutorService getExecutorService() {
        ExecutorService executorService;
        synchronized (SupplementalInfoRetriever.class) {
            if (executorInstance == null) {
                executorInstance = Executors.newCachedThreadPool(new C02331());
            }
            executorService = executorInstance;
        }
        return executorService;
    }

    public static void maybeInvokeRetrieval(TextView textView, ParsedResult result, Handler handler, Context context) {
        SupplementalInfoRetriever retriever = null;
        if (result instanceof URIParsedResult) {
            retriever = new URIResultInfoRetriever(textView, (URIParsedResult) result, handler, context);
        } else if (result instanceof ProductParsedResult) {
            retriever = new ProductResultInfoRetriever(textView, ((ProductParsedResult) result).getProductID(), handler, context);
        } else if (result instanceof ISBNParsedResult) {
            retriever = new ProductResultInfoRetriever(textView, ((ISBNParsedResult) result).getISBN(), handler, context);
        }
        if (retriever != null) {
            ExecutorService executor = getExecutorService();
            executor.submit(new KillerCallable(executor.submit(retriever), 10, TimeUnit.SECONDS));
        }
    }

    SupplementalInfoRetriever(TextView textView, Handler handler, Context context) {
        this.textViewRef = new WeakReference(textView);
        this.handler = handler;
        this.context = context;
    }

    public final Void call() throws IOException, InterruptedException {
        retrieveSupplementalInfo();
        return null;
    }

    final void append(final String newText) throws InterruptedException {
        final TextView textView = (TextView) this.textViewRef.get();
        if (textView == null) {
            throw new InterruptedException();
        }
        this.handler.post(new Runnable() {
            public void run() {
                textView.append(Html.fromHtml(newText + '\n'));
            }
        });
    }

    final void setLink(final String uri) {
        ((TextView) this.textViewRef.get()).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uri));
                intent.addFlags(524288);
                SupplementalInfoRetriever.this.context.startActivity(intent);
            }
        });
    }
}
