package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import com.google.zxing.client.android.AndroidHttpClient;
import com.google.zxing.client.android.LocaleManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

final class ProductResultInfoRetriever extends SupplementalInfoRetriever {
    private static final String BASE_PRODUCT_URI = ("http://www.google." + LocaleManager.getProductSearchCountryTLD() + "/m/products?ie=utf8&oe=utf8&scoring=p&source=zxing&q=");
    private static final Pattern PRODUCT_NAME_PRICE_PATTERN = Pattern.compile("owb63p\">([^<]+).+zdi3pb\">([^<]+)");
    private static final String TAG = ProductResultInfoRetriever.class.getSimpleName();
    private final String productID;

    ProductResultInfoRetriever(TextView textView, String productID, Handler handler, Context context) {
        super(textView, handler, context);
        this.productID = productID;
    }

    void retrieveSupplementalInfo() throws IOException, InterruptedException {
        String uri = BASE_PRODUCT_URI + URLEncoder.encode(this.productID, "UTF-8");
        HttpResponse response = AndroidHttpClient.newInstance(null).execute(new HttpGet(uri));
        if (response.getStatusLine().getStatusCode() == 200) {
            Matcher matcher = PRODUCT_NAME_PRICE_PATTERN.matcher(consume(response.getEntity()));
            if (matcher.find()) {
                append(matcher.group(1));
                append(matcher.group(2));
            }
            setLink(uri);
        }
    }

    private static String consume(HttpEntity entity) {
        Log.d(TAG, "Consuming entity");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = entity.getContent();
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead <= 0) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                }
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
        }
        try {
            return new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }
    }
}
