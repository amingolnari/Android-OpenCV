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

public class GaussianBlurActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = "OpenCV Loader ";
    Mat Gblur;
    JavaCameraView javaCameraView;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    GaussianBlurActivity.this.javaCameraView.enableView();
                    return;
                default:
                    super.onManagerConnected(status);
                    return;
            }
        }
    };
    Mat mRGBa;
    public int sigmaX = 5;
    public int sigmaY = 5;
    public int var = 9;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        final String[] S = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter kSize (Integer & odd)");
        final EditText edt = new EditText(this);
        builder.setView(edt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "kSize is set to : " + GaussianBlurActivity.this.var, 0).show();
                    return;
                }
                GaussianBlurActivity.this.var = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (GaussianBlurActivity.this.var > 49) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "max-kSize is : 49", 0).show();
                    GaussianBlurActivity.this.var = 49;
                } else if (GaussianBlurActivity.this.var < 1) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "min-kSize is : 1", 0).show();
                    GaussianBlurActivity.this.var = 1;
                }
                if (GaussianBlurActivity.this.var % 2 == 0) {
                    GaussianBlurActivity.this.var++;
                    if (GaussianBlurActivity.this.var > 49) {
                        GaussianBlurActivity.this.var = 49;
                    }
                }
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "kSize is set to : " + GaussianBlurActivity.this.var, 0).show();
            }
        });
        builder.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "kSize is set to : " + GaussianBlurActivity.this.var, 0).show();
            }
        });
        builder.create().show();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Enter sigmaY (Integer)");
        final EditText edt1 = new EditText(this);
        builder1.setView(edt1);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt1.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaX is set to : " + GaussianBlurActivity.this.var, 0).show();
                    return;
                }
                GaussianBlurActivity.this.sigmaY = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (GaussianBlurActivity.this.sigmaY > 10) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "max-sigmaX is : 10", 0).show();
                    GaussianBlurActivity.this.sigmaY = 10;
                } else if (GaussianBlurActivity.this.sigmaY < 0) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "min-sigmaX is : 0", 0).show();
                    GaussianBlurActivity.this.sigmaY = 0;
                }
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaY is set to : " + GaussianBlurActivity.this.sigmaY, 0).show();
            }
        });
        builder1.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaY is set to : " + GaussianBlurActivity.this.sigmaY, 0).show();
            }
        });
        builder1.create().show();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Enter sigmaX (Integer)");
        final EditText edt2 = new EditText(this);
        builder2.setView(edt2);
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                S[0] = edt2.getText().toString();
                if (S[0].isEmpty()) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "No numbers entered", 0).show();
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaX is set to : " + GaussianBlurActivity.this.sigmaX, 0).show();
                    return;
                }
                GaussianBlurActivity.this.sigmaX = Integer.parseInt(S[0].replaceAll("[\\D]", ""));
                if (GaussianBlurActivity.this.sigmaX > 10) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "max-sigmaX is : 10", 0).show();
                    GaussianBlurActivity.this.sigmaX = 10;
                } else if (GaussianBlurActivity.this.sigmaX < 0) {
                    Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "min-sigmaX is : 0", 0).show();
                    GaussianBlurActivity.this.sigmaX = 0;
                }
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaX is set to : " + GaussianBlurActivity.this.sigmaX, 0).show();
            }
        });
        builder2.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GaussianBlurActivity.this.getApplicationContext(), "sigmaX is set to : " + GaussianBlurActivity.this.sigmaX, 0).show();
            }
        });
        builder2.create().show();
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
        this.Gblur = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        this.mRGBa.release();
        this.Gblur.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.mRGBa = inputFrame.gray();
        Imgproc.GaussianBlur(this.mRGBa, this.Gblur, new Size((double) this.var, (double) this.var), (double) this.sigmaX, (double) this.sigmaY);
        return this.Gblur;
    }
}
