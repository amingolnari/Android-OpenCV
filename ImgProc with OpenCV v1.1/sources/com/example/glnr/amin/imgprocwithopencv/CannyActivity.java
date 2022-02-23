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
import org.opencv.imgproc.Imgproc;

public class CannyActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loader ";
    Mat imgCanny;
    Mat imgGray;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    CannyActivity.this.javaCameraView.enableView();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    Mat mRGBa;
    public int maxVar = 150;
    public int minVar = 80;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        final String[] S = new String[1];
        final EditText edt = new EditText(this);
        final EditText edt1 = new EditText(this);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Enter Threshold2 (Integer)");
        builder1.setView(edt1);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt1.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    if (CannyActivity.this.maxVar < CannyActivity.this.minVar) {
                        CannyActivity.this.maxVar += ((256 - CannyActivity.this.minVar) / 3) + 1;
                    }
                    if (CannyActivity.this.maxVar > 255) {
                        CannyActivity.this.maxVar = 255;
                    } else if (CannyActivity.this.maxVar < 1) {
                        CannyActivity.this.maxVar = 1;
                    }
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold2 is set to : " + CannyActivity.this.maxVar, 0).show();
                    return;
                }
                CannyActivity.this.maxVar = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (CannyActivity.this.maxVar < CannyActivity.this.minVar) {
                    CannyActivity.this.maxVar += ((255 - CannyActivity.this.minVar) / 2) + 256;
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold2 should not be smaller than threshold1", 0).show();
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold2 is set to default", 0).show();
                }
                if (CannyActivity.this.maxVar > 255) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "max threshold2 is : 255", 0).show();
                    CannyActivity.this.maxVar = 255;
                } else if (CannyActivity.this.maxVar < 1) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "min threshold2 is : 1", 0).show();
                    CannyActivity.this.maxVar = 1;
                }
                Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold2 is set to : " + CannyActivity.this.maxVar, 0).show();
            }
        });
        builder1.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CannyActivity.this.getApplicationContext(), "default threshold2 is set to : " + CannyActivity.this.maxVar, 0).show();
            }
        });
        builder1.create().show();
        AlertDialog.Builder builder0 = new AlertDialog.Builder(this);
        builder0.setTitle("Enter Threshold1 (Integer)");
        builder0.setView(edt);
        builder0.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt.getText().toString();
                if (S[0].equals("")) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    if (CannyActivity.this.minVar > 254) {
                        CannyActivity.this.minVar = 254;
                    } else if (CannyActivity.this.minVar < 0) {
                        CannyActivity.this.minVar = 0;
                    }
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold1 is set to : " + CannyActivity.this.minVar, 0).show();
                    return;
                }
                CannyActivity.this.minVar = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (CannyActivity.this.minVar > 254) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "max threshold1 is : 254", 0).show();
                    CannyActivity.this.minVar = 254;
                } else if (CannyActivity.this.minVar < 0) {
                    Toast.makeText(CannyActivity.this.getApplicationContext(), "min threshold1 is : 0", 0).show();
                    CannyActivity.this.minVar = 0;
                }
                Toast.makeText(CannyActivity.this.getApplicationContext(), "threshold1 is set to : " + CannyActivity.this.minVar, 0).show();
            }
        });
        builder0.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CannyActivity.this.getApplicationContext(), "default threshold1 is set to : " + CannyActivity.this.minVar, 0).show();
            }
        });
        builder0.create().show();
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
        this.imgGray = new Mat(height, width, CvType.CV_8UC1);
        this.imgCanny = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        this.mRGBa.release();
        this.imgCanny.release();
        this.imgGray.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.mRGBa = inputFrame.rgba();
        Imgproc.cvtColor(this.mRGBa, this.imgGray, 6);
        Imgproc.Canny(this.imgGray, this.imgCanny, (double) this.minVar, (double) this.maxVar);
        return this.imgCanny;
    }
}
