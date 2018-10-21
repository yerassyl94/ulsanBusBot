package com.google.zxing.client.android.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.util.regex.Pattern;

final class CameraConfigurationManager {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final String TAG = CameraConfigurationManager.class.getSimpleName();
    private static final int TEN_DESIRED_ZOOM = 27;
    private Point cameraResolution;
    private final Context context;
    private int previewFormat;
    private String previewFormatString;
    private Point screenResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    void initFromCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        this.previewFormat = parameters.getPreviewFormat();
        this.previewFormatString = parameters.get("preview-format");
        Log.d(TAG, "Default preview format: " + this.previewFormat + '/' + this.previewFormatString);
        Display display = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        this.screenResolution = new Point(display.getWidth(), display.getHeight());
        Log.d(TAG, "Screen resolution: " + this.screenResolution);
        this.cameraResolution = getCameraResolution(parameters, this.screenResolution);
        Log.d(TAG, "Camera resolution: " + this.cameraResolution);
    }

    void setDesiredCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        Log.d(TAG, "Setting preview size: " + this.cameraResolution);
        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        setFlash(parameters);
        setZoom(parameters);
        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }

    Point getScreenResolution() {
        return this.screenResolution;
    }

    int getPreviewFormat() {
        return this.previewFormat;
    }

    String getPreviewFormatString() {
        return this.previewFormatString;
    }

    private static Point getCameraResolution(Parameters parameters, Point screenResolution) {
        String previewSizeValueString = parameters.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }
        Point cameraResolution = null;
        if (previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }
        if (cameraResolution == null) {
            return new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
        }
        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            String previewSize2 = previewSize2.trim();
            int dimPosition = previewSize2.indexOf(120);
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize2);
            } else {
                try {
                    int newX = Integer.parseInt(previewSize2.substring(0, dimPosition));
                    int newY = Integer.parseInt(previewSize2.substring(dimPosition + 1));
                    int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
                    if (newDiff == 0) {
                        bestX = newX;
                        bestY = newY;
                        break;
                    } else if (newDiff < diff) {
                        bestX = newX;
                        bestY = newY;
                        diff = newDiff;
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Bad preview-size: " + previewSize2);
                }
            }
        }
        if (bestX <= 0 || bestY <= 0) {
            return null;
        }
        return new Point(bestX, bestY);
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        String[] split = COMMA_PATTERN.split(stringValues);
        int length = split.length;
        int i = 0;
        while (i < length) {
            try {
                double value = Double.parseDouble(split[i].trim());
                int tenValue = (int) (10.0d * value);
                if (Math.abs(((double) tenDesiredZoom) - value) < ((double) Math.abs(tenDesiredZoom - tenBestValue))) {
                    tenBestValue = tenValue;
                }
                i++;
            } catch (NumberFormatException e) {
                return tenDesiredZoom;
            }
        }
        return tenBestValue;
    }

    private void setFlash(Parameters parameters) {
        if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) {
            parameters.set("flash-value", 1);
        } else {
            parameters.set("flash-value", 2);
        }
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString == null || Boolean.parseBoolean(zoomSupportedString)) {
            int tenMaxZoom;
            int tenDesiredZoom = 27;
            String maxZoomString = parameters.get("max-zoom");
            if (maxZoomString != null) {
                try {
                    tenMaxZoom = (int) (10.0d * Double.parseDouble(maxZoomString));
                    if (27 > tenMaxZoom) {
                        tenDesiredZoom = tenMaxZoom;
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Bad max-zoom: " + maxZoomString);
                }
            }
            String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
            if (takingPictureZoomMaxString != null) {
                try {
                    tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                    if (tenDesiredZoom > tenMaxZoom) {
                        tenDesiredZoom = tenMaxZoom;
                    }
                } catch (NumberFormatException e2) {
                    Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
                }
            }
            String motZoomValuesString = parameters.get("mot-zoom-values");
            if (motZoomValuesString != null) {
                tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
            }
            String motZoomStepString = parameters.get("mot-zoom-step");
            if (motZoomStepString != null) {
                try {
                    int tenZoomStep = (int) (10.0d * Double.parseDouble(motZoomStepString.trim()));
                    if (tenZoomStep > 1) {
                        tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                    }
                } catch (NumberFormatException e3) {
                }
            }
            if (!(maxZoomString == null && motZoomValuesString == null)) {
                parameters.set("zoom", String.valueOf(((double) tenDesiredZoom) / 10.0d));
            }
            if (takingPictureZoomMaxString != null) {
                parameters.set("taking-picture-zoom", tenDesiredZoom);
            }
        }
    }
}
