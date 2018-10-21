package net.daum.mf.map.common.io;

import java.io.Closeable;
import java.io.IOException;

public class CloseableUtils {
    public static void closeQuietly(Closeable target) {
        if (target != null) {
            try {
                target.close();
            } catch (IOException e) {
            }
        }
    }
}
