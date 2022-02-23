package com.example.glnr.amin.imgprocwithopencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "OpenCV Loaded ";
    Button BnrBtn;
    private final int CAMERA_RESULT = 101;
    Button CannyBtn;
    Button FaceBtn;
    Button GausBtn;
    Button GrayBtn;
    Button HsvBtn;
    Button SobelBtn;
    TextView txt;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV Successfully");
        } else {
            Log.i(TAG, "OpenCV unSuccessfully");
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0) {
            dispatchPictureIntent();
        } else {
            if (shouldShowRequestPermissionRationale("android.permission.CAMERA")) {
                Toast.makeText(getApplicationContext(), "Permission Needed", 1).show();
            }
            requestPermissions(new String[]{"android.permission.CAMERA"}, 101);
        }
        this.txt = (TextView) findViewById(R.id.textView);
        this.CannyBtn = (Button) findViewById(R.id.Canny_btn);
        this.GrayBtn = (Button) findViewById(R.id.Gray_btn);
        this.HsvBtn = (Button) findViewById(R.id.Hsv_btn);
        this.GausBtn = (Button) findViewById(R.id.Gaus_btn);
        this.SobelBtn = (Button) findViewById(R.id.Blr_btn);
        this.BnrBtn = (Button) findViewById(R.id.Bnr_btn);
        this.FaceBtn = (Button) findViewById(R.id.Face_btn);
        this.FaceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Face Detector is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, FaceActivity.class));
            }
        });
        this.CannyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Canny Edge Detector is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, CannyActivity.class));
            }
        });
        this.GrayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Convert to Gray is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, GrayActivity.class));
            }
        });
        this.HsvBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "RGB to HSV Convert is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, HsvActivity.class));
            }
        });
        this.GausBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Gaussian Blur is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, GaussianBlurActivity.class));
            }
        });
        this.SobelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Blur is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, BlurActivity.class));
            }
        });
        this.BnrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Convert to Binary is Running", 0).show();
                MainActivity.this.startActivity(new Intent(MainActivity.this, BinaryActivity.class));
            }
        });
    }

    private void dispatchPictureIntent() {
        if (OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "OpenCV Loaded Successfully", 1).show();
        } else {
            Toast.makeText(getApplicationContext(), "OpenCV Loaded unSuccessfully", 0).show();
        }
        if (new Intent("android.media.action.IMAGE_CAPTURE").resolveActivity(getPackageManager()) != null) {
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 101) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(getApplicationContext(), "Permission Neded", 1).show();
        } else {
            dispatchPictureIntent();
        }
    }
}
