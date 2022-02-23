package com.example.glnr.amin.imgprocwithopencv;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HsvActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loader ";
    Mat imgHsv;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    HsvActivity.this.javaCameraView.enableView();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    Mat mRGBa;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        this.javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        this.javaCameraView.setVisibility(0);
        this.javaCameraView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener2) this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.javaCameraView != null) {
            this.javaCameraView.disableView();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV Loaded Successfully");
            this.mLoaderCallBack.onManagerConnected(0);
            return;
        }
        Log.i(TAG, "OpenCV Loaded unSuccessfully");
        OpenCVLoader.initAsync("2.4.11", this, this.mLoaderCallBack);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.javaCameraView != null) {
            this.javaCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height) {
        this.mRGBa = new Mat(height, width, CvType.CV_8UC4);
        this.imgHsv = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        this.mRGBa.release();
        this.imgHsv.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.mRGBa = inputFrame.rgba();
        Imgproc.cvtColor(this.mRGBa, this.imgHsv, 67);
        return this.imgHsv;
    }
}
