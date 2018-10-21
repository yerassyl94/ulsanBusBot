package com.google.zxing.client.android.result.supplement;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import com.google.zxing.client.android.AndroidHttpClient;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.URIParsedResult;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;

final class URIResultInfoRetriever extends SupplementalInfoRetriever {
    private static final String[] REDIRECTOR_HOSTS = new String[]{"http://bit.ly/", "http://tinyurl.com/", "http://tr.im/", "http://goo.gl/", "http://ow.ly/"};
    private final String redirectString;
    private final URIParsedResult result;

    URIResultInfoRetriever(TextView textView, URIParsedResult result, Handler handler, Context context) {
        super(textView, handler, context);
        this.redirectString = context.getString(C0224R.string.msg_redirect);
        this.result = result;
    }

    void retrieveSupplementalInfo() throws IOException, InterruptedException {
        String oldURI = this.result.getURI();
        String newURI = unredirect(oldURI);
        int count = 0;
        while (count < 3 && !oldURI.equals(newURI)) {
            append(this.redirectString + ": " + newURI);
            count++;
            oldURI = newURI;
            newURI = unredirect(newURI);
        }
        setLink(newURI);
    }

    private static String unredirect(String uri) throws IOException {
        if (!isRedirector(uri)) {
            return uri;
        }
        HttpResponse response = AndroidHttpClient.newInstance(null).execute(new HttpHead(uri));
        int status = response.getStatusLine().getStatusCode();
        if (status != 301 && status != 302) {
            return uri;
        }
        Header redirect = response.getFirstHeader("Location");
        if (redirect == null) {
            return uri;
        }
        String location = redirect.getValue();
        if (location != null) {
            return location;
        }
        return uri;
    }

    private static boolean isRedirector(String uri) {
        for (String redirectorHost : REDIRECTOR_HOSTS) {
            if (uri.startsWith(redirectorHost)) {
                return true;
            }
        }
        return false;
    }
}
