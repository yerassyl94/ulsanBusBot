package net.daum.mf.map.common;

import android.os.Build.VERSION;
import android.view.MotionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MotionEventAdapter {
    public static final int ACTION_POINTER_1_DOWN = 5;
    public static final int ACTION_POINTER_1_UP = 6;
    public static final int ACTION_POINTER_2_DOWN = 261;
    public static final int ACTION_POINTER_2_UP = 262;
    public static final int ACTION_POINTER_3_DOWN = 517;
    public static final int ACTION_POINTER_3_UP = 518;
    protected MotionEvent _event;
    protected Method getPointerCountMethod;
    protected Method getXMethod;
    protected Method getYMethod;
    protected Class<MotionEvent> motionEventClass = null;

    public MotionEventAdapter(MotionEvent event) {
        this._event = event;
        if (VERSION.SDK_INT > 4) {
            try {
                this.motionEventClass = Class.forName("android.view.MotionEvent");
                this.getPointerCountMethod = this.motionEventClass.getMethod("getPointerCount", (Class[]) null);
                Class<Integer>[] arguments = new Class[]{Integer.TYPE};
                this.getXMethod = this.motionEventClass.getMethod("getX", arguments);
                this.getYMethod = this.motionEventClass.getMethod("getY", arguments);
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e2) {
            } catch (IllegalArgumentException e3) {
            } catch (ClassNotFoundException e4) {
            }
        }
    }

    public int getPointerCount() {
        if (VERSION.SDK_INT > 4) {
            try {
                Object returnValue = this.getPointerCountMethod.invoke(this._event, (Object[]) null);
                if (returnValue != null) {
                    return ((Integer) returnValue).intValue();
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
            }
        }
        return 1;
    }

    public float getX(int index) {
        if (VERSION.SDK_INT > 4) {
            try {
                Object returnValue = this.getXMethod.invoke(this._event, new Object[]{Integer.valueOf(index)});
                if (returnValue != null) {
                    return ((Float) returnValue).floatValue();
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
            }
        }
        return -1.0f;
    }

    public float getY(int index) {
        if (VERSION.SDK_INT >= 4) {
            try {
                Object returnValue = this.getYMethod.invoke(this._event, new Object[]{Integer.valueOf(index)});
                if (returnValue != null) {
                    return ((Float) returnValue).floatValue();
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
            }
        }
        return -1.0f;
    }
}
