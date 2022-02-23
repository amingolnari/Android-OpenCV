package org.opencv.android;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;

public class NativeCameraView extends CameraBridgeViewBase {
    public static final String TAG = "NativeCameraView";
    protected VideoCapture mCamera;
    protected NativeCameraFrame mFrame;
    /* access modifiers changed from: private */
    public boolean mStopThread;
    private Thread mThread;

    public NativeCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public NativeCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public boolean connectCamera(int width, int height) {
        if (!initializeCamera(width, height)) {
            return false;
        }
        this.mThread = new Thread(new CameraWorker());
        this.mThread.start();
        return true;
    }

    /* access modifiers changed from: protected */
    public void disconnectCamera() {
        if (this.mThread != null) {
            try {
                this.mStopThread = true;
                this.mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.mThread = null;
                this.mStopThread = false;
            }
        }
        releaseCamera();
    }

    public static class OpenCvSizeAccessor implements CameraBridgeViewBase.ListItemAccessor {
        public int getWidth(Object obj) {
            return (int) ((Size) obj).width;
        }

        public int getHeight(Object obj) {
            return (int) ((Size) obj).height;
        }
    }

    private boolean initializeCamera(int width, int height) {
        synchronized (this) {
            if (this.mCameraIndex == -1) {
                this.mCamera = new VideoCapture(1000);
            } else {
                this.mCamera = new VideoCapture(this.mCameraIndex + 1000);
            }
            if (this.mCamera == null) {
                return false;
            }
            if (!this.mCamera.isOpened()) {
                return false;
            }
            this.mFrame = new NativeCameraFrame(this.mCamera);
            Size frameSize = calculateCameraFrameSize(this.mCamera.getSupportedPreviewSizes(), new OpenCvSizeAccessor(), width, height);
            this.mFrameWidth = (int) frameSize.width;
            this.mFrameHeight = (int) frameSize.height;
            if (getLayoutParams().width == -1 && getLayoutParams().height == -1) {
                this.mScale = Math.min(((float) height) / ((float) this.mFrameHeight), ((float) width) / ((float) this.mFrameWidth));
            } else {
                this.mScale = 0.0f;
            }
            if (this.mFpsMeter != null) {
                this.mFpsMeter.setResolution(this.mFrameWidth, this.mFrameHeight);
            }
            AllocateCache();
            this.mCamera.set(3, frameSize.width);
            this.mCamera.set(4, frameSize.height);
            Log.i(TAG, "Selected camera frame size = (" + this.mFrameWidth + ", " + this.mFrameHeight + ")");
            return true;
        }
    }

    private void releaseCamera() {
        synchronized (this) {
            if (this.mFrame != null) {
                this.mFrame.release();
            }
            if (this.mCamera != null) {
                this.mCamera.release();
            }
        }
    }

    private static class NativeCameraFrame implements CameraBridgeViewBase.CvCameraViewFrame {
        private VideoCapture mCapture;
        private Mat mGray = new Mat();
        private Mat mRgba = new Mat();

        public Mat rgba() {
            this.mCapture.retrieve(this.mRgba, 4);
            return this.mRgba;
        }

        public Mat gray() {
            this.mCapture.retrieve(this.mGray, 1);
            return this.mGray;
        }

        public NativeCameraFrame(VideoCapture capture) {
            this.mCapture = capture;
        }

        public void release() {
            if (this.mGray != null) {
                this.mGray.release();
            }
            if (this.mRgba != null) {
                this.mRgba.release();
            }
        }
    }

    private class CameraWorker implements Runnable {
        private CameraWorker() {
        }

        public void run() {
            while (NativeCameraView.this.mCamera.grab()) {
                NativeCameraView.this.deliverAndDrawFrame(NativeCameraView.this.mFrame);
                if (NativeCameraView.this.mStopThread) {
                    return;
                }
            }
            Log.e(NativeCameraView.TAG, "Camera frame grab failed");
        }
    }
}
