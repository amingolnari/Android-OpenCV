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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BlurActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loader ";
    Mat imgBlur;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    BlurActivity.this.javaCameraView.enableView();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    Mat mRGBa;
    public int var = 9;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        final String[] S = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter kSize (Integer)");
        final EditText edt = new EditText(this);
        builder.setView(edt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(BlurActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    Toast.makeText(BlurActivity.this.getApplicationContext(), "kSize is set to : " + BlurActivity.this.var, 0).show();
                    return;
                }
                BlurActivity.this.var = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (BlurActivity.this.var > 50) {
                    Toast.makeText(BlurActivity.this.getApplicationContext(), "max-kSize is : 50", 0).show();
                    BlurActivity.this.var = 50;
                } else if (BlurActivity.this.var < 1) {
                    Toast.makeText(BlurActivity.this.getApplicationContext(), "min-kSize is : 1", 0).show();
                    BlurActivity.this.var = 1;
                }
                Toast.makeText(BlurActivity.this.getApplicationContext(), "kSize is set to : " + BlurActivity.this.var, 0).show();
            }
        });
        builder.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BlurActivity.this.getApplicationContext(), "kSize is set to : " + BlurActivity.this.var, 0).show();
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
        this.mRGBa = new Mat(height, width, CvType.CV_8UC4);
        this.imgBlur = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        this.mRGBa.release();
        this.imgBlur.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.mRGBa = inputFrame.gray();
        Imgproc.blur(this.mRGBa, this.imgBlur, new Size((double) this.var, (double) this.var), 0, 2);
        return this.imgBlur;
    }
}
