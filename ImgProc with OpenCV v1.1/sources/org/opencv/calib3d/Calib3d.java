package org.opencv.calib3d;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.utils.Converters;

public class Calib3d {
    public static final int CALIB_CB_ADAPTIVE_THRESH = 1;
    public static final int CALIB_CB_ASYMMETRIC_GRID = 2;
    public static final int CALIB_CB_CLUSTERING = 4;
    public static final int CALIB_CB_FAST_CHECK = 8;
    public static final int CALIB_CB_FILTER_QUADS = 4;
    public static final int CALIB_CB_NORMALIZE_IMAGE = 2;
    public static final int CALIB_CB_SYMMETRIC_GRID = 1;
    public static final int CALIB_FIX_ASPECT_RATIO = 2;
    public static final int CALIB_FIX_FOCAL_LENGTH = 16;
    public static final int CALIB_FIX_INTRINSIC = 256;
    public static final int CALIB_FIX_K1 = 32;
    public static final int CALIB_FIX_K2 = 64;
    public static final int CALIB_FIX_K3 = 128;
    public static final int CALIB_FIX_K4 = 2048;
    public static final int CALIB_FIX_K5 = 4096;
    public static final int CALIB_FIX_K6 = 8192;
    public static final int CALIB_FIX_PRINCIPAL_POINT = 4;
    public static final int CALIB_RATIONAL_MODEL = 16384;
    public static final int CALIB_SAME_FOCAL_LENGTH = 512;
    public static final int CALIB_USE_INTRINSIC_GUESS = 1;
    public static final int CALIB_ZERO_DISPARITY = 1024;
    public static final int CALIB_ZERO_TANGENT_DIST = 8;
    private static final int CV_CALIB_FIX_ASPECT_RATIO = 2;
    private static final int CV_CALIB_FIX_FOCAL_LENGTH = 16;
    private static final int CV_CALIB_FIX_INTRINSIC = 256;
    private static final int CV_CALIB_FIX_K1 = 32;
    private static final int CV_CALIB_FIX_K2 = 64;
    private static final int CV_CALIB_FIX_K3 = 128;
    private static final int CV_CALIB_FIX_K4 = 2048;
    private static final int CV_CALIB_FIX_K5 = 4096;
    private static final int CV_CALIB_FIX_K6 = 8192;
    private static final int CV_CALIB_FIX_PRINCIPAL_POINT = 4;
    private static final int CV_CALIB_RATIONAL_MODEL = 16384;
    private static final int CV_CALIB_SAME_FOCAL_LENGTH = 512;
    private static final int CV_CALIB_USE_INTRINSIC_GUESS = 1;
    private static final int CV_CALIB_ZERO_DISPARITY = 1024;
    private static final int CV_CALIB_ZERO_TANGENT_DIST = 8;
    public static final int CV_EPNP = 1;
    private static final int CV_FM_7POINT = 1;
    private static final int CV_FM_8POINT = 2;
    private static final int CV_FM_LMEDS = 4;
    private static final int CV_FM_RANSAC = 8;
    public static final int CV_ITERATIVE = 0;
    private static final int CV_LMEDS = 4;
    public static final int CV_P3P = 2;
    private static final int CV_RANSAC = 8;
    public static final int EPNP = 1;
    public static final int FM_7POINT = 1;
    public static final int FM_8POINT = 2;
    public static final int FM_LMEDS = 4;
    public static final int FM_RANSAC = 8;
    public static final int ITERATIVE = 0;
    public static final int LMEDS = 4;
    public static final int P3P = 2;
    public static final int RANSAC = 8;

    private static native double[] RQDecomp3x3_0(long j, long j2, long j3, long j4, long j5, long j6);

    private static native double[] RQDecomp3x3_1(long j, long j2, long j3);

    private static native void Rodrigues_0(long j, long j2, long j3);

    private static native void Rodrigues_1(long j, long j2);

    private static native double calibrateCamera_0(long j, long j2, double d, double d2, long j3, long j4, long j5, long j6, int i, int i2, int i3, double d3);

