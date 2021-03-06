package com.example.ecamera2;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class compare {

    /*****************比對是否類似********************/
    //sourceMat為目前拍的的圖片 templateMat為原本計算出來要比對的
    public static boolean comparePic(Mat sourceMat, Mat templateMat, Point destination){
        boolean good = false;

        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(1000);

        sourceMat = pictureCut(sourceMat, destination);
        Imgproc.calcHist(Arrays.asList(sourceMat), new MatOfInt(0), new Mat(), sourceMat, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(templateMat), new MatOfInt(0), new Mat(), templateMat, histSize, ranges);
        double res = Imgproc.compareHist(sourceMat, templateMat, Imgproc.CV_COMP_CORREL);
        if(res > 0.6){
            good = true;
        }
        return good;
    }
    /****************照片裁切*****************/
    public static Mat pictureCut(Mat tem, Point Center){

        int[] point = new int[2];
        point[0] = (int) Center.x;
        point[1] = (int) Center.y;

        int disHeight = 480;
        int disWidth = 270;
        Rect rect;
        if(point[0] < 0|| point[1] < 0)
            rect = new Rect(0, 0,disWidth, disHeight);
        else if((point[0] - disWidth/2 ) < 0|| (point[1] - disHeight/2 ) <0) {
            if(point[0] + disWidth > tem.width())
                rect = new Rect(tem.width() -disWidth -1, point[1], disWidth, disHeight);
            else if (point[1] + disHeight > tem.height())
                rect = new Rect(point[0], point[1] - disHeight -1, disWidth, disHeight);
            rect = new Rect(point[0], point[1], disWidth, disHeight);
        }
        else if ((point[0] + disWidth/2 ) > tem.width()|| (point[1] + disHeight/2 ) > tem.height())
            rect = new Rect(tem.width() -disWidth -1,tem.height() - disHeight -1, disWidth, disHeight);
        else
            rect = new Rect(point[0] - disWidth/2, point[1] - disHeight/2, disWidth, disHeight);
//        Mat result = new Mat(tem, rect);
        return new Mat(tem, rect);
    }
    /*****************移動特徵點*****************/
    public Point moveOrigPoint(Mat temMat, Point checkPoint, Point origPoint){
            Point Target = new Point();

            int width = temMat.width();
            int height = temMat.height();
            double x = width/2; double y = height/2;
            double differX = checkPoint.x - x;
            double differY = checkPoint.y - y;

            Target.x = origPoint.x +differX;
            Target.y = origPoint.y +differY;

        return Target;
    }

    /***************調整並繪上推薦框*************************/
    //temMat為要畫的圖，checkPoint為推薦之目標點，origPoint為原始找到的特徵點
    public Mat drawRecoommandation(Mat temMat, Point checkPoint, Point origPoint){

        moveOrigPoint(temMat, checkPoint, origPoint);
        Imgproc.rectangle(temMat, new Point(origPoint.x + (temMat.width()/ 16), origPoint.y + (temMat.height()/ 16)), new Point(origPoint.x - (temMat.width()/ 16), origPoint.y - (temMat.height()/ 16)), new Scalar(0, 255, 255), 10);
        Imgproc.rectangle(temMat, new Point(checkPoint.x + (temMat.width()/ 16), checkPoint.y + (temMat.height()/ 16)), new Point(checkPoint.x - (temMat.width()/ 16), checkPoint.y - (temMat.height()/ 16)), new Scalar(0, 0, 255), 10);

        return temMat;
    }
}
