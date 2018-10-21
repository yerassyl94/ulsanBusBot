package net.daum.mf.map.api;

import android.graphics.Bitmap;
import android.view.View;
import java.util.Date;
import net.daum.android.map.MapEngineManager;
import net.daum.android.map.util.BitmapUtils;
import net.daum.mf.map.api.MapPoint.PlainCoordinate;
import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.internal.NativePOIItemMarkerManager;
import net.daum.mf.map.task.MapTaskManager;

public class MapPOIItem {
    static final String CUSTOM_CALLOUT_BALLOON_IMAGE_DIR = "image/custom_info_window";
    static final String CUSTOM_PRESSED_CALLOUT_BALLOON_IMAGE_DIR = "image/custom_pressed_info_window";
    private float alpha = 1.0f;
    long createdTime = -1;
    private Bitmap customCalloutBalloonBitmap;
    private View customCalloutBalloonView = null;
    private ImageOffset customImageAnchorPointOffset = null;
    private float customImageAnchorRatioFromTopLeftOriginX = -1.0f;
    private float customImageAnchorRatioFromTopLeftOriginY = -1.0f;
    private boolean customImageAutoscale = true;
    private Bitmap customImageBitmap = null;
    private int customImageResourceId = 0;
    private Bitmap customPressedCalloutBalloonBitmap;
    private View customPressedCalloutBalloonView = null;
    private Bitmap customSelectedImageBitmap = null;
    private int customSelectedImageResourceId = 0;
    private boolean draggable = false;
    private int id = -1;
    private String itemName = null;
    private int leftSideButtonResourceIdOnCalloutBalloon = 0;
    private MapPoint mapPoint = null;
    private MarkerType markerType = MarkerType.BluePin;
    private boolean moveToCenterOnSelect = true;
    private int rightSideButtonResourceIdOnCalloutBalloon = 0;
    private float rotation = 0.0f;
    private MarkerType selectedMarkerType = null;
    private ShowAnimationType showAnimationType = ShowAnimationType.NoAnimation;
    private boolean showCalloutBalloonOnTouch = true;
    private boolean showDisclosureButtonOnCalloutBalloon = true;
    private int tag = 0;
    private Object userObject = null;

    public enum CalloutBalloonButtonType {
        MainButton,
        LeftSideButton,
        RightSideButton
    }

    public static class ImageOffset {
        public int offsetX;
        public int offsetY;

        public ImageOffset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    public enum MarkerType {
        BluePin,
        RedPin,
        YellowPin,
        CustomImage
    }

    public enum ShowAnimationType {
        NoAnimation,
        DropFromHeaven,
        SpringFromGround
    }

