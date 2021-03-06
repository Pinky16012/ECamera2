package com.example.ecamera2;


import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class horizontal {
    Mat tem_img = new Mat();
    Mat gray_Mat = new Mat();
    Mat Canny_Mat = new Mat();
    Mat lines = new Mat();
    Mat HouphLines = new Mat();

    double Score = 0.0;
    double Theta = 0.0;

    Double LargestLine = 0.0;

    double[] Lpoint;
    List<MatOfPoint> vertex = new ArrayList<>();


    /******************主要進程*********************/
    public Double horizontal_composition(Bitmap tem){

        Utils.bitmapToMat(tem, tem_img);

        Imgproc.cvtColor(tem_img, gray_Mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray_Mat, gray_Mat, new Size(9, 9), 0);
        Imgproc.Canny(gray_Mat,Canny_Mat, 50, 100);
        Imgproc.HoughLinesP(Canny_Mat, lines, 1, Math.PI/180, 10, 10, 2);

        HouphLines.create(Canny_Mat.rows(), Canny_Mat.cols(), CvType.CV_8UC1);

        //保留30以下線並找出最長線
        for (int j = 0; j < lines.rows(); j++) {
            double[] points = lines.get(j, 0);
            double x1, y1, x2, y2 , theta;
            x1 = points[0];
            y1 = points[1];
            x2 = points[2];
            y2 = points[3];

            theta = azimuthAngle(x1, y1, x2, y2);
            //If 30 >theta >= 0 or  180 > theta >= 150, drawing lines on an image
            if((theta >= 0 && theta <= 30) || (theta >= 150 && theta <= 180) || (theta >= 60 && theta <= 120)){
                double temDis = Distance(x1, y1, x2, y2);
                Point pt1 = new Point(x1, y1);
                Point pt2 = new Point(x2, y2);
                Imgproc.line(HouphLines, pt1, pt2, new Scalar(255, 0, 0), 2);

                if( temDis > LargestLine){
                    LargestLine = temDis;
                    double[] temLpoint = {x1, y1, x2, y2};
                    Lpoint = temLpoint;
                }
            }
        }
        // 畫出最長線
        Double x1, y1, x2, y2;
        x1 = Lpoint[0]; y1 = Lpoint[1]; x2 = Lpoint[2]; y2 = Lpoint[3];
        Point pt1 = new Point(x1, y1);
        Point pt2 = new Point(x2, y2);
        Imgproc.line(tem_img, pt1, pt2, new Scalar(0, 255, 255), 10);

//        Utils.matToBitmap(tem_img, tem);
        asume_Score();

        return Score;
    }

    /****************計算分數*******************/
    private void asume_Score(){
        double theta = 0.0, tem_Score;
        theta = azimuthAngle(Lpoint[0], Lpoint[1], Lpoint[2], Lpoint[3]);
        Theta = Math.toRadians(theta);
        if(theta > 30)
            if (theta <= 90)
                theta = 90 - theta;
            else if(theta > 90)
                if (theta > 150)
                    theta = 180 -theta;
                else
                    theta = theta - 90;

        System.out.println("theta"+theta);
        System.out.println("Theta"+Theta);
        tem_Score = 100 - (theta / 30) * 100;
        BigDecimal b = new BigDecimal(tem_Score);
        Score = b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    private double azimuthAngle(double x1, double y1, double x2, double y2){
        double angle = 0;
        double x_dis, y_dis, LongEdge;

        x_dis = Math.abs(x1 -x2);
        y_dis = Math.abs(y1 - y2);
        LongEdge = Math.sqrt(x_dis * x_dis + y_dis * y_dis);
        angle = (Math.asin(y_dis / LongEdge)/Math.PI * 180);

        checkAngle(angle, x1, y1, x2, y2);
        return angle;
    }

    private double checkAngle(double angle, double x1, double y1, double x2, double y2){
        //0-90
        if (x1>x2 && y1>y2) {
            angle=angle+180;
        }
        //271-360
        else if (x1>x2  &&  y1>y2) {
            angle=(90-angle)+270;
        }
        return angle;
    }

    private double Distance(double x1, double y1, double x2, double y2){
        Double x_dis, y_dis, LongEdge;
        x_dis = Math.abs(x1 -x2);
        y_dis = Math.abs(y1 - y2);
        LongEdge = Math.sqrt(x_dis * x_dis + y_dis * y_dis);

        return LongEdge;
    }

    /*********************計算要畫出的方形位置************************/
    private void calVertex() {
        Point a, b, c, d;           //方形四個頂點
        double width = tem_img.width() / 8;
        double height = tem_img.height() / 8;
        double theta = azimuthAngle(Lpoint[0], Lpoint[1], Lpoint[2], Lpoint[3]);
        if (theta >= 60 && theta <= 120){
            if (theta <= 90)
                theta = 90 - theta;
            else if(theta > 90)
                if (theta > 150)
                    theta = 180 -theta;
                else
                    theta = theta - 90;
            double Theta2= Math.toRadians(theta);
            if (Lpoint[1] <= Lpoint[3]) {
                a = new Point(Lpoint[0], Lpoint[1]);
                d = new Point(Lpoint[0] + (Math.sin(Theta2) * height), Lpoint[1] + (Math.cos(Theta2) * height));
                b = new Point(a.x - (Math.cos(Theta2) * width), a.y + (Math.sin(Theta2) * width));
                c = new Point(b.x + (Math.sin(Theta2) * height), b.y + (Math.cos(Theta2) * height));
            } else {
                a = new Point(Lpoint[0], Lpoint[1]);
                b = new Point(Lpoint[0] - (Math.cos(Theta) * height), Lpoint[1] + (Math.sin(Theta) * height));
                c = new Point(b.x - (Math.sin(Theta) * width), b.y - (Math.cos(Theta) * width));
                d = new Point(a.x - (Math.sin(Theta) * width), a.y - (Math.cos(Theta) * width));
            }
         }
        else {
            if (Lpoint[1] <= Lpoint[3]) {
                a = new Point(Lpoint[0], Lpoint[1]);
                b = new Point(Lpoint[0] + (Math.cos(Theta) * width), Lpoint[1] + (Math.sin(Theta) * width));
                c = new Point(b.x - (Math.sin(Theta) * height), b.y + (Math.cos(Theta) * height));
                d = new Point(a.x - (Math.sin(Theta) * height), a.y + (Math.cos(Theta) * height));
            } else {
                a = new Point(Lpoint[0], Lpoint[1]);
                b = new Point(Lpoint[0] + (Math.cos(Theta) * width), Lpoint[1] - (Math.sin(Theta) * width));
                c = new Point(b.x + (Math.sin(Theta) * height), b.y + (Math.cos(Theta) * height));
                d = new Point(a.x + (Math.sin(Theta) * height), a.y + (Math.cos(Theta) * height));
            }
        }
        System.out.println(Math.cos(Theta)* width);
        System.out.println(Theta);
        vertex.add(
                new MatOfPoint(a, b, c, d)
        );
    }


    /*********************畫出推薦方形************************/
    public Bitmap recommend(Bitmap temBitmap){
        Mat temMat = new Mat();
        Utils.bitmapToMat(temBitmap, temMat);
        double theta = azimuthAngle(Lpoint[0], Lpoint[1], Lpoint[2], Lpoint[3]);

        //drawline
        calVertex();
        Imgproc.polylines(temMat, vertex, true, new Scalar(227, 23, 13), 10);
//        Imgproc.line(temMat, new Point(Lpoint[0], Lpoint[1]), new Point(Lpoint[2], Lpoint[3]), new Scalar(255, 255, 255), 10);



        if (theta >= 60 && theta <= 120)
            Imgproc.rectangle(temMat, new Point(Lpoint[0], Lpoint[1]), new Point(Lpoint[0] - (temMat.width()/8), Lpoint[1] + temMat.height()/8), new Scalar(0, 0, 255), 10);
        else
            Imgproc.rectangle(temMat, new Point(Lpoint[0], Lpoint[1]), new Point(Lpoint[0] + (temMat.width()/8), Lpoint[1] + temMat.height()/8), new Scalar(0, 0, 255), 10);
            Imgproc.polylines(temMat, vertex, true, new Scalar(227, 23, 13), 10);

        Utils.matToBitmap(temMat, temBitmap);

        return temBitmap;
    }

    public Mat pictureCut(Mat tem, Bitmap temBitmap){
        int[] point = new int[2];

        int disHeight = 480;
        int disWidth = 270;
        Rect rect;
        Utils.matToBitmap(tem ,temBitmap);
        Matrix matrix = new Matrix();

        matrix.reset();

        matrix.postRotate((float)Theta);
        temBitmap = Bitmap.createBitmap(temBitmap, 0, 0, temBitmap.getWidth(), temBitmap.getHeight(),matrix, true);
        Point temPoint = new Point();
        temPoint.x = Lpoint[0] * Math.acos(Theta) - Lpoint[1] *Math.asin(Theta);
        temPoint.y = Lpoint[1] * Math.asin(Theta) + Lpoint[0] *Math.acos(Theta);

        Utils.bitmapToMat(temBitmap, tem);
        point[0] = (int) temPoint.x;
        point[1] = (int) temPoint.y;

        if(point[0] < 0|| point[1] < 0)
            rect = new Rect(0, 0,disWidth, disHeight);
        else if((point[0] - disWidth/2 ) < 0|| (point[1] - disHeight/2 ) <0)
            rect = new Rect(point[0], point[1], disWidth , disHeight);
        else if ((point[0] + disWidth/2 ) > tem.width()|| (point[1] + disHeight/2 ) > tem.height())
            rect = new Rect(tem.width() -disWidth -1,tem.height() - disHeight -1, disWidth, disHeight);
        else
            rect = new Rect(point[0] - disWidth/2, point[1] - disHeight/2, disWidth, disHeight);
        Mat result = new Mat(tem, rect);

        return new Mat(tem, rect);
    }

    public Point returnDisP(){
        Point point = new Point();
        point.x = Lpoint[0];
        point.y = Lpoint[1];

        return point;
    }
}