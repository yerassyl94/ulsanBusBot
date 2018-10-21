package net.daum.mf.map.common.io;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamUtils {
    public static String toString(InputStream is, String encoding, int contentLength) {
        StringBuilder sb;
        String result = null;
        if (contentLength > 0) {
            try {
                sb = new StringBuilder(contentLength);
            } catch (Exception e) {
                Log.e(InputStreamUtils.class.getName(), "" + e.getMessage());
            } finally {
                CloseableUtils.closeQuietly(is);
            }
        } else {
            sb = new StringBuilder();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            sb.append(line).append("\n");
        }
        result = sb.toString();
        CloseableUtils.closeQuietly(is);
        return result;
    }
}
