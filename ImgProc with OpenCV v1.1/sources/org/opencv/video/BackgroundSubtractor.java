package org.opencv.video;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

public class BackgroundSubtractor extends Algorithm {
    private static native void apply_0(long j, long j2, long j3, double d);

    private static native void apply_1(long j, long j2, long j3);

    private static native void delete(long j);

    protected BackgroundSubtractor(long addr) {
        super(addr);
    }

    public void apply(Mat image, Mat fgmask, double learningRate) {
        apply_0(this.nativeObj, image.nativeObj, fgmask.nativeObj, learningRate);
    }

    public void apply(Mat image, Mat fgmask) {
        apply_1(this.nativeObj, image.nativeObj, fgmask.nativeObj);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        delete(this.nativeObj);
    }
}
