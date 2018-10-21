package android.opengl.alta;

import android.content.Context;
import android.opengl.GLDebugHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceView extends SurfaceView implements Callback {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final String TAG = "GLSurfaceView";
    private static final GLThreadManager sGLThreadManager = new GLThreadManager();
    private int mDebugFlags;
    private boolean mDetached;
    private EGLConfigChooser mEGLConfigChooser;
    private int mEGLContextClientVersion;
    private EGLContextFactory mEGLContextFactory;
    private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    private GLWrapper mGLWrapper;
    private boolean mPreserveEGLContextOnPause;
    private Renderer mRenderer;
    private final WeakReference<GLSurfaceView> mThisWeakRef = new WeakReference(this);

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

    private static class EglHelper {
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;

        public EglHelper(WeakReference<GLSurfaceView> glSurfaceViewWeakRef) {
            this.mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
        }

        public void start() {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                GLSurfaceView view = (GLSurfaceView) this.mGLSurfaceViewWeakRef.get();
                if (view == null) {
                    this.mEglConfig = null;
                    this.mEglContext = null;
                } else {
                    this.mEglConfig = view.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                    this.mEglContext = view.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
                }
                if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                    this.mEglContext = null;
                    throwEglException("createContext");
                }
                this.mEglSurface = null;
                return;
            }
            throw new RuntimeException("eglInitialize failed");
        }

        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            } else if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            } else if (this.mEglConfig == null) {
                throw new RuntimeException("mEglConfig not initialized");
            } else {
                destroySurfaceImp();
                GLSurfaceView view = (GLSurfaceView) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    this.mEglSurface = view.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, view.getHolder());
                } else {
                    this.mEglSurface = null;
                }
                if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                    if (this.mEgl.eglGetError() != 12299) {
                        return false;
                    }
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    return false;
                } else if (this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                    return true;
                } else {
                    logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
                    return false;
                }
            }
        }

        GL createGL() {
            GL gl = this.mEglContext.getGL();
            GLSurfaceView view = (GLSurfaceView) this.mGLSurfaceViewWeakRef.get();
            if (view == null) {
                return gl;
            }
            if (view.mGLWrapper != null) {
                gl = view.mGLWrapper.wrap(gl);
            }
            if ((view.mDebugFlags & 3) == 0) {
                return gl;
            }
            int configFlags = 0;
            Writer log = null;
            if ((view.mDebugFlags & 1) != 0) {
                configFlags = 0 | 1;
            }
            if ((view.mDebugFlags & 2) != 0) {
                log = new LogWriter();
            }
            return GLDebugHelper.wrap(gl, configFlags, log);
        }

        public int swap() {
            if (this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return 12288;
            }
            return this.mEgl.eglGetError();
        }

        public void destroySurface() {
            destroySurfaceImp();
        }

        private void destroySurfaceImp() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLSurfaceView view = (GLSurfaceView) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    view.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                }
                this.mEglSurface = null;
            }
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLSurfaceView view = (GLSurfaceView) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    view.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                }
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }

        private void throwEglException(String function) {
            throwEglException(function, this.mEgl.eglGetError());
        }

        public static void throwEglException(String function, int error) {
            throw new RuntimeException(formatEglError(function, error));
        }

        public static void logEglErrorAsWarning(String tag, String function, int error) {
            Log.w(tag, formatEglError(function, error));
        }

        public static String formatEglError(String function, int error) {
            return function + " failed: " + GLSurfaceView.getErrorString(error);
        }
    }

    static class GLThread extends Thread {
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList();
        private boolean mExited;
        private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private int mHeight = 0;
        private boolean mPaused;
        private boolean mRenderComplete;
        private int mRenderMode = 1;
        private boolean mRequestPaused;
        private boolean mRequestRender = true;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSizeChanged = true;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private int mWidth = 0;

        GLThread(WeakReference<GLSurfaceView> glSurfaceViewWeakRef) {
            this.mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
        }

        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } finally {
                GLSurfaceView.sGLThreadManager.threadExiting(this);
            }
        }

        private void stopEglSurfaceLocked() {
            if (this.mHaveEglSurface) {
                this.mHaveEglSurface = false;
                this.mEglHelper.destroySurface();
            }
        }

        private void stopEglContextLocked() {
            if (this.mHaveEglContext) {
                this.mEglHelper.finish();
                this.mHaveEglContext = false;
                GLSurfaceView.sGLThreadManager.releaseEglContextLocked(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
            r26 = this;
            r23 = new android.opengl.alta.GLSurfaceView$EglHelper;
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;
            r24 = r0;
            r23.<init>(r24);
            r0 = r23;
            r1 = r26;
            r1.mEglHelper = r0;
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mHaveEglContext = r0;
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mHaveEglSurface = r0;
            r10 = 0;
            r5 = 0;
            r6 = 0;
            r7 = 0;
            r12 = 0;
            r16 = 0;
            r22 = 0;
            r8 = 0;
            r3 = 0;
            r21 = 0;
            r11 = 0;
            r9 = 0;
            r19 = 0;
        L_0x0032:
            r24 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031f }
            monitor-enter(r24);	 Catch:{ all -> 0x031f }
        L_0x0037:
            r0 = r26;
            r0 = r0.mShouldExit;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x0072;
        L_0x003f:
            monitor-exit(r24);	 Catch:{ all -> 0x031c }
            r24 = android.opengl.alta.GLSurfaceView.sGLThreadManager;
            monitor-enter(r24);
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x006f }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x006f }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x006f }
            if (r20 == 0) goto L_0x005c;
        L_0x0053:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x006f }
            r0 = r23;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x006f }
        L_0x005c:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x006f }
            r26.stopEglContextLocked();	 Catch:{ all -> 0x006f }
            if (r20 == 0) goto L_0x006d;
        L_0x0064:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x006f }
            r0 = r23;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x006f }
        L_0x006d:
            monitor-exit(r24);	 Catch:{ all -> 0x006f }
            return;
        L_0x006f:
            r23 = move-exception;
            monitor-exit(r24);	 Catch:{ all -> 0x006f }
            throw r23;
        L_0x0072:
            r4 = 1;
            r0 = r26;
            r0 = r0.mEventQueue;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r23 = r23.isEmpty();	 Catch:{ all -> 0x031c }
            if (r23 != 0) goto L_0x009e;
        L_0x007f:
            if (r4 == 0) goto L_0x009e;
        L_0x0081:
            r0 = r26;
            r0 = r0.mEventQueue;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r25 = 0;
            r0 = r23;
            r1 = r25;
            r23 = r0.remove(r1);	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0 = (java.lang.Runnable) r0;	 Catch:{ all -> 0x031c }
            r9 = r0;
        L_0x0096:
            monitor-exit(r24);	 Catch:{ all -> 0x031c }
            if (r9 == 0) goto L_0x0394;
        L_0x0099:
            r9.run();	 Catch:{ all -> 0x031f }
            r9 = 0;
            goto L_0x0032;
        L_0x009e:
            r14 = 0;
            r0 = r26;
            r0 = r0.mPaused;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r0 = r26;
            r0 = r0.mRequestPaused;	 Catch:{ all -> 0x031c }
            r25 = r0;
            r0 = r23;
            r1 = r25;
            if (r0 == r1) goto L_0x00c8;
        L_0x00b1:
            r0 = r26;
            r14 = r0.mRequestPaused;	 Catch:{ all -> 0x031c }
            r0 = r26;
            r0 = r0.mRequestPaused;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r0 = r23;
            r1 = r26;
            r1.mPaused = r0;	 Catch:{ all -> 0x031c }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
        L_0x00c8:
            r0 = r26;
            r0 = r0.mShouldReleaseEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x0124;
        L_0x00d0:
            r13 = 0;
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x00e1;
        L_0x00d9:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x00fb;
        L_0x00e1:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x00fb;
        L_0x00ef:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x031c }
            r13 = 1;
            r19 = 1;
        L_0x00fb:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x031c }
            r26.stopEglContextLocked();	 Catch:{ all -> 0x031c }
            if (r13 == 0) goto L_0x011b;
        L_0x0103:
            r13 = 0;
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x011b;
        L_0x0112:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x031c }
        L_0x011b:
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mShouldReleaseEglContext = r0;	 Catch:{ all -> 0x031c }
            r3 = 1;
        L_0x0124:
            if (r12 == 0) goto L_0x0172;
        L_0x0126:
            r13 = 0;
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x0137;
        L_0x012f:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x0151;
        L_0x0137:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x0151;
        L_0x0145:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x031c }
            r13 = 1;
            r19 = 1;
        L_0x0151:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x031c }
            r26.stopEglContextLocked();	 Catch:{ all -> 0x031c }
            if (r13 == 0) goto L_0x0171;
        L_0x0159:
            r13 = 0;
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x0171;
        L_0x0168:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x031c }
        L_0x0171:
            r12 = 0;
        L_0x0172:
            if (r14 == 0) goto L_0x0215;
        L_0x0174:
            r13 = 0;
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x018f;
        L_0x017d:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x018f;
        L_0x0185:
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23 = r23.shouldTerminateEGLWhenPausing();	 Catch:{ all -> 0x031c }
            if (r23 == 0) goto L_0x01b1;
        L_0x018f:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x01b1;
        L_0x019d:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x031c }
            r13 = 1;
            r19 = 1;
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mSizeChanged = r0;	 Catch:{ all -> 0x031c }
        L_0x01b1:
            if (r14 == 0) goto L_0x01be;
        L_0x01b3:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x01be;
        L_0x01bb:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x031c }
        L_0x01be:
            if (r14 == 0) goto L_0x01e6;
        L_0x01c0:
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x01e6;
        L_0x01c8:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 != 0) goto L_0x034f;
        L_0x01d6:
            r15 = 0;
        L_0x01d7:
            if (r15 == 0) goto L_0x01e3;
        L_0x01d9:
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23 = r23.shouldReleaseEGLContextWhenPausing();	 Catch:{ all -> 0x031c }
            if (r23 == 0) goto L_0x01e6;
        L_0x01e3:
            r26.stopEglContextLocked();	 Catch:{ all -> 0x031c }
        L_0x01e6:
            if (r14 == 0) goto L_0x01fb;
        L_0x01e8:
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23 = r23.shouldTerminateEGLWhenPausing();	 Catch:{ all -> 0x031c }
            if (r23 == 0) goto L_0x01fb;
        L_0x01f2:
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r23.finish();	 Catch:{ all -> 0x031c }
        L_0x01fb:
            if (r13 == 0) goto L_0x0215;
        L_0x01fd:
            r13 = 0;
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x0215;
        L_0x020c:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x031c }
        L_0x0215:
            r0 = r26;
            r0 = r0.mHasSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x0284;
        L_0x021d:
            r0 = r26;
            r0 = r0.mWaitingForSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x0284;
        L_0x0225:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x026d;
        L_0x022d:
            r13 = 0;
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x0250;
        L_0x0236:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x0250;
        L_0x0244:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x031c }
            r13 = 1;
            r19 = 1;
        L_0x0250:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x031c }
            if (r13 == 0) goto L_0x026d;
        L_0x0255:
            r13 = 0;
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031c }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031c }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031c }
            if (r20 == 0) goto L_0x026d;
        L_0x0264:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x031c }
        L_0x026d:
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mWaitingForSurface = r0;	 Catch:{ all -> 0x031c }
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mSurfaceIsBad = r0;	 Catch:{ all -> 0x031c }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
        L_0x0284:
            r0 = r26;
            r0 = r0.mHasSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x02a3;
        L_0x028c:
            r0 = r26;
            r0 = r0.mWaitingForSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x02a3;
        L_0x0294:
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mWaitingForSurface = r0;	 Catch:{ all -> 0x031c }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
        L_0x02a3:
            if (r8 == 0) goto L_0x02b7;
        L_0x02a5:
            r22 = 0;
            r8 = 0;
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mRenderComplete = r0;	 Catch:{ all -> 0x031c }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
        L_0x02b7:
            r23 = r26.readyToDraw();	 Catch:{ all -> 0x031c }
            if (r23 == 0) goto L_0x038b;
        L_0x02bd:
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x02c8;
        L_0x02c5:
            if (r3 == 0) goto L_0x0355;
        L_0x02c7:
            r3 = 0;
        L_0x02c8:
            r0 = r26;
            r0 = r0.mHaveEglContext;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x02e4;
        L_0x02d0:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 != 0) goto L_0x02e4;
        L_0x02d8:
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mHaveEglSurface = r0;	 Catch:{ all -> 0x031c }
            r6 = 1;
            r7 = 1;
            r16 = 1;
        L_0x02e4:
            r0 = r26;
            r0 = r0.mHaveEglSurface;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x038b;
        L_0x02ec:
            r0 = r26;
            r0 = r0.mSizeChanged;	 Catch:{ all -> 0x031c }
            r23 = r0;
            if (r23 == 0) goto L_0x030b;
        L_0x02f4:
            r16 = 1;
            r0 = r26;
            r0 = r0.mWidth;	 Catch:{ all -> 0x031c }
            r21 = r0;
            r0 = r26;
            r11 = r0.mHeight;	 Catch:{ all -> 0x031c }
            r22 = 1;
            r6 = 1;
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mSizeChanged = r0;	 Catch:{ all -> 0x031c }
        L_0x030b:
            r23 = 0;
            r0 = r23;
            r1 = r26;
            r1.mRequestRender = r0;	 Catch:{ all -> 0x031c }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
            goto L_0x0096;
        L_0x031c:
            r23 = move-exception;
            monitor-exit(r24);	 Catch:{ all -> 0x031c }
            throw r23;	 Catch:{ all -> 0x031f }
        L_0x031f:
            r23 = move-exception;
            r24 = android.opengl.alta.GLSurfaceView.sGLThreadManager;
            monitor-enter(r24);
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x0494 }
            r25 = r0;
            r20 = r25.get();	 Catch:{ all -> 0x0494 }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x0494 }
            if (r20 == 0) goto L_0x033c;
        L_0x0333:
            r25 = r20.mRenderer;	 Catch:{ all -> 0x0494 }
            r0 = r25;
            r0.onBeforeFinished(r10);	 Catch:{ all -> 0x0494 }
        L_0x033c:
            r26.stopEglSurfaceLocked();	 Catch:{ all -> 0x0494 }
            r26.stopEglContextLocked();	 Catch:{ all -> 0x0494 }
            if (r20 == 0) goto L_0x034d;
        L_0x0344:
            r25 = r20.mRenderer;	 Catch:{ all -> 0x0494 }
            r0 = r25;
            r0.onAfterFinished(r10);	 Catch:{ all -> 0x0494 }
        L_0x034d:
            monitor-exit(r24);	 Catch:{ all -> 0x0494 }
            throw r23;
        L_0x034f:
            r15 = r20.mPreserveEGLContextOnPause;	 Catch:{ all -> 0x031c }
            goto L_0x01d7;
        L_0x0355:
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r1 = r26;
            r23 = r0.tryAcquireEglContextLocked(r1);	 Catch:{ all -> 0x031c }
            if (r23 == 0) goto L_0x02c8;
        L_0x0363:
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ RuntimeException -> 0x037e }
            r23 = r0;
            r23.start();	 Catch:{ RuntimeException -> 0x037e }
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mHaveEglContext = r0;	 Catch:{ all -> 0x031c }
            r5 = 1;
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.notifyAll();	 Catch:{ all -> 0x031c }
            goto L_0x02c8;
        L_0x037e:
            r18 = move-exception;
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r0 = r23;
            r1 = r26;
            r0.releaseEglContextLocked(r1);	 Catch:{ all -> 0x031c }
            throw r18;	 Catch:{ all -> 0x031c }
        L_0x038b:
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031c }
            r23.wait();	 Catch:{ all -> 0x031c }
            goto L_0x0037;
        L_0x0394:
            if (r6 == 0) goto L_0x03bd;
        L_0x0396:
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r23 = r23.createSurface();	 Catch:{ all -> 0x031f }
            if (r23 != 0) goto L_0x03bc;
        L_0x03a2:
            r24 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031f }
            monitor-enter(r24);	 Catch:{ all -> 0x031f }
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mSurfaceIsBad = r0;	 Catch:{ all -> 0x03b9 }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x03b9 }
            r23.notifyAll();	 Catch:{ all -> 0x03b9 }
            monitor-exit(r24);	 Catch:{ all -> 0x03b9 }
            goto L_0x0032;
        L_0x03b9:
            r23 = move-exception;
            monitor-exit(r24);	 Catch:{ all -> 0x03b9 }
            throw r23;	 Catch:{ all -> 0x031f }
        L_0x03bc:
            r6 = 0;
        L_0x03bd:
            if (r7 == 0) goto L_0x03db;
        L_0x03bf:
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r23 = r23.createGL();	 Catch:{ all -> 0x031f }
            r0 = r23;
            r0 = (javax.microedition.khronos.opengles.GL10) r0;	 Catch:{ all -> 0x031f }
            r10 = r0;
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031f }
            r0 = r23;
            r0.checkGLDriver(r10);	 Catch:{ all -> 0x031f }
            if (r19 == 0) goto L_0x03da;
        L_0x03d9:
            r5 = 1;
        L_0x03da:
            r7 = 0;
        L_0x03db:
            if (r5 == 0) goto L_0x0403;
        L_0x03dd:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031f }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031f }
            if (r20 == 0) goto L_0x0402;
        L_0x03eb:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031f }
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x031f }
            r24 = r0;
            r0 = r24;
            r0 = r0.mEglConfig;	 Catch:{ all -> 0x031f }
            r24 = r0;
            r0 = r23;
            r1 = r24;
            r0.onSurfaceCreated(r10, r1);	 Catch:{ all -> 0x031f }
        L_0x0402:
            r5 = 0;
        L_0x0403:
            if (r16 == 0) goto L_0x0420;
        L_0x0405:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031f }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031f }
            if (r20 == 0) goto L_0x041e;
        L_0x0413:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031f }
            r0 = r23;
            r1 = r21;
            r0.onSurfaceChanged(r10, r1, r11);	 Catch:{ all -> 0x031f }
        L_0x041e:
            r16 = 0;
        L_0x0420:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031f }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031f }
            if (r20 == 0) goto L_0x0437;
        L_0x042e:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031f }
            r0 = r23;
            r0.onDrawFrame(r10);	 Catch:{ all -> 0x031f }
        L_0x0437:
            r0 = r26;
            r0 = r0.mGLSurfaceViewWeakRef;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r20 = r23.get();	 Catch:{ all -> 0x031f }
            r20 = (android.opengl.alta.GLSurfaceView) r20;	 Catch:{ all -> 0x031f }
            if (r20 == 0) goto L_0x0480;
        L_0x0445:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031f }
            r0 = r23;
            r23 = r0.needsSwap(r10);	 Catch:{ all -> 0x031f }
            if (r23 == 0) goto L_0x0480;
        L_0x0451:
            r0 = r26;
            r0 = r0.mEglHelper;	 Catch:{ all -> 0x031f }
            r23 = r0;
            r17 = r23.swap();	 Catch:{ all -> 0x031f }
            switch(r17) {
                case 12288: goto L_0x0485;
                case 12302: goto L_0x048f;
                default: goto L_0x045e;
            };	 Catch:{ all -> 0x031f }
        L_0x045e:
            r23 = "GLThread";
            r24 = "eglSwapBuffers";
            r0 = r23;
            r1 = r24;
            r2 = r17;
            android.opengl.alta.GLSurfaceView.EglHelper.logEglErrorAsWarning(r0, r1, r2);	 Catch:{ all -> 0x031f }
            r24 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x031f }
            monitor-enter(r24);	 Catch:{ all -> 0x031f }
            r23 = 1;
            r0 = r23;
            r1 = r26;
            r1.mSurfaceIsBad = r0;	 Catch:{ all -> 0x0491 }
            r23 = android.opengl.alta.GLSurfaceView.sGLThreadManager;	 Catch:{ all -> 0x0491 }
            r23.notifyAll();	 Catch:{ all -> 0x0491 }
            monitor-exit(r24);	 Catch:{ all -> 0x0491 }
        L_0x0480:
            if (r22 == 0) goto L_0x0032;
        L_0x0482:
            r8 = 1;
            goto L_0x0032;
        L_0x0485:
            r23 = r20.mRenderer;	 Catch:{ all -> 0x031f }
            r0 = r23;
            r0.didSwap(r10);	 Catch:{ all -> 0x031f }
            goto L_0x0480;
        L_0x048f:
            r12 = 1;
            goto L_0x0480;
        L_0x0491:
            r23 = move-exception;
            monitor-exit(r24);	 Catch:{ all -> 0x0491 }
            throw r23;	 Catch:{ all -> 0x031f }
        L_0x0494:
            r23 = move-exception;
            monitor-exit(r24);	 Catch:{ all -> 0x0494 }
            throw r23;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.opengl.alta.GLSurfaceView.GLThread.guardedRun():void");
        }

        public boolean ableToDraw() {
            return this.mHaveEglContext && this.mHaveEglSurface && readyToDraw();
        }

        private boolean readyToDraw() {
            return !this.mPaused && this.mHasSurface && !this.mSurfaceIsBad && this.mWidth > 0 && this.mHeight > 0 && (this.mRequestRender || this.mRenderMode == 1);
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRenderMode = renderMode;
                GLSurfaceView.sGLThreadManager.notifyAll();
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
                while (this.mWaitingForSurface && !this.mExited) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mHasSurface = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
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
                this.mRequestPaused = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRequestPaused = false;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && this.mPaused && !this.mRenderComplete) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mWidth = w;
                this.mHeight = h;
                this.mSizeChanged = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused && !this.mRenderComplete && ableToDraw()) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestExitAndWait() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mShouldExit = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEglContextLocked() {
            this.mShouldReleaseEglContext = true;
            GLSurfaceView.sGLThreadManager.notifyAll();
        }

        public void queueEvent(Runnable r) {
            if (r == null) {
                throw new IllegalArgumentException("r must not be null");
            }
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mEventQueue.add(r);
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }
    }

    private static class GLThreadManager {
        private static String TAG = "GLThreadManager";
        private static final int kGLES_20 = 131072;
        private static final String kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 ";
        private GLThread mEglOwner;
        private boolean mGLESDriverCheckComplete;
        private int mGLESVersion;
        private boolean mGLESVersionCheckComplete;
        private boolean mLimitedGLESContexts;
        private boolean mMultipleGLESContextsAllowed;

        private GLThreadManager() {
        }

        public synchronized void threadExiting(GLThread thread) {
            thread.mExited = true;
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public boolean tryAcquireEglContextLocked(GLThread thread) {
            if (this.mEglOwner == thread || this.mEglOwner == null) {
                this.mEglOwner = thread;
                notifyAll();
                return true;
            }
            checkGLESVersion();
            if (this.mMultipleGLESContextsAllowed) {
                return true;
            }
            if (this.mEglOwner != null) {
                this.mEglOwner.requestReleaseEglContextLocked();
            }
            return false;
        }

        public void releaseEglContextLocked(GLThread thread) {
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public synchronized boolean shouldReleaseEGLContextWhenPausing() {
            return this.mLimitedGLESContexts;
        }

        public synchronized boolean shouldTerminateEGLWhenPausing() {
            checkGLESVersion();
            return !this.mMultipleGLESContextsAllowed;
        }

        public synchronized void checkGLDriver(GL10 gl) {
            boolean z = true;
            synchronized (this) {
                if (!this.mGLESDriverCheckComplete) {
                    checkGLESVersion();
                    String renderer = gl.glGetString(7937);
                    if (this.mGLESVersion < 131072) {
                        boolean z2;
                        if (renderer.startsWith(kMSM7K_RENDERER_PREFIX)) {
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        this.mMultipleGLESContextsAllowed = z2;
                        notifyAll();
                    }
                    if (this.mMultipleGLESContextsAllowed) {
                        z = false;
                    }
                    this.mLimitedGLESContexts = z;
                    this.mGLESDriverCheckComplete = true;
                }
            }
        }

        private void checkGLESVersion() {
            if (!this.mGLESVersionCheckComplete) {
                this.mGLESVersion = 131072;
                if (this.mGLESVersion >= 131072) {
                    this.mMultipleGLESContextsAllowed = true;
                }
                this.mGLESVersionCheckComplete = true;
            }
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
                Log.v(GLSurfaceView.TAG, this.mBuilder.toString());
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

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] configSpec) {
            this.mConfigSpec = filterConfigSpec(configSpec);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            if (egl.eglChooseConfig(display, this.mConfigSpec, null, 0, num_config)) {
                int numConfigs = num_config[0];
                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
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

        private int[] filterConfigSpec(int[] configSpec) {
            if (GLSurfaceView.this.mEGLContextClientVersion != 2) {
                return configSpec;
            }
            int len = configSpec.length;
            int[] newConfigSpec = new int[(len + 2)];
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
            newConfigSpec[len - 1] = 12352;
            newConfigSpec[len] = 4;
            newConfigSpec[len + 1] = 12344;
            return newConfigSpec;
        }
    }

    private class DefaultContextFactory implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION;

        private DefaultContextFactory() {
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attrib_list = new int[]{this.EGL_CONTEXT_CLIENT_VERSION, GLSurfaceView.this.mEGLContextClientVersion, 12344};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (GLSurfaceView.this.mEGLContextClientVersion == 0) {
                attrib_list = null;
            }
            return egl.eglCreateContext(display, config, eGLContext, attrib_list);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                EglHelper.throwEglException("eglDestroyContex", egl.eglGetError());
            }
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            EGLSurface result = null;
            try {
                result = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            } catch (IllegalArgumentException e) {
                Log.e(GLSurfaceView.TAG, "eglCreateWindowSurface", e);
            }
            return result;
        }

        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
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

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, 12325, 0);
                int s = findConfigAttrib(egl, display, config, 12326, 0);
                if (d >= this.mDepthSize && s >= this.mStencilSize) {
                    int r = findConfigAttrib(egl, display, config, 12324, 0);
                    int g = findConfigAttrib(egl, display, config, 12323, 0);
                    int b = findConfigAttrib(egl, display, config, 12322, 0);
                    int a = findConfigAttrib(egl, display, config, 12321, 0);
                    if (r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            int i;
            if (withDepthBuffer) {
                i = 16;
            } else {
                i = 0;
            }
            super(8, 8, 8, 0, i, 0);
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

    protected void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
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

    public void setPreserveEGLContextOnPause(boolean preserveOnPause) {
        this.mPreserveEGLContextOnPause = preserveOnPause;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }

    public void setRenderer(Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.mRenderer = renderer;
        this.mGLThread = new GLThread(this.mThisWeakRef);
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
        setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        setEGLConfigChooser(new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setEGLContextClientVersion(int version) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = version;
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

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            int renderMode = 1;
            if (this.mGLThread != null) {
                renderMode = this.mGLThread.getRenderMode();
            }
            this.mGLThread = new GLThread(this.mThisWeakRef);
            if (renderMode != 1) {
                this.mGLThread.setRenderMode(renderMode);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }

    protected void onDetachedFromWindow() {
        if (this.mGLThread != null) {
            this.mGLThread.requestExitAndWait();
        }
        this.mDetached = true;
        super.onDetachedFromWindow();
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private static String getHex(int value) {
        return "0x" + Integer.toHexString(value);
    }

    public static String getErrorString(int error) {
        switch (error) {
            case 12288:
                return "EGL_SUCCESS";
            case 12289:
                return "EGL_NOT_INITIALIZED";
            case 12290:
                return "EGL_BAD_ACCESS";
            case 12291:
                return "EGL_BAD_ALLOC";
            case 12292:
                return "EGL_BAD_ATTRIBUTE";
            case 12293:
                return "EGL_BAD_CONFIG";
            case 12294:
                return "EGL_BAD_CONTEXT";
            case 12295:
                return "EGL_BAD_CURRENT_SURFACE";
            case 12296:
                return "EGL_BAD_DISPLAY";
            case 12297:
                return "EGL_BAD_MATCH";
            case 12298:
                return "EGL_BAD_NATIVE_PIXMAP";
            case 12299:
                return "EGL_BAD_NATIVE_WINDOW";
            case 12300:
                return "EGL_BAD_PARAMETER";
            case 12301:
                return "EGL_BAD_SURFACE";
            case 12302:
                return "EGL_CONTEXT_LOST";
            default:
                return getHex(error);
        }
    }
}