    private static native double calibrateCamera_1(long j, long j2, double d, double d2, long j3, long j4, long j5, long j6, int i);

    private static native double calibrateCamera_2(long j, long j2, double d, double d2, long j3, long j4, long j5, long j6);

    private static native void calibrationMatrixValues_0(long j, double d, double d2, double d3, double d4, double[] dArr, double[] dArr2, double[] dArr3, double[] dArr4, double[] dArr5);

    private static native void composeRT_0(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, long j9, long j10, long j11, long j12, long j13, long j14);

    private static native void composeRT_1(long j, long j2, long j3, long j4, long j5, long j6);

    private static native void computeCorrespondEpilines_0(long j, int i, long j2, long j3);

    private static native void convertPointsFromHomogeneous_0(long j, long j2);

    private static native void convertPointsToHomogeneous_0(long j, long j2);

    private static native void correctMatches_0(long j, long j2, long j3, long j4, long j5);

    private static native void decomposeProjectionMatrix_0(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8);

    private static native void decomposeProjectionMatrix_1(long j, long j2, long j3, long j4);

    private static native void drawChessboardCorners_0(long j, double d, double d2, long j2, boolean z);

    private static native int estimateAffine3D_0(long j, long j2, long j3, long j4, double d, double d2);

    private static native int estimateAffine3D_1(long j, long j2, long j3, long j4);

    private static native void filterSpeckles_0(long j, double d, int i, double d2, long j2);

    private static native void filterSpeckles_1(long j, double d, int i, double d2);

    private static native boolean findChessboardCorners_0(long j, double d, double d2, long j2, int i);

    private static native boolean findChessboardCorners_1(long j, double d, double d2, long j2);

    private static native boolean findCirclesGridDefault_0(long j, double d, double d2, long j2, int i);

    private static native boolean findCirclesGridDefault_1(long j, double d, double d2, long j2);

    private static native long findFundamentalMat_0(long j, long j2, int i, double d, double d2, long j3);

    private static native long findFundamentalMat_1(long j, long j2, int i, double d, double d2);

    private static native long findFundamentalMat_2(long j, long j2);

    private static native long findHomography_0(long j, long j2, int i, double d, long j3);

    private static native long findHomography_1(long j, long j2, int i, double d);

    private static native long findHomography_2(long j, long j2);

    private static native long getOptimalNewCameraMatrix_0(long j, long j2, double d, double d2, double d3, double d4, double d5, double[] dArr, boolean z);

    private static native long getOptimalNewCameraMatrix_1(long j, long j2, double d, double d2, double d3);

    private static native double[] getValidDisparityROI_0(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11);

    private static native long initCameraMatrix2D_0(long j, long j2, double d, double d2, double d3);

    private static native long initCameraMatrix2D_1(long j, long j2, double d, double d2);

    private static native void matMulDeriv_0(long j, long j2, long j3, long j4);

    private static native void projectPoints_0(long j, long j2, long j3, long j4, long j5, long j6, long j7, double d);

    private static native void projectPoints_1(long j, long j2, long j3, long j4, long j5, long j6);

    private static native float rectify3Collinear_0(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, double d, double d2, long j9, long j10, long j11, long j12, long j13, long j14, long j15, long j16, long j17, long j18, long j19, double d3, double d4, double d5, double[] dArr, double[] dArr2, int i);

    private static native void reprojectImageTo3D_0(long j, long j2, long j3, boolean z, int i);

    private static native void reprojectImageTo3D_1(long j, long j2, long j3, boolean z);

    private static native void reprojectImageTo3D_2(long j, long j2, long j3);

    private static native void solvePnPRansac_0(long j, long j2, long j3, long j4, long j5, long j6, boolean z, int i, float f, int i2, long j7, int i3);

    private static native void solvePnPRansac_1(long j, long j2, long j3, long j4, long j5, long j6);

