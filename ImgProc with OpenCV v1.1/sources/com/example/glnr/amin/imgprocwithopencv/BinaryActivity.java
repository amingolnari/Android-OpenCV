package com.example.glnr.amin.imgprocwithopencv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BinaryActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loaded ";
    Mat imgBinary;
    Mat imgGray;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    BinaryActivity.this.javaCameraView.enableView();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    public int var = 120;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        final String[] S = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter binary threshold");
        final EditText edt = new EditText(this);
        builder.setView(edt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(BinaryActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    Toast.makeText(BinaryActivity.this.getApplicationContext(), "Threshold is set to : " + BinaryActivity.this.var, 0).show();
                    return;
                }
                BinaryActivity.this.var = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (BinaryActivity.this.var > 255) {
                    Toast.makeText(BinaryActivity.this.getApplicationContext(), "maxThreshold is : 255", 0).show();
                    BinaryActivity.this.var = 255;
                } else if (BinaryActivity.this.var < 0) {
                    Toast.makeText(BinaryActivity.this.getApplicationContext(), "minThreshold is : 0", 0).show();
                    BinaryActivity.this.var = 0;
                }
                Toast.makeText(BinaryActivity.this.getApplicationContext(), "threshold is set to : " + BinaryActivity.this.var, 0).show();
            }
        });
        builder.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BinaryActivity.this.var = 120;
                Toast.makeText(BinaryActivity.this.getApplicationContext(), "threshold is set to : 120", 0).show();
            }
        });
        builder.create().show();
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
        this.imgBinary = new Mat(height, width, CvType.CV_8UC1);
        this.imgGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        this.imgBinary.release();
        this.imgGray.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.imgGray = inputFrame.gray();
        Core.extractChannel(this.imgGray, this.imgBinary, 0);
        Imgproc.threshold(this.imgGray, this.imgBinary, (double) this.var, 255.0d, 0);
        return this.imgBinary;
    }
}
