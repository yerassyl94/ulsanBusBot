package net.daum.mf.map.p000n.api;

import android.view.MotionEvent;
import net.daum.mf.map.common.MapEvent;
import net.daum.mf.map.common.MotionEventAdapter;

/* renamed from: net.daum.mf.map.n.api.NativeMapViewUiEvent */
public class NativeMapViewUiEvent extends MapEvent {
    public static final int ACTION_DOWN = 1;
    public static final int ACTION_MOVE = 3;
    public static final int ACTION_UNDEFINED = 0;
    public static final int ACTION_UP = 2;
    protected int action;
    protected int pointCount;
    protected long timestamp;
    /* renamed from: x */
    protected float[] f27x = new float[this.pointCount];
    /* renamed from: y */
    protected float[] f28y = new float[this.pointCount];

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public NativeMapViewUiEvent(MotionEvent event) {
        MotionEventAdapter eventAdapter = new MotionEventAdapter(event);
        this.pointCount = eventAdapter.getPointerCount();
        setX(event.getX());
        setY(event.getY());
        if (this.pointCount > 1) {
            setX(eventAdapter.getX(1), 1);
            setY(eventAdapter.getY(1), 1);
        }
        setTimstamp(event.getEventTime());
        switch (event.getAction()) {
            case 0:
            case 5:
            case MotionEventAdapter.ACTION_POINTER_2_DOWN /*261*/:
            case MotionEventAdapter.ACTION_POINTER_3_DOWN /*517*/:
                setAction(1);
                return;
            case 1:
            case 6:
            case MotionEventAdapter.ACTION_POINTER_2_UP /*262*/:
            case MotionEventAdapter.ACTION_POINTER_3_UP /*518*/:
                setAction(2);
                return;
            case 2:
                setAction(3);
                return;
            default:
                return;
        }
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public float getX() {
        return this.f27x[0];
    }

    public void setX(float x) {
        this.f27x[0] = x;
    }

    public float getY() {
        return this.f28y[0];
    }

    public void setY(float y) {
        this.f28y[0] = y;
    }

    public float getX(int index) {
        return this.f27x[index];
    }

    public void setX(float x, int index) {
        this.f27x[index] = x;
    }

    public float getY(int index) {
        return this.f28y[index];
    }

    public void setY(float y, int index) {
        this.f28y[index] = y;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimstamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPointCount() {
        return this.pointCount;
    }
}
