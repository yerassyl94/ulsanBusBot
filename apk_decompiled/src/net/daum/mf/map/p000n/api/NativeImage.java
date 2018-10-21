package net.daum.mf.map.p000n.api;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import net.daum.android.map.MapBuildSettings;

/* renamed from: net.daum.mf.map.n.api.NativeImage */
public class NativeImage {
    static final int BOTTOM_CENTER = 31;
    static final int BOTTOM_LEFT = 30;
    static final int BOTTOM_RIGHT = 32;
    static final int MIDDLE_CENTER = 21;
    static final int MIDDLE_LEFT = 20;
    static final int MIDDLE_RIGHT = 22;
    public static int RESOURCE_ABSOLUTE_PATH = -3;
    public static int RESOURCE_CLASS_PATH = -2;
    public static int RESOURCE_ERROR = -1;
    static final int TOP_CENTER = 11;
    static final int TOP_LEFT = 10;
    static final int TOP_RIGHT = 12;
    private Bitmap bitmap;
    private int contentHeight;
    private int contentWidth;
    private int textColorA;
    private int textColorB;
    private int textColorG;
    private int textColorR;

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public NativeImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getWidth() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getWidth();
    }

    public int getHeight() {
        if (this.bitmap == null) {
            return 0;
        }
        return this.bitmap.getHeight();
    }

    public int getContentWidth() {
        return this.contentWidth;
    }

    public void setContentWidth(int contentWidth) {
        this.contentWidth = contentWidth;
    }

    public int getContentHeight() {
        return this.contentHeight;
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    public int getPixelLengthInBytes() {
        if (this.bitmap == null) {
            return 0;
        }
        if (this.bitmap.hasAlpha()) {
            return 4;
        }
        return 3;
    }

    public int readTo(int[] target, int targetSize) {
        if (this.bitmap == null || target == null) {
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.e(NativeImage.class.getName(), "bitmap == null || target == null");
            }
            return 0;
        }
        int width = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();
        int size = width * height;
        if (size > targetSize && !MapBuildSettings.getInstance().isDistribution()) {
            Log.e(NativeImage.class.getName(), "not enough targetSize=" + targetSize);
        }
        try {
            this.bitmap.getPixels(target, 0, width, 0, 0, width, height);
            return size;
        } catch (Exception e) {
            Log.e(NativeImage.class.getName(), "" + e.getMessage());
            return 0;
        }
    }

    public static NativeImage newNativeImage(ByteBuffer dataBuffer, int length, String name, float targetScale) {
        boolean hasArray;
        byte[] data;
        try {
            hasArray = dataBuffer.hasArray();
        } catch (UnsupportedOperationException e) {
            hasArray = false;
        } catch (Exception e2) {
            hasArray = false;
        }
        if (hasArray) {
            data = dataBuffer.array();
        } else {
            data = new byte[length];
            dataBuffer.get(data);
        }
        Bitmap bitmap = null;
        try {
            Options opts = new Options();
            if (targetScale > 0.0f && targetScale != 1.0f) {
                opts.inDensity = 160;
                opts.inTargetDensity = Math.round(160.0f * targetScale);
            }
            bitmap = BitmapFactory.decodeByteArray(data, 0, length, opts);
        } catch (Exception e3) {
            Log.e(NativeImage.class.getName(), "" + e3.getMessage());
        }
        if (bitmap == null) {
            return null;
        }
        return new NativeImage(bitmap);
    }

    public void setTextColor(int a, int r, int g, int b) {
        this.textColorA = a;
        this.textColorR = r;
        this.textColorG = g;
        this.textColorB = b;
    }

    public int getTextColorA() {
        return this.textColorA;
    }

    public int getTextColorR() {
        return this.textColorR;
    }

    public int getTextColorG() {
        return this.textColorG;
    }

    public int getTextColorB() {
        return this.textColorB;
    }

    public static RectF sizeWithFont(String text, int fontSize, float maxWidth) {
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Style.FILL);
        textPaint.setTextSize((float) fontSize);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 0, 0, 0);
        StaticLayout sl = new StaticLayout(text, textPaint, (int) maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        return new RectF(0.0f, 0.0f, (float) sl.getWidth(), (float) sl.getHeight());
    }

    public static NativeImage newNativeImageWithText_SingleLine(String text, int frameWidth, int frameHeight, float maxWidth, float maxHeight, int color, int fontSize, int textAlignment) {
        if (text == null) {
            return null;
        }
        if (!MapBuildSettings.getInstance().isDistribution()) {
            Log.i(NativeImage.class.getName(), String.format("newNativeImageWithText(%s, %d, %d)", new Object[]{text, Integer.valueOf(frameWidth), Integer.valueOf(frameHeight)}));
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Style.FILL);
        textPaint.setTextSize((float) fontSize);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setAntiAlias(true);
        FontMetrics fm = textPaint.getFontMetrics();
        Rect textRect = new Rect();
        textPaint.setARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
        CharSequence ellipsisText = TextUtils.ellipsize(text, textPaint, maxWidth, TruncateAt.END);
        String ellipsisString = ellipsisText.toString();
        textPaint.getTextBounds(ellipsisString, 0, ellipsisString.length(), textRect);
        int textWidth = textRect.right - textRect.left;
        int textHeight = textRect.bottom - textRect.top;
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_8888);
        } catch (Exception e) {
            Log.e(NativeImage.class.getName(), "" + e.getMessage());
        }
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(255, 255, 0, 0);
        float x = (maxWidth / 2.0f) - (((float) textWidth) / 2.0f);
        float y = ((float) (textHeight / 2)) - ((fm.ascent + fm.descent) / 2.0f);
        NativeImage image = new NativeImage(bitmap);
        canvas.drawText(ellipsisText.toString(), x, 1.0f + y, textPaint);
        image.setContentWidth(textWidth);
        image.setContentHeight(textHeight);
        return image;
    }

    private static Layout createWorkingLayout(String workingText, int width, TextPaint paint) {
        return new StaticLayout(workingText, paint, width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public static NativeImage newNativeImageWithText(String text, int frameWidth, int frameHeight, float maxWidth, float maxHeight, int color, int fontSize, int textAlignment) {
        if (text == null) {
            return null;
        }
        CharSequence ellipsisText;
        TextPaint textPaint = new TextPaint();
        Alignment layoutAlignment = Alignment.ALIGN_NORMAL;
        if (textAlignment == 21) {
            layoutAlignment = Alignment.ALIGN_CENTER;
        }
        textPaint.setStyle(Style.FILL);
        textPaint.setTextSize((float) fontSize);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
        StaticLayout sl = new StaticLayout(text, textPaint, (int) maxWidth, layoutAlignment, 1.0f, 0.0f, false);
        int numberOfLines = sl.getLineCount();
        int height = sl.getHeight();
        int width = sl.getWidth();
        if (!MapBuildSettings.getInstance().isDistribution()) {
            Log.d(NativeImage.class.getName(), String.format(">>>> newNativeImageWithText2(%s, %d, %d) / %d lines width= %d height=%d", new Object[]{text, Integer.valueOf(frameWidth), Integer.valueOf(frameHeight), Integer.valueOf(numberOfLines), Integer.valueOf(width), Integer.valueOf(height)}));
        }
        String ELLIPSIS = "...";
        boolean truncate = false;
        if (numberOfLines == 1) {
            Rect textRect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            width = textRect.width();
            height = textRect.height();
            ellipsisText = null;
        } else {
            if (((int) maxHeight) < height) {
                int maxLines = numberOfLines - 1;
                Layout layout = NativeImage.createWorkingLayout(text, (int) maxWidth, textPaint);
                if (layout.getLineCount() > maxLines) {
                    String workingText = text.substring(0, layout.getLineEnd(maxLines - 1)).trim();
                    while (NativeImage.createWorkingLayout(workingText + "...", (int) maxWidth, textPaint).getLineCount() > maxLines) {
                        int lastSpace = workingText.lastIndexOf(32);
                        if (lastSpace == -1) {
                            break;
                        }
                        workingText = workingText.substring(0, lastSpace);
                    }
                    truncate = true;
                    Object ellipsisText2 = workingText + "...";
                }
            }
            ellipsisText = null;
        }
        if (!truncate) {
            ellipsisText = text;
        }
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_8888);
        } catch (Exception e) {
            Log.e(NativeImage.class.getName(), "" + e.getMessage());
        }
        if (bitmap == null) {
            return null;
        }
        int finalHeight;
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        NativeImage nativeImage = new NativeImage(bitmap);
        new StaticLayout(ellipsisText, textPaint, (int) maxWidth, layoutAlignment, 1.0f, 0.0f, false).draw(canvas);
        if (((int) maxHeight) < height) {
            finalHeight = (int) maxHeight;
        } else {
            finalHeight = height;
        }
        nativeImage.setContentWidth(width);
        nativeImage.setContentHeight(finalHeight + 2);
        return nativeImage;
    }

    public static NativeImage newNativeImage(String path, float targetScale) {
        Resources res = NativeMapEngineContext.getInstance().getApplicationContext().getResources();
        int id = NativeImage.getResourceIdFromPath(path);
        if (id == RESOURCE_CLASS_PATH || id == RESOURCE_ABSOLUTE_PATH) {
            return NativeImage.newNativeInternalImage(path, targetScale, id);
        }
        if (id < 0) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            Options opts = new Options();
            if (targetScale > 0.0f && targetScale != 1.0f) {
                opts.inDensity = 160;
                opts.inTargetDensity = Math.round(160.0f * targetScale);
            }
            bitmap = BitmapFactory.decodeResource(res, id, opts);
        } catch (Exception e) {
            Log.e(NativeImage.class.getName(), "" + e.getMessage());
        }
        if (bitmap != null) {
            return new NativeImage(bitmap);
        }
        return null;
    }

    private static NativeImage newNativeInternalImage(String fullPath, float targetScale, int type) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            String path = fullPath.substring(fullPath.indexOf(":") + 1);
            if (type == RESOURCE_CLASS_PATH) {
                is = NativeMapEngineContext.getInstance().getClass().getResourceAsStream(path);
            } else if (type == RESOURCE_ABSOLUTE_PATH) {
                File file = new File(path);
                if (file.exists()) {
                    is = new FileInputStream(file);
                }
            }
            if (is != null) {
                Options opts = new Options();
                if (targetScale > 0.0f && targetScale != 1.0f) {
                    opts.inDensity = 160;
                    opts.inTargetDensity = Math.round(160.0f * targetScale);
                }
                bitmap = BitmapFactory.decodeStream(is, null, opts);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (bitmap != null) {
                    return new NativeImage(bitmap);
                }
                return null;
            } else if (is == null) {
                return null;
            } else {
                try {
                    is.close();
                    return null;
                } catch (IOException e2) {
                    return null;
                }
            }
        } catch (Exception e3) {
            Log.e(NativeImage.class.getName(), "" + e3.getMessage());
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e5) {
                }
            }
        }
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    private static int getResourceIdFromPath(String path) {
        if (path == null) {
            return RESOURCE_ERROR;
        }
        String fullName = path.trim();
        int len = fullName.length();
        int cPos = fullName.indexOf(":");
        if (cPos < 0) {
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.e(NativeImage.class.getName(), "not resId format: '" + fullName + "'");
            }
            return RESOURCE_ERROR;
        }
        String schemaString = fullName.substring(0, cPos);
        int idStartPos = cPos + 1;
        if (idStartPos >= len) {
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.e(NativeImage.class.getName(), "not resId format: '" + fullName + "'");
            }
            return RESOURCE_ERROR;
        } else if (schemaString.equals("res")) {
            String idString = fullName.substring(idStartPos);
            int id = RESOURCE_ERROR;
            try {
                return Integer.parseInt(idString, 10);
            } catch (Throwable th) {
                Log.e(NativeImage.class.getName(), "cannot parse int : '" + idString + "'");
                return id;
            }
        } else if (schemaString.equals("classPath")) {
            return RESOURCE_CLASS_PATH;
        } else {
            if (schemaString.equals("absolutePath")) {
                return RESOURCE_ABSOLUTE_PATH;
            }
            return RESOURCE_ERROR;
        }
    }
}