    public void setRotation(final float rotation) {
        this.rotation = rotation;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.setRotation(MapPOIItem.this.getId(), rotation);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setAlpha(final float alpha) {
        this.alpha = alpha;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.setAlpha(MapPOIItem.this.getId(), alpha);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getRotation() {
        return this.rotation;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
        final int mapComponentId = getId();
        final String name = itemName;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.setName(mapComponentId, name);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapPoint getMapPoint() {
        return this.mapPoint;
    }

    public void setMapPoint(MapPoint mapPoint) {
        this.mapPoint = mapPoint;
        PlainCoordinate mapPointWCONG = mapPoint.getMapPointWCONGCoord();
        final NativeMapCoord nativeMapPoint = new NativeMapCoord(mapPointWCONG.f13x, mapPointWCONG.f14y, 2);
        final int mapComponentId = getId();
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.setCoord(mapComponentId, nativeMapPoint);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void moveWithAnimation(MapPoint mapPoint, Boolean animate) {
        this.mapPoint = mapPoint;
        PlainCoordinate mapPointWCONG = mapPoint.getMapPointWCONGCoord();
        final Boolean useAnimation = animate;
        final NativeMapCoord nativeMapPoint = new NativeMapCoord(mapPointWCONG.f13x, mapPointWCONG.f14y, 2);
        final int mapComponentId = getId();
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.moveWithAnimation(mapComponentId, nativeMapPoint, useAnimation);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MarkerType getMarkerType() {
        return this.markerType;
    }

    public void setMarkerType(MarkerType markerType) {
        this.markerType = markerType;
    }

    public MarkerType getSelectedMarkerType() {
        return this.selectedMarkerType;
    }

    public void setSelectedMarkerType(MarkerType markerType) {
        this.selectedMarkerType = markerType;
    }

    public ShowAnimationType getShowAnimationType() {
        return this.showAnimationType;
    }

    public void setShowAnimationType(ShowAnimationType showAnimationType) {
        this.showAnimationType = showAnimationType;
    }

    public boolean isShowCalloutBalloonOnTouch() {
        return this.showCalloutBalloonOnTouch;
    }

    public void setShowCalloutBalloonOnTouch(boolean showCalloutBalloonOnTouch) {
        this.showCalloutBalloonOnTouch = showCalloutBalloonOnTouch;
    }

    public boolean isShowDisclosureButtonOnCalloutBalloon() {
        return this.showDisclosureButtonOnCalloutBalloon;
    }

    public void setShowDisclosureButtonOnCalloutBalloon(boolean showDisclosureButtonOnCalloutBalloon) {
        this.showDisclosureButtonOnCalloutBalloon = showDisclosureButtonOnCalloutBalloon;
    }

    public void setLeftSideButtonResourceIdOnCalloutBalloon(int imageResourceId) {
        this.leftSideButtonResourceIdOnCalloutBalloon = imageResourceId;
    }

    public void setRightSideButtonResourceIdOnCalloutBalloon(int imageResourceId) {
        this.rightSideButtonResourceIdOnCalloutBalloon = imageResourceId;
    }

    public int getLeftSideButtonResourceIdOnCalloutBalloon() {
        return this.leftSideButtonResourceIdOnCalloutBalloon;
    }

    public int getRightSideButtonResourceIdOnCalloutBalloon() {
        return this.rightSideButtonResourceIdOnCalloutBalloon;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public int getCustomImageResourceId() {
        return this.customImageResourceId;
    }

    public int getCustomSelectedImageResourceId() {
        return this.customSelectedImageResourceId;
    }

    public Bitmap getCustomImageBitmap() {
        return this.customImageBitmap;
    }

    public Bitmap getCustomSelectedImageBitmap() {
        return this.customSelectedImageBitmap;
    }

    public void setCustomImageBitmap(Bitmap bm) {
        this.customImageBitmap = bm;
    }

    public void setCustomSelectedImageBitmap(Bitmap bm) {
        this.customSelectedImageBitmap = bm;
    }

    public void setCustomImageResourceId(int imageResourceId) {
        this.customImageResourceId = imageResourceId;
    }

    public void setCustomSelectedImageResourceId(int imageResourceId) {
        this.customSelectedImageResourceId = imageResourceId;
    }

    public void setCustomImageAnchor(float ratioFromTopLeftOriginX, float ratioFromTopLeftOriginY) {
        this.customImageAnchorRatioFromTopLeftOriginX = ratioFromTopLeftOriginX;
        this.customImageAnchorRatioFromTopLeftOriginY = ratioFromTopLeftOriginY;
    }

    public float getCustomImageAnchorRatioFromTopLeftOriginX() {
        return this.customImageAnchorRatioFromTopLeftOriginX;
    }

    public float getCustomImageAnchorRatioFromTopLeftOriginY() {
        return this.customImageAnchorRatioFromTopLeftOriginY;
    }

    public ImageOffset getCustomImageAnchorPointOffset() {
        return this.customImageAnchorPointOffset;
    }

    public void setCustomImageAnchorPointOffset(ImageOffset customImageAnchorPointOffset) {
        this.customImageAnchorPointOffset = customImageAnchorPointOffset;
    }

    public void setCustomCalloutBalloon(View view) {
        this.customCalloutBalloonView = view;
        this.customCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(view);
    }

    public View getCustomCalloutBalloon() {
        return this.customCalloutBalloonView;
    }

    public Bitmap getCustomCalloutBalloonBitmap() {
        return this.customCalloutBalloonBitmap;
    }

    public void setCustomPressedCalloutBalloon(View view) {
        this.customPressedCalloutBalloonView = view;
        this.customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(view);
    }

    public void setCustomCalloutBalloonBitmap(Bitmap customCalloutBalloonBitmap) {
        this.customCalloutBalloonBitmap = customCalloutBalloonBitmap;
    }

    public void setCustomPressedCalloutBalloonBitmap(Bitmap customPressedCalloutBalloonBitmap) {
        this.customPressedCalloutBalloonBitmap = customPressedCalloutBalloonBitmap;
    }

    public Bitmap getCustomPressedCalloutBalloonBitmap() {
        return this.customPressedCalloutBalloonBitmap;
    }

    public View getCustomPressedCalloutBalloon() {
        return this.customPressedCalloutBalloonView;
    }

    public boolean isCustomImageAutoscale() {
        return this.customImageAutoscale;
    }

    public void setCustomImageAutoscale(boolean enableAutoscale) {
        this.customImageAutoscale = enableAutoscale;
    }

    int getId() {
        return this.id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getCustomImageResourcePath() {
        if (this.customImageResourceId == 0) {
            return null;
        }
        return String.format("res:%d", new Object[]{Integer.valueOf(this.customImageResourceId)});
    }

    String getCustomSelectedImageResourcePath() {
        if (this.customSelectedImageResourceId == 0) {
            return null;
        }
        return String.format("res:%d", new Object[]{Integer.valueOf(this.customSelectedImageResourceId)});
    }

    long getCreatedTime() {
        if (this.createdTime == -1) {
            this.createdTime = new Date().getTime() + ((long) hashCode());
        }
        return this.createdTime;
    }

    private String getCustomImageNameWithPrefix(String prefix) {
        return prefix + "_" + getTag() + "_" + getCreatedTime();
    }

    String getCustomImageFileName() {
        return getCustomImageNameWithPrefix("customImage_") + ".png";
    }

    String getCustomSelectedImageFileName() {
        return getCustomImageNameWithPrefix("customImage_") + "_selected.png";
    }

    String getCustomCalloutBalloonImageFileName() {
        return getCustomImageNameWithPrefix("customCalloutImage_") + ".png";
    }

    String getCustomPressedCalloutBalloonImageFileName() {
        return getCustomImageNameWithPrefix("customCalloutImage_") + "_pressed.png";
    }

    public boolean isMoveToCenterOnSelect() {
        return this.moveToCenterOnSelect;
    }

    public void setMoveToCenterOnSelect(boolean moveToCenterOnSelect) {
        this.moveToCenterOnSelect = moveToCenterOnSelect;
    }
}