    private static native boolean solvePnP_0(long j, long j2, long j3, long j4, long j5, long j6, boolean z, int i);

    private static native boolean solvePnP_1(long j, long j2, long j3, long j4, long j5, long j6);

    private static native double stereoCalibrate_0(long j, long j2, long j3, long j4, long j5, long j6, long j7, double d, double d2, long j8, long j9, long j10, long j11, int i, int i2, double d3, int i3);

    private static native double stereoCalibrate_1(long j, long j2, long j3, long j4, long j5, long j6, long j7, double d, double d2, long j8, long j9, long j10, long j11);

    private static native boolean stereoRectifyUncalibrated_0(long j, long j2, long j3, double d, double d2, long j4, long j5, double d3);

    private static native boolean stereoRectifyUncalibrated_1(long j, long j2, long j3, double d, double d2, long j4, long j5);

    private static native void stereoRectify_0(long j, long j2, long j3, long j4, double d, double d2, long j5, long j6, long j7, long j8, long j9, long j10, long j11, int i, double d3, double d4, double d5, double[] dArr, double[] dArr2);

    private static native void stereoRectify_1(long j, long j2, long j3, long j4, double d, double d2, long j5, long j6, long j7, long j8, long j9, long j10, long j11);

    private static native void triangulatePoints_0(long j, long j2, long j3, long j4, long j5);

    private static native void validateDisparity_0(long j, long j2, int i, int i2, int i3);

    private static native void validateDisparity_1(long j, long j2, int i, int i2);

    public static double[] RQDecomp3x3(Mat src, Mat mtxR, Mat mtxQ, Mat Qx, Mat Qy, Mat Qz) {
        return RQDecomp3x3_0(src.nativeObj, mtxR.nativeObj, mtxQ.nativeObj, Qx.nativeObj, Qy.nativeObj, Qz.nativeObj);
    }

    public static double[] RQDecomp3x3(Mat src, Mat mtxR, Mat mtxQ) {
        return RQDecomp3x3_1(src.nativeObj, mtxR.nativeObj, mtxQ.nativeObj);
    }

    public static void Rodrigues(Mat src, Mat dst, Mat jacobian) {
        Rodrigues_0(src.nativeObj, dst.nativeObj, jacobian.nativeObj);
    }

