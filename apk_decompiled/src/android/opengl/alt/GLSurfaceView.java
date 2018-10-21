package android.opengl.alt;

import android.content.Context;
import android.opengl.GLDebugHelper;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.Writer;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import net.daum.android.map.MapBuildSettings;
import net.daum.mf.map.common.MapThreadSettings;
import net.daum.mf.map.p000n.api.NativeThread;

public class GLSurfaceView extends SurfaceView implements Callback {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final GLThreadManager sGLThreadManager = new GLThreadManager();
    private int mDebugFlags;
    private EGLConfigChooser mEGLConfigChooser;
    private EGLContextFactory mEGLContextFactory;
    private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    private GLWrapper mGLWrapper;
    private boolean mSizeChanged = true;

    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay);
    }

    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    private class EglHelper {
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;

        public void start() {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.mEgl.eglInitialize(this.mEglDisplay, new int[2]);
            this.mEglConfig = GLSurfaceView.this.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
            this.mEglContext = GLSurfaceView.this.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
            if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                throw new RuntimeException("createContext failed");
            }
            this.mEglSurface = null;
        }

        public GL createSurface(SurfaceHolder holder) {
            if (!(this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE)) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLSurfaceView.this.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
            }
            this.mEglSurface = GLSurfaceView.this.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, holder);
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("createWindowSurface failed");
            } else if (this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                GL gl = this.mEglContext.getGL();
                if (GLSurfaceView.this.mGLWrapper != null) {
                    gl = GLSurfaceView.this.mGLWrapper.wrap(gl);
                }
                if ((GLSurfaceView.this.mDebugFlags & 3) == 0) {
                    return gl;
                }
                Writer log = null;
                if ((GLSurfaceView.this.mDebugFlags & 1) != 0) {
                }
                if ((GLSurfaceView.this.mDebugFlags & 2) != 0) {
                    log = new LogWriter();
                }
                return GLDebugHelper.wrap(gl, 0, log);
            } else {
                throw new RuntimeException("eglMakeCurrent failed.");
            }
        }

        public boolean swap() {
            this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
            return this.mEgl.eglGetError() != 12302;
        }

        public void destroySurface() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLSurfaceView.this.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
            }
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLSurfaceView.this.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }
    }

    interface EglHelperWrapper {
        void beforeSwap(GL10 gl10);

        GL10 createSurface(GL10 gl10, SurfaceHolder surfaceHolder);
    }

    private static class GLThreadManager {
        private GLThread mEglOwner;

        private GLThreadManager() {
        }

        public synchronized void threadExiting(GLThread thread) {
            thread.mDone = true;
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public synchronized boolean tryAcquireEglSurface(GLThread thread) {
            boolean z;
            if (this.mEglOwner == thread || this.mEglOwner == null) {
                this.mEglOwner = thread;
                notifyAll();
                z = true;
            } else {
                z = false;
            }
            return z;
        }

        public synchronized void releaseEglSurface(GLThread thread) {
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        public void close() {
            flushBuilder();
        }

        public void flush() {
            flushBuilder();
        }

        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == '\n') {
                    flushBuilder();
                } else {
                    this.mBuilder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                if (!MapBuildSettings.getInstance().isDistribution()) {
                    Log.d(GLSurfaceView.class.getName(), this.mBuilder.toString());
                }
                this.mBuilder.delete(0, this.mBuilder.length());
            }
        }
    }

    public interface Renderer {
        void didSwap(GL10 gl10);

        boolean needsSwap(GL10 gl10);

        void onAfterFinished(GL10 gl10);

        void onBeforeFinished(GL10 gl10);

        void onDrawFrame(GL10 gl10);

        void onLoopWhenPaused(GL10 gl10);

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
    }

    private static abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] configSpec) {
            this.mConfigSpec = configSpec;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int numConfigs;
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, this.mConfigSpec, null, 0, num_config);
            int numConfigs2 = num_config[0];
            if (numConfigs2 <= 0) {
                this.mConfigSpec[11] = 0;
                egl.eglChooseConfig(display, this.mConfigSpec, null, 0, num_config);
                numConfigs = num_config[0];
                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
            }
            numConfigs = numConfigs2;
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, this.mConfigSpec, configs, numConfigs, num_config);
            EGLConfig config = chooseConfig(egl, display, configs);
            if (config != null) {
                return config;
            }
            throw new IllegalArgumentException("No config chosen");
        }
    }

    private static class DefaultContextFactory implements EGLContextFactory {
        private DefaultContextFactory() {
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            return egl.eglCreateWindowSurface(display, config, nativeWindow, null);
        }

        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private class EglHelperWrapperDonut implements EglHelperWrapper {
        private EglHelper _eglHelper = null;

        public EglHelperWrapperDonut(EglHelper eglHelper) {
            this._eglHelper = eglHelper;
        }

        public GL10 createSurface(GL10 gl, SurfaceHolder holder) {
            return (GL10) this._eglHelper.createSurface(GLSurfaceView.this.getHolder());
        }

        public void beforeSwap(GL10 gl) {
            gl.glFinish();
        }
    }

    private class EglHelperWrapperEclair implements EglHelperWrapper {
        private EglHelper _eglHelper = null;

        public EglHelperWrapperEclair(EglHelper eglHelper) {
            this._eglHelper = eglHelper;
        }

        public GL10 createSurface(GL10 gl, SurfaceHolder holder) {
            if (this._eglHelper.mEglSurface == null) {
                return (GL10) this._eglHelper.createSurface(GLSurfaceView.this.getHolder());
            }
            return gl;
        }

        public void beforeSwap(GL10 gl) {
        }
    }

    class GLThread extends NativeThread {
        public boolean isSurfaceInited;
        private boolean mDone = false;
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList();
        private boolean mEventsWaiting;
        private boolean mHasSurface;
        private boolean mHaveEgl;
        private int mHeight = 0;
        private boolean mPaused;
        private int mRenderMode = 1;
        private Renderer mRenderer;
        private boolean mRequestRender = true;
        private boolean mWaitingForSurface;
        private int mWidth = 0;

        GLThread(Renderer renderer) {
            this.mRenderer = renderer;
            this.isSurfaceInited = false;
        }

        protected void nativeRun() {
            setName("GLThread " + getId());
            Thread.currentThread().setPriority(MapThreadSettings.getGlPriority());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } finally {
                GLSurfaceView.sGLThreadManager.threadExiting(this);
            }
        }

        private void stopEglLocked() {
            if (this.mHaveEgl) {
                this.mHaveEgl = false;
                this.mEglHelper.destroySurface();
                this.mEglHelper.finish();
                GLSurfaceView.sGLThreadManager.releaseEglSurface(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
            r20 = this;
            r15 = new android.opengl.alt.GLSurfaceView$EglHelper;
            r0 = r20;
            r0 = android.opengl.alt.GLSurfaceView.this;
            r16 = r0;
            r15.<init>();
            r0 = r20;
            r0.mEglHelper = r15;
            r15 = android.os.Build.VERSION.SDK_INT;
            r16 = 4;
            r0 = r16;
            if (r15 <= r0) goto L_0x006d;
        L_0x0017:
            r3 = new android.opengl.alt.GLSurfaceView$EglHelperWrapperEclair;
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;
            r0 = r20;
            r0 = r0.mEglHelper;
            r16 = r0;
            r0 = r16;
            r3.<init>(r0);
        L_0x0028:
            r5 = 0;
            r12 = 1;
            r11 = 1;
            r13 = 0;
        L_0x002c:
            r15 = r20.isDone();	 Catch:{ all -> 0x004c }
            if (r15 != 0) goto L_0x0262;
        L_0x0032:
            r14 = 0;
            r6 = 0;
            r2 = 0;
            r8 = 0;
            r4 = 0;
            r0 = r20;
            r15 = r0.mEglHelper;	 Catch:{ all -> 0x004c }
            r15 = r15.mEglSurface;	 Catch:{ all -> 0x004c }
            if (r15 == 0) goto L_0x007f;
        L_0x003f:
            r7 = 1;
        L_0x0040:
            if (r7 == 0) goto L_0x0081;
        L_0x0042:
            r10 = r20.getEvent();	 Catch:{ all -> 0x004c }
            if (r10 == 0) goto L_0x0081;
        L_0x0048:
            r10.run();	 Catch:{ all -> 0x004c }
            goto L_0x0042;
        L_0x004c:
            r15 = move-exception;
            r16 = android.opengl.alt.GLSurfaceView.sGLThreadManager;
            monitor-enter(r16);
            r0 = r20;
            r0 = r0.mRenderer;	 Catch:{ all -> 0x027e }
            r17 = r0;
            r0 = r17;
            r0.onBeforeFinished(r5);	 Catch:{ all -> 0x027e }
            r20.stopEglLocked();	 Catch:{ all -> 0x027e }
            r0 = r20;
            r0 = r0.mRenderer;	 Catch:{ all -> 0x027e }
            r17 = r0;
            r0 = r17;
            r0.onAfterFinished(r5);	 Catch:{ all -> 0x027e }
            monitor-exit(r16);	 Catch:{ all -> 0x027e }
            throw r15;
        L_0x006d:
            r3 = new android.opengl.alt.GLSurfaceView$EglHelperWrapperDonut;
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;
            r0 = r20;
            r0 = r0.mEglHelper;
            r16 = r0;
            r0 = r16;
            r3.<init>(r0);
            goto L_0x0028;
        L_0x007f:
            r7 = 0;
            goto L_0x0040;
        L_0x0081:
            r16 = android.opengl.alt.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x004c }
            monitor-enter(r16);	 Catch:{ all -> 0x004c }
        L_0x0086:
            r0 = r20;
            r15 = r0.mPaused;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00b9;
        L_0x008c:
            r9 = 0;
            r0 = r20;
            r15 = r0.mHaveEgl;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0099;
        L_0x0093:
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00ad;
        L_0x0099:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x00e7 }
            r15.onBeforeFinished(r5);	 Catch:{ all -> 0x00e7 }
            r9 = 1;
            r13 = 1;
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;	 Catch:{ all -> 0x00e7 }
            r17 = 1;
            r0 = r17;
            r15.mSizeChanged = r0;	 Catch:{ all -> 0x00e7 }
        L_0x00ad:
            r20.stopEglLocked();	 Catch:{ all -> 0x00e7 }
            if (r9 == 0) goto L_0x00b9;
        L_0x00b2:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x00e7 }
            r15.onAfterFinished(r5);	 Catch:{ all -> 0x00e7 }
        L_0x00b9:
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;	 Catch:{ all -> 0x00e7 }
            r15 = r15.mSizeChanged;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x00ea;
        L_0x00c3:
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00ea;
        L_0x00c9:
            r0 = r20;
            r15 = r0.isSurfaceInited;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00ea;
        L_0x00cf:
            r15 = net.daum.mf.map.common.MapThreadScheduling.needToWait();	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00ea;
        L_0x00d5:
            r0 = r20;
            r15 = r0.mEventsWaiting;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x00ea;
        L_0x00db:
            r15 = net.daum.mf.map.common.MapThreadScheduling.isWaiting();	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x00ef;
        L_0x00e1:
            r18 = 6;
            java.lang.Thread.sleep(r18);	 Catch:{ all -> 0x00e7 }
            goto L_0x00db;
        L_0x00e7:
            r15 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x00e7 }
            throw r15;	 Catch:{ all -> 0x004c }
        L_0x00ea:
            r18 = 2;
            java.lang.Thread.sleep(r18);	 Catch:{ all -> 0x00e7 }
        L_0x00ef:
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0148;
        L_0x00f5:
            r0 = r20;
            r15 = r0.mWaitingForSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0129;
        L_0x00fb:
            r9 = 0;
            r0 = r20;
            r15 = r0.mHaveEgl;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0108;
        L_0x0102:
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x0111;
        L_0x0108:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x00e7 }
            r15.onBeforeFinished(r5);	 Catch:{ all -> 0x00e7 }
            r9 = 1;
            r13 = 1;
        L_0x0111:
            r20.stopEglLocked();	 Catch:{ all -> 0x00e7 }
            if (r9 == 0) goto L_0x011d;
        L_0x0116:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x00e7 }
            r15.onAfterFinished(r5);	 Catch:{ all -> 0x00e7 }
        L_0x011d:
            r15 = 1;
            r0 = r20;
            r0.mWaitingForSurface = r15;	 Catch:{ all -> 0x00e7 }
            r15 = android.opengl.alt.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x00e7 }
            r15.notifyAll();	 Catch:{ all -> 0x00e7 }
        L_0x0129:
            r0 = r20;
            r15 = r0.mDone;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x0170;
        L_0x012f:
            monitor-exit(r16);	 Catch:{ all -> 0x00e7 }
            r16 = android.opengl.alt.GLSurfaceView.sGLThreadManager;
            monitor-enter(r16);
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x016d }
            r15.onBeforeFinished(r5);	 Catch:{ all -> 0x016d }
            r20.stopEglLocked();	 Catch:{ all -> 0x016d }
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x016d }
            r15.onAfterFinished(r5);	 Catch:{ all -> 0x016d }
            monitor-exit(r16);	 Catch:{ all -> 0x016d }
        L_0x0147:
            return;
        L_0x0148:
            r0 = r20;
            r15 = r0.mHaveEgl;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0129;
        L_0x014e:
            r15 = android.opengl.alt.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r15 = r15.tryAcquireEglSurface(r0);	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x0129;
        L_0x015a:
            r15 = 1;
            r0 = r20;
            r0.mHaveEgl = r15;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r15 = r0.mEglHelper;	 Catch:{ all -> 0x00e7 }
            r15.start();	 Catch:{ all -> 0x00e7 }
            r15 = 1;
            r0 = r20;
            r0.mRequestRender = r15;	 Catch:{ all -> 0x00e7 }
            r8 = 1;
            goto L_0x0129;
        L_0x016d:
            r15 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x016d }
            throw r15;
        L_0x0170:
            r0 = r20;
            r15 = r0.mEventsWaiting;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x01e5;
        L_0x0176:
            r4 = 1;
            r15 = 0;
            r0 = r20;
            r0.mEventsWaiting = r15;	 Catch:{ all -> 0x00e7 }
        L_0x017c:
            monitor-exit(r16);	 Catch:{ all -> 0x00e7 }
            if (r4 != 0) goto L_0x002c;
        L_0x017f:
            if (r8 == 0) goto L_0x0183;
        L_0x0181:
            r12 = 1;
            r2 = 1;
        L_0x0183:
            if (r2 == 0) goto L_0x019a;
        L_0x0185:
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;	 Catch:{ all -> 0x004c }
            r15 = r15.getHolder();	 Catch:{ all -> 0x004c }
            r5 = r3.createSurface(r5, r15);	 Catch:{ all -> 0x004c }
            if (r13 == 0) goto L_0x0194;
        L_0x0193:
            r12 = 1;
        L_0x0194:
            r11 = 1;
            r15 = 1;
            r0 = r20;
            r0.isSurfaceInited = r15;	 Catch:{ all -> 0x004c }
        L_0x019a:
            if (r12 == 0) goto L_0x01b3;
        L_0x019c:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x004c }
            r0 = r20;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x004c }
            r16 = r0;
            r0 = r16;
            r0 = r0.mEglConfig;	 Catch:{ all -> 0x004c }
            r16 = r0;
            r0 = r16;
            r15.onSurfaceCreated(r5, r0);	 Catch:{ all -> 0x004c }
            r12 = 0;
            r13 = 0;
        L_0x01b3:
            if (r11 == 0) goto L_0x01bd;
        L_0x01b5:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x004c }
            r15.onSurfaceChanged(r5, r14, r6);	 Catch:{ all -> 0x004c }
            r11 = 0;
        L_0x01bd:
            if (r14 <= 0) goto L_0x002c;
        L_0x01bf:
            if (r6 <= 0) goto L_0x002c;
        L_0x01c1:
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x004c }
            r15.onDrawFrame(r5);	 Catch:{ all -> 0x004c }
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x004c }
            r15 = r15.needsSwap(r5);	 Catch:{ all -> 0x004c }
            if (r15 == 0) goto L_0x002c;
        L_0x01d2:
            r3.beforeSwap(r5);	 Catch:{ all -> 0x004c }
            r0 = r20;
            r15 = r0.mEglHelper;	 Catch:{ all -> 0x004c }
            r15.swap();	 Catch:{ all -> 0x004c }
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x004c }
            r15.didSwap(r5);	 Catch:{ all -> 0x004c }
            goto L_0x002c;
        L_0x01e5:
            r0 = r20;
            r15 = r0.mPaused;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x024e;
        L_0x01eb:
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x024e;
        L_0x01f1:
            r0 = r20;
            r15 = r0.mHaveEgl;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x024e;
        L_0x01f7:
            r0 = r20;
            r15 = r0.mWidth;	 Catch:{ all -> 0x00e7 }
            if (r15 <= 0) goto L_0x024e;
        L_0x01fd:
            r0 = r20;
            r15 = r0.mHeight;	 Catch:{ all -> 0x00e7 }
            if (r15 <= 0) goto L_0x024e;
        L_0x0203:
            r0 = r20;
            r15 = r0.mRequestRender;	 Catch:{ all -> 0x00e7 }
            if (r15 != 0) goto L_0x0213;
        L_0x0209:
            r0 = r20;
            r15 = r0.mRenderMode;	 Catch:{ all -> 0x00e7 }
            r17 = 1;
            r0 = r17;
            if (r15 != r0) goto L_0x024e;
        L_0x0213:
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;	 Catch:{ all -> 0x00e7 }
            r2 = r15.mSizeChanged;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r14 = r0.mWidth;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r6 = r0.mHeight;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r15 = android.opengl.alt.GLSurfaceView.this;	 Catch:{ all -> 0x00e7 }
            r17 = 0;
            r0 = r17;
            r15.mSizeChanged = r0;	 Catch:{ all -> 0x00e7 }
            r15 = 0;
            r0 = r20;
            r0.mRequestRender = r15;	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r15 = r0.mHasSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x017c;
        L_0x0239:
            r0 = r20;
            r15 = r0.mWaitingForSurface;	 Catch:{ all -> 0x00e7 }
            if (r15 == 0) goto L_0x017c;
        L_0x023f:
            r2 = 1;
            r15 = 0;
            r0 = r20;
            r0.mWaitingForSurface = r15;	 Catch:{ all -> 0x00e7 }
            r15 = android.opengl.alt.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x00e7 }
            r15.notifyAll();	 Catch:{ all -> 0x00e7 }
            goto L_0x017c;
        L_0x024e:
            r15 = android.opengl.alt.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x00e7 }
            r18 = 10;
            r0 = r18;
            r15.wait(r0);	 Catch:{ all -> 0x00e7 }
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x00e7 }
            r15.onLoopWhenPaused(r5);	 Catch:{ all -> 0x00e7 }
            goto L_0x0086;
        L_0x0262:
            r16 = android.opengl.alt.GLSurfaceView.sGLThreadManager;
            monitor-enter(r16);
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x027b }
            r15.onBeforeFinished(r5);	 Catch:{ all -> 0x027b }
            r20.stopEglLocked();	 Catch:{ all -> 0x027b }
            r0 = r20;
            r15 = r0.mRenderer;	 Catch:{ all -> 0x027b }
            r15.onAfterFinished(r5);	 Catch:{ all -> 0x027b }
            monitor-exit(r16);	 Catch:{ all -> 0x027b }
            goto L_0x0147;
        L_0x027b:
            r15 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x027b }
            throw r15;
        L_0x027e:
            r15 = move-exception;
            monitor-exit(r16);	 Catch:{ all -> 0x027e }
            throw r15;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.opengl.alt.GLSurfaceView.GLThread.guardedRun():void");
        }

        private boolean isDone() {
            boolean z;
            synchronized (GLSurfaceView.sGLThreadManager) {
                z = this.mDone;
            }
            return z;
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRenderMode = renderMode;
                if (renderMode == 1) {
                    GLSurfaceView.sGLThreadManager.notifyAll();
                }
            }
        }

        public int getRenderMode() {
            int i;
            synchronized (GLSurfaceView.sGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void requestRender() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRequestRender = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceCreated() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mHasSurface = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mHasSurface = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && isAlive() && !this.mDone) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mPaused = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void onResume() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mPaused = false;
                this.mRequestRender = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mWidth = w;
                this.mHeight = h;
                GLSurfaceView.this.mSizeChanged = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void requestExitAndWait() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mDone = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
            try {
                join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void queueEvent(Runnable r) {
            synchronized (this) {
                this.mEventQueue.add(r);
                synchronized (GLSurfaceView.sGLThreadManager) {
                    this.mEventsWaiting = true;
                    GLSurfaceView.sGLThreadManager.notifyAll();
                }
            }
        }

        private Runnable getEvent() {
            synchronized (this) {
                if (this.mEventQueue.size() > 0) {
                    Runnable runnable = (Runnable) this.mEventQueue.remove(0);
                    return runnable;
                }
                return null;
            }
        }
    }

    private static class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
            super(new int[]{12324, redSize, 12323, greenSize, 12322, blueSize, 12321, alphaSize, 12325, depthSize, 12326, stencilSize, 12344});
            this.mRedSize = redSize;
            this.mGreenSize = greenSize;
            this.mBlueSize = blueSize;
            this.mAlphaSize = alphaSize;
            this.mDepthSize = depthSize;
            this.mStencilSize = stencilSize;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            if (egl.eglChooseConfig(display, this.mConfigSpec, null, 0, num_config)) {
                int numConfigs;
                int numConfigs2 = num_config[0];
                if (numConfigs2 <= 0) {
                    this.mStencilSize = 0;
                    if (egl.eglChooseConfig(display, this.mConfigSpec, null, 0, num_config)) {
                        numConfigs = num_config[0];
                        if (numConfigs <= 0) {
                            throw new IllegalArgumentException("No configs match configSpec");
                        }
                    }
                    throw new IllegalArgumentException("eglChooseConfig failed");
                }
                numConfigs = numConfigs2;
                EGLConfig[] configs = new EGLConfig[numConfigs];
                if (egl.eglChooseConfig(display, this.mConfigSpec, configs, numConfigs, num_config)) {
                    EGLConfig config = chooseConfig(egl, display, configs);
                    if (config != null) {
                        return config;
                    }
                    throw new IllegalArgumentException("No config chosen");
                }
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            EGLConfig closestConfig = null;
            int closestDistance = 1000;
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, 12325, 0);
                int s = findConfigAttrib(egl, display, config, 12326, 0);
                if (d >= this.mDepthSize && s >= this.mStencilSize) {
                    int distance = ((Math.abs(findConfigAttrib(egl, display, config, 12324, 0) - this.mRedSize) + Math.abs(findConfigAttrib(egl, display, config, 12323, 0) - this.mGreenSize)) + Math.abs(findConfigAttrib(egl, display, config, 12322, 0) - this.mBlueSize)) + Math.abs(findConfigAttrib(egl, display, config, 12321, 0) - this.mAlphaSize);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestConfig = config;
                    }
                }
            }
            return closestConfig;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }
    }

    private static class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            super(4, 4, 4, 0, withDepthBuffer ? 16 : 0, 8);
            this.mRedSize = 5;
            this.mGreenSize = 6;
            this.mBlueSize = 5;
        }
    }

    private static class DaumMapsEGLConfigChooser extends SimpleEGLConfigChooser {
        private SurfaceHolder _holder;
        private EGLWindowSurfaceFactory _windowSurfaceFactory;
        private int[] mValue2 = new int[1];

        public DaumMapsEGLConfigChooser(boolean withDepthBuffer, EGLWindowSurfaceFactory windowSurfaceFactory, SurfaceHolder holder) {
            super(withDepthBuffer);
            this._windowSurfaceFactory = windowSurfaceFactory;
            this._holder = holder;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            EGLConfig closestConfig = null;
            EGLConfig lastClosestConfig = null;
            int closestDistance = 1000;
            for (EGLConfig config : configs) {
                int distance = ((((Math.abs(findConfigAttrib(egl, display, config, 12324, 0) - this.mRedSize) + Math.abs(findConfigAttrib(egl, display, config, 12323, 0) - this.mGreenSize)) + Math.abs(findConfigAttrib(egl, display, config, 12322, 0) - this.mBlueSize)) + Math.abs(findConfigAttrib(egl, display, config, 12321, 0) - this.mAlphaSize)) + Math.abs(findConfigAttrib(egl, display, config, 12325, 0) - this.mDepthSize)) + Math.abs(findConfigAttrib(egl, display, config, 12326, 0) - this.mStencilSize);
                if (distance < closestDistance) {
                    if (testConfiguration(egl, display, config)) {
                        closestDistance = distance;
                        closestConfig = config;
                    } else {
                        lastClosestConfig = config;
                    }
                }
            }
            if (closestConfig == null) {
                return lastClosestConfig;
            }
            return closestConfig;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue2)) {
                return this.mValue2[0];
            }
            return defaultValue;
        }

        private boolean testConfiguration(EGL10 egl, EGLDisplay display, EGLConfig config) {
            EGLSurface eglSurface = null;
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.d(GLSurfaceView.class.getName(), "Test Creating Surface " + eglSurface);
            }
            try {
                eglSurface = this._windowSurfaceFactory.createWindowSurface(egl, display, config, this._holder);
            } catch (IllegalArgumentException e) {
                if (!MapBuildSettings.getInstance().isDistribution()) {
                    Log.d(GLSurfaceView.class.getName(), "IllegalArgumentException Creating Surface " + eglSurface);
                }
                eglSurface = null;
            }
            if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
                return false;
            }
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.d(GLSurfaceView.class.getName(), "Destroying Surface " + eglSurface);
            }
            this._windowSurfaceFactory.destroySurface(egl, display, eglSurface);
            return true;
        }
    }

    public GLSurfaceView(Context context) {
        super(context);
        init();
    }

    public GLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GLSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    public void setGLWrapper(GLWrapper glWrapper) {
        this.mGLWrapper = glWrapper;
    }

    public void setDebugFlags(int debugFlags) {
        this.mDebugFlags = debugFlags;
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public void setRenderer(Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new DaumMapsEGLConfigChooser(true, this.mEGLWindowSurfaceFactory, getHolder());
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        this.mGLThread = new GLThread(renderer);
        this.mGLThread.start();
    }

    public void setEGLContextFactory(EGLContextFactory factory) {
        checkRenderThreadState();
        this.mEGLContextFactory = factory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = factory;
    }

    public void setEGLConfigChooser(EGLConfigChooser configChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = configChooser;
    }

    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser(new DaumMapsEGLConfigChooser(needDepth, this.mEGLWindowSurfaceFactory, getHolder()));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        if (VERSION.SDK_INT < 16) {
            setEGLConfigChooser(new ComponentSizeChooser(5, 6, 5, 0, 16, 8));
        } else {
            setEGLConfigChooser(new ComponentSizeChooser(8, 8, 8, 8, 16, 8));
        }
    }

    public void setRenderMode(int renderMode) {
        this.mGLThread.setRenderMode(renderMode);
    }

    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mGLThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mGLThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.mGLThread.onWindowResize(w, h);
    }

    public void onPause() {
        this.mGLThread.onPause();
    }

    public void onResume() {
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable r) {
        this.mGLThread.queueEvent(r);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mGLThread.requestExitAndWait();
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }
}
