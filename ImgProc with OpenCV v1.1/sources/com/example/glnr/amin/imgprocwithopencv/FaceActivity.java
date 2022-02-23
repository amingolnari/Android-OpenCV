package com.example.glnr.amin.imgprocwithopencv;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loaded ";
    Mat RGBa;
    private int absoluteFaceSize;
    private CascadeClassifier cascadeClassifier;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    FaceActivity.this.javaCameraView.enableView();
                    FaceActivity.this.initializeOpenCVDependencies();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    Mat rgb;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        this.javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        this.javaCameraView.setVisibility(0);
        this.javaCameraView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener2) this);
    }

    /* access modifiers changed from: private */
    public void initializeOpenCVDependencies() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File mCascadeFile = new File(getDir("cascade", 0), "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    os.write(buffer, 0, bytesRead);
                } else {
                    inputStream.close();
                    os.close();
                    this.cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                    Log.i("Cascade::Activity", "Loading cascade successfully");
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("Cascade::Activity", "Error loading cascade", e);
        }
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
        this.RGBa = new Mat(height, width, CvType.CV_8UC4);
        this.rgb = new Mat(height, width, CvType.CV_8UC4);
        this.absoluteFaceSize = (int) (((double) height) * 0.3d);
    }

    public void onCameraViewStopped() {
        this.RGBa.release();
        this.rgb.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.RGBa = inputFrame.rgba();
        Imgproc.cvtColor(this.RGBa, this.rgb, 3);
        try {
            MatOfRect faces = new MatOfRect();
            if (this.cascadeClassifier != null) {
                this.cascadeClassifier.detectMultiScale(this.rgb, faces, 1.1d, 2, 2, new Size((double) this.absoluteFaceSize, (double) this.absoluteFaceSize), new Size());
            }
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                Core.rectangle(this.RGBa, facesArray[i].tl(), facesArray[i].br(), new Scalar(0.0d, 255.0d, 255.0d, 0.0d), 3);
            }
        } catch (CvException e) {
            Log.e("Exception", e.getMessage());
        }
        return this.RGBa;
    }
}
