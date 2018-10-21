package net.daum.mf.map.p000n.api;

/* renamed from: net.daum.mf.map.n.api.NativeThread */
public abstract class NativeThread extends Thread {
    public static native void deleteAutoreleasePool();

    public static native void initAutoreleasePool();

    protected abstract void nativeRun();

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public NativeThread(Runnable target, String name) {
        super(target, name);
    }

    public NativeThread(Runnable target) {
        super(target);
    }

    public NativeThread(String name) {
        super(name);
    }

    public NativeThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public NativeThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public NativeThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public NativeThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public final void run() {
        NativeThread.initAutoreleasePool();
        super.run();
        nativeRun();
        NativeThread.deleteAutoreleasePool();
    }
}
