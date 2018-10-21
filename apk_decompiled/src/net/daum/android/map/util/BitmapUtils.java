package net.daum.android.map.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    public static Bitmap createBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        if (v.getMeasuredHeight() <= 0) {
            v.measure(-2, -2);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Config.ARGB_8888);
        c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static Bitmap createBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static File saveBitmapAsPngFile(Context context, Bitmap bmp, String dirPath, String fileName) {
        Exception e;
        Throwable th;
        File file = new File(new ContextWrapper(context).getDir("dirPath", 0), fileName);
        FileOutputStream out = null;
        try {
            FileOutputStream out2 = new FileOutputStream(file);
            try {
                bmp.compress(CompressFormat.PNG, 90, out2);
                if (out2 != null) {
                    try {
                        out2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        out = out2;
                    }
                }
                out = out2;
            } catch (Exception e3) {
                e = e3;
                out = out2;
                try {
                    e.printStackTrace();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    return file;
                } catch (Throwable th2) {
                    th = th2;
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                if (out != null) {
                    out.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            if (out != null) {
                out.close();
            }
            return file;
        }
        return file;
    }

    public static void deleteFile(Context context, String dirPath, String fileName) {
        File file = new File(new ContextWrapper(context).getDir("dirPath", 0), fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteAllFilesInDirectory(Context context, String dirPath) {
        purgeDirectory(new ContextWrapper(context).getDir("dirPath", 0));
    }

    private static void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }
}
