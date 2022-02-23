package org.opencv.android;

import android.content.Context;

public class OpenCVLoader {
    public static final String OPENCV_VERSION_2_4_10 = "2.4.10";
    public static final String OPENCV_VERSION_2_4_11 = "2.4.11";
    public static final String OPENCV_VERSION_2_4_2 = "2.4.2";
    public static final String OPENCV_VERSION_2_4_3 = "2.4.3";
    public static final String OPENCV_VERSION_2_4_4 = "2.4.4";
    public static final String OPENCV_VERSION_2_4_5 = "2.4.5";
    public static final String OPENCV_VERSION_2_4_6 = "2.4.6";
    public static final String OPENCV_VERSION_2_4_7 = "2.4.7";
    public static final String OPENCV_VERSION_2_4_8 = "2.4.8";
    public static final String OPENCV_VERSION_2_4_9 = "2.4.9";

    public static boolean initDebug() {
        return StaticHelper.initOpenCV(false);
    }

    public static boolean initDebug(boolean InitCuda) {
        return StaticHelper.initOpenCV(InitCuda);
    }

    public static boolean initAsync(String Version, Context AppContext, LoaderCallbackInterface Callback) {
        return AsyncServiceHelper.initOpenCV(Version, AppContext, Callback);
    }
}
