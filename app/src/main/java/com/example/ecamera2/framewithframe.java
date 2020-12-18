package com.example.ecamera2;

import android.graphics.Bitmap;
import android.util.Size;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class framewithframe {
    public Bitmap frameCal(Bitmap input){
        Mat result = new Mat();
        Utils.bitmapToMat(input, result);
        result = grabCut(result);
        Imgproc.cvtColor(result, result, Imgproc.COLOR_RGBA2GRAY);
        Utils.matToBitmap(result, input);

        return input;
    }
    public Mat grabCut(Mat source){
        Mat result = new Mat();
        Mat mask = new Mat();
        Rect rect = new Rect(15, 15, source.cols()-15, source.rows()-15);

        Mat bgdModel = new Mat();
        Mat fgdModel = new Mat();
        Size sz;
        if(source.rows() > source.cols()){
            sz = new Size(192, 144);
        }else{
            sz = new Size(144, 196);
        }


        Imgproc.grabCut(source, mask, rect, bgdModel, fgdModel, 1, 0);
        Core.compare(mask, source, mask, Core.CMP_EQ);
        Mat foreground = new Mat(source.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        source.copyTo(foreground, result);

        return foreground;
    }

    private void findMaxrect(Mat input){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        //尋找輪廓
        Imgproc.findContours(input, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //最大輪廓
        double area = Imgproc.boundingRect(contours.get(0)).area();
        int Maxindex = 0;
        for (int i = 0; i < contours.size(); i++) {
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
            if (tempArea > area) {
                area = tempArea;
                Maxindex = i;
            }
        }
    }
}