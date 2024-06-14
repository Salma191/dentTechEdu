package com.dentproject;

import androidx.annotation.NonNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import org.opencv.core.Point;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class ProcessImage extends ReactContextBaseJavaModule {
    ProcessImage(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {

        return "ProcessImage";
    }

    @ReactMethod
    public void createCalendarEvent(String name, String location) {
        Log.d("CalendarModule", "Create event called with name: " + name
                + " and location: " + location);
    }

    @ReactMethod
    public void applyGrayscaleFilter(String base64Image, Callback callback) {
        if (OpenCVLoader.initDebug()) {
            Log.d("CalendarModule", "I am here");
        } else {
            Log.d("CalendarModule", "I am not here");
        }

        // Convert base64Image to suitable Java image format (assuming Bitmap here)
        Bitmap bitmapImage = decodeBase64ToBitmap(base64Image);

        int newWidth = 500; // Set your desired width
        int newHeight = 500; // Set your desired height

        int originalWidth = bitmapImage.getWidth();
        int originalHeight = bitmapImage.getHeight();

        float scaleWidth = ((float) newWidth) / originalWidth;
        float scaleHeight = ((float) newHeight) / originalHeight;

        // Calculate the ratio to maintain the aspect ratio
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        // Calculate the new dimensions
        int finalWidth = Math.round(originalWidth * scaleFactor);
        int finalHeight = Math.round(originalHeight * scaleFactor);

        // Convert Bitmap to Mat (OpenCV format)
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmapImage, srcMat);
        // Resize the image using OpenCV

        Mat resizedMat = new Mat();
        Size newSize = new Size(finalWidth, finalHeight);

        Imgproc.resize(srcMat, resizedMat, newSize);

        // Convert image to grayscale for edge detection
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizedMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat blurredMat = new Mat();
        Size size = new Size(15, 15); // Adjust kernel size as needed

        Imgproc.GaussianBlur(grayMat, blurredMat, size, 0);

        // Apply Canny edge detection
        Mat edgesMat = new Mat();
        Imgproc.Canny(blurredMat, edgesMat, 50, 150);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edgesMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Draw contours on the original image
        Mat drawing = Mat.zeros(edgesMat.size(), CvType.CV_8UC3);
        Scalar color = new Scalar(255, 255, 255); // Green color for contours
        Imgproc.drawContours(drawing, contours, -1, color, 1);

        // Trace points on contours
        List<Point> tracedPoints = new ArrayList<>();
        Scalar colorPoints = new Scalar(255, 0, 0); // Blue color for traced points
        for (MatOfPoint contour : contours) {
            Point[] points = contour.toArray();
            int step = 4; // Adjust step size to trace points every 'n' steps

            for (int i = 0; i < points.length; i += step) {
                Imgproc.circle(drawing, points[i], 1, colorPoints, -1); // Draw a circle at each point
                tracedPoints.add(points[i]); // Collect traced points
            }
        }

        // Convert Mat back to Bitmap
        Bitmap processedBitmap = Bitmap.createBitmap(drawing.cols(), drawing.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(drawing, processedBitmap);

        // Convert Bitmap to base64 for JavaScript
        String processedImageBase64 = encodeBitmapToBase64(processedBitmap);

        // Prepare the result to send back to JavaScript
        WritableMap resultData = Arguments.createMap();
        resultData.putString("processedImage", processedImageBase64);
        resultData.putArray("tracedPoints", convertPointsToWritableArray(tracedPoints)); // Convert tracedPoints to suitable format for React Native

        // Resolve the promise and pass the processed image and traced points back to JavaScript
        callback.invoke(resultData);
    }

    private WritableArray convertPointsToWritableArray(List<Point> points) {
        WritableArray writableArray = Arguments.createArray();
        for (Point point : points) {
            WritableMap pointMap = Arguments.createMap();
            pointMap.putDouble("x", point.x);
            pointMap.putDouble("y", point.y);
            writableArray.pushMap(pointMap);
        }
        return writableArray;
    }
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        // Implement base64 to Bitmap decoding here
        // This code is platform-specific and can be found in Android documentation or third-party libraries
        // For example:
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        // Implement Bitmap to base64 encoding here
        // This code is platform-specific and can be found in Android documentation or third-party libraries
        // For example:
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }





}