    public static void Rodrigues(Mat src, Mat dst) {
        Rodrigues_1(src.nativeObj, dst.nativeObj);
    }

    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs, int flags, TermCriteria criteria) {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_0(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj, flags, criteria.type, criteria.maxCount, criteria.epsilon);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        return retVal;
    }

    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs, int flags) {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_1(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj, flags);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        return retVal;
    }

    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs) {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_2(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        return retVal;
    }

    public static void calibrationMatrixValues(Mat cameraMatrix, Size imageSize, double apertureWidth, double apertureHeight, double[] fovx, double[] fovy, double[] focalLength, Point principalPoint, double[] aspectRatio) {
        double[] fovx_out = new double[1];
        double[] fovy_out = new double[1];
        double[] focalLength_out = new double[1];
        double[] principalPoint_out = new double[2];
        double[] aspectRatio_out = new double[1];
        calibrationMatrixValues_0(cameraMatrix.nativeObj, imageSize.width, imageSize.height, apertureWidth, apertureHeight, fovx_out, fovy_out, focalLength_out, principalPoint_out, aspectRatio_out);
        if (fovx != null) {
            fovx[0] = fovx_out[0];
        }
        if (fovy != null) {
            fovy[0] = fovy_out[0];
        }
        if (focalLength != null) {
            focalLength[0] = focalLength_out[0];
        }
        if (principalPoint != null) {
            principalPoint.x = principalPoint_out[0];
            principalPoint.y = principalPoint_out[1];
        }
        if (aspectRatio != null) {
            aspectRatio[0] = aspectRatio_out[0];
        }
    }

    public static void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat rvec3, Mat tvec3, Mat dr3dr1, Mat dr3dt1, Mat dr3dr2, Mat dr3dt2, Mat dt3dr1, Mat dt3dt1, Mat dt3dr2, Mat dt3dt2) {
        composeRT_0(rvec1.nativeObj, tvec1.nativeObj, rvec2.nativeObj, tvec2.nativeObj, rvec3.nativeObj, tvec3.nativeObj, dr3dr1.nativeObj, dr3dt1.nativeObj, dr3dr2.nativeObj, dr3dt2.nativeObj, dt3dr1.nativeObj, dt3dt1.nativeObj, dt3dr2.nativeObj, dt3dt2.nativeObj);
    }

    public static void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat rvec3, Mat tvec3) {
        composeRT_1(rvec1.nativeObj, tvec1.nativeObj, rvec2.nativeObj, tvec2.nativeObj, rvec3.nativeObj, tvec3.nativeObj);
    }

    public static void computeCorrespondEpilines(Mat points, int whichImage, Mat F, Mat lines) {
        computeCorrespondEpilines_0(points.nativeObj, whichImage, F.nativeObj, lines.nativeObj);
    }

    public static void convertPointsFromHomogeneous(Mat src, Mat dst) {
        convertPointsFromHomogeneous_0(src.nativeObj, dst.nativeObj);
    }

    public static void convertPointsToHomogeneous(Mat src, Mat dst) {
        convertPointsToHomogeneous_0(src.nativeObj, dst.nativeObj);
    }

    public static void correctMatches(Mat F, Mat points1, Mat points2, Mat newPoints1, Mat newPoints2) {
        correctMatches_0(F.nativeObj, points1.nativeObj, points2.nativeObj, newPoints1.nativeObj, newPoints2.nativeObj);
    }

    public static void decomposeProjectionMatrix(Mat projMatrix, Mat cameraMatrix, Mat rotMatrix, Mat transVect, Mat rotMatrixX, Mat rotMatrixY, Mat rotMatrixZ, Mat eulerAngles) {
        decomposeProjectionMatrix_0(projMatrix.nativeObj, cameraMatrix.nativeObj, rotMatrix.nativeObj, transVect.nativeObj, rotMatrixX.nativeObj, rotMatrixY.nativeObj, rotMatrixZ.nativeObj, eulerAngles.nativeObj);
    }

    public static void decomposeProjectionMatrix(Mat projMatrix, Mat cameraMatrix, Mat rotMatrix, Mat transVect) {
        decomposeProjectionMatrix_1(projMatrix.nativeObj, cameraMatrix.nativeObj, rotMatrix.nativeObj, transVect.nativeObj);
    }

    public static void drawChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners, boolean patternWasFound) {
        drawChessboardCorners_0(image.nativeObj, patternSize.width, patternSize.height, corners.nativeObj, patternWasFound);
    }

    public static int estimateAffine3D(Mat src, Mat dst, Mat out, Mat inliers, double ransacThreshold, double confidence) {
        return estimateAffine3D_0(src.nativeObj, dst.nativeObj, out.nativeObj, inliers.nativeObj, ransacThreshold, confidence);
    }

    public static int estimateAffine3D(Mat src, Mat dst, Mat out, Mat inliers) {
        return estimateAffine3D_1(src.nativeObj, dst.nativeObj, out.nativeObj, inliers.nativeObj);
    }

    public static void filterSpeckles(Mat img, double newVal, int maxSpeckleSize, double maxDiff, Mat buf) {
        filterSpeckles_0(img.nativeObj, newVal, maxSpeckleSize, maxDiff, buf.nativeObj);
    }

    public static void filterSpeckles(Mat img, double newVal, int maxSpeckleSize, double maxDiff) {
        filterSpeckles_1(img.nativeObj, newVal, maxSpeckleSize, maxDiff);
    }

    public static boolean findChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners, int flags) {
        return findChessboardCorners_0(image.nativeObj, patternSize.width, patternSize.height, corners.nativeObj, flags);
    }

    public static boolean findChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners) {
        return findChessboardCorners_1(image.nativeObj, patternSize.width, patternSize.height, corners.nativeObj);
    }

    public static boolean findCirclesGridDefault(Mat image, Size patternSize, Mat centers, int flags) {
        return findCirclesGridDefault_0(image.nativeObj, patternSize.width, patternSize.height, centers.nativeObj, flags);
    }

    public static boolean findCirclesGridDefault(Mat image, Size patternSize, Mat centers) {
        return findCirclesGridDefault_1(image.nativeObj, patternSize.width, patternSize.height, centers.nativeObj);
    }

    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2, int method, double param1, double param2, Mat mask) {
        return new Mat(findFundamentalMat_0(points1.nativeObj, points2.nativeObj, method, param1, param2, mask.nativeObj));
    }

    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2, int method, double param1, double param2) {
        return new Mat(findFundamentalMat_1(points1.nativeObj, points2.nativeObj, method, param1, param2));
    }

    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2) {
        return new Mat(findFundamentalMat_2(points1.nativeObj, points2.nativeObj));
    }

    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints, int method, double ransacReprojThreshold, Mat mask) {
        return new Mat(findHomography_0(srcPoints.nativeObj, dstPoints.nativeObj, method, ransacReprojThreshold, mask.nativeObj));
    }

    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints, int method, double ransacReprojThreshold) {
        return new Mat(findHomography_1(srcPoints.nativeObj, dstPoints.nativeObj, method, ransacReprojThreshold));
    }

    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints) {
        return new Mat(findHomography_2(srcPoints.nativeObj, dstPoints.nativeObj));
    }

    public static Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha, Size newImgSize, Rect validPixROI, boolean centerPrincipalPoint) {
        double[] validPixROI_out = new double[4];
        Mat mat = new Mat(getOptimalNewCameraMatrix_0(cameraMatrix.nativeObj, distCoeffs.nativeObj, imageSize.width, imageSize.height, alpha, newImgSize.width, newImgSize.height, validPixROI_out, centerPrincipalPoint));
        if (validPixROI != null) {
            validPixROI.x = (int) validPixROI_out[0];
            validPixROI.y = (int) validPixROI_out[1];
            validPixROI.width = (int) validPixROI_out[2];
            validPixROI.height = (int) validPixROI_out[3];
        }
        return mat;
    }

    public static Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha) {
        return new Mat(getOptimalNewCameraMatrix_1(cameraMatrix.nativeObj, distCoeffs.nativeObj, imageSize.width, imageSize.height, alpha));
    }

    public static Rect getValidDisparityROI(Rect roi1, Rect roi2, int minDisparity, int numberOfDisparities, int SADWindowSize) {
        return new Rect(getValidDisparityROI_0(roi1.x, roi1.y, roi1.width, roi1.height, roi2.x, roi2.y, roi2.width, roi2.height, minDisparity, numberOfDisparities, SADWindowSize));
    }

    public static Mat initCameraMatrix2D(List<MatOfPoint3f> objectPoints, List<MatOfPoint2f> imagePoints, Size imageSize, double aspectRatio) {
        return new Mat(initCameraMatrix2D_0(Converters.vector_vector_Point3f_to_Mat(objectPoints, new ArrayList<>(objectPoints != null ? objectPoints.size() : 0)).nativeObj, Converters.vector_vector_Point2f_to_Mat(imagePoints, new ArrayList<>(imagePoints != null ? imagePoints.size() : 0)).nativeObj, imageSize.width, imageSize.height, aspectRatio));
    }

    public static Mat initCameraMatrix2D(List<MatOfPoint3f> objectPoints, List<MatOfPoint2f> imagePoints, Size imageSize) {
        int i;
        int i2 = 0;
        if (objectPoints != null) {
            i = objectPoints.size();
        } else {
            i = 0;
        }
        Mat objectPoints_mat = Converters.vector_vector_Point3f_to_Mat(objectPoints, new ArrayList<>(i));
        if (imagePoints != null) {
            i2 = imagePoints.size();
        }
        return new Mat(initCameraMatrix2D_1(objectPoints_mat.nativeObj, Converters.vector_vector_Point2f_to_Mat(imagePoints, new ArrayList<>(i2)).nativeObj, imageSize.width, imageSize.height));
    }

    public static void matMulDeriv(Mat A, Mat B, Mat dABdA, Mat dABdB) {
        matMulDeriv_0(A.nativeObj, B.nativeObj, dABdA.nativeObj, dABdB.nativeObj);
    }

    public static void projectPoints(MatOfPoint3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, MatOfDouble distCoeffs, MatOfPoint2f imagePoints, Mat jacobian, double aspectRatio) {
        projectPoints_0(objectPoints.nativeObj, rvec.nativeObj, tvec.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, imagePoints.nativeObj, jacobian.nativeObj, aspectRatio);
    }

    public static void projectPoints(MatOfPoint3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, MatOfDouble distCoeffs, MatOfPoint2f imagePoints) {
        projectPoints_1(objectPoints.nativeObj, rvec.nativeObj, tvec.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, imagePoints.nativeObj);
    }

    public static float rectify3Collinear(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Mat cameraMatrix3, Mat distCoeffs3, List<Mat> imgpt1, List<Mat> imgpt3, Size imageSize, Mat R12, Mat T12, Mat R13, Mat T13, Mat R1, Mat R2, Mat R3, Mat P1, Mat P2, Mat P3, Mat Q, double alpha, Size newImgSize, Rect roi1, Rect roi2, int flags) {
        double[] roi1_out = new double[4];
        double[] roi2_out = new double[4];
        double d = alpha;
        float retVal = rectify3Collinear_0(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, cameraMatrix3.nativeObj, distCoeffs3.nativeObj, Converters.vector_Mat_to_Mat(imgpt1).nativeObj, Converters.vector_Mat_to_Mat(imgpt3).nativeObj, imageSize.width, imageSize.height, R12.nativeObj, T12.nativeObj, R13.nativeObj, T13.nativeObj, R1.nativeObj, R2.nativeObj, R3.nativeObj, P1.nativeObj, P2.nativeObj, P3.nativeObj, Q.nativeObj, d, newImgSize.width, newImgSize.height, roi1_out, roi2_out, flags);
        if (roi1 != null) {
            roi1.x = (int) roi1_out[0];
            roi1.y = (int) roi1_out[1];
            roi1.width = (int) roi1_out[2];
            roi1.height = (int) roi1_out[3];
        }
        if (roi2 != null) {
            roi2.x = (int) roi2_out[0];
            roi2.y = (int) roi2_out[1];
            roi2.width = (int) roi2_out[2];
            roi2.height = (int) roi2_out[3];
        }
        return retVal;
    }

    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q, boolean handleMissingValues, int ddepth) {
        reprojectImageTo3D_0(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj, handleMissingValues, ddepth);
    }

    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q, boolean handleMissingValues) {
        reprojectImageTo3D_1(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj, handleMissingValues);
    }

    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q) {
        reprojectImageTo3D_2(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj);
    }

    public static boolean solvePnP(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec, boolean useExtrinsicGuess, int flags) {
        return solvePnP_0(objectPoints.nativeObj, imagePoints.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvec.nativeObj, tvec.nativeObj, useExtrinsicGuess, flags);
    }

    public static boolean solvePnP(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec) {
        return solvePnP_1(objectPoints.nativeObj, imagePoints.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvec.nativeObj, tvec.nativeObj);
    }

    public static void solvePnPRansac(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec, boolean useExtrinsicGuess, int iterationsCount, float reprojectionError, int minInliersCount, Mat inliers, int flags) {
        solvePnPRansac_0(objectPoints.nativeObj, imagePoints.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvec.nativeObj, tvec.nativeObj, useExtrinsicGuess, iterationsCount, reprojectionError, minInliersCount, inliers.nativeObj, flags);
    }

    public static void solvePnPRansac(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec) {
        solvePnPRansac_1(objectPoints.nativeObj, imagePoints.nativeObj, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvec.nativeObj, tvec.nativeObj);
    }

    public static double stereoCalibrate(List<Mat> objectPoints, List<Mat> imagePoints1, List<Mat> imagePoints2, Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat E, Mat F, TermCriteria criteria, int flags) {
        return stereoCalibrate_0(Converters.vector_Mat_to_Mat(objectPoints).nativeObj, Converters.vector_Mat_to_Mat(imagePoints1).nativeObj, Converters.vector_Mat_to_Mat(imagePoints2).nativeObj, cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, E.nativeObj, F.nativeObj, criteria.type, criteria.maxCount, criteria.epsilon, flags);
    }

    public static double stereoCalibrate(List<Mat> objectPoints, List<Mat> imagePoints1, List<Mat> imagePoints2, Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat E, Mat F) {
        return stereoCalibrate_1(Converters.vector_Mat_to_Mat(objectPoints).nativeObj, Converters.vector_Mat_to_Mat(imagePoints1).nativeObj, Converters.vector_Mat_to_Mat(imagePoints2).nativeObj, cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, E.nativeObj, F.nativeObj);
    }

    public static void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat R1, Mat R2, Mat P1, Mat P2, Mat Q, int flags, double alpha, Size newImageSize, Rect validPixROI1, Rect validPixROI2) {
        double[] validPixROI1_out = new double[4];
        double[] validPixROI2_out = new double[4];
        stereoRectify_0(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, R1.nativeObj, R2.nativeObj, P1.nativeObj, P2.nativeObj, Q.nativeObj, flags, alpha, newImageSize.width, newImageSize.height, validPixROI1_out, validPixROI2_out);
        if (validPixROI1 != null) {
            validPixROI1.x = (int) validPixROI1_out[0];
            validPixROI1.y = (int) validPixROI1_out[1];
            validPixROI1.width = (int) validPixROI1_out[2];
            validPixROI1.height = (int) validPixROI1_out[3];
        }
        if (validPixROI2 != null) {
            validPixROI2.x = (int) validPixROI2_out[0];
            validPixROI2.y = (int) validPixROI2_out[1];
            validPixROI2.width = (int) validPixROI2_out[2];
            validPixROI2.height = (int) validPixROI2_out[3];
        }
    }

    public static void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat R1, Mat R2, Mat P1, Mat P2, Mat Q) {
        stereoRectify_1(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, R1.nativeObj, R2.nativeObj, P1.nativeObj, P2.nativeObj, Q.nativeObj);
    }

    public static boolean stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat H1, Mat H2, double threshold) {
        return stereoRectifyUncalibrated_0(points1.nativeObj, points2.nativeObj, F.nativeObj, imgSize.width, imgSize.height, H1.nativeObj, H2.nativeObj, threshold);
    }

    public static boolean stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat H1, Mat H2) {
        return stereoRectifyUncalibrated_1(points1.nativeObj, points2.nativeObj, F.nativeObj, imgSize.width, imgSize.height, H1.nativeObj, H2.nativeObj);
    }

    public static void triangulatePoints(Mat projMatr1, Mat projMatr2, Mat projPoints1, Mat projPoints2, Mat points4D) {
        triangulatePoints_0(projMatr1.nativeObj, projMatr2.nativeObj, projPoints1.nativeObj, projPoints2.nativeObj, points4D.nativeObj);
    }

    public static void validateDisparity(Mat disparity, Mat cost, int minDisparity, int numberOfDisparities, int disp12MaxDisp) {
        validateDisparity_0(disparity.nativeObj, cost.nativeObj, minDisparity, numberOfDisparities, disp12MaxDisp);
    }

    public static void validateDisparity(Mat disparity, Mat cost, int minDisparity, int numberOfDisparities) {
        validateDisparity_1(disparity.nativeObj, cost.nativeObj, minDisparity, numberOfDisparities);
    }
}